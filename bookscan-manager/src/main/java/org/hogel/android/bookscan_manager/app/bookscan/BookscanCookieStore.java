package org.hogel.android.bookscan_manager.app.bookscan;

import android.content.Context;
import com.loopj.android.http.PersistentCookieStore;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BookscanCookieStore extends PersistentCookieStore {
    @Inject
    public BookscanCookieStore(Context context) {
        super(context);
    }
}
