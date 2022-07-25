package com.example.dartapp.data.persistent.keyvalue

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow


class InMemoryKeyValueStorage : IKeyValueStorage {

    private val map = HashMap<String, MutableStateFlow<String?>>()

    override suspend fun put(key: String, value: String) {
        if (map.containsKey(key)) {
            map[key]!!.emit(value)
        } else {
            map[key] = MutableStateFlow(value)
        }
    }

    override fun getFlow(key: String): Flow<String?> {
        if (!map.containsKey(key)) {
            map[key] = MutableStateFlow(null)
        }
        return map[key]!!
    }
}