package com.development_felber.dartapp.data.persistent.database


import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.development_felber.dartapp.data.persistent.database.dart_set.DartSet
import com.development_felber.dartapp.data.persistent.database.dart_set.DartSetDao
import com.development_felber.dartapp.data.persistent.database.leg.Leg
import com.development_felber.dartapp.data.persistent.database.leg.LegDao
import com.development_felber.dartapp.data.persistent.database.multiplayer_game.MultiplayerGame
import com.development_felber.dartapp.data.persistent.database.multiplayer_game.MultiplayerGameDao
import com.development_felber.dartapp.data.persistent.database.player.Player
import com.development_felber.dartapp.data.persistent.database.player.PlayerDao

@Database(
    entities = [Leg::class, MultiplayerGame::class, DartSet::class, Player::class],
    version = 7,
    // Version 6: Initial app launch version (up to 1.1)
    exportSchema = true,
    autoMigrations = [
        AutoMigration (from = 6, to = 7)
    ]
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getLegDao(): LegDao

    abstract fun getMultiplayerGameDao(): MultiplayerGameDao

    abstract fun getDartSetDao(): DartSetDao

    abstract fun getPlayerDao(): PlayerDao
}