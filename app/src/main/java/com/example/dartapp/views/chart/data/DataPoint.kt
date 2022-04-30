package com.example.dartapp.views.chart.data

import com.example.dartapp.util.NumberFormatter
import com.example.dartapp.views.chart.util.DataSet
import java.text.SimpleDateFormat
import java.util.*

class DataPoint(var x: Any, var y: Number) {
    fun yString() : String {
        return NumberFormatter.decimalToString(y)
    }

    fun xString(type: DataSet.Type) : String {
        return xString(x, type)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataPoint

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }


    companion object {
        fun format(n: Number) : String {
            return NumberFormatter.decimalToString(n)
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
