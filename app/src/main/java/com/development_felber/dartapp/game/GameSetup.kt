package com.development_felber.dartapp.game

import androidx.compose.runtime.internal.illegalDecoyCallException
import com.development_felber.dartapp.data.persistent.database.player.Player
import com.development_felber.dartapp.ui.screens.game.PlayerScore

sealed class GameSetup {

    abstract val availablePlayerRoles: List<PlayerRole>

    abstract fun getPlayerName(role: PlayerRole) : String?
    abstract fun createPlayerScore(setsWon: Int, legsWonInCurrentSet: Int): PlayerScore


    object Solo : GameSetup() {
        override val availablePlayerRoles = listOf(PlayerRole.One)

        override fun getPlayerName(role: PlayerRole): String? {
            return null
        }

        override fun createPlayerScore(setsWon: Int, legsWonInCurrentSet: Int): PlayerScore {
            return PlayerScore(
                setsWon = setsWon,
                setsToWin = 1,
                legsWon = legsWonInCurrentSet,
                legsToWin = 1,
            )
        }
    }

    class Multiplayer(
        val player1: Player,
        val player2: Player,
        val legsToWinSet: Int,
        val setsToWin: Int,
    ) : GameSetup() {
        override val availablePlayerRoles = listOf(PlayerRole.One, PlayerRole.Two)

        override fun getPlayerName(role: PlayerRole): String? {
            return if (role == PlayerRole.One) {
                player1.name
            } else if (role == PlayerRole.Two) {
                player2.name
            } else {
                "Unkown player name"
            }
        }

        override fun createPlayerScore(setsWon: Int, legsWonInCurrentSet: Int): PlayerScore {
            return PlayerScore(
                legsWon = legsWonInCurrentSet,
                legsToWin = legsToWinSet,
                setsWon = setsWon,
                setsToWin = setsToWin
            )
        }
    }
}