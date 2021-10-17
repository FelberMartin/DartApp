package com.example.dartapp.views.numbergrid

abstract class Tile(val text: String) {

}

class DigitTile(val digit: Int) : Tile(digit.toString()) {

}

class ActionTile(val action: Action) : Tile(action.name) {
    enum class Action {
        CLEAR,
        CONFIRM
    }
}