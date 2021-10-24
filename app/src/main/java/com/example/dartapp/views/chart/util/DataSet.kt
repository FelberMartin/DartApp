package com.example.dartapp.views.chart.util

import com.example.dartapp.util.decimalToString
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random


class DataPoint(var x: Any, var y: Number) {
    fun yString() : String {
        return decimalToString(y)
    }

    fun xString(type: DataSet.Type) : String {
        return xString(x, type)
    }

    companion object {
        fun format(n: Number) : String {
            return decimalToString(n)
        }

        fun xString(x: Any, type: DataSet.Type) : String {
            return when (type) {
                DataSet.Type.STRING -> x as String
                DataSet.Type.NUMBER -> format((x as Number))
                DataSet.Type.DATE -> {
                    val xLong = x as Long
                    val date = Date(xLong)
                    return SimpleDateFormat.getDateInstance().format(date)
                }
            }
        }
    }
}

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

    fun xString(index: Int) : String {
        return get(index).xString(dataPointXType)
    }

    fun xStringFrom(x: Any) : String {
        return DataPoint.xString(x, dataPointXType)
    }

    companion object Generator {

        private val names = listOf("Apple Pie", "Silly StackOverflow questions", "Litre of Wine",
            "Fluffy Puppies", "Pfannkuchen", "UwU", "Money", "Kilos of Elephant's shit", "Beer",
            "Koks", "IQ", "Hours till you die", "ÄÖüµ€@{*|^^°ÓÈ")


        fun random(type: Type = Type.NUMBER, count: Int = 4, randomX: Boolean = false, min: Double = 0.0, max: Double = 100.0) : DataSet {
            var data = DataSet()
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
            var data = DataSet()
            data.dataPointXType = Type.STRING
            data.add(DataPoint("Very large text, some may even say its as large as my ...", 100))
            data.add(DataPoint("Lorem Ipso, deshalb geh ich Disco, pogo logo schoko schoki!", 100))
            data.add(DataPoint("On the top", 5))
            return data
        }

        fun line() : DataSet {
            var data = DataSet()
            data.dataPointXType = Type.NUMBER
            data.add(DataPoint(0f, 10f))
            data.add(DataPoint(1f, 10f))
            data.add(DataPoint(2f, 10f))
            return data
        }
    }

}

