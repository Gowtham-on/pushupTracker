package com.cmp.pushuptracker.utils

import kotlinx.datetime.*
import kotlin.time.Clock
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.ExperimentalTime

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


    @OptIn(ExperimentalTime::class)
    fun getWeekDates(weekOffset: Int): List<String> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val startOfWeek = today.minus(DatePeriod(days = today.dayOfWeek.isoDayNumber - 1))
        val targetWeekStart = startOfWeek.plus(DatePeriod(days = weekOffset * 7))

        return (0..6).map { dayOffset ->
            val date = targetWeekStart.plus(DatePeriod(days = dayOffset))
            "%02d/%02d/%04d".format(date.dayOfMonth, date.monthNumber, date.year)
        }
    }

    @OptIn(ExperimentalTime::class)
    fun getWeekDateRangeString(weekOffset: Int): String {
        if (weekOffset == 0) {
            return "This Week"
        } else if (weekOffset == -1) {
            return "Last Week"
        } else if (weekOffset == 1){
            return "Next Week"
        }
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

        // Start of current week (Monday)
        val startOfWeek = today.minus(DatePeriod(days = today.dayOfWeek.isoDayNumber - 1))

        // Shift to target week
        val targetStart = startOfWeek.plus(DatePeriod(days = weekOffset * 7))
        val targetEnd = targetStart.plus(DatePeriod(days = 6))

        fun format(date: LocalDate): String {
            return "%02d/%02d/%04d".format(date.dayOfMonth, date.monthNumber, date.year)
        }

        return "${format(targetStart)} - ${format(targetEnd)}"
    }
}