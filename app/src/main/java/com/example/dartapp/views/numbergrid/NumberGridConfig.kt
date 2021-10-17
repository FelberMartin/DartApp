package com.example.dartapp.views.numbergrid


class NumberGridConfig(val columns: Int, val rows: Int, private val layout: List<Tile>) {

    fun tileAt(index: Int) : Tile {
        return layout[index]
    }

    fun tileAt(row: Int, column: Int) : Tile {
        val index = row * columns + column
        return tileAt(index)
    }

    companion object Generator {
        fun default3x4() : NumberGridConfig {
            val layout = ArrayList<Tile>()

            // Upper 3x3
            for (row in 0..2) {
                for (column in 0..2) {
                    val number = 7 + column - row * 3
                    layout.add(NumberTile(number))
                }
            }

            // Bottom row
            layout.add(ActionTile(ActionTile.Action.CLEAR))
            layout.add(NumberTile(0))
            layout.add(ActionTile(ActionTile.Action.CONFIRM))

            return NumberGridConfig(3, 4, layout)
        }

        fun simple2x2() : NumberGridConfig {
            val layout = ArrayList<Tile>()
            for (i in 0..3)
                layout.add(NumberTile(i))

            return NumberGridConfig(2, 2, layout)
        }
    }
}
