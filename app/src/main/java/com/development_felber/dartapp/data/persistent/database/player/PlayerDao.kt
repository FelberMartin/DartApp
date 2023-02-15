package com.development_felber.dartapp.data.persistent.database.player

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {

    @Insert
    suspend fun insertPlayer(player: Player)

    @Query("SELECT * FROM players_table WHERE name = :name")
    suspend fun getPlayer(name: String): Player?

    @Query("SELECT * FROM players_table")
    suspend fun getPlayers(): List<Player>

    @Query("SELECT * FROM players_table")
    fun observePlayers(): Flow<List<Player>>
}