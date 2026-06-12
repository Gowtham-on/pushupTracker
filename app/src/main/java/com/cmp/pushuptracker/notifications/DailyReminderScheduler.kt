package com.cmp.pushuptracker.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.cmp.pushuptracker.utils.PreferenceUtil
import com.cmp.pushuptracker.utils.ReminderType
import java.time.Duration
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

object DailyReminderScheduler {
    const val TYPE_MORNING = ReminderType.MORNING
    const val TYPE_EVENING = ReminderType.EVENING
    internal const val KEY_REMINDER_TYPE = "reminder_type"
    private const val FLEX_INTERVAL_MINUTES = 15L

    fun scheduleAll(context: Context) {
        scheduleReminder(context, TYPE_MORNING)
        scheduleReminder(context, TYPE_EVENING)
    }

    fun scheduleReminder(context: Context, reminderType: String) {
        if (!PreferenceUtil.isReminderEnabled(context, reminderType)) {
            WorkManager.getInstance(context).cancelUniqueWork(uniqueName(reminderType))
            return
        }
        val delayMillis = computeDelayMillis(context, reminderType)
        val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS, FLEX_INTERVAL_MINUTES, TimeUnit.MINUTES)
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(KEY_REMINDER_TYPE to reminderType))
            .addTag(reminderType)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            uniqueName(reminderType),
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    private fun computeDelayMillis(context: Context, reminderType: String): Long {
        val now = ZonedDateTime.now()
        val reminderTime = PreferenceUtil.getReminderTime(context, reminderType)
        val targetTime = LocalTime.of(reminderTime.hour, reminderTime.minute)

        var next = now.withHour(targetTime.hour)
            .withMinute(targetTime.minute)
            .withSecond(0)
            .withNano(0)

        if (!next.isAfter(now)) {
            next = next.plusDays(1)
        }

        return Duration.between(now, next).toMillis().coerceAtLeast(0L)
    }

    private fun uniqueName(reminderType: String) = "daily_reminder_$reminderType"
}
