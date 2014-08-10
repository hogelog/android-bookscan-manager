package org.hogel.android.bookscanmanager.app.guice;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import com.google.inject.*;
import org.hogel.android.bookscanmanager.app.util.Preferences;
import org.hogel.bookscan.BookscanClient;
import roboguice.inject.ContextSingleton;

public class BookscanModule implements Module {
    @Override
    public void configure(Binder binder) {
        binder.bind(DownloadManager.class).toProvider(DownloadManagerProvider.class).in(ContextSingleton.class);
        binder.bind(BookscanClient.class).toProvider(BookscanClientProvider.class).in(Singleton.class);
    }

    public static class DownloadManagerProvider implements Provider<DownloadManager> {
        @Inject
        private Context context;

        @Override
        public DownloadManager get() {
            return (DownloadManager) context.getSystemService(Service.DOWNLOAD_SERVICE);
        }
    }

    public static class BookscanClientProvider implements Provider<BookscanClient> {
        @Inject
        private Preferences preferences;

        @Override
        public BookscanClient get() {
            final BookscanClient client = new BookscanClient();
            client.putCookies(preferences.getCookies());
            return client;
        }
    }
}
