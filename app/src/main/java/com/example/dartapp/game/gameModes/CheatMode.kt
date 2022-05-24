package com.example.dartapp.game.gameModes

import com.example.dartapp.game.Game

class CheatMode : GameMode() {

    override val startPoints: Int = 1
    override val type: Type = Type.CHEAT

    override fun isServeValid(serve: Int, game: Game): Boolean {
        return true
    }

    override fun isGameFinished(game: Game): Boolean {
        return game.pointsLeft <= 0
    }
}