package org.hogel.android.bookscan_manager.app.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import org.hogel.android.bookscan_manager.app.bookscan.CookieManager;
import org.hogel.android.bookscan_manager.app.dao.DatabaseHelper;

public class BookscanModule implements Module {
    @Override
    public void configure(Binder binder) {
    }
}
