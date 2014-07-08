package org.hogel.android.bookscanmanager.app.util;

import android.content.SharedPreferences;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import net.arnx.jsonic.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.util.Strings;

import java.util.Map;

public class Preferences {
    private static final Logger LOG = LoggerFactory.getLogger(Preferences.class);

    private static final String LOGIN_MAIL = "login_mail";
    private static final String LOGIN_PASS = "login_pass";
    private static final String COOKIES = "cookies";

    @Inject
    private SharedPreferences preferences;

    @Inject
    public Preferences() {
    }

    public String getLoginMail() {
        return preferences.getString(LOGIN_MAIL, "");
    }

    public void putLoginMail(String loginMail) {
        preferences.edit().putString(LOGIN_MAIL, loginMail).commit();
    }

    public String getLoginPass() {
        return preferences.getString(LOGIN_PASS, "");
    }

    public void putLoginPass(String loginPass) {
        preferences.edit().putString(LOGIN_PASS, loginPass).commit();
    }

    public boolean hasLoginPreference() {
        return Strings.notEmpty(getLoginMail()) && Strings.notEmpty(getLoginPass());
    }

    public void putLoginPreference(String loginMail, String loginPass) {
        preferences
                .edit()
                .putString(LOGIN_MAIL, loginMail)
                .putString(LOGIN_PASS, loginPass)
                .commit();
    }

    public Map<String, String> getCookies() {
        String jsonCookies = preferences.getString(COOKIES, null);
        if (jsonCookies == null) {
            return ImmutableMap.of();
        }
        return JSON.decode(jsonCookies);
    }

    public void putCookies(Map<String, String> cookies) {
        String jsonCookies = JSON.encode(cookies);
        preferences.edit().putString(COOKIES, jsonCookies).commit();
    }
}
