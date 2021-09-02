package com.example.dartapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "legs_table")
data class Leg(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "end_time_milli")
    var endTime: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "serves_count")
    var servesCount: Int = 0,

//    @ColumnInfo(name="serves")
//    var serves: List<Int> = List(servesCount)


)



