package org.hogel.android.bookscanmanager.app.guice;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import com.google.inject.Binder;
import com.google.inject.Module;
import roboguice.inject.ContextSingleton;

import javax.inject.Inject;
import javax.inject.Provider;

public class BookscanModule implements Module {
    @Override
    public void configure(Binder binder) {
        binder.bind(DownloadManager.class).toProvider(DownloadManagerProvider.class);
    }

    @ContextSingleton
    public static class DownloadManagerProvider implements Provider<DownloadManager> {
        @Inject
        private Context context;

        @Override
        public DownloadManager get() {
            return (DownloadManager) context.getSystemService(Service.DOWNLOAD_SERVICE);
        }
    }
}
