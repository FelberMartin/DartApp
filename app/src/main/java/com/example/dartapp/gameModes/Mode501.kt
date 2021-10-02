package com.example.dartapp.gameModes

class Mode501 : GameMode() {

    // These are all the serves which cannot be achived in a normal 501 game
    private val generalInvalidServes: List<Int> = listOf(163, 166, 169, 172, 173, 175, 176, 178, 179)

    override fun initGameStatus(): GameStatus {
        return GameStatus(501)
    }

    override fun isServeValid(serve: Int, gameStatus: GameStatus): Boolean {
        if (serve < 0 || serve > 180) return false
        if (generalInvalidServes.contains(serve)) return false


        return true
    }

    override fun isGameOver(gameStatus: GameStatus): Boolean {
        return gameStatus.pointsLeft == 0
    }

}