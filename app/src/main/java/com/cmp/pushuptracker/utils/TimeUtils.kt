package com.cmp.pushuptracker.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeUtils {

    /**
     * Returns today's date formatted according to the given pattern.
     *
     * @param pattern A date-time pattern string, for example "yyyy-MM-dd" or "dd MMMM yyyy".
     * @return The formatted date string for today's date.
     */
    fun getTodayDate(pattern: String): String {
        val date = Date()
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(date)
    }

    fun formatTimestamp(
        millis: Long,
        pattern: String,
        locale: Locale = Locale.getDefault()
    ): String {
        val date = Date(millis)
        val formatter = SimpleDateFormat(pattern, locale)
        return formatter.format(date)
    }

    fun getMinsSecFromSeconds(interval: Long): String {
        val minutes = interval / 60
        val seconds = interval % 60

        return buildString {
            if (minutes > 0) append("$minutes min")
            if (minutes > 0 && seconds > 0) append(" ")
            if (seconds > 0) append("$seconds sec")
            if (minutes == 0L && seconds == 0L) append("0 sec")
        }
    }
}