package com.development_felber.dartapp.chartlibrary.compose.piechart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.development_felber.dartapp.chartlibrary.compose.DataSet
import com.development_felber.dartapp.ui.shared.extensions.CoroutineScopeProvider
import com.development_felber.dartapp.util.extensions.translated
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlin.math.PI
import kotlin.math.atan


val NO_SELECTION = -1

class PieChartViewModel() : ViewModel(), CoroutineScopeProvider {

    private val radiusToMaxRadiusRatio = 0.95f
    private val startAngle = -90f

    private val _dataSet = MutableStateFlow(DataSet())
    val dataSet = _dataSet.asStateFlow()

    private val _canvasSize = MutableStateFlow(Size(0f, 0f))
    val canvasSize = _canvasSize.asStateFlow()

    val fractions = dataSet.mapState { dataSet ->
        val sum = dataSet.sumOf { it.y }
        dataSet.map { (it.y / sum).toFloat() }
    }

    val accumulatedFractions = fractions.mapState { fractions ->
        val list = mutableListOf<Float>()
        var accumulated = 0f
        for (fraction in fractions) {
            list.add(accumulated)
            accumulated += fraction
        }
        list
    }

    val radiusSelected = canvasSize.mapState { it.minDimension / 2f }
    val radiusNormal = radiusSelected.mapState { it * radiusToMaxRadiusRatio }
    val center = canvasSize.mapState { Offset(it.width / 2f, it.height / 2f) }
    val topLeftOffset = combine(radiusSelected, radiusNormal) { maxRadius, radius ->
        val offset = maxRadius - radius
        Offset(offset, offset)
    }.asStateFlow(Offset(0f, 0f))

    val startAngles = accumulatedFractions.mapState { accumulatedFractions ->
        accumulatedFractions.map { startAngle + it * 360f }
    }
    val startPoints = combine(radiusSelected, center, startAngles) { maxRadius, center, startAngles ->
        startAngles.map {
            center.translated(distance = maxRadius, angleDegrees = it)
        }
    }.asStateFlow(listOf())
    val segmentMiddlePoints = combine(startAngles, fractions, center, radiusNormal) {
            startAngles, fractions, center, radius ->
        startAngles.mapIndexed { index, startAngle ->
            val angle = startAngle + fractions[index] * 360f
            center.translated(radius, angle)
        }
    }


    fun setDataSet(dataSet: DataSet) {
        _dataSet.value = dataSet
    }

    fun setCanvasSize(canvasSize: Size) {
        _canvasSize.value = canvasSize
    }


    private val _selectedIndex = MutableStateFlow(NO_SELECTION)
    val selectedIndex = _selectedIndex.asStateFlow()

    fun onTouch(touch: Offset) {
        val index = getTouchedIndex(touch)
        if (selectedIndex.value == index) {
            _selectedIndex.value = NO_SELECTION
        } else {
            _selectedIndex.value = index
        }
    }

    private fun getTouchedIndex(touch: Offset) : Int {
        val fromCenter = touch.minus(center.value)
        val directionIndex = getTouchDirectionIndex(fromCenter)

        if (fromCenter.getDistance() <= radiusNormal.value) {
            return directionIndex
        }

        val selected = selectedIndex.value == directionIndex
        if (selected && fromCenter.getDistance() <= radiusSelected.value) {
            return directionIndex
        }
        return NO_SELECTION
    }

    private fun getTouchDirectionIndex(fromCenter: Offset): Int {
        var touchAngleRad = atan(fromCenter.y / fromCenter.x)

        // atan only returns -PI/2 to PI/2
        if (fromCenter.x < 0)
            touchAngleRad += PI.toFloat()

        val touchAngle = touchAngleRad / (2 * Math.PI) * 360f

        // finding the segment within which the touched angle lays
        val size = startAngles.value.size
        for (index in startAngles.value.indices) {
            val from = startAngles.value[index]
            val to = startAngles.value[(index + 1) % size]
            if (touchAngle in from..to || index == size - 1) {
                return index
            }
        }
        return NO_SELECTION
    }


    override fun getCoroutineScope(): CoroutineScope = viewModelScope
}