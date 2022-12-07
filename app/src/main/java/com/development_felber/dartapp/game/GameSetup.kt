package com.development_felber.dartapp.game

import com.development_felber.dartapp.data.persistent.database.player.Player

sealed class GameSetup {

    abstract val availablePlayerRoles: List<PlayerRole>

    object Solo : GameSetup() {
        override val availablePlayerRoles = listOf(PlayerRole.One)
    }

    class Multiplayer(
        val player1: Player,
        val player2: Player,
        val legsToWin: Int,
        val setsToWin: Int,
    ) : GameSetup() {
        override val availablePlayerRoles = listOf(PlayerRole.One, PlayerRole.Two)
    }
}