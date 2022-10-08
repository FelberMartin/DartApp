package com.example.dartapp.data.repository

import com.example.dartapp.data.AppearanceOption
import com.example.dartapp.data.persistent.keyvalue.IKeyValueStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val keyValueStorage: IKeyValueStorage
) {

    enum class BooleanSetting(val defaultValue: Boolean = true) {
        AskForDouble,
        AskForCheckout,
        ShowStatsAfterLegFinished,
    }

    private val APPEARANCE_KEY = "appearance"


    val appearanceOptionFlow: Flow<AppearanceOption> = keyValueStorage.getFlow(APPEARANCE_KEY).map { value: String? ->
        if (value == null) {
            return@map AppearanceOption.Default
        }
        val ordinal = value.toInt()
        return@map AppearanceOption.fromOrdinal(ordinal)
    }

    suspend fun setAppearanceOption(value: AppearanceOption) {
        keyValueStorage.put(APPEARANCE_KEY, value.ordinal.toString())
    }

    fun getBooleanSettingFlow(setting: BooleanSetting) : Flow<Boolean> {
        return keyValueStorage.getFlow(setting.name).map { value: String? ->
            value?.toBoolean() ?: setting.defaultValue
        }
    }

    suspend fun setBooleanSetting(setting: BooleanSetting, value: Boolean) {
        keyValueStorage.put(setting.name, value.toString())
    }
}