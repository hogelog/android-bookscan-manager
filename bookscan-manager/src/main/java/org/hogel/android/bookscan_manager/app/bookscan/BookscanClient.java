package org.hogel.android.bookscan_manager.app.bookscan;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.hogel.android.bookscan_manager.app.R;
import org.hogel.android.bookscan_manager.app.activity.LoginDialogFragment;
import org.hogel.android.bookscan_manager.app.bookscan.model.Book;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.inject.InjectResource;
import roboguice.util.Strings;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class BookscanClient {
    private static final Logger LOG = LoggerFactory.getLogger(BookscanClient.class);

    @Inject
    private SharedPreferences preferences;

    @Inject
    private Context context;

    @InjectResource(R.string.prefs_login_mail)
    private String prefsLoginMail;
    @InjectResource(R.string.prefs_login_pass)
    private String prefsLoginPass;

    @Inject
    private FragmentManager fragmentManager;
    @Inject
    private LoginDialogFragment loginDialogFragment;

    @Inject
    private CookieManager cookieManager;

    public void login() {
        if (!hasLoginPreference()) {
            loginDialogFragment.show(fragmentManager, "login");
        } else {
            _login(preferences.getString(prefsLoginMail, ""), preferences.getString(prefsLoginPass, ""));
        }
    }

    public void login(String loginMail, String loginPass) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(prefsLoginMail, loginMail);
        editor.putString(prefsLoginPass, loginPass);
        editor.commit();
        _login(loginMail, loginPass);
    }

    private void _login(String loginMail, String loginPass) {
        Connection connection = connect(R.string.url_login)
            .method(Connection.Method.POST)
            .data("email", loginMail, "password", loginPass);
        Optional<Document> result = execute(connection);
        if (result.isPresent()) {
            Toast.makeText(context, R.string.action_login_success, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.action_login_fail, Toast.LENGTH_SHORT).show();
        }
    }

    public List<Book> fetchBookList() {
        final List<Book> books = Lists.newArrayList();
        Connection connection = connect(R.string.url_book_list).method(Connection.Method.GET);
        Optional<Document> result = execute(connection);

        if (!result.isPresent()) {
            return books;
        }

        Document document = result.get();
        Elements bookLinks = document.select("#sortable_box > div > a");
        for (Element bookLink : bookLinks) {
            String href = bookLink.attr("href");
            Uri bookUri = Uri.parse(href);
            String digest = bookUri.getQueryParameter("d");
            String hash = bookUri.getQueryParameter("h");
            String filename = bookUri.getQueryParameter("f");
            final Book book = new Book(filename, hash, digest);
            books.add(book);
        }
        return books;
    }

    public Connection connect(int urlId) {
        String url = context.getString(urlId);
        return Jsoup.connect(url).cookies(cookieManager.getCookies());
    }

    public Optional<Document> execute(Connection connection) {
        AsyncTask<Connection, Void, Optional<Document>> task = new AsyncTask<Connection, Void, Optional<Document>>() {
            @Override
            protected Optional<Document> doInBackground(Connection... connections) {
                try {
                    Connection.Response response = connections[0].execute();
                    Document document = response.parse();
                    Map<String, String> cookies = response.cookies();
                    if (cookies.size() > 0) {
                        cookieManager.putAll(cookies);
                    }
                    return Optional.of(document);
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                    Toast.makeText(context, R.string.error_network, Toast.LENGTH_LONG).show();
                }
                return Optional.absent();
            }

            @Override
            protected void onPreExecute() {
                Activity activity = (Activity) context;
                activity.setProgressBarIndeterminateVisibility(true);
            }

            @Override
            protected void onPostExecute(Optional<Document> documentOptional) {
                Activity activity = (Activity) context;
                activity.setProgressBarIndeterminateVisibility(false);
            }
        };
        task.execute(connection);
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            LOG.error(e.getMessage(), e);
        }
        return Optional.absent();
    }

    public boolean hasLoginPreference() {
        return hasLoginMail() && hasLoginPass();
    }

    private boolean hasLoginMail() {
        return Strings.notEmpty(preferences.getString(prefsLoginMail, ""));
    }

    private boolean hasLoginPass() {
        return Strings.notEmpty(preferences.getString(prefsLoginPass, ""));
    }

    public boolean isLogin() {
        return cookieManager.getCookies().size() > 0;
    }
}
