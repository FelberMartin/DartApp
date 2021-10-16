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

    fun processServe(serve: Int): Boolean {
        if (!game.isServeValid(serve)) {
            return false
        }

        game.serves.add(serve)
        update()
        return true
    }

    fun isFinished(): Boolean {
        return game.isFinished()
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
        dartCount.value = game.serves.size * 3

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