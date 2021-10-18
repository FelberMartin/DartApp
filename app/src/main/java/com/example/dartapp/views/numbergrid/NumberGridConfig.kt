package com.example.dartapp.views.numbergrid

import android.content.Context


class NumberGridConfig(val columns: Int, val rows: Int, private val layout: List<Tile>) {

    fun tileAt(index: Int) : Tile {
        return layout[index]
    }

    fun tileAt(row: Int, column: Int) : Tile {
        val index = row * columns + column
        return tileAt(index)
    }

    companion object Generator {
        fun default3x4(context: Context) : NumberGridConfig {
            val layout = ArrayList<Tile>()

            // Upper 3x3
            for (row in 0..2) {
                for (column in 0..2) {
                    val number = 7 + column - row * 3
                    layout.add(DigitTile(context, number))
                }
            }

            // Bottom row
            layout.add(ActionTile(context, ActionTile.Action.CLEAR))
            layout.add(DigitTile(context, 0))
            layout.add(ConfirmNoScoreTile(context))

            return NumberGridConfig(3, 4, layout)
        }

        fun simple2x2(context: Context) : NumberGridConfig {
            val layout = ArrayList<Tile>()
            for (i in 0..3)
                layout.add(DigitTile(context, i))

            return NumberGridConfig(2, 2, layout)
        }
    }
}
