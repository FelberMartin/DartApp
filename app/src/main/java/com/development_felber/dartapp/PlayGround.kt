package com.development_felber.dartapp

import java.text.DecimalFormat

class PlayGround {
    companion object {
        fun all() {
//            validServes()
//            formatting()
//            testMarkers()
//            textTimeFormat()
        }

        fun validServes() {
            val serveValids = ArrayList<Int>()
            val valids = validThrows()
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
            val valids = ArrayList<Int>()
            for (multiplier in 1..3) {
                for (value in 0..20) {
                    valids.add(multiplier * value)
                }
            }

            valids.add(25)
            valids.add(50)
            return valids
        }

        fun formatting() {
            val values = listOf(232, 0.18, 1232831821, 4.45, 0, 1.2345)
            for (v in values) {
                val s = DecimalFormat("#.##").format(v)
                println(s)
            }
        }

//        fun testMarkers() {
//            repeat(30) {
//                val v = CoordinateBasedChart.getDistance(it)
//                println("$v")
//            }
//        }


    }



}