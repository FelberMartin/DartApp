package com.example.dartapp.database

import androidx.room.TypeConverter

object Converters {
    @TypeConverter
    fun fromListOfInts(list: List<Int>?): String {
        return list?.joinToString(separator = ",") { it.toString() } ?: ""
    }

    @TypeConverter
    fun toListOfInts(string: String?): List<Int> {
        return ArrayList(string?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList())
    }
}