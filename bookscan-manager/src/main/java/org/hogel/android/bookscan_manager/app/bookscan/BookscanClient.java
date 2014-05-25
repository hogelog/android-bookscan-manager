package org.hogel.android.bookscan_manager.app.bookscan;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
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
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Singleton
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

    public void login(Listener listener) {
        if (!hasLoginPreference()) {
            loginDialogFragment.show(fragmentManager, "login");
        } else {
            _login(preferences.getString(prefsLoginMail, ""), preferences.getString(prefsLoginPass, ""), listener);
        }
    }

    public void login(String loginMail, String loginPass, Listener listener) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(prefsLoginMail, loginMail);
        editor.putString(prefsLoginPass, loginPass);
        editor.commit();
        _login(loginMail, loginPass, listener);
    }

    private void _login(String loginMail, String loginPass, final Listener listener) {
        bookscanCookieStore.clear();

        final String url = context.getString(R.string.url_login);
        final RequestParams params = new RequestParams("email", loginMail, "password", loginPass);
        bookscanHttpClient.post(context, url, params, new ResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(context, R.string.action_login_success, Toast.LENGTH_SHORT).show();
                listener.onSuccess(url, responseBody);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                listener.onFinish();
            }
        });
    }

    public void fetchBookList(final Listener listener) {
        final String url = context.getString(R.string.url_book_list);
        bookscanHttpClient.get(context, url, new ResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode != HttpStatus.SC_OK) {
                    LOG.error("{}: {}", url, statusCode);
                    return;
                }
                listener.onSuccess(url, responseBody);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                listener.onFinish();
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

    public void download(final Book book, final Listener listener) {
        final String downloadUrl  = context.getString(R.string.url_download);
        final RequestParams params = new RequestParams("d", book.getDigest(), "f", book.getFilename());

        book.setDownloading(true);
        bookscanHttpClient.get(downloadUrl, params, new ResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                final File downloadFile = new File(context.getString(R.string.path_donload), book.getFilename());
                try {
                    Files.write(responseBody, downloadFile);
                    Toast.makeText(context, R.string.action_download_success, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                    Toast.makeText(context, R.string.action_download_fail, Toast.LENGTH_SHORT).show();
                }
                listener.onSuccess(downloadUrl, responseBody);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                book.setDownloading(false);
                listener.onFinish();
            }
        });
    }

    private class ResponseHandler extends AsyncHttpResponseHandler {
        @Override
        public void onStart() {
            Activity activity = (Activity) context;
            activity.setProgressBarIndeterminateVisibility(true);
        }

        @Override
        public void onFinish() {
            Activity activity = (Activity) context;
            activity.setProgressBarIndeterminateVisibility(false);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
            LOG.error(error.getMessage(), error);
            Toast.makeText(context, R.string.action_network_error, Toast.LENGTH_SHORT).show();
        }
    }

    public static class Listener {
        public void onSuccess(String url, byte[] responseBody) {
        }

        public void onFinish() {
        }
    }

    abstract public static class FetchBookListListener extends Listener {
        @Override
        public void onSuccess(String url, byte[] responseBody) {
            final String html = new String(responseBody, Charsets.UTF_8);
            final List<Book> books = Lists.newArrayList();
            Document document = Jsoup.parse(html, url);
            Elements bookLinks = document.select("#sortable_box > div > a");
            for (Element bookLink : bookLinks) {
                String href = bookLink.attr("href");
                Uri bookUri = Uri.parse(href);
                String digest = bookUri.getQueryParameter("d");
                String hash = bookUri.getQueryParameter("h");
                String filename = bookUri.getQueryParameter("f");
                final Book book = new Book(filename, hash, digest, false);
                books.add(book);
            }

            onSuccess(url, html, books);
        }

        abstract public void onSuccess(String url, String html, List<Book> books);
    }
}
