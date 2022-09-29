package com.example.dartapp.views.chart


import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import com.example.dartapp.views.chart.util.DataSet
import com.example.dartapp.views.chart.util.InfoTextBox
import kotlin.math.min


class BarChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CoordinateBasedChart(context, attrs, defStyleAttr) {


    private val barRects = arrayListOf<RectF>()
    var barMaxHeight = Float.MAX_VALUE

    /** Corner radius of the upper tip of the bars. */
    var roundedBarRadius: Float = 12f

    private val barDefaultSize = 0.7f
    private val barSelectedSize = 0.85f

    private val barPaint = Paint().apply {
        isAntiAlias = true
    }

    private var info = InfoTextBox(this)
    private var infoTranslation = PointF()

    init {
        showXAxisArrow = false
        showXAxisMarkers = false
        showVerticalGrid = false
        xStartAtZero = true
        yStartAtZero = true
        verticalAutoPadding = false
        topAutoPadding = true
        if (isInEditMode) {
            data = DataSet.random(type = DataSet.Type.STRING)
        }

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        enterAnimation = ObjectAnimator.ofFloat(
            this, "barMaxHeight", 0f, coordPixelRect.height()
        ).apply {
            duration = enterAnimationDuration
            addUpdateListener(this@BarChart)
            if (!isInEditMode) start()
        }

        reload()
    }

    override fun reload() {
        super.reload()
        updateBarRects()
    }


    override fun onSelectionUpdate() {
        updateBarRects()

        if (selectedIndex == -1) return

        info.title = data.xString(selectedIndex)
        info.description = data[selectedIndex].yString()
        info.update()

        val selectedRect = barRects[selectedIndex]
        infoTranslation = PointF(selectedRect.centerX(), selectedRect.centerY())
        info.fitInto(coordPixelRect, infoTranslation)
    }

    private fun updateBarRects() {

        barRects.clear()
        for ((index, dp) in data.withIndex()) {
            val multiplier = if (index == selectedIndex) barSelectedSize else barDefaultSize
            val barSize = coordPixelRect.width() / data.size * multiplier

            val numericXValue = if (dp.x is Number) (dp.x as Number).toFloat() else index.toFloat()
            val x = coordXToPixel(numericXValue)
            val yTop = coordYToPixel(dp.y.toFloat())

            barRects.add(RectF(x - barSize / 2, yTop,
                x + barSize / 2, coordPixelRect.bottom))
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawGrid(canvas)
        drawBars(canvas)
        drawMarkers(canvas)
        drawArrows(canvas)

        drawSelectionInfo(canvas)
    }

    private fun drawBars(canvas: Canvas) {
        for (i in 0 until barRects.size) {
            barPaint.color = colorManager.get(i)
            val rect = barRects[i]

            val height = min(barMaxHeight, rect.height())
            val heightLimitedRect = RectF(rect.left, rect.bottom - height, rect.right, rect.bottom)
            canvas.drawRoundRect(heightLimitedRect, roundedBarRadius, roundedBarRadius, barPaint)
            
            val bottomCutoutsCoverHeight = min(height, roundedBarRadius)
            val bottomCutoutsCoverRect = RectF(rect.left, rect.bottom - bottomCutoutsCoverHeight, rect.right,
                rect.bottom)
            canvas.drawRect(bottomCutoutsCoverRect, barPaint)
        }
    }

    private fun drawSelectionInfo(canvas: Canvas) {
        if (selectedIndex == -1) return
        canvas.save()

        canvas.translate(infoTranslation.x, infoTranslation.y)
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