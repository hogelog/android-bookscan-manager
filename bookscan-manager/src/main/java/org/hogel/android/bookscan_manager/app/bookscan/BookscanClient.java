package org.hogel.android.bookscan_manager.app.bookscan;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import com.loopj.android.http.AsyncHttpClient;
import org.hogel.android.bookscan_manager.app.R;
import org.hogel.android.bookscan_manager.app.activity.LoginDialogFragment;
import roboguice.inject.InjectResource;
import roboguice.util.Strings;

import javax.inject.Inject;

public class BookscanClient {
    @Inject
    private AsyncHttpClient asyncHttpClient;

    @Inject
    private SharedPreferences preferences;

    @Inject
    private Context context;

    @InjectResource(R.string.prefs_login_mail)
    private String prefsLoginMail;
    @InjectResource(R.string.prefs_login_pass)
    private String prefsLoginPass;

    @Inject
    private FragmentManager fragmentManager;
    @Inject
    private LoginDialogFragment loginDialogFragment;

    public BookscanClient() {
    }

    public void login() {
        if (!hasLoginPreference()) {
            loginDialogFragment.show(fragmentManager, "hoge");
        }
    }

    public void login(String loginMail, String loginPass) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(prefsLoginMail, loginMail);
        editor.putString(prefsLoginPass, loginPass);
        editor.commit();
    }

    public boolean hasLoginPreference() {
        return hasLoginMail() && hasLoginPass();
    }

    private boolean hasLoginMail() {
        return Strings.notEmpty(preferences.getString(prefsLoginMail, ""));
    }

    private boolean hasLoginPass() {
        return Strings.notEmpty(preferences.getString(prefsLoginPass, ""));
    }
}
