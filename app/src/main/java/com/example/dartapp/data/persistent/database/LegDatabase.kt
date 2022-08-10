package com.example.dartapp.data.persistent.database


import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Leg::class],
    version = 6
)
abstract class LegDatabase: RoomDatabase() {

    abstract fun legDatabaseDao(): LegDatabaseDao
}