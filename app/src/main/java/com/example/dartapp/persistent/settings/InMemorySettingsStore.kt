package com.example.dartapp.persistent.settings

class InMemorySettingsStore : SettingsStoreBase() {

    private val map = HashMap<String, String>()

    override suspend fun readString(key: String): String? {
        return map[key]
    }

    override suspend fun writeString(key: String, value: String) {
        map[key] = value
    }
}