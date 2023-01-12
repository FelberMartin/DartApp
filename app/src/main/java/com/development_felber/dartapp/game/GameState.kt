package com.development_felber.dartapp.game

import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLeg
import com.development_felber.dartapp.game.gameaction.GameActionBase
import com.development_felber.dartapp.ui.screens.game.PlayerScore
import java.util.*

class GameState(
    private val setup: GameSetup,
) {
    private val gameActions = Stack<GameActionBase>()
    val undoAvailable: Boolean
        get() = gameActions.isNotEmpty()

    val availablePlayerRoles: List<PlayerRole> = setup.availablePlayerRoles
    val playerGameStatesByPlayerRole: Map<PlayerRole, PlayerGameState> = availablePlayerRoles.associateWith {
        PlayerGameState()
    }
    val currentPlayerGameState = playerGameStatesByPlayerRole[getCurrentPlayerRole()]!!
    var gameStatus: GameStatus = GameStatus.LegInProgress

    val currentLeg: Leg = currentPlayerGameState.currentLeg


    fun getCurrentPlayerRole() : PlayerRole {
        val sortedByPlayerRole = playerGameStatesByPlayerRole.entries.sortedBy {
            it.key.ordinal  // Prefer player 1 over player 2
        }
        val entryWithLeastDartsThrown = sortedByPlayerRole.minBy {
            val playerGameState = it.value
            playerGameState.currentLeg.dartCount
        }
        return entryWithLeastDartsThrown.key
    }


    fun applyAction(action: GameActionBase) {
        gameActions.push(action)
        val leg = currentPlayerGameState.currentLeg
        action.apply(leg)
        if (leg.isOver) {
            gameActions.clear()     // Only allow undo during legs.
            updateGameStatus()
        }
    }

    private fun updateGameStatus() {
        gameStatus = currentPlayerGameState.updateAndGetGameStatus(setup)
        when(gameStatus) {
            GameStatus.LegInProgress -> { /* Do nothing */ }
            GameStatus.LegJustFinished -> {
                playerGameStatesByPlayerRole.values.forEach {
                    it.resetCurrentLeg()
                }
            }
            GameStatus.SetJustFinished -> {
                playerGameStatesByPlayerRole.values.forEach {
                    it.resetCurrentSet()
                }
            }
            GameStatus.Finished -> {
                // Don't know yet what to do here.
            }
        }
    }

    fun undo() {
        if (undoAvailable) {
            gameActions.pop().undo(currentPlayerGameState.currentLeg)
        }
    }
}

data class PlayerGameState(
    var legsWonInCurrentSetCount: Int = 0,
    var setsWonCount: Int = 0,
    var currentLeg: Leg = Leg(),
    val previousLegs: MutableList<FinishedLeg> = mutableListOf(),
) {
    fun updateAndGetGameStatus(gameSetup: GameSetup) : GameStatus {
        return when(gameSetup) {
            is GameSetup.Solo -> updateForSoloAndGetStatus(gameSetup)
            is GameSetup.Multiplayer -> updateForMultiplayerAndGetStatus(gameSetup)
        }
    }

    private fun updateForSoloAndGetStatus(gameSetup: GameSetup.Solo) : GameStatus {
        if (currentLeg.isOver) {
            return GameStatus.Finished
        }
        return GameStatus.LegInProgress
    }

    private fun updateForMultiplayerAndGetStatus(gameSetup: GameSetup.Multiplayer) : GameStatus {
        if (!currentLeg.isOver) {
            return GameStatus.LegInProgress
        }

        var status: GameStatus = GameStatus.LegJustFinished
        legsWonInCurrentSetCount++

        if (legsWonInCurrentSetCount >= gameSetup.legsToWinSet) {
            status = GameStatus.SetJustFinished
            setsWonCount++
        }
        if (setsWonCount >= gameSetup.setsToWin) {
            status = GameStatus.Finished
        }

        return status
    }

    fun resetCurrentLeg() {
        previousLegs.add(currentLeg.toFinishedLeg())
        currentLeg = Leg()
    }

    fun resetCurrentSet() {
        legsWonInCurrentSetCount = 0
    }
}

sealed class GameStatus {
    sealed class InProgress : GameStatus()
    object LegInProgress : InProgress()
    object LegJustFinished : InProgress()
    object SetJustFinished : InProgress()

    object Finished : GameStatus()
}

