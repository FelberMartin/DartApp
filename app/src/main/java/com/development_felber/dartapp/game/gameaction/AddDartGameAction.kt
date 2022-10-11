package com.development_felber.dartapp.game.gameaction

import com.development_felber.dartapp.game.Game

class AddDartGameAction(val dart: Int) : GameActionBase() {

    override fun apply(game: Game) {
        game.dartsEntered.add(dart)
    }

    override fun undo(game: Game) {
        game.dartsEntered.removeLast()
    }
}