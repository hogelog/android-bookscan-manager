package org.hogel.android.bookscan_manager.app.bookscan;

import android.content.Context;
import com.loopj.android.http.PersistentCookieStore;
import org.apache.http.cookie.Cookie;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class BookscanCookieStore extends PersistentCookieStore {
    @Inject
    public BookscanCookieStore(Context context) {
        super(context);
    }

    public String pack() {
        List<Cookie> cookies = getCookies();
        StringBuilder cookieBuilder = new StringBuilder();
        for (Cookie cookie : cookies) {
            if (cookieBuilder.length() > 0) {
                cookieBuilder.append("; ");
            }
            cookieBuilder.append(cookie.getName()).append("=").append(cookie.getValue());
        }
        return cookieBuilder.toString();
    }
}
