package com.example.dartapp.game.gameModes

import com.example.dartapp.game.Game

abstract class GameMode {

    abstract fun initGame(): Game

    abstract fun isServeValid(serve: Int, game: Game): Boolean

    abstract fun isGameOver(game: Game): Boolean
}