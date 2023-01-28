package com.development_felber.dartapp.game

import android.util.Log
import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLeg
import com.development_felber.dartapp.game.gameaction.FillServeGameAction
import com.development_felber.dartapp.game.gameaction.GameActionBase
import java.util.*

class GameState(
    val setup: GameSetup,
) {

    private val gameActions = Stack<Pair<GameActionBase, Leg>>()
    val undoAvailable: Boolean
        get() = gameActions.isNotEmpty()

    val availablePlayerRoles: List<PlayerRole> = setup.availablePlayerRoles
    val playerGameStates: List<PlayerGameState> = availablePlayerRoles.map { PlayerGameState(it) }

    var currentPlayerRole: PlayerRole = availablePlayerRoles.first()
    val currentPlayerGameState
        get() = playerGameStates.first { it.playerRole == currentPlayerRole }
    val currentLeg: Leg
        get() = currentPlayerGameState.currentLeg

    var gameStatus: GameStatus = GameStatus.LegInProgress

    private fun updateCurrentPlayerRole() {
        val legCount = currentPlayerGameState.previousLegsPerSet.last().size
        val playerCount = availablePlayerRoles.size
        val sortedByPlayerRole = playerGameStates.sortedBy {
            // Rotate the starting player with each Leg.
            (it.playerRole.ordinal + legCount) % playerCount
        }
        val entryWithLeastDartsThrown = sortedByPlayerRole.minBy {
            val serveCount = it.currentLeg.dartCount / 3
            serveCount
        }
        currentPlayerRole = entryWithLeastDartsThrown.playerRole
    }

    fun completeDartsToFullServe() {
        val started = currentLeg.dartCount % 3
        if (started == 0) {
            return
        }
        applyAction(FillServeGameAction(3 - started))
    }

    fun applyAction(
        action: GameActionBase,
        executeBeforeUpdate: () -> Unit = {},
    ) {
        gameActions.push(Pair(action, currentLeg))
        action.apply(currentLeg)
        if (currentLeg.isOver) {
            gameActions.clear()     // Only allow undo during legs.
        }

        gameStatus = currentPlayerGameState.updateAndGetGameStatus(setup)
        executeBeforeUpdate()
        resetIfNecessary()
        updateCurrentPlayerRole()
    }

    private fun resetIfNecessary() {
        when(gameStatus) {
            GameStatus.LegInProgress -> { /* Do nothing */ }
            is GameStatus.LegJustFinished -> {
                playerGameStates.forEach {
                    it.resetCurrentLeg()
                }
            }
            is GameStatus.SetJustFinished -> {
                playerGameStates.forEach {
                    it.resetCurrentSet()
                }
            }
            GameStatus.Finished -> {
                playerGameStates.forEach {
                    it.finishCurrentLeg()
                }
            }
        }
    }

    fun undo() {
        if (undoAvailable) {
            val (action, leg) = gameActions.pop()
            action.undo(leg)
        }
        updateCurrentPlayerRole()
    }
}

data class PlayerGameState(
    val playerRole: PlayerRole,
    var legsWonInCurrentSetCount: Int = 0,
    var setsWonCount: Int = 0,
    var currentLeg: Leg = Leg(),
    val previousLegsPerSet: MutableList<MutableList<Leg>> = mutableListOf(mutableListOf()),
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

        var status: GameStatus = GameStatus.LegJustFinished(playerRole)
        legsWonInCurrentSetCount++

        if (legsWonInCurrentSetCount >= gameSetup.legsToWinSet) {
            status = GameStatus.SetJustFinished(playerRole)
            setsWonCount++
        }
        if (setsWonCount >= gameSetup.setsToWin) {
            status = GameStatus.Finished
        }

        return status
    }

    fun resetCurrentLeg() {
        finishCurrentLeg()
        currentLeg = Leg()
    }

    fun resetCurrentSet() {
        resetCurrentLeg()
        previousLegsPerSet.add(mutableListOf())
        legsWonInCurrentSetCount = 0
    }

    fun finishCurrentLeg() {
        previousLegsPerSet.last().add(currentLeg)
    }

}

sealed class GameStatus {
    sealed class InProgress : GameStatus()
    object LegInProgress : InProgress()
    class LegJustFinished(val winningPlayer: PlayerRole) : InProgress()
    class SetJustFinished(val winningPlayer: PlayerRole) : InProgress()

    object Finished : GameStatus()
}

