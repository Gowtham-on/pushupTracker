package com.cmp.pushuptracker.utils

import android.content.Context
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
}