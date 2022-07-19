package com.example.dartapp.ui.screens.settings

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dartapp.persistent.settings.SettingKey
import com.example.dartapp.persistent.settings.SettingsStoreBase
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val settingsStore: SettingsStoreBase
): ViewModel() {

    private val _askForDouble = MutableLiveData<Boolean>()
    val askForDouble: LiveData<Boolean> = _askForDouble

    val askForCheckout = mutableStateOf(SettingKey.ASK_FOR_CHECKOUT.defaultValue)

    init {
        viewModelScope.launch {
            _askForDouble.value = settingsStore.read(SettingKey.ASK_FOR_DOUBLE)
            askForCheckout.value = settingsStore.read(SettingKey.ASK_FOR_CHECKOUT)
        }
    }

    fun changeAskForDouble(checked: Boolean) {
        viewModelScope.launch {
            settingsStore.write(SettingKey.ASK_FOR_DOUBLE, checked)
            _askForDouble.value = checked
        }
    }

    fun changeAskForCheckout(checked: Boolean) {
        viewModelScope.launch {
            settingsStore.write(SettingKey.ASK_FOR_CHECKOUT, checked)
            askForCheckout.value = checked
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
