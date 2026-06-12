package com.cmp.pushuptracker.utils

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import java.text.SimpleDateFormat
import java.time.format.DateTimeParseException
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

object TimeUtils {
    const val STORAGE_DATE_PATTERN = "yyyy-MM-dd"
    const val LEGACY_DATE_PATTERN = "dd/MM/yyyy"

    private val storageDateFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern(STORAGE_DATE_PATTERN, Locale.getDefault())
    private val legacyDateFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern(LEGACY_DATE_PATTERN, Locale.getDefault())

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

    fun todayStorageDate(): String {
        return java.time.LocalDate.now().format(storageDateFormatter)
    }

    fun formatTimestampForStorage(millis: Long): String {
        val date = java.time.Instant.ofEpochMilli(millis)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()
        return date.format(storageDateFormatter)
    }

    fun parseAppDate(input: String): java.time.LocalDate? {
        if (input.isBlank()) return null
        return try {
            java.time.LocalDate.parse(input, storageDateFormatter)
        } catch (_: DateTimeParseException) {
            try {
                java.time.LocalDate.parse(input, legacyDateFormatter)
            } catch (_: DateTimeParseException) {
                null
            }
        }
    }

    fun toStorageDate(input: String): String {
        return parseAppDate(input)?.format(storageDateFormatter) ?: input
    }

    fun formatDateForDisplay(
        input: String,
        pattern: String = LEGACY_DATE_PATTERN,
        locale: Locale = Locale.getDefault()
    ): String {
        val date = parseAppDate(input) ?: return input
        return date.format(DateTimeFormatter.ofPattern(pattern, locale))
    }

    fun dayOfMonthLabel(input: String): String {
        return parseAppDate(input)?.dayOfMonth?.toString() ?: input.take(2)
    }

    fun getHrsMinsSecFromSeconds(interval: Long): String {
        val hours = interval / 3600
        val minutes = (interval % 3600) / 60
        val seconds = interval % 60

        return buildString {
            when {
                hours > 0 -> {
                    append("$hours hr")
                    if (minutes > 0) append(" $minutes min")
                }
                minutes > 0 -> {
                    append("$minutes min")
                    if (seconds > 0) append(" $seconds sec")
                }
                else -> {
                    append("$seconds sec")
                }
            }
        }
    }


    @OptIn(ExperimentalTime::class)
    fun getWeekDates(weekOffset: Int): List<String> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val startOfWeek = today.minus(DatePeriod(days = today.dayOfWeek.isoDayNumber - 1))
        val targetWeekStart = startOfWeek.plus(DatePeriod(days = weekOffset * 7))

        return (0..6).map { dayOffset ->
            val date = targetWeekStart.plus(DatePeriod(days = dayOffset))
            java.time.LocalDate.of(date.year, date.month.number, date.day)
                .format(storageDateFormatter)
        }
    }

    @OptIn(ExperimentalTime::class)
    fun getWeekDateRangeString(weekOffset: Int): String {
        if (weekOffset == 0) {
            return "This Week"
        } else if (weekOffset == -1) {
            return "Last Week"
        } else if (weekOffset == 1) {
            return "Next Week"
        }
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

        // Start of current week (Monday)
        val startOfWeek = today.minus(DatePeriod(days = today.dayOfWeek.isoDayNumber - 1))

        // Shift to target week
        val targetStart = startOfWeek.plus(DatePeriod(days = weekOffset * 7))
        val targetEnd = targetStart.plus(DatePeriod(days = 6))

        fun format(date: LocalDate): String {
            return java.time.LocalDate.of(date.year, date.month.number, date.day)
                .format(DateTimeFormatter.ofPattern(LEGACY_DATE_PATTERN, Locale.getDefault()))
        }

        return "${format(targetStart)} - ${format(targetEnd)}"
    }

    fun getDateRangeLastSundayToThisSaturday(pattern: String, noOfDaysBefore: Int): List<String> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)

        val lastSundayCal = cal.clone() as Calendar
        lastSundayCal.add(Calendar.DAY_OF_YEAR, -noOfDaysBefore)

        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        val result = mutableListOf<String>()

        val tempCal = lastSundayCal.clone() as Calendar
        while (!tempCal.after(cal)) {
            result.add(sdf.format(tempCal.time))
            tempCal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return result
    }

    fun isToday(dateString: String): Boolean {
        val parsedDate = parseAppDate(dateString)
        if (parsedDate != null) return parsedDate == java.time.LocalDate.now()

        val today = Calendar.getInstance()
        return today.get(Calendar.DAY_OF_MONTH) == dateString.toIntOrNull()
    }


    fun getCurrentMonthYear(): String {
        val formatter =
            java.time.format.DateTimeFormatter.ofPattern("MMM yyyy", Locale.getDefault())
        val currentDate = java.time.LocalDate.now()
        return currentDate.format(formatter)
    }

    fun formatShortDate(input: String): String {
        return formatDateForDisplay(input, "dd-MMMM yyyy")
    }

}
