package com.example.dartapp.views.numbergrid

import android.widget.Button
import androidx.annotation.StringRes
import com.example.dartapp.R
import com.example.dartapp.util.Strings

abstract class Tile(var text: String) {

}

class DigitTile(val digit: Int) : Tile(digit.toString()) {

}

open class ActionTile(val action: Action) : Tile(action.buttonString()) {
    enum class Action(@StringRes val stringResId: Int) {
        CLEAR(R.string.clear),
        CONFIRM(R.string.confirm),
        NO_SCORE(R.string.noScore);

        fun buttonString() : String {
            return Strings.get(stringResId)
        }
    }
}

class ConfirmNoScoreTile : ActionTile(Action.CONFIRM) {

    var button: Button? = null

    init {
        text = Action.NO_SCORE.buttonString()
    }

    fun updateDynamicText(number: Int) {
        val buttonText = when (number) {
            0 -> Action.NO_SCORE.buttonString()
            else -> Action.CONFIRM.buttonString()
        }

        button?.text = buttonText
    }
}