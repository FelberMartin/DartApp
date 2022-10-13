package com.development_felber.dartapp.game

import com.development_felber.dartapp.data.persistent.database.Converters
import com.development_felber.dartapp.data.persistent.database.Leg
import com.development_felber.dartapp.game.gameaction.FillServeGameAction
import com.development_felber.dartapp.game.gameaction.GameActionBase
import com.development_felber.dartapp.util.CheckoutTip
import com.development_felber.dartapp.util.GameUtil
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
    val doubleAttempts: Int
        get() = doubleAttemptsList.sum()

    var unusedDartCount = 0

    val dartCount
        get() = dartsEntered.size - unusedDartCount


    fun applyAction(action: GameActionBase) {
        gameActions.push(action)
        action.apply(this)
    }

    fun undo() {
        if (gameActions.isNotEmpty()) {
            gameActions.pop().undo(this)
        }
    }

    fun getAverage(perDart: Boolean = false) : Double?  {
        if (dartsEntered.isEmpty()) {
            return null
        }

        if (perDart) {
            return dartsEntered.average()
        }

        return getServes().sum().toDouble() / (dartCount.toDouble() / 3.0)
    }

    fun getLast(perDart: Boolean = false) : Int? {
        if (dartsEntered.isEmpty()) {
            return null
        }

        if (perDart) {
            dartsEntered.last()
        }
        return getServes().last()
    }

    fun completeDartsToFullServe() {
        val started = dartCount % 3
        if (started == 0) {
            return
        }
        applyAction(FillServeGameAction(3 - started))
    }

    fun isNumberValid(number: Int, singleDart: Boolean, doubleModifierEnabled: Boolean = false) : Boolean {
        val pointsAfter = pointsLeft - number
        if (pointsAfter < 0 || pointsAfter == 1) {
            return false
        }
        if (singleDart) {
            if (pointsAfter == 0) {
                return doubleModifierEnabled
            }
            return true
        } else {
            return isServeValid(number)
        }
    }

    fun pointsLeftBeforeLastServe(): Int {
        if (dartsEntered.isEmpty()) {
            return 501
        }
        val lastServePoints = dartsEntered.subList(dartsEntered.size - 3, dartsEntered.size).sum()
        return pointsLeft + lastServePoints
    }

    private fun isServeValid(serve: Int) : Boolean {
        if (serve > 180) {
            return false
        }
        if (GameUtil.INVALID_SERVES.contains(serve)) {
            return false
        }
        val pointsAfter = pointsLeft - serve
        if (pointsAfter == 0) {
            return CheckoutTip.checkoutTips.contains(serve)
        }
        return true
    }

    private fun getServes() : List<Int> {
        val serves = ArrayList<Int>()
        val serveCount = Math.ceil(dartsEntered.size / 3.0).toInt()
        for (i in 0 until serveCount) {
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
            durationSeconds = Converters.fromDuration(Duration.between(startDateTime, now)),
            dartCount = dartCount,
            average = getAverage()!!,
            doubleAttempts = doubleAttempts,
            servesList = Converters.fromListOfInts(serves),
            doubleAttemptsList = Converters.fromListOfInts(doubleAttemptsList),
            checkout = serves.last()
        )
    }

}