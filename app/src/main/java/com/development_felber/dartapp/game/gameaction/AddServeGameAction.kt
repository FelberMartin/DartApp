package com.development_felber.dartapp.game.gameaction

import com.development_felber.dartapp.game.Game

class AddServeGameAction(val serve: Int) : GameActionBase() {

    override fun apply(game: Game) {
        game.dartsEntered.addAll(listOf(0, 0, serve))
    }

    override fun undo(game: Game) {
        repeat(3)  {
            game.dartsEntered.removeLast()
        }
    }
}