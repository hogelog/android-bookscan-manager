package org.hogel.android.bookscanmanager.app.bookscan;

import com.loopj.android.http.AsyncHttpClient;

import javax.inject.Inject;

public class BookscanHttpClient extends AsyncHttpClient {
    @Inject
    public BookscanHttpClient(BookscanCookieStore bookscanCookieStore) {
        setCookieStore(bookscanCookieStore);
    }
}
