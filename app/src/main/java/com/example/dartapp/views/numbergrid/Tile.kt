package com.example.dartapp.views.numbergrid

import android.content.Context
import android.widget.Button
import androidx.annotation.StringRes
import com.example.dartapp.R
import com.example.dartapp.util.resources.Strings

abstract class Tile(val context: Context, var text: String) {

}

class DigitTile(context: Context, val digit: Int) : Tile(context, digit.toString()) {

}

open class ActionTile(context: Context, val action: Action) : Tile(context, action.buttonString(context)) {
    enum class Action(@StringRes val stringResId: Int) {
        CLEAR(R.string.clear),
        CONFIRM(R.string.confirm),
        NO_SCORE(R.string.no_score);

        fun buttonString(context: Context) : String {
            return Strings.get(stringResId, context)
        }
    }
}

class ConfirmNoScoreTile(context: Context) : ActionTile(context, Action.CONFIRM) {

    var button: Button? = null

    init {
        text = Action.NO_SCORE.buttonString(context)
    }

    fun updateDynamicText(number: Int) {
        val buttonText = when (number) {
            0 -> Action.NO_SCORE.buttonString(context)
            else -> Action.CONFIRM.buttonString(context)
        }

        button?.text = buttonText
    }
}