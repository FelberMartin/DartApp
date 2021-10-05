package com.example.dartapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dartapp.database.LegDatabase
import com.example.dartapp.game.gameModes.GameMode
import com.example.dartapp.game.Game
import com.example.dartapp.util.App

class GameViewModel(private val mode: GameMode) : ViewModel() {

    private var game: Game = Game(mode)
    var pointsLeft: MutableLiveData<Int> = MutableLiveData(game.pointsLeft)
    var last: MutableLiveData<String> = MutableLiveData("-")

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
        pointsLeft.value = game.pointsLeft
        if (game.lastServe == -1)
            last.value = "-"
        else
            last.value = game.lastServe.toString()
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