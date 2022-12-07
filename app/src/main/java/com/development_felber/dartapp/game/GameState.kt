package com.development_felber.dartapp.game

import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLeg
import com.development_felber.dartapp.game.gameaction.GameActionBase
import java.util.*

class GameState(
    val setup: GameSetup,
) {
    private val gameActions = Stack<GameActionBase>()
    val undoAvailable: Boolean
        get() = gameActions.isNotEmpty()

    val availablePlayerRoles: List<PlayerRole> = setup.availablePlayerRoles
    val playerGameStates: Map<PlayerRole, PlayerGameState> = availablePlayerRoles.associateWith {
        PlayerGameState()
    }
    val currentPlayerGameState = playerGameStates[getCurrentPlayerRole()]!!
    var gameStatus = GameStatus.LegInProgress


    private fun getCurrentPlayerRole() : PlayerRole {
        val sortedByPlayerRole = playerGameStates.entries.sortedBy {
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
        }
    }

    fun undo() {
        if (undoAvailable) {
            gameActions.pop().undo(currentPlayerGameState.currentLeg)
        }
    }
}

data class PlayerGameState(
    val legsWonCount: Int = 0,
    val setsWonCount: Int = 0,
    val currentLeg: Leg = Leg(),
    val previousLegs: List<FinishedLeg> = emptyList(),
)

sealed class GameStatus {
    open class InProgress : GameStatus()
    object LegInProgress : InProgress()
    object LegJustFinished : InProgress()
    object SetJustFinished : InProgress()

    object Finished : GameStatus()
}

