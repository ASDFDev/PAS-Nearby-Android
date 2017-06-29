package org.sp.attendance.models


import com.mcxiaoke.koi.ext.asString
import java.text.SimpleDateFormat
import java.util.*

object DateTime {

    fun getTrueCalendarToString(date: Date): String {
        return date.asString(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()))
    }

    fun getTrueDateToString(date: Date): String {
        return date.asString(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()))
    }

    fun getTrueTimeToString(date: Date): String {
        return date.asString(SimpleDateFormat("HH:mm:ss", Locale.getDefault()))
    }

    fun getTrueYearToString(date: Date): String {
        return date.asString(SimpleDateFormat("yyyy", Locale.getDefault()))
    }

    fun getTrueMonthToString(date: Date): String {
        return date.asString(SimpleDateFormat("LLLL", Locale.getDefault()))
    }

    fun getTrueDayToString(date : Date): String {
        return date.asString(SimpleDateFormat("dd", Locale.getDefault()))
    }

    fun convertMilliToSecs(time : Int): Int {
        return time * 60000
    }

    fun convertSecondsToMins(time: Int): Int{
        return time * 60
    }
}
