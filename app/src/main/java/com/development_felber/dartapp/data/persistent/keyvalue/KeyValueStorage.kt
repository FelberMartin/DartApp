package com.development_felber.dartapp.data.persistent.keyvalue

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


class KeyValueStorage(
    private val context: Context
): IKeyValueStorage {

    override suspend fun put(key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        context.dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    override fun getFlow(key: String): Flow<String?> {
        val dataStoreKey = stringPreferencesKey(key)
        return context.dataStore.data.map { preferences -> preferences[dataStoreKey] }
    }

}