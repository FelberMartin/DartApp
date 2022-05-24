package com.example.dartapp.game.gameModes

import androidx.annotation.StringRes
import com.example.dartapp.R
import com.example.dartapp.game.Game

abstract class GameMode {

    abstract val startPoints: Int
    abstract val type: Type

    abstract fun isServeValid(serve: Int, game: Game): Boolean

    open fun isGameFinished(game: Game): Boolean {
        return game.pointsLeft == 0
    }

    open fun askForDoubleAttempts(game: Game) : Boolean {
        return game.pointsLeft <= 170
    }


    companion object {
        fun fromType(type: GameMode.Type): GameMode {
            return when (type) {
                Type.X01 -> Mode501()
                Type.CHEAT -> CheatMode()

                else -> CheatMode()
            }
        }

        fun fromTypeId(id: Int): GameMode {
            return fromType(Type.fromId(id))
        }
    }

    enum class Type(val id: Int, @StringRes val stringRes: Int) {
        ALL(-1, R.string.mode_all),     // Used for selection modes in the stats table
        X01(1, R.string.mode_501_label),
        CHEAT(69, R.string.mode_cheat_label);

        companion object {
            fun fromId(id: Int) : Type {
                return values().first { it.id == id }
            }
        }
    }
}