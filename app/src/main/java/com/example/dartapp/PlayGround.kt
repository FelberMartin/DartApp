package com.example.dartapp

class PlayGround {
    companion object {
        fun all() {
            validServes()
        }

        fun validServes() {
            var serveValids = ArrayList<Int>()
            var valids = validThrows()
            for (first in valids) {
                for (second in valids) {
                    for (third in valids) {
                        val serve = first + second + third
                        serveValids.add(serve)
                    }
                }
            }

            println("Invalid serves:")
            for (i in 0..180) {
                if (!serveValids.contains(i)) {
                    print(", $i")
                }
            }
            println()
        }

        fun validThrows(): List<Int> {
            var valids = ArrayList<Int>()
            for (multiplier in 1..3) {
                for (value in 0..20) {
                    valids.add(multiplier * value)
                }
            }

            valids.add(25)
            valids.add(50)
            return valids
        }
    }

}