package com.example.dartapp.data

enum class AppearanceOption(val text: String) {
    LIGHT("Light"),
    DARK("Dark"),
    SYSTEM("System Preference");

    fun useDarkTheme(isSystemInDarkTheme: Boolean): Boolean {
        if (this == DARK) {
            return true
        }
        return this == SYSTEM && isSystemInDarkTheme
    }

    companion object {
        val Default = SYSTEM

        fun fromOrdinal(ordinal: Int): AppearanceOption {
            return values()[ordinal]
        }
    }
}