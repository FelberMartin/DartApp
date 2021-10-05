package com.example.dartapp.game.gameModes

import com.example.dartapp.game.Game

abstract class GameMode {

    abstract val startPoints: Int
    abstract val id: ID

    abstract fun isServeValid(serve: Int, game: Game): Boolean

    open fun isGameFinished(game: Game): Boolean {
        return game.pointsLeft == 0
    }


    enum class ID(val value: Int) {
        X01(1), CHEAT(69)
    }
}