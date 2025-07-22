package com.cmp.pushuptracker.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.cmp.pushuptracker.utils.Theme
import com.cmp.pushuptracker.utils.ThemePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class UtilViewmodel @Inject constructor(
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    var theme by mutableStateOf(Theme.SYSTEM.name)
        private set

    fun setTheme(theme: Theme) {
        this.theme = theme.name
        ThemePreferences.saveThemePreference(appContext, theme)
    }

    fun getThemeFromPreference(): String {
        return ThemePreferences.getThemePreference(appContext)
    }

    init {
        theme = getThemeFromPreference()
    }

}