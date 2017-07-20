package org.sp.attendance.service

/*
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

import android.util.Log
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class NtpHttp {

    private val webServers = arrayOf("google.com",
            /*Student portal */
            "esp.sp.edu.sg",
            /*SP email service provider */
            "pod51057.outlook.com",
            "www.sp.edu.sg",
            "facebook.com")

    private var date: Date? = null

    var TAG: String = "NtpHttp"

    fun getRemoteTime(): Date? {
        val RANDOM_WEB_SERVER = webServers[Random().nextInt(webServers.size)]
        val url = URL("https://" + RANDOM_WEB_SERVER)
        val conn = url.openConnection()
        if (conn is HttpURLConnection) {
            conn.requestMethod = "HEAD"
            val dateTime = conn.getHeaderFieldDate("Date", 0)
            if (dateTime > 0) {
                date = Date(dateTime)
                Log.i(TAG, "Using web server time from: " + RANDOM_WEB_SERVER)
                return date
            }
            return date
        }
        return date
    }
}