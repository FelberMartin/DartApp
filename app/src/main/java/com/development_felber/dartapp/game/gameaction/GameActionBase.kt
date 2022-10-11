package com.development_felber.dartapp.game.gameaction

import com.development_felber.dartapp.game.Game

abstract class GameActionBase {

    abstract fun apply(game: Game)
    abstract fun undo(game: Game)
}