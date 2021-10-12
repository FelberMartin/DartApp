package com.example.dartapp.views.chart


import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import com.example.dartapp.views.chart.ARROW_STRENGTH
import com.example.dartapp.views.chart.CoordinateBasedChart
import com.example.dartapp.views.chart.util.DataSet
import com.example.dartapp.views.chart.util.InfoTextBox


class BarChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CoordinateBasedChart(context, attrs, defStyleAttr) {


    private val barRects = arrayListOf<RectF>()

    private val barDefaultSize = 0.7f
    private val barSelectedSize = 0.85f

    private val barPaint = Paint().apply {
        isAntiAlias = true
    }

    private var info = InfoTextBox(this)

    init {
        showVerticalGrid = false
        xStartAtZero = true
        yStartAtZero = true
        verticalAutoPadding = false
        topAutoPadding = true
        data = DataSet.random(type = DataSet.Type.STRING)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        update()
    }

    override fun dataChanged() {
        super.dataChanged()
        update()
    }

    private fun update() {
        updateBarRects()
    }


    override fun onSelectionUpdate() {
        updateBarRects()

        if (selectedIndex == -1) return

        info.title = data.xString(selectedIndex)
        info.description = data[selectedIndex].yString()
        info.update()
    }

    private fun updateBarRects() {


        barRects.clear()
        for ((index, dp) in data.withIndex()) {
            val multiplier = if (index == selectedIndex) barSelectedSize else barDefaultSize
            val barSize = coordPixelRect.width() / data.size * multiplier

            val x = coordXToPixel(index.toFloat())
            val yTop = coordYToPixel(dp.y.toFloat())

            barRects.add(RectF(x - barSize / 2, yTop,
                x + barSize / 2, coordPixelRect.bottom))
        }
    }

    override fun onDraw(canvas: Canvas) {
        drawGrid(canvas)
        drawBars(canvas)
        drawMarkers(canvas)
        drawArrows(canvas)

        drawSelectionInfo(canvas)
    }

    private fun drawBars(canvas: Canvas) {
        for (i in 0 until barRects.size) {
            barPaint.color = colorManager.get(i)
            canvas.drawRect(barRects[i], barPaint)
        }
    }

    private fun drawSelectionInfo(canvas: Canvas) {
        if (selectedIndex == -1) return
        canvas.save()

        val selectedRect = barRects[selectedIndex]
        canvas.translate(selectedRect.centerX(), selectedRect.centerY())
        info.draw(canvas)

        canvas.restore()
    }

    override fun getTouchedIndex(x: Float, y: Float): Int {
        for (i in 0 until barRects.size)
            if (barRects[i].contains(x, y))
                return i
        return -1
    }

}