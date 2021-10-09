package com.example.dartapp.views.chart

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random


class DataPoint(var x: Any, var y: Number) {
    fun yString() : String {
        return decimalFormat.format(y)
    }

    fun xString(type: DataSet.Type) : String {
        return when (type) {
            DataSet.Type.STRING -> x as String
            DataSet.Type.NUMBER -> decimalFormat.format(x)
            DataSet.Type.DATE -> {
                val xLong = x as Long
                val date = Date(xLong)
                return SimpleDateFormat.getDateInstance().format(date)
            }
        }
    }

    companion object {
        val decimalFormat = DecimalFormat("#.##")
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

    companion object Generator {

        private val names = listOf("Apple Pie", "Silly StackOverflow questions", "Litre of Wine",
            "Fluffy Puppies", "Pfannenkuchen", "UwU", "Money", "Kilos of Elaphant's shit", "Beer",
            "Koks", "IQ", "Hours till you die", "ÄÖüµ€@{*|^^°ÓÈ")


        fun random(type: Type = Type.NUMBER, count: Int = 4, min: Double = 0.0, max: Double = 100.0) : DataSet {
            var data = DataSet()
            data.dataPointXType = type

            for (i in 0 until count) {
                val rnd = Random.nextDouble(min, max)

                if (type == Type.STRING) {
                    val name = names[Random.nextInt(names.size)]
                    data.add(DataPoint(name, rnd))

                } else
                    data.add(DataPoint(i, rnd))
            }

            return data
        }
    }

}

