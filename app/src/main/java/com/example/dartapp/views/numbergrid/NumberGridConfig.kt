package com.example.dartapp.views.numbergrid

import com.example.dartapp.views.numbergrid.NumberGridConfig.Tile.*

class NumberGridConfig(val columns: Int, val rows: Int, val layout: Array<Array<Tile>>) {
    enum class Tile(stringValue: String) {
        ZERO("0"),
        ONE("1"),
        TWO("2"),
        THREE("3"),
        FOUR("4"),
        FIVE("5"),
        SIX("6"),
        SEVEN("7"),
        EIGHT("8"),
        NINE("9"),
        CLEAR("Clear"),
        CONFIRM("Confirm"),
        BACK("Back"),
        DOUBLE("x2"),
        TRIPLE("x3")
    }


    companion object {
        val DEFAULT_3x4 = NumberGridConfig(3, 4, arrayOf(
            arrayOf(SEVEN, EIGHT, NINE),
            arrayOf(FOUR, FIVE, SIX),
            arrayOf(ONE, TWO, THREE),
            arrayOf(CLEAR, ZERO, CONFIRM)
        ))
    }
}