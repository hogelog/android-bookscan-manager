package org.hogel.android.bookscanmanager.app.bookscan;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;
import com.google.inject.Inject;
import org.hogel.android.bookscanmanager.app.util.Cookies;
import org.hogel.bookscan.BookscanClient;
import org.hogel.bookscan.model.Book;

public class BookscanDownloadManager {
    @Inject
    BookscanClient client;

    @Inject
    DownloadManager downloadManager;

    @Inject
    public BookscanDownloadManager() {
    }

    public void download(Book book) {
        String downloadUrl = book.createDownloadUrl();
        final DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(downloadUrl));
        downloadRequest.addRequestHeader("Cookie", Cookies.pack(client.getCookies()));
        downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, book.getFilename());

        downloadManager.enqueue(downloadRequest);
    }
}
