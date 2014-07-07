package org.hogel.android.bookscanmanager.app.guice;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import com.google.inject.*;
import org.hogel.android.bookscanmanager.app.util.Preferences;
import org.hogel.bookscan.AsyncBookscanClient;
import roboguice.inject.ContextSingleton;

public class BookscanModule implements Module {
    @Override
    public void configure(Binder binder) {
        binder.bind(DownloadManager.class).toProvider(DownloadManagerProvider.class).in(ContextSingleton.class);
        binder.bind(AsyncBookscanClient.class).toProvider(AsyncBookscanClientProvider.class).in(Singleton.class);
    }

    public static class DownloadManagerProvider implements Provider<DownloadManager> {
        @Inject
        private Context context;

        @Override
        public DownloadManager get() {
            return (DownloadManager) context.getSystemService(Service.DOWNLOAD_SERVICE);
        }
    }

    public static class AsyncBookscanClientProvider implements Provider<AsyncBookscanClient> {
        @Inject
        private Preferences preferences;

        @Override
        public AsyncBookscanClient get() {
            final AsyncBookscanClient client = new AsyncBookscanClient();
            client.putCookies(preferences.getCookies());
            return client;
        }
    }
}
