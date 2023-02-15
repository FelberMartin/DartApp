package com.development_felber.dartapp.game.gameaction

import com.development_felber.dartapp.game.Leg

class AddServeGameAction(val serve: Int) : GameActionBase() {

    override fun apply(leg: Leg) {
        leg.dartsEntered.addAll(listOf(0, 0, serve))
    }

    override fun undo(leg: Leg) {
        repeat(3)  {
            leg.dartsEntered.removeLast()
        }
    }
}