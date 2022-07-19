package com.example.dartapp.ui.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dartapp.persistent.settings.SettingKey
import com.example.dartapp.persistent.settings.SettingsStoreBase
import com.example.dartapp.persistent.settings.options.AppearanceOption
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val settingsStore: SettingsStoreBase
): ViewModel() {

    var askForDouble by mutableStateOf(SettingKey.ASK_FOR_DOUBLE.defaultValue)
        private set

    var askForCheckout by mutableStateOf(SettingKey.ASK_FOR_CHECKOUT.defaultValue)
        private set

    var appearanceOption by mutableStateOf(AppearanceOption.values()[SettingKey.APPEARANCE.defaultValue])
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

    fun changeAppearanceOption(newAppearanceOption: AppearanceOption) {
        viewModelScope.launch {
            settingsStore.write(SettingKey.APPEARANCE, newAppearanceOption.ordinal)
            appearanceOption = newAppearanceOption
        }
    }

}
