package com.development_felber.dartapp.game.gameaction

import com.development_felber.dartapp.game.Game

class FillServeGameAction(private val remaining: Int) : GameActionBase() {

    override fun apply(game: Game) {
        repeat(remaining) {
            game.dartsEntered.add(0)
        }
    }

    override fun undo(game: Game) {
        repeat(remaining) {
            game.dartsEntered.removeLast()
        }
    }
}