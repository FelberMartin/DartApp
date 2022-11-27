package com.development_felber.dartapp.game

import com.development_felber.dartapp.data.persistent.database.player.Player

sealed class GameSetup {

    object Solo : GameSetup()

    class Multiplayer(
        val player1: Player,
        val player2: Player,
        val legsToWin: Int,
        val setsToWin: Int,
    ) : GameSetup()
}