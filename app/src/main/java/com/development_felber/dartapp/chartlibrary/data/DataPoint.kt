package com.development_felber.dartapp.views.chart.data

import com.development_felber.dartapp.util.NumberFormatter
import com.development_felber.dartapp.views.chart.util.DataSet
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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

    override fun toString(): String {
        return "[$x : $y]"
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
                    val xLong = (x as Number).toLong()
                    val date = LocalDateTime.ofInstant(Instant.ofEpochSecond(xLong), ZoneId.systemDefault())
                    return date.format(DateTimeFormatter.ofPattern("dd/MM/yy"))
                }
            }
        }
    }
}
