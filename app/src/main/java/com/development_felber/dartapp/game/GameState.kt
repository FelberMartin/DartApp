package com.development_felber.dartapp.game

import com.development_felber.dartapp.data.persistent.database.leg.Leg

class GameState(
    val setup: GameSetup,
) {
    var currentPlayerRole: PlayerRole = PlayerRole.One
    var playerGameStates: List<PlayerGameState> = emptyList()
    var gameStatus = GameStatus.LegInProgress
}

data class PlayerGameState(
    val legsWonCount: Int,
    val setsWonCount: Int,
    val servesThisLeg: List<Int>,
    val previousLegs: List<Leg>
)

sealed class GameStatus {
    open class InProgress : GameStatus()
    object LegInProgress : InProgress()
    object LegJustFinished : InProgress()
    object SetJustFinished : InProgress()

    object Finished : GameStatus()
}

