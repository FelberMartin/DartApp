package com.example.dartapp.persistent.settings

abstract class SettingsStoreBase {

    suspend fun <T: Any> read(key: SettingKey<T>): T {
        val string = readString(key.key) ?: return key.defaultValue
        return when (string::class) {
            Int::class -> string.toInt()
            Long::class -> string.toLong()
            Float::class -> string.toFloat()
            Double::class -> string.toDouble()
            Boolean::class -> string.toBoolean()
            else -> string
        } as T
    }

    suspend fun <T: Any> write(key: SettingKey<T>, value: T) {
        writeString(key.key, value.toString())
    }

    protected abstract suspend fun readString(key: String): String?
    protected abstract suspend fun writeString(key: String, value: String)

}