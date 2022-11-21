package com.development_felber.dartapp.data.persistent.database.player

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players_table")
data class Player(
    @PrimaryKey
    val name: String
)
