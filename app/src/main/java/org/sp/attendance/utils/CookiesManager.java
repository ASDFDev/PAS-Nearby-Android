package org.sp.attendance.utils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by HexGate on 5/6/16.
 */

public class CookiesManager {

    public static Boolean isCookiesStored;
    private static List<String> storedCookies;

    public static List<String> getCookies() {
        return storedCookies;
    }

    public static void setCookies(List<String> cookies) {
        if (isCookiesStored = false) {
            storedCookies = cookies;
        }
    }

    public static void clearCookies() {
        storedCookies = Arrays.asList();
    }

}