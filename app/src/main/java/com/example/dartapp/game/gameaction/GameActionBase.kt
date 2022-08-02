package com.example.dartapp.game.gameaction

import com.example.dartapp.game.Game

abstract class GameActionBase {

    abstract fun apply(game: Game)
    abstract fun undo(game: Game)
}