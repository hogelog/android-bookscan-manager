package org.hogel.android.bookscan_manager.app.bookscan;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.hogel.android.bookscan_manager.app.R;
import org.hogel.android.bookscan_manager.app.activity.LoginDialogFragment;
import org.hogel.android.bookscan_manager.app.bookscan.model.Book;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.inject.InjectResource;
import roboguice.util.Strings;

import javax.inject.Inject;
import java.util.List;

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
    private BookscanHttpClient bookscanHttpClient;
    @Inject
    private BookscanCookieStore bookscanCookieStore;

    public void login(SuccessListener listener) {
        if (!hasLoginPreference()) {
            loginDialogFragment.show(fragmentManager, "login");
        } else {
            _login(preferences.getString(prefsLoginMail, ""), preferences.getString(prefsLoginPass, ""), listener);
        }
    }

    public void login(String loginMail, String loginPass, SuccessListener listener) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(prefsLoginMail, loginMail);
        editor.putString(prefsLoginPass, loginPass);
        editor.commit();
        _login(loginMail, loginPass, listener);
    }

    private void _login(String loginMail, String loginPass, final SuccessListener listener) {
        bookscanCookieStore.clear();

        final String url = context.getString(R.string.url_login);
        final RequestParams params = new RequestParams("email", loginMail, "password", loginPass);
        bookscanHttpClient.post(context, url, params, new ResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(context, R.string.action_login_success, Toast.LENGTH_SHORT).show();
                final String html = new String(responseBody, Charsets.UTF_8);
                listener.onSuccess(url, html);
            }
        });
    }

    public void fetchBookList(final SuccessListener listener) {
        final String url = context.getString(R.string.url_book_list);
        bookscanHttpClient.get(context, url, new ResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode != HttpStatus.SC_OK) {
                    LOG.error("{}: {}", url, statusCode);
                    return;
                }
                final String html = new String(responseBody, Charsets.UTF_8);
                listener.onSuccess(url, html);
            }
        });
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
        return bookscanCookieStore.getCookies().size() > 0;
    }

    private class ResponseHandler extends AsyncHttpResponseHandler {
        @Override
        public void onStart() {
            Activity activity = (Activity) context;
            activity.setProgressBarIndeterminateVisibility(true);
            super.onStart();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            Activity activity = (Activity) context;
            activity.setProgressBarIndeterminateVisibility(false);
        }
    }

    public static interface SuccessListener {
        public void onSuccess(String url, String html);
    }

    abstract public static class FetchBookListListener implements SuccessListener {
        @Override
        public void onSuccess(String url, String html) {
            final List<Book> books = Lists.newArrayList();
            Document document = Jsoup.parse(html, url);
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

            onSuccess(url, html, books);
        }

        abstract public void onSuccess(String url, String html, List<Book> books);
    }
}
