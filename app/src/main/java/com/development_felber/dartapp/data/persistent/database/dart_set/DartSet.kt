package com.development_felber.dartapp.data.persistent.database.dart_set

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.development_felber.dartapp.data.persistent.database.Converters

@Entity(tableName = "dart_sets_table")
data class DartSet(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @Ignore
    var legIds: List<Long> = listOf(),

    @ColumnInfo(name = "leg_ids")
    var _legIds: String = Converters.fromListOfLongs(legIds),

    @ColumnInfo(name = "legs_won_player1")
    var legsWonPlayer1: Int = 0,

    @ColumnInfo(name = "legs_won_player2")
    var legsWonPlayer2: Int = 0,
)
