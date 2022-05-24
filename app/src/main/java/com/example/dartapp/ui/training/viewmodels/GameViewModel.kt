package com.example.dartapp.ui.training.viewmodels

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dartapp.database.LegDatabaseDao
import com.example.dartapp.game.FinishHelper
import com.example.dartapp.game.Game
import com.example.dartapp.game.gameModes.GameMode
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject



class GameViewModel @AssistedInject constructor(
    private val legDatabaseDao: LegDatabaseDao,
    @Assisted private val mode: GameMode
) : ViewModel() {

    @AssistedFactory
    interface GameViewModelFactory {
        fun create(gameMode: GameMode): GameViewModel
    }

    companion object {
        fun providesFactory(
            assistedFactory: GameViewModel.GameViewModelFactory,
            gameMode: GameMode
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {

                return assistedFactory.create(gameMode) as T
            }
        }
    }

    private val NO_DATA = "--"

    private var game: Game = Game(mode)
    var pointsLeft: MutableLiveData<Int> = MutableLiveData(game.pointsLeft)
    var last: MutableLiveData<String> = MutableLiveData(NO_DATA)
    var avg: MutableLiveData<String> = MutableLiveData(NO_DATA)
    var dartCount: MutableLiveData<Int> = MutableLiveData(0)
    var finishSuggestion: MutableLiveData<String> = MutableLiveData(NO_DATA)
    var suggestionVisibility: MutableLiveData<Int> = MutableLiveData(View.GONE)

    fun isServeValid(serve: Int): Boolean {
        return game.isServeValid(serve)
    }

    fun processServe(serve: Int, dartCount: Int = 3) {
        // Normally zero unused darts, only used for checkout
        val unused = 3 - dartCount
        game.unusedDartCount += unused

        game.serves.add(serve)
        update()
    }

    fun wouldBeFinished(serve: Int): Boolean {
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

        // Finish Suggestion
        finishSuggestion.value = getFinishSuggestion()
        suggestionVisibility.value = if (canFinish()) View.VISIBLE else View.GONE
    }

    private fun getFinishSuggestion(): String {
        return FinishHelper.finishSuggestions[game.pointsLeft] ?: "No Suggestion"
    }

    private fun canFinish(): Boolean {
        return FinishHelper.finishSuggestions.containsKey(game.pointsLeft)
    }

    fun restart() {
        game = Game(mode)
        update()
    }

    fun saveGameToDatabase() {
        legDatabaseDao.insert(game.toLeg())
        Log.d("LegDatabase", "Leg stored")
    }





}