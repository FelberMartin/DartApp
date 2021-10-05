package com.example.dartapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dartapp.game.gameModes.GameMode
import com.example.dartapp.game.Game

class GameViewModel(private val mode: GameMode) : ViewModel() {

    private var game: Game = mode.initGame()
    var pointsLeft: MutableLiveData<Int> = MutableLiveData(game.pointsLeft)
    var last: MutableLiveData<String> = MutableLiveData("-")

    fun processServe(value: Int): Boolean {
        if (!mode.isServeValid(value, game)) {
            return false
        }

        game.serves.add(value)
        update()
        return true
    }

    fun isOver(): Boolean {
        return mode.isGameOver(game)
    }

    private fun update() {
        pointsLeft.value = game.pointsLeft
        if (game.lastServe == -1)
            last.value = "-"
        else
            last.value = game.lastServe.toString()
    }

    fun restart() {
        game = mode.initGame()
        update()
    }


}