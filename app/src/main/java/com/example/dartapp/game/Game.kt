package com.example.dartapp.game

import com.example.dartapp.database.Converters
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

    var doubleAttemptsList = ArrayList<Int>()
    var doubleAttempts: Int = 0
        get() = doubleAttemptsList.sum()

    var unusedDartCount = 0

    var dartCount = 0
        get() = serves.size * 3 - unusedDartCount

    private val startTimeMilli = System.currentTimeMillis()


    fun isFinished() : Boolean {
        return mode.isGameFinished(this)
    }

    fun askForDoubleAttempts() : Boolean {
        return mode.askForDoubleAttempts(this)
    }

    fun isServeValid(serve: Int) : Boolean {
        return mode.isServeValid(serve, this)
    }

    fun undo() {
        serves.removeLastOrNull()
        doubleAttemptsList.removeLastOrNull()
    }


    fun toLeg() : Leg {
        val time = System.currentTimeMillis()
        return Leg(
            endTime = time,
            durationMilli = time - startTimeMilli,
            gameMode = mode.id.value,
            dartCount = dartCount,
            servesAvg = avg,
            doubleAttempts = doubleAttempts,
            servesList = Converters.fromArrayListOfInts(serves),
            doubleAttemptsList = Converters.fromArrayListOfInts(doubleAttemptsList),
            checkout = serves.last()
        )
    }



}