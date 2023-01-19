package com.development_felber.dartapp.game

import android.util.Log
import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLeg
import com.development_felber.dartapp.game.gameaction.FillServeGameAction
import com.development_felber.dartapp.game.gameaction.GameActionBase
import java.util.*

class GameState(
    private val setup: GameSetup,
) {
    private val gameActions = Stack<Pair<GameActionBase, Leg>>()
    val undoAvailable: Boolean
        get() = gameActions.isNotEmpty()

    val availablePlayerRoles: List<PlayerRole> = setup.availablePlayerRoles
    val playerGameStatesByPlayerRole: Map<PlayerRole, PlayerGameState> = availablePlayerRoles.associateWith {
        PlayerGameState()
    }
    val currentPlayerGameState
        get() = playerGameStatesByPlayerRole[getCurrentPlayerRole()]!!
    val currentLeg: Leg
        get() = currentPlayerGameState.currentLeg

    var gameStatus: GameStatus = GameStatus.LegInProgress


    fun getCurrentPlayerRole() : PlayerRole {
        val sortedByPlayerRole = playerGameStatesByPlayerRole.entries.sortedBy {
            it.key.ordinal  // Prefer player 1 over player 2
        }
        val entryWithLeastDartsThrown = sortedByPlayerRole.minBy {
            val playerGameState = it.value
            val serveCount = playerGameState.currentLeg.dartCount / 3
            serveCount
        }
        return entryWithLeastDartsThrown.key
    }

    fun completeDartsToFullServe() {
        val started = currentLeg.dartCount % 3
        if (started == 0) {
            return
        }
        applyAction(FillServeGameAction(3 - started))
    }

    fun applyAction(action: GameActionBase) {
        gameActions.push(Pair(action, currentLeg))
        action.apply(currentLeg)
        if (currentLeg.isOver) {
            Log.i("GameState", "Player ${getCurrentPlayerRole()} finished leg")
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
            val (action, leg) = gameActions.pop()
            action.undo(leg)
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

