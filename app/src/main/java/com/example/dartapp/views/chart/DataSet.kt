package com.example.dartapp.views.chart

import kotlin.random.Random


class DataPoint(var x: Any, var y: Number)

/**
 * Class responsible for providing the data for the chart views.
 * This is not really a Set but rather an arrayList.
 */
class DataSet(c: MutableCollection<out DataPoint>) : ArrayList<DataPoint>(c) {

    constructor() : this(ArrayList())

    enum class Type {
        DATE, NUMBER, STRING
    }

    var dataPointXType: Type = Type.STRING

    companion object Generator {
        fun randomNumbered(count: Int = 4, min: Double = 0.0, max: Double = 100.0) : DataSet {
            var data = DataSet()
            data.dataPointXType = Type.NUMBER
            for (i in 0 until count) {
                val rnd = Random.nextDouble(min, max)
                data.add(DataPoint(i, rnd))
            }

            return data
        }
    }
}

