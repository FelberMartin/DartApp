package com.development_felber.dartapp.game.gameaction

import com.development_felber.dartapp.game.Leg

abstract class GameActionBase {

    abstract fun apply(leg: Leg)
    abstract fun undo(leg: Leg)
}