package com.example.dartapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dartapp.util.App
import com.example.dartapp.R
import com.example.dartapp.game.gameModes.Mode501
import com.example.dartapp.util.Strings


class GameViewModelFactory(private val gameModeString: String?) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            val mode = when (gameModeString) {
                Strings.get(R.string.mode_501_label) -> Mode501()

                // This is the default behaviour
                else -> Mode501()
            }

            return GameViewModel(mode) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }

}