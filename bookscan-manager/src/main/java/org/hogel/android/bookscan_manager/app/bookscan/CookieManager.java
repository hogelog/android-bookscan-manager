package org.hogel.android.bookscan_manager.app.bookscan;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.common.collect.Maps;
import lombok.Getter;
import net.arnx.jsonic.JSON;
import org.hogel.android.bookscan_manager.app.R;
import roboguice.inject.InjectResource;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

@Singleton
public class CookieManager {
    @Getter
    private final ConcurrentMap<String, String> cookies = Maps.newConcurrentMap();

    private final SharedPreferences preferences;

    private final String prefsCookies;

    @Inject
    public CookieManager(Context context, SharedPreferences preferences) {
        this.preferences = preferences;
        prefsCookies = context.getString(R.string.prefs_cookies);
        String jsonCookies = preferences.getString(prefsCookies, "{}");
        cookies.clear();;
        cookies.putAll((Map<? extends String, ? extends String>) JSON.decode(jsonCookies));
    }

    public void putAll(Map<String, String> cookies) {
        this.cookies.putAll(cookies);
        SharedPreferences.Editor edit = preferences.edit();
        final String jsonCookies = JSON.encode(cookies);
        edit.putString(prefsCookies, jsonCookies);
        edit.commit();
    }
}
