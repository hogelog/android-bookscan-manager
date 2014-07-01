package org.hogel.android.bookscanmanager.app.util;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;

public class BusProvider {
    private static final Bus BUS = new Bus(ThreadEnforcer.MAIN);

    private static final Handler HANDLER = new Handler();

    public static void register(Activity activity) {
        BUS.register(activity);
    }

    public static void register(Fragment fragment) {
        BUS.register(fragment);
    }

    public static void post(final Object event) {
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                BUS.post(event);
            }
        });
    }

    private BusProvider() {
    }
}
