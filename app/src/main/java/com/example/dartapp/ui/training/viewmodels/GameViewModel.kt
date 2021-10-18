package com.example.dartapp.ui.training.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dartapp.database.LegDatabase
import com.example.dartapp.game.gameModes.GameMode
import com.example.dartapp.game.Game
import com.example.dartapp.util.App

class GameViewModel(private val mode: GameMode) : ViewModel() {

    private val NO_DATA = "--"

    private var game: Game = Game(mode)
    var pointsLeft: MutableLiveData<Int> = MutableLiveData(game.pointsLeft)
    var last: MutableLiveData<String> = MutableLiveData(NO_DATA)
    var avg: MutableLiveData<String> = MutableLiveData(NO_DATA)
    var dartCount: MutableLiveData<Int> = MutableLiveData(0)


    fun isServeValid(serve: Int) : Boolean {
        return game.isServeValid(serve)
    }

    fun processServe(serve: Int, dartCount: Int = 3) {
        // Normally zero unused darts, only used for checkout
        val unused = 3 - dartCount
        game.unusedDartCount += unused

        game.serves.add(serve)
        update()
    }

    fun wouldBeFinished(serve: Int) : Boolean {
        return game.pointsLeft - serve == 0
    }

    fun isFinished(): Boolean {
        return game.isFinished()
    }

    fun askForDoubleAttempts(): Boolean {
        return game.askForDoubleAttempts()
    }

    fun addDoubleAttempts(count: Int) {
        game.doubleAttemptsList.add(count)
    }

    fun undo() {
        game.undo()
        update()
    }

    private fun update() {
        // Points Left
        pointsLeft.value = game.pointsLeft

        // Last Serve
        if (game.lastServe == -1)
            last.value = NO_DATA
        else
            last.value = game.lastServe.toString()

        // Average Points per Serve
        if (game.lastServe == -1)
            avg.value = NO_DATA
        else
            avg.value = String.format("%.1f" ,game.avg)

        // Dart Count
        dartCount.value = game.dartCount
    }

    fun restart() {
        game = Game(mode)
        update()
    }

    fun saveGameToDatabase() {
        val db = LegDatabase.getInstance(App.instance.applicationContext)
        val legDao = db.legDatabaseDao
        legDao.insert(game.toLeg())
        Log.d("LegDatabase", "Leg stored")
    }


}