package com.example.dartapp.gameModes

abstract class GameMode {

    abstract fun initGameStatus(): GameStatus

    abstract fun isServeValid(serve: Int, gameStatus: GameStatus): Boolean

    abstract fun isGameOver(gameStatus: GameStatus): Boolean
}