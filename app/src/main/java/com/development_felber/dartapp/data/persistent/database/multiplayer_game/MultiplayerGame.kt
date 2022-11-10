package com.development_felber.dartapp.data.persistent.database.multiplayer_game

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.development_felber.dartapp.data.persistent.database.Converters

@Entity(tableName = "multiplayer_games_table")
data class MultiplayerGame(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "player1_name")
    var player1Name: String = "",

    @ColumnInfo(name = "player2_name")
    var player2Name: String = "",

    var setIds: List<Long> = listOf(),
    @ColumnInfo(name = "set_ids")
    private var _setIds: String = Converters.fromListOfLongs(setIds),

    @ColumnInfo(name = "sets_won_player1")
    var setsWonPlayer1: Int = 0,

    @ColumnInfo(name = "sets_won_player2")
    var setsWonPlayer2: Int = 0,

    @ColumnInfo(name = "end_time")
    var endTime: String = "",
) {
}