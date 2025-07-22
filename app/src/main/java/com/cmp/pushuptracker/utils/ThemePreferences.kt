package com.cmp.pushuptracker.utils
import android.content.Context
import androidx.core.content.edit

/**
 * Helper object to store and retrieve the user's theme preference.
 */
object ThemePreferences {
    private const val PREFS_NAME = "theme_prefs"
    private const val APP_THEME = "app_theme"

    fun saveThemePreference(context: Context, theme: Theme) {
        context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putString(APP_THEME, theme.name)
            }
    }

    fun getThemePreference(context: Context): String {
        val defaultTheme =  context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(APP_THEME, "")

       return  defaultTheme ?: Theme.SYSTEM.name
    }
}
