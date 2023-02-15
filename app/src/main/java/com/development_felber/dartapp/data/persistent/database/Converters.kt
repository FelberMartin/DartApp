package com.development_felber.dartapp.data.persistent.database

import androidx.room.TypeConverter
import java.time.Duration
import java.time.LocalDateTime

object Converters {
    @TypeConverter
    fun fromListOfInts(list: List<Int>?): String {
        return list?.joinToString(separator = ",") { it.toString() } ?: ""
    }

    @TypeConverter
    fun toListOfInts(string: String?): List<Int> {
        return ArrayList(string?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList())
    }

    @TypeConverter
    fun fromListOfLongs(list: List<Long>?): String {
        return list?.joinToString(separator = ",") { it.toString() } ?: ""
    }

    @TypeConverter
    fun toListOfLongs(string: String?): List<Long> {
        return ArrayList(string?.split(",")?.mapNotNull { it.toLongOrNull() } ?: emptyList())
    }

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime): String {
        return dateTime.toString()
    }

    @TypeConverter
    fun toLocalDateTime(string: String): LocalDateTime {
        return LocalDateTime.parse(string)
    }

    @TypeConverter
    fun fromDuration(duration: Duration): Long {
        return duration.seconds
    }

    @TypeConverter
    fun toDuration(seconds: Long): Duration {
        return Duration.ofSeconds(seconds)
    }
}