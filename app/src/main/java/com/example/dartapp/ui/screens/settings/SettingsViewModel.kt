package com.example.dartapp.ui.screens.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.dartapp.data.AppearanceOption
import com.example.dartapp.data.repository.SettingsRepository
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.shared.NavigationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    navigationManager: NavigationManager
): NavigationViewModel(navigationManager) {

    val appearanceOption: LiveData<AppearanceOption> = settingsRepository.appearanceOptionFlow.asLiveData()
    val askForDouble: LiveData<Boolean> =
        settingsRepository.getBooleanSettingFlow(SettingsRepository.BooleanSetting.AskForDouble).asLiveData()
    val askForCheckout: LiveData<Boolean> =
        settingsRepository.getBooleanSettingFlow(SettingsRepository.BooleanSetting.AskForCheckout).asLiveData()


    fun changeAppearanceOption(newAppearanceOption: AppearanceOption) {
        viewModelScope.launch {
            settingsRepository.setAppearanceOption(newAppearanceOption)
        }
    }

    fun changeAskForDouble(checked: Boolean) {
        viewModelScope.launch {
            settingsRepository.setBooleanSetting(SettingsRepository.BooleanSetting.AskForDouble, checked)
        }
    }

    fun changeAskForCheckout(checked: Boolean) {
        viewModelScope.launch {
            settingsRepository.setBooleanSetting(SettingsRepository.BooleanSetting.AskForCheckout, checked)
        }
    }

}
