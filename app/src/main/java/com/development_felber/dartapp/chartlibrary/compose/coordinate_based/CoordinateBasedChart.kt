package com.development_felber.dartapp.chartlibrary.compose.coordinate_based

import android.util.Range
import androidx.compose.runtime.Composable
import com.development_felber.dartapp.chartlibrary.compose.DataSet

@Composable
fun CoordinateBasedChart(
    dataSet: DataSet,
    content: @Composable () -> Unit,
) {

}

class CoordinateScope {

}

class CoordinateSystemBuilder {
    var showHorizontalGrid: Boolean = true
    var showVerticalGrid: Boolean = true
    var showXMarkers: Boolean = true
    var showXArrow: Boolean = true
    var startBehaviour = StartBehaviour.StartAtDataStart
    var coordinateRangeCreator: CoordinateRangeCreator = CoordinateRangeCreator.StartEndPadding()

    enum class StartBehaviour {
        StartAtZero, StartAtDataStart
    }

    sealed class CoordinateRangeCreator() {
        fun createRange(dataMin: Double, dataMax: Double, dataSize: Int): Range<Double> {
            if (dataSize == 0) {
                return Range.create(0.0, 1.0)
            }
            if (dataSize == 1) {
                return Range.create(dataMin - 1, dataMin + 1)
            }
            return safeCreateRange(dataMin, dataMax, dataSize)
        }

        protected abstract fun safeCreateRange(dataMin: Double, dataMax: Double, dataSize: Int): Range<Double>

        class NoPadding : CoordinateRangeCreator() {
            override fun safeCreateRange(dataMin: Double, dataMax: Double, dataSize: Int): Range<Double> {
                return Range.create(dataMin, dataMax)
            }
        }

        class EndPadding : CoordinateRangeCreator() {
            override fun safeCreateRange(dataMin: Double, dataMax: Double, dataSize: Int): Range<Double> {
                val padding = (dataMax - dataMin) * 0.1f
                return Range.create(dataMin, dataMax + padding)
            }
        }

        class StartEndPadding : CoordinateRangeCreator() {
            override fun safeCreateRange(dataMin: Double, dataMax: Double, dataSize: Int): Range<Double> {
                val avgStepSize = (dataMax - dataMin) / (dataSize - 1)
                val padding = avgStepSize / 2
                return Range.create(dataMin - padding, dataMax + padding)
            }
        }
    }
}