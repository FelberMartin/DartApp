package com.development_felber.dartapp.game.gameaction

import com.development_felber.dartapp.game.Leg

class AddDartGameAction(val dart: Int) : GameActionBase() {

    override fun apply(leg: Leg) {
        leg.dartsEntered.add(dart)
    }

    override fun undo(leg: Leg) {
        leg.dartsEntered.removeLast()
    }
}