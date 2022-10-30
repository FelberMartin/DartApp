package com.development_felber.dartapp.chartlibrary.compose

import kotlin.random.Random


data class DataPoint(
    val x: String,
    val y: Double,
)

class DataSet(collection: Collection<DataPoint>) : ArrayList<DataPoint>(collection) {

    constructor() : this(ArrayList())

    fun nonNaNIndices() : List<Int> {
        return this.mapIndexed { index, dataPoint -> Pair(index, dataPoint) }
            .filter { pair -> !pair.second.y.toFloat().isNaN() }
            .map { pair -> pair.first }
    }

    companion object {
        fun pieChartTest() : DataSet {
            return DataSet(listOf(
                DataPoint("Blue", Random.nextDouble()),
                DataPoint("Wale", Random.nextDouble()),
                DataPoint("Small", Random.nextDouble()),
                DataPoint("Biig", Random.nextDouble()),
                DataPoint("Pi :D", Random.nextDouble()),
            ))
        }
    }
}


