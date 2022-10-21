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

    companion object {
        fun pieChartTest() : DataSet {
            return DataSet(listOf(
                DataPoint("Blue", 20.0),
                DataPoint("Wale", 10.0),
                DataPoint("Small", 5.0),
                DataPoint("Biig", 55.0),
                DataPoint("Pi :D", 3.1415926535897932383270),
            ))
        }
    }
}


