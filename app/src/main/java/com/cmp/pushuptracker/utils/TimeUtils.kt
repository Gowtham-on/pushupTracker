package com.cmp.pushuptracker.utils

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
}