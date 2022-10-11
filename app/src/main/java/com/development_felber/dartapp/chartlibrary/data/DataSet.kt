package com.development_felber.dartapp.views.chart.util

import com.development_felber.dartapp.views.chart.data.DataPoint
import kotlin.random.Random

/**
 * Class responsible for providing the data for the chart views.
 * This is not really a Set but rather an arrayList.
 */
class DataSet(collection: Collection<DataPoint>) : ArrayList<DataPoint>(collection) {

    constructor() : this(ArrayList())

    enum class Type {
        DATE, NUMBER, STRING
    }

    var dataPointXType: Type = Type.STRING

    fun xString(index: Int) : String {
        return get(index).xString(dataPointXType)
    }

    fun xStringFrom(x: Any) : String {
        return DataPoint.xString(x, dataPointXType)
    }

    fun nonNaNIndices() : List<Int> {
        return this.mapIndexed { index, dataPoint -> Pair(index, dataPoint) }
            .filter { pair -> !pair.second.y.toFloat().isNaN() }
            .map { pair -> pair.first }
    }

    companion object Generator {

        private val names = listOf("Apple Pie", "Silly StackOverflow questions", "Litre of Wine",
            "Fluffy Puppies", "Pfannkuchen", "UwU", "Money", "Kilos of Elephant's shit", "Beer",
            "Koks", "IQ", "Hours till you die", "ÄÖüµ€@{*|^^°ÓÈ")


        fun random(type: Type = Type.NUMBER, count: Int = 4, randomX: Boolean = false, min: Double = 0.0, max: Double = 100.0) : DataSet {
            val data = DataSet()
            data.dataPointXType = type

            var xStart = 0
            if (randomX) xStart = Random.nextInt(1000)
            for (i in 0 until count) {
                val rnd = Random.nextDouble(min, max)

                if (type == Type.STRING) {
                    val name = names[Random.nextInt(names.size)]
                    data.add(DataPoint(name, rnd))

                } else
                    data.add(DataPoint(xStart, rnd))

                var increase = 1
                if (randomX) increase = Random.nextInt(100)
                xStart += increase
            }

            return data
        }

        fun pieLargeTexts() : DataSet {
            val data = DataSet()
            data.dataPointXType = Type.STRING
            data.add(DataPoint("Very large text, some may even say its as large as my ...", 100))
            data.add(DataPoint("Lorem Ipso, deshalb geh ich Disco, pogo logo schoko schoki!", 100))
            data.add(DataPoint("On the top", 5))
            return data
        }

        fun line() : DataSet {
            val data = DataSet()
            data.dataPointXType = Type.NUMBER
            data.add(DataPoint(0f, 10f))
            data.add(DataPoint(1f, 10f))
            data.add(DataPoint(2f, 10f))
            return data
        }
    }

}

