package com.example.dartapp.data.repository

import com.example.dartapp.data.AppearanceOption
import com.example.dartapp.data.persistent.keyvalue.IKeyValueStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val keyValueStorage: IKeyValueStorage
) {

    private val APPEARANCE_KEY = "appearance"
    private val ASK_FOR_DOUBLE_KEY = "ask_for_double"
    private val ASK_FOR_CHECKOUT_KEY = "ask_for_checkout"

    val appearanceOptionFlow: Flow<AppearanceOption> = keyValueStorage.getFlow(APPEARANCE_KEY).map { value: String? ->
        if (value == null) {
            return@map AppearanceOption.Default
        }
        val ordinal = value.toInt()
        return@map AppearanceOption.fromOrdinal(ordinal)
    }

    val askForDoubleFlow: Flow<Boolean> = keyValueStorage.getFlow(ASK_FOR_DOUBLE_KEY).map { value: String? ->
        value?.toBoolean() ?: true
    }
    val askForCheckoutFlow: Flow<Boolean> = keyValueStorage.getFlow(ASK_FOR_CHECKOUT_KEY).map { value: String? ->
        value?.toBoolean() ?: true
    }

    suspend fun setAppearanceOption(value: AppearanceOption) {
        keyValueStorage.put(APPEARANCE_KEY, value.ordinal.toString())
    }

    suspend fun setAskForDouble(value: Boolean) {
        keyValueStorage.put(ASK_FOR_DOUBLE_KEY, value.toString())
    }

    suspend fun setAskForCheckout(value: Boolean) {
        keyValueStorage.put(ASK_FOR_CHECKOUT_KEY, value.toString())
    }
}