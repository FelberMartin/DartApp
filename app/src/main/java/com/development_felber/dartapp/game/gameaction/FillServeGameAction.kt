package com.development_felber.dartapp.game.gameaction

import com.development_felber.dartapp.game.Leg

class FillServeGameAction(private val remaining: Int) : GameActionBase() {

    override fun apply(leg: Leg) {
        repeat(remaining) {
            leg.dartsEntered.add(0)
        }
    }

    override fun undo(leg: Leg) {
        repeat(remaining) {
            leg.dartsEntered.removeLast()
        }
    }
}