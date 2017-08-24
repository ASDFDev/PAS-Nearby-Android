package org.sp.attendance.utils

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

import com.mcxiaoke.koi.ext.asDateString
import com.mcxiaoke.koi.ext.asString
import com.mcxiaoke.koi.ext.timestamp
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateTime {

    fun getTrueCalendarToString(date: Date): String =
            date.asString(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()))

    fun getTrueDateToString(date: Date): String =
            date.asString(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()))

    fun getTrueTimeToString(date: Date): String =
            date.asString(SimpleDateFormat("HH:mm:ss", Locale.getDefault()))

    fun getTrueYearToString(date: Date): String =
            date.asString(SimpleDateFormat("yyyy", Locale.getDefault()))

    fun getTrueMonthToString(date: Date): String =
            date.asString(SimpleDateFormat("LLLL", Locale.getDefault()))

    fun getMonthInNumberToName(monthInInt: String): String{
        val monthParse = SimpleDateFormat("MM", Locale.getDefault())
        val monthDisplay = SimpleDateFormat("MMMM", Locale.getDefault())
        return monthDisplay.format(monthParse.parse(monthInInt))
    }

    fun getTrueDayToString(date : Date): String =
            date.asString(SimpleDateFormat("dd", Locale.getDefault()))

    fun convertMilliToSecs(time : Int): Int = time * 60000

    fun convertSecondsToMins(time: Int): Int = time * 60

    fun timeInHourMinSecs(time: Long): String{
        val FORMAT = "%02d:%02d:%02d"
        return String.format(FORMAT,
                TimeUnit.MILLISECONDS.toHours(time),
                TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(time)),
                TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(time)))
    }

    fun getDeviceCurrentTime(): String{
        val timeStampInLong = timestamp()
        return timeStampInLong.asDateString()
    }
}
