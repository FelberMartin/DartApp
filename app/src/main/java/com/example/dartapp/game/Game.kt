package com.example.dartapp.game

import com.example.dartapp.data.persistent.database.Converters
import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.game.gameModes.GameMode
import java.time.Duration
import java.time.LocalDateTime

class Game (private val mode: GameMode) {

    private val startPoints = mode.startPoints

    var serves: ArrayList<Int> = ArrayList()

    val pointsLeft: Int
        get() = startPoints - serves.sum()

    val lastServe: Int
        get() {
            if (serves.isEmpty()) return -1
            return serves.last()
        }

    val avg: Double
        get() = serves.average()

    var doubleAttemptsList = ArrayList<Int>()
    private val doubleAttempts: Int
        get() = doubleAttemptsList.sum()

    var unusedDartCount = 0

    val dartCount
        get() = serves.size * 3 - unusedDartCount

    private val startDateTime = LocalDateTime.now()


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
        val now = LocalDateTime.now()
        return Leg(
            endTime = Converters.fromLocalDateTime(now),
            duration = Converters.fromDuration(Duration.between(startDateTime, now)),
            gameMode = mode.type.id,
            dartCount = dartCount,
            servesAvg = avg,
            doubleAttempts = doubleAttempts,
            servesList = Converters.fromListOfInts(serves),
            doubleAttemptsList = Converters.fromListOfInts(doubleAttemptsList),
            checkout = serves.last()
        )
    }



}