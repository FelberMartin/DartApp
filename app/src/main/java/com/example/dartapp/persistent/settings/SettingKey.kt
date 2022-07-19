package com.example.dartapp.persistent.settings

import com.example.dartapp.persistent.settings.options.AppearanceOption

sealed class SettingKey<T: Any>(val key: String, val defaultValue: T) {
    object APPEARANCE : SettingKey<Int>("appearance", AppearanceOption.LIGHT.ordinal)
    object ASK_FOR_DOUBLE : SettingKey<Boolean>("ask_for_double", true)
    object ASK_FOR_CHECKOUT : SettingKey<Boolean>("ask_for_checkout", true)
}
