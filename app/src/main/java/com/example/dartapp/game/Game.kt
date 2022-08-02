package com.example.dartapp.game

import com.example.dartapp.data.persistent.database.Converters
import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.game.gameaction.GameActionBase
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

class Game() {

    private val startDateTime = LocalDateTime.now()
    private val startPoints = 501

    var dartsEntered: ArrayList<Int> = ArrayList()
    private val gameActions = Stack<GameActionBase>()


    val pointsLeft: Int
        get() = startPoints - dartsEntered.sum()

    var doubleAttemptsList = ArrayList<Int>()
    private val doubleAttempts: Int
        get() = doubleAttemptsList.sum()

    var unusedDartCount = 0

    val dartCount
        get() = dartsEntered.size


    fun applyAction(action: GameActionBase) {
        gameActions.push(action)
        action.apply(this)
    }

    fun undo() {
        if (gameActions.isNotEmpty()) {
            gameActions.pop().undo(this)
        }
    }

    private fun getAverage(perDart: Boolean = false) : Double  {
        if (perDart) {
            return dartsEntered.average()
        }
        return getServes().average()
    }

    private fun getLast(perDart: Boolean = false) : Int {
        if (dartsEntered.isEmpty()) {
            return NO_LAST_VALUE
        }

        if (perDart) {
            dartsEntered.last()
        }
        return getServes().last()
    }

    private fun getServes() : List<Int> {
        val serves = ArrayList<Int>()
        val serveCount = Math.ceil(dartsEntered.size / 3.0).toInt()
        for (i in 0..serveCount) {
            var sum = 0
            for (j in 0..2) {
                val index = i * 3 + j
                if (index < dartsEntered.size) {
                    sum += dartsEntered[index]
                }
            }
            serves.add(sum)
        }
        return serves
    }

    fun toLeg() : Leg {
        val now = LocalDateTime.now()
        val serves = getServes()
        return Leg(
            endTime = Converters.fromLocalDateTime(now),
            duration = Converters.fromDuration(Duration.between(startDateTime, now)),
            dartCount = dartCount,
            servesAvg = getAverage(),
            doubleAttempts = doubleAttempts,
            servesList = Converters.fromListOfInts(serves),
            doubleAttemptsList = Converters.fromListOfInts(doubleAttemptsList),
            checkout = serves.last()
        )
    }

    companion object {
        const val NO_LAST_VALUE = -1
    }

}