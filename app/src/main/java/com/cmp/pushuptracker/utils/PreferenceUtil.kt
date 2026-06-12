package com.cmp.pushuptracker.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.core.content.edit

object PreferenceUtil {
    const val ONBOARDING_PREF = "onboarding_pref"
    const val PUSHUP_PREF = "pushup_pref"
    const val SHOULDER_DETECT = "shoulder_detect"
    const val ELBOW_DETECT = "elbow_detect"
    const val HIP_DETECT = "hip_detect"

    const val TOTAL_INTERVAL = "total_interval"
    const val TOTAL_REP = "total_rep"
    const val TOTAL_SET = "total_set"

    private const val REVIEW_PREF = "review_pref"
    private const val REVIEW_LAUNCH_COUNT = "review_launch_count"
    private const val REVIEW_SHOWN = "review_shown"
    private const val REVIEW_LAST_PROMPT_AT = "review_last_prompt_at"
    private const val REVIEW_MIN_INTERVAL_MS = 7 * 24 * 60 * 60 * 1000L
    private const val REVIEW_LAUNCH_THRESHOLD = 5
    private const val NOTIFICATION_PREF = "notification_pref"
    private const val NOTIFICATION_PROMPTED = "notification_prompted"
    private const val REMINDER_PREF = "reminder_pref"
    private const val REMINDER_MORNING_ENABLED = "reminder_morning_enabled"
    private const val REMINDER_EVENING_ENABLED = "reminder_evening_enabled"
    private const val REMINDER_MORNING_HOUR = "reminder_morning_hour"
    private const val REMINDER_MORNING_MINUTE = "reminder_morning_minute"
    private const val REMINDER_EVENING_HOUR = "reminder_evening_hour"
    private const val REMINDER_EVENING_MINUTE = "reminder_evening_minute"

    fun savePushupSettingsPreference(prefName: String, context: Context, enabled: Boolean) {
        context
            .getSharedPreferences(PUSHUP_PREF, Context.MODE_PRIVATE)
            .edit {
                putBoolean(prefName, enabled)
            }
    }

    fun getPushupSettingsPreference(prefName: String, context: Context): Boolean {
        val defaultTheme = context
            .getSharedPreferences(PUSHUP_PREF, Context.MODE_PRIVATE)
            .getBoolean(prefName, false)

        return defaultTheme
    }

    fun savePreviewPushupPref(prefName: String, context: Context, count: Int) {
        context
            .getSharedPreferences(PUSHUP_PREF, Context.MODE_PRIVATE)
            .edit {
                putInt(prefName, count)
            }
    }

    fun getPreviewPushupPref(prefName: String, context: Context): Int {
        val defaultTheme = context
            .getSharedPreferences(PUSHUP_PREF, Context.MODE_PRIVATE)
            .getInt(prefName, 0)

        return defaultTheme
    }

    fun isOnboardingCompleted(context: Context): Boolean {
        return context.getSharedPreferences(ONBOARDING_PREF, Context.MODE_PRIVATE)
            .getBoolean("onboarding_completed", false)
    }

    fun completeOnboarding(context: Context) {
        context.getSharedPreferences(ONBOARDING_PREF, Context.MODE_PRIVATE)
            .edit {
                putBoolean("onboarding_completed", true)
            }
    }

    fun recordAppLaunch(context: Context) {
        val prefs = context.getSharedPreferences(REVIEW_PREF, Context.MODE_PRIVATE)
        val newCount = prefs.getInt(REVIEW_LAUNCH_COUNT, 0) + 1
        prefs.edit {
            putInt(REVIEW_LAUNCH_COUNT, newCount)
        }
    }

    fun shouldPromptForReview(context: Context): Boolean {
        if (isDebuggable(context)) return true
        val prefs = context.getSharedPreferences(REVIEW_PREF, Context.MODE_PRIVATE)
        if (prefs.getBoolean(REVIEW_SHOWN, false)) return false

        val launches = prefs.getInt(REVIEW_LAUNCH_COUNT, 0)
        if (launches < REVIEW_LAUNCH_THRESHOLD) return false

        val lastPromptAt = prefs.getLong(REVIEW_LAST_PROMPT_AT, 0L)
        val now = System.currentTimeMillis()
        return now - lastPromptAt >= REVIEW_MIN_INTERVAL_MS
    }

    fun markReviewPrompted(context: Context, completed: Boolean) {
        if (isDebuggable(context)) return
        context
            .getSharedPreferences(REVIEW_PREF, Context.MODE_PRIVATE)
            .edit {
                putLong(REVIEW_LAST_PROMPT_AT, System.currentTimeMillis())
                if (completed) {
                    putBoolean(REVIEW_SHOWN, true)
                }
            }
    }

    private fun isDebuggable(context: Context): Boolean {
        return (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }

    fun shouldAskForNotificationPermission(context: Context): Boolean {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU) return false
        val prefs = context.getSharedPreferences(NOTIFICATION_PREF, Context.MODE_PRIVATE)
        val alreadyPrompted = prefs.getBoolean(NOTIFICATION_PROMPTED, false)
        return !alreadyPrompted
    }

    fun markNotificationPermissionAsked(context: Context) {
        context
            .getSharedPreferences(NOTIFICATION_PREF, Context.MODE_PRIVATE)
            .edit {
                putBoolean(NOTIFICATION_PROMPTED, true)
            }
    }

    fun isReminderEnabled(context: Context, reminderType: String): Boolean {
        val key = when (reminderType) {
            ReminderType.MORNING -> REMINDER_MORNING_ENABLED
            ReminderType.EVENING -> REMINDER_EVENING_ENABLED
            else -> return false
        }
        return context
            .getSharedPreferences(REMINDER_PREF, Context.MODE_PRIVATE)
            .getBoolean(key, true)
    }

    fun setReminderEnabled(context: Context, reminderType: String, enabled: Boolean) {
        val key = when (reminderType) {
            ReminderType.MORNING -> REMINDER_MORNING_ENABLED
            ReminderType.EVENING -> REMINDER_EVENING_ENABLED
            else -> return
        }
        context
            .getSharedPreferences(REMINDER_PREF, Context.MODE_PRIVATE)
            .edit {
                putBoolean(key, enabled)
            }
    }

    fun getReminderTime(context: Context, reminderType: String): ReminderTime {
        val prefs = context.getSharedPreferences(REMINDER_PREF, Context.MODE_PRIVATE)
        val (hourKey, minuteKey, defaultHour) = when (reminderType) {
            ReminderType.MORNING -> Triple(REMINDER_MORNING_HOUR, REMINDER_MORNING_MINUTE, 8)
            ReminderType.EVENING -> Triple(REMINDER_EVENING_HOUR, REMINDER_EVENING_MINUTE, 21)
            else -> Triple(REMINDER_MORNING_HOUR, REMINDER_MORNING_MINUTE, 8)
        }
        return ReminderTime(
            hour = prefs.getInt(hourKey, defaultHour).coerceIn(0, 23),
            minute = prefs.getInt(minuteKey, 0).coerceIn(0, 59)
        )
    }

    fun setReminderTime(context: Context, reminderType: String, time: ReminderTime) {
        val (hourKey, minuteKey) = when (reminderType) {
            ReminderType.MORNING -> REMINDER_MORNING_HOUR to REMINDER_MORNING_MINUTE
            ReminderType.EVENING -> REMINDER_EVENING_HOUR to REMINDER_EVENING_MINUTE
            else -> return
        }
        context
            .getSharedPreferences(REMINDER_PREF, Context.MODE_PRIVATE)
            .edit {
                putInt(hourKey, time.hour.coerceIn(0, 23))
                putInt(minuteKey, time.minute.coerceIn(0, 59))
            }
    }
}

data class ReminderTime(val hour: Int, val minute: Int) {
    fun displayText(): String = "%02d:%02d".format(hour, minute)
}

object ReminderType {
    const val MORNING = "morning"
    const val EVENING = "evening"
}
