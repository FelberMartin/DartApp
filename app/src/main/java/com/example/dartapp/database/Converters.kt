package com.example.dartapp.database

import androidx.room.TypeConverter

object Converters {
    @TypeConverter
    fun fromArrayListOfInts(list: ArrayList<Int>?): String {
        return list?.joinToString(separator = ",") { it.toString() } ?: ""
    }

    @TypeConverter
    fun toArrayListOfInts(string: String?): ArrayList<Int> {
        return ArrayList(string?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList())
    }
}