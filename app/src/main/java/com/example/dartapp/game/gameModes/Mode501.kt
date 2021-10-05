package com.example.dartapp.game.gameModes

import com.example.dartapp.game.Game

class Mode501 : GameMode() {

    // These are all the serves which cannot be achived in a normal 501 game
    private val generalInvalidServes: List<Int> = listOf(163, 166, 169, 172, 173, 175, 176, 178, 179)

    override val startPoints: Int = 501
    override val id: ID = ID.X01

    override fun isServeValid(serve: Int, game: Game): Boolean {
        if (serve < 0 || serve > 180) return false
        if (generalInvalidServes.contains(serve)) return false
        if (game.pointsLeft - serve < 0) return false



        return true
    }


}