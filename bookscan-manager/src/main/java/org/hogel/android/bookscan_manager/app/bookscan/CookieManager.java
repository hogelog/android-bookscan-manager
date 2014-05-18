package org.hogel.android.bookscan_manager.app.bookscan;

import com.google.common.collect.Maps;
import lombok.Getter;

import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class CookieManager {
    @Getter
    private final Map<String, String> cookies = Maps.newTreeMap();

    public void putAll(Map<String, String> cookies) {
        this.cookies.putAll(cookies);
    }
}
