package com.example.dartapp.data.persistent.keyvalue

import kotlinx.coroutines.flow.Flow

interface IKeyValueStorage {

    suspend fun put(key: String, value: String)
    fun getFlow(key: String): Flow<String?>
}