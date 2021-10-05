package com.example.dartapp.game.gameModes

import com.example.dartapp.game.Game

class CheatMode : GameMode() {
    override fun initGame(): Game {
        return Game(1)
    }

    override fun isServeValid(serve: Int, game: Game): Boolean {
        return true
    }

    override fun isGameOver(game: Game): Boolean {
        return game.pointsLeft <= 0
    }
}