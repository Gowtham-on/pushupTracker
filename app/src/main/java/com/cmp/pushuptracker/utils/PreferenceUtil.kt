package com.cmp.pushuptracker.utils

import android.content.Context
import androidx.core.content.edit

object PreferenceUtil {
    const val PUSHUP_PREF = "pushup_pref"
    const val SHOULDER_DETECT = "shoulder_detect"
    const val ELBOW_DETECT = "elbow_detect"
    const val HIP_DETECT = "hip_detect"

    fun savePushupPreference(prefName: String, context: Context, enabled: Boolean) {
        context
            .getSharedPreferences(PUSHUP_PREF, Context.MODE_PRIVATE)
            .edit {
                putBoolean(prefName, enabled)
            }
    }

    fun getPushupPreference(prefName: String, context: Context): Boolean {
        val defaultTheme = context
            .getSharedPreferences(PUSHUP_PREF, Context.MODE_PRIVATE)
            .getBoolean(prefName, false)

        return defaultTheme
    }

}