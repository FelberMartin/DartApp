package com.development_felber.dartapp.util.extensions

import androidx.compose.ui.geometry.Offset
import kotlin.math.cos
import kotlin.math.sin

fun Offset.translated(distance: Float, angleDegrees: Float) : Offset {
    val angleInRad = angleDegrees / 360f * 2 * Math.PI
    val offsetFromThis = Offset(
        x = (cos(angleInRad) * distance).toFloat(),
        y = (sin(angleInRad) * distance).toFloat()
    )
    return this.plus(offsetFromThis)
}