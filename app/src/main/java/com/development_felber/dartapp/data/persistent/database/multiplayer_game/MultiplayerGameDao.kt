package com.development_felber.dartapp.data.persistent.database.multiplayer_game

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MultiplayerGameDao {

    @Insert
    suspend fun insert(multiplayerGame: MultiplayerGame)

    @Query("SELECT * FROM multiplayer_games_table WHERE id = :id")
    suspend fun get(id: Long): MultiplayerGame?

    @Query("SELECT * FROM multiplayer_games_table ORDER BY end_time")
    fun getAll(): List<MultiplayerGame>
}