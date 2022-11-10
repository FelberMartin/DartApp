package com.development_felber.dartapp.data.persistent.database


import androidx.room.Database
import androidx.room.RoomDatabase
import com.development_felber.dartapp.data.persistent.database.dart_set.DartSetDao
import com.development_felber.dartapp.data.persistent.database.leg.Leg
import com.development_felber.dartapp.data.persistent.database.leg.LegDao
import com.development_felber.dartapp.data.persistent.database.multiplayer_game.MultiplayerGameDao

@Database(
    entities = [Leg::class],
    version = 7,
    // Version 6: Initial app launch version (up to 1.1)
    exportSchema = true
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getLegDao(): LegDao

    abstract fun getMultiplayerGameDao(): MultiplayerGameDao

    abstract fun getDartSetDao(): DartSetDao
}