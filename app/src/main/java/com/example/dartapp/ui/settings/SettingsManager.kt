package com.example.dartapp.ui.settings

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import java.util.prefs.PreferenceChangeListener

object SettingsManager: SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onSharedPreferenceChanged(sp: SharedPreferences?, key: String?) {
        if (sp == null) return

        if (key == "dark_theme" || key == "auto_theme") {
            applyStoredTheming(sp)
        }
    }

    /**
     * Apply the theme to the app that was stored in shared preferences.
     */
    fun applyStoredTheming(sp: SharedPreferences) {
        val autoTheme = sp.getBoolean("auto_theme", true)
        val darkTheme = sp.getBoolean("dark_theme", false)
        applyTheming(autoTheme, darkTheme)
    }

    private fun applyTheming(autoTheme: Boolean, darkTheme: Boolean = false) {
        var mode = when (darkTheme) {
            true -> AppCompatDelegate.MODE_NIGHT_YES
            false -> AppCompatDelegate.MODE_NIGHT_NO
        }

        if (autoTheme) {
            mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        AppCompatDelegate.setDefaultNightMode(mode)
    }

}