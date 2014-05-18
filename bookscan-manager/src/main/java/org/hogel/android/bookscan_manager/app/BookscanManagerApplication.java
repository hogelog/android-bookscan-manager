package org.hogel.android.bookscan_manager.app;

import android.app.Application;
import org.hogel.android.bookscan_manager.app.guice.BookscanModule;
import roboguice.RoboGuice;
import roboguice.config.DefaultRoboModule;

public class BookscanManagerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        DefaultRoboModule defaultRoboModule = RoboGuice.newDefaultRoboModule(this);
        BookscanModule bookscanModule = new BookscanModule();
        RoboGuice.setBaseApplicationInjector(this,
            RoboGuice.DEFAULT_STAGE,
            defaultRoboModule,
            bookscanModule);
    }
}
