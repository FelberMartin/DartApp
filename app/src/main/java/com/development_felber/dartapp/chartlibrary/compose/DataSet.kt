package com.development_felber.dartapp.chartlibrary.compose

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
}


