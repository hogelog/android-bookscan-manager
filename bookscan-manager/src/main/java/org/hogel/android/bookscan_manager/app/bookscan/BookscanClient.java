package org.hogel.android.bookscan_manager.app.bookscan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.loopj.android.http.AsyncHttpClient;
import org.hogel.android.bookscan_manager.app.R;
import org.hogel.android.bookscan_manager.app.activity.BookscanPreferenceActivity;
import roboguice.RoboGuice;
import roboguice.config.DefaultRoboModule;
import roboguice.inject.InjectResource;
import roboguice.util.Strings;

import javax.inject.Inject;

public class BookscanClient {
    @Inject private AsyncHttpClient asyncHttpClient;
    @Inject private SharedPreferences preferences;
    @Inject private Context context;

    @InjectResource(R.string.prefs_login_mail) private String prefs_login_mail;
    @InjectResource(R.string.prefs_login_pass) private String prefs_login_pass;

    public BookscanClient() {
    }

    public void login() {
        if (!hasLoginPreference()) {
            final Intent intent = new Intent(context, BookscanPreferenceActivity.class);
            context.startActivity(intent);
        }
    }

    public boolean hasLoginPreference() {
        return hasLoginMail() && hasLoginPass();
    }

    private boolean hasLoginMail() {
        return Strings.notEmpty(preferences.getString(prefs_login_mail, ""));
    }

    private boolean hasLoginPass() {
        return Strings.notEmpty(preferences.getString(prefs_login_pass, ""));
    }
}
