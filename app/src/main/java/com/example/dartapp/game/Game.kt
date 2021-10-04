package com.example.dartapp.game

class Game (val startPoints: Int) {

    var serves: ArrayList<Int> = ArrayList()

    var pointsLeft: Int = startPoints
        get() = startPoints - serves.sum()

    var lastServe: Int = -1
        get() {
            if (serves.isEmpty()) return -1
            return serves.last()
        }

    var avg: Double = 0.0
        get() = serves.average()



}