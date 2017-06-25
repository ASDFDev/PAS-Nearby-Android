package org.sp.attendance.utils.account.spice;


/* This file was originally part of ATS_NativeUI. It was adapted to suit the needs of this project.
 * ATS_NativeUI is also licensed under GPLv3. You can find the original code here:
 * https://github.com/Minatosan/ATS_NativeUI
 *
 * Copyright 2017 Daniel Quah and Justin Xin
 *
 * This file is part of org.sp.attendance
 *
 * ATS_Nearby is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ATS_Nearby is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

import java.util.Collections;
import java.util.List;

class CookiesManager {

    private static List<String> storedCookies;
    static Boolean isCookiesStored;

    static List<String> getCookies() {
        return storedCookies;
    }

    static void setCookies(List<String> cookies) {
    }

    static void clearCookies() {
        storedCookies = Collections.emptyList();
    }

}
