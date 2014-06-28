package org.hogel.android.bookscanmanager.app.bookscan;

import com.google.inject.Inject;

import org.hogel.android.bookscanmanager.app.util.Cookies;
import org.hogel.bookscan.AsyncBookscanClient;
import org.hogel.bookscan.Constants;
import org.hogel.bookscan.model.Book;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;

public class BookscanDownloadManager {
    @Inject
    AsyncBookscanClient client;

    @Inject
    DownloadManager downloadManager;

    @Inject
    public BookscanDownloadManager() {
    }

    public void download(Book book) {
        final Uri.Builder downloadUriBuilder = new Uri.Builder();
        downloadUriBuilder.scheme(Constants.URL_SCHEME);
        downloadUriBuilder.authority(Constants.URL_DOMAIN);
        downloadUriBuilder.path("/download.php");
        downloadUriBuilder.appendQueryParameter("d", book.getDigest());
        downloadUriBuilder.appendQueryParameter("f", book.getFilename());

        final DownloadManager.Request downloadRequest = new DownloadManager.Request(downloadUriBuilder.build());
        downloadRequest.addRequestHeader("Cookie", Cookies.pack(client.getCookies()));
        downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, book.getFilename());

        downloadManager.enqueue(downloadRequest);
    }
}
