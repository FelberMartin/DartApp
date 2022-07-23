package com.example.dartapp.ui.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.dartapp.persistent.settings.SettingKey
import com.example.dartapp.persistent.settings.SettingsStoreBase
import com.example.dartapp.persistent.settings.options.AppearanceOption
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.shared.NavigationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsStore: SettingsStoreBase,
    navigationManager: NavigationManager
): NavigationViewModel(navigationManager) {

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


    fun useDarkTheme(isSystemInDarkMode: Boolean): Boolean {
        if (appearanceOption == AppearanceOption.DARK) {
            return true
        }
        return appearanceOption == AppearanceOption.SYSTEM && isSystemInDarkMode
    }

}
