package org.sp.attendance.models


import com.mcxiaoke.koi.ext.asString
import java.text.SimpleDateFormat
import java.util.*

object DateTime {

    fun getTrueCalendar(date: Date): String {
        return date.asString(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()))
    }

    fun getTrueDate(date: Date): String {
        return date.asString(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()))
    }

    fun getTrueTime(date: Date): String {
        return date.asString(SimpleDateFormat("HH:mm:ss", Locale.getDefault()))
    }

    fun getTrueYear(date: Date): String {
        return date.asString(SimpleDateFormat("yyyy", Locale.getDefault()))
    }

    fun getTrueMonth(date: Date): String {
        return date.asString(SimpleDateFormat("LLLL", Locale.getDefault()))
    }

    fun getTrueDay(date : Date): String {
        return date.asString(SimpleDateFormat("dd", Locale.getDefault()))
    }

}
