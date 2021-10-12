package com.example.dartapp.views.chart.util

import android.graphics.*
import android.view.View
import com.example.dartapp.R
import com.example.dartapp.views.chart.height
import kotlin.math.max

// Padding between the edge of the TextBox rectangle and the title and description texts
private const val TEXT_BOX_PADDING = 10f
private const val TEXT_BOX_ROUNDING = 10f

class InfoTextBox(private val view: View) {


    var title: String = "Title"
    var description: String = "Description"

    var drawFromCenter = true

    private lateinit var textBoxRect: RectF

    // Text baselines for the texts
    private var titleBaseLine = 0f
    private var descBaseLine = 0f

    private val titlePaint = Paint().apply {
        isAntiAlias = true
        textSize = 30f
        isFakeBoldText = true
        textAlign = Paint.Align.CENTER
        color = view.getAttrColor(R.attr.colorOnBackground)
    }
    private val descPaint = Paint().apply {
        isAntiAlias = true
        textSize = 24f
        textAlign = Paint.Align.CENTER
        color = view.getAttrColor(R.attr.colorOnBackground)
    }
    private val textBoxPaint = Paint().apply {
        color = view.getAttrColor(R.attr.backgroundColor)
        alpha = 160
    }


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
        val height = 2 * TEXT_BOX_PADDING + titleHeight + descHeight + titleFM.leading

        if (drawFromCenter) {
            textBoxRect = RectF(- width / 2, - height / 2, width / 2, height / 2)
            titleBaseLine -= height / 2
            descBaseLine -= height / 2
        } else {
            textBoxRect = RectF(0f, 0f, width, height)
        }

    }



    fun draw(canvas: Canvas) {
        val x = textBoxRect.centerX()

        canvas.drawRoundRect(textBoxRect, TEXT_BOX_ROUNDING, TEXT_BOX_ROUNDING, textBoxPaint)
        canvas.drawText(title, x, titleBaseLine, titlePaint)
        canvas.drawText(description, x, descBaseLine, descPaint)
    }

}