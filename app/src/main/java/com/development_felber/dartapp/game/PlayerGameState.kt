package com.development_felber.dartapp.game

import com.development_felber.dartapp.ui.screens.game.dialog.multi.GameOverallStatistics

data class PlayerGameState(
    val playerRole: PlayerRole,
    var legsWonInCurrentSetCount: Int = 0,
    var setsWonCount: Int = 0,
    var currentLeg: Leg = Leg(),
    val previousLegsPerSet: MutableList<MutableList<Leg>> = mutableListOf(mutableListOf()),
) {

    val overallStatistics: GameOverallStatistics
        get() = GameOverallStatistics.fromLegs(previousLegsPerSet.flatten())

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
        if (currentLeg.isOver && currentLeg.doubleAttempts == 0) {
            // This may happen if the double attempts get not added by the player, because
            // it is disabled in the settings.
            currentLeg.doubleAttemptsList.add(1)
        }
        previousLegsPerSet.last().add(currentLeg)
    }
}