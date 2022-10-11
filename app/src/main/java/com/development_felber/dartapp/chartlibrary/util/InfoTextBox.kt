package com.development_felber.dartapp.views.chart.util

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import com.development_felber.dartapp.views.chart.Chart
import com.development_felber.dartapp.views.chart.height
import kotlin.math.max

// Padding between the edge of the TextBox rectangle and the title and description texts
private const val TEXT_BOX_PADDING = 10f
private const val TEXT_BOX_ROUNDING = 10f

class InfoTextBox(
    private val chart: Chart
) {

    var title: String = "Title"
    var description: String = "Description"

    var drawFromCenter = true

    private lateinit var textBoxRect: RectF

    // Text baselines for the texts
    private var titleBaseLine = 0f
    private var descBaseLine = 0f

    private val titlePaint = Paint().apply {
        isAntiAlias = true
        textSize = 36f
        isFakeBoldText = true
        textAlign = Paint.Align.CENTER
    }
    private val descPaint = Paint().apply {
        isAntiAlias = true
        textSize = 30f
        textAlign = Paint.Align.CENTER
    }
    private val textBoxPaint = Paint()


    fun update() {
        updateDimensions()
    }

    // Updates the values for the selection TextBox
    private fun updateDimensions() {
        val titleFM = titlePaint.fontMetrics
        val descFM = descPaint.fontMetrics
        val titleHeight = titleFM.height()
        val descHeight = descFM.height()

        titleBaseLine = TEXT_BOX_PADDING - titleFM.top
        descBaseLine = TEXT_BOX_PADDING + titleHeight + titleFM.leading - descFM.top

        val width = 2 * TEXT_BOX_PADDING +
                max(titlePaint.measureText(title),
                    descPaint.measureText(description))
        var height = 2 * TEXT_BOX_PADDING + titleHeight + descHeight + titleFM.leading

        if (title == "") {
            // Center the description text
            height -= titleHeight
            descBaseLine = height / 2 + 10

        }

        if (drawFromCenter) {
            textBoxRect = RectF(- width / 2, - height / 2, width / 2, height / 2)
            titleBaseLine -= height / 2
            descBaseLine -= height / 2
        } else {
            textBoxRect = RectF(0f, 0f, width, height)
        }
    }

    fun fitInto(bounds: RectF, translation: PointF) {
        var offset = PointF()

        val left = translation.x + textBoxRect.left
        val top = translation.y + textBoxRect.top
        val right = translation.x + textBoxRect.right
        val bottom = translation.y + textBoxRect.bottom

        if (left < bounds.left) {
            offset.x = bounds.left - left
        } else if (right > bounds.right) {
            offset.x = bounds.right - right
        }
        if (top < bounds.top) {
            offset.y = bounds.top - top
        } else if (bottom > bounds.bottom) {
            offset.y = bounds.bottom - bottom
        }

        textBoxRect.offset(offset.x, offset.y)
        titleBaseLine += offset.y
        descBaseLine += offset.y
    }



    fun draw(canvas: Canvas) {
        val x = textBoxRect.centerX()

        textBoxPaint.color = chart.colorManager.selectionLabelBackground
        canvas.drawRoundRect(textBoxRect, TEXT_BOX_ROUNDING, TEXT_BOX_ROUNDING, textBoxPaint)

        titlePaint.color = chart.colorManager.selectionLabelTitle
        canvas.drawText(title, x, titleBaseLine, titlePaint)

        descPaint.color = chart.colorManager.selectionLabelDescription
        canvas.drawText(description, x, descBaseLine, descPaint)
    }

}