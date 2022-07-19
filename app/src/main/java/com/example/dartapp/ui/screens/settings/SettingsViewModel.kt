package com.example.dartapp.ui.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dartapp.persistent.settings.SettingKey
import com.example.dartapp.persistent.settings.SettingsStoreBase
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val settingsStore: SettingsStoreBase
): ViewModel() {

    var askForDouble by mutableStateOf(SettingKey.ASK_FOR_DOUBLE.defaultValue)
        private set

    var askForCheckout by mutableStateOf(SettingKey.ASK_FOR_CHECKOUT.defaultValue)
        private set

    init {
        viewModelScope.launch {
            askForDouble = settingsStore.read(SettingKey.ASK_FOR_DOUBLE)
            askForCheckout = settingsStore.read(SettingKey.ASK_FOR_CHECKOUT)
        }
    }

    fun changeAskForDouble(checked: Boolean) {
        viewModelScope.launch {
            settingsStore.write(SettingKey.ASK_FOR_DOUBLE, checked)
            askForDouble = checked
        }
    }

    fun changeAskForCheckout(checked: Boolean) {
        viewModelScope.launch {
            settingsStore.write(SettingKey.ASK_FOR_CHECKOUT, checked)
            askForCheckout = checked
        }
    }

//    fun readAppearanceOption(): AppearanceOption {
//        viewModelScope.launch {
//            AppearanceOption.values()[settingsStore.read(SettingKey.APPEARANCE)]
//        }
//    }
//
//    suspend fun writeAppearanceOption(appearanceOption: AppearanceOption) =
//        settingsStore.write(SettingKey.APPEARANCE, appearanceOption.ordinal)
//
//    suspend fun <T:Any> readSetting(key: SettingKey<T>): T = settingsStore.read(key)
//    suspend fun <T:Any> writeSetting(key: SettingKey<T>, value: T) = settingsStore.write(key, value)
}
