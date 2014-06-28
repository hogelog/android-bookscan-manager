package org.hogel.android.bookscanmanager.app.util;

import java.util.Map;

public class Cookies {
    public static String pack(Map<String, String> cookies) {
        StringBuilder cookieBuilder = new StringBuilder();
        for (String name : cookies.keySet()) {
            if (cookieBuilder.length() > 0) {
                cookieBuilder.append("; ");
            }
            cookieBuilder.append(name).append("=").append(cookies.get(name));
        }
        return cookieBuilder.toString();
    }
}
