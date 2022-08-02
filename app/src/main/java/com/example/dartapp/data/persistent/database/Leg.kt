package com.example.dartapp.data.persistent.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "legs_table")
data class Leg(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "end_time")
    var endTime: String = "",

    @ColumnInfo(name = "duration_seconds")
    var duration: Long = 20 * 60L,

    @ColumnInfo(name = "dart_count")
    var dartCount: Int = 0,

    @ColumnInfo(name = "serves_avg")
    var servesAvg: Double = 0.0,

    @ColumnInfo(name = "double_attempts")
    var doubleAttempts: Int = 0,

    @ColumnInfo(name = "checkout")
    var checkout: Int = 0,

    @ColumnInfo(name = "serve_list")
    var servesList: String = "",

    @ColumnInfo(name = "double_attempts_list")
    var doubleAttemptsList: String = "",


    )



