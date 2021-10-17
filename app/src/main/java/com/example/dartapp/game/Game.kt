package com.example.dartapp.game

import com.example.dartapp.database.Leg
import com.example.dartapp.game.gameModes.GameMode

class Game (private val mode: GameMode) {

    private val startPoints = mode.startPoints

    var serves: ArrayList<Int> = ArrayList()

    var pointsLeft: Int = startPoints
        get() = startPoints - serves.sum()

    var lastServe: Int = -1
        get() {
            if (serves.isEmpty()) return -1
            return serves.last()
        }

    var avg: Double = 0.0
        get() = serves.average()

    var doubleAttempts: Int = 0
    set(value) {
        field = value
        println("Double attempts: $value")
    }



    fun isFinished() : Boolean {
        return mode.isGameFinished(this)
    }

    fun askForDoubleAttempts() : Boolean {
        return mode.askForDoubleAttempts(this)
    }

    fun isServeValid(serve: Int) : Boolean {
        return mode.isServeValid(serve, this)
    }


    fun toLeg() : Leg {
        return Leg(
            endTime = System.currentTimeMillis(),
            gameMode = mode.id.value,
            servesCount = serves.size,
            servesAvg = avg,
            doubleAttempts = doubleAttempts
        )
    }



}