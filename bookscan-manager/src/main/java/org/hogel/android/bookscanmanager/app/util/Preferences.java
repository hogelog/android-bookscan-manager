package org.hogel.android.bookscanmanager.app.util;

import com.google.inject.Inject;

import android.content.SharedPreferences;

import roboguice.util.Strings;

public class Preferences {
    private static final String LOGIN_MAIL = "login_mail";
    private static final String LOGIN_PASS = "login_pass";

    @Inject
    private SharedPreferences preferences;

    @Inject
    public Preferences() {
    }

    public String getLoginMail() {
        return preferences.getString(LOGIN_MAIL, "");
    }

    public void putLoginMail(String loginMail) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LOGIN_MAIL, loginMail);
        editor.commit();
    }

    public String getLoginPass() {
        return preferences.getString(LOGIN_PASS, "");
    }

    public void putLoginPass(String loginPass) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LOGIN_PASS, loginPass);
        editor.commit();
    }

    public boolean hasLoginPreference() {
        return Strings.notEmpty(getLoginMail()) && Strings.notEmpty(getLoginPass());
    }
}
