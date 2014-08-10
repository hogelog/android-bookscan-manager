package org.hogel.android.bookscanmanager.app.bookscan;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;
import com.google.inject.Inject;
import org.hogel.android.bookscanmanager.app.util.Cookies;
import org.hogel.bookscan.BookscanClient;
import org.hogel.bookscan.model.Book;
import org.hogel.bookscan.model.OptimizedBook;

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
        download(downloadUrl, book.getFilename());
    }

    public void download(OptimizedBook optimizedBook) {
        String downloadUrl = optimizedBook.createDownloadUrl();
        download(downloadUrl, optimizedBook.getFilename());
    }

    private void download(String url, String filename) {
        final DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(url));
        downloadRequest.addRequestHeader("Cookie", Cookies.pack(client.getCookies()));
        downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

        downloadManager.enqueue(downloadRequest);
    }
}
