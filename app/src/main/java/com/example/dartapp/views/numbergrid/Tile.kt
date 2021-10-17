package com.example.dartapp.views.numbergrid

abstract class Tile(val text: String) {

}

class NumberTile(val number: Int) : Tile(number.toString()) {

}

class ActionTile(val action: Action) : Tile(action.name) {
    enum class Action {
        CLEAR,
        CONFIRM
    }
}