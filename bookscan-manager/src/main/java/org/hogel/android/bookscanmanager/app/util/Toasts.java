package org.hogel.android.bookscanmanager.app.util;

import android.content.Context;
import android.widget.Toast;

public class Toasts {
    public static void show(Context context, int resId) {
        if (context == null) {
            return;
        }
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }
}
