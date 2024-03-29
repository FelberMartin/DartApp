package com.development_felber.dartapp.views.chart

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.graphics.plus
import com.development_felber.dartapp.views.chart.util.DataSet
import com.development_felber.dartapp.views.chart.util.InfoTextBox

private const val TAG = "LineChart"

private const val TOUCH_RADIUS = 50f

class LineChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CoordinateBasedChart(context, attrs, defStyleAttr) {

    private var points = arrayListOf<PointF?>()
    private var linePath = Path()
    var smoothedLine = true

    var shownLineFraction = 1f
    override var enterAnimation: ObjectAnimator? = ObjectAnimator.ofFloat(
        this, "shownLineFraction", 0f, 1f).apply {
            duration = enterAnimationDuration
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener(this@LineChart)
            if (!isInEditMode) start()
    }

    var showSelectionInfoTitle = true

    private var selectionInfo = InfoTextBox(this)


    private val lineShader: Shader = LinearGradient(0f, 0f, 0f, 500f, intArrayOf(
        Color.parseColor("#F97C3C"),
        Color.parseColor("#FDB54E"),
        Color.parseColor("#64B678"),
        Color.parseColor("#478AEA"),
        Color.parseColor("#8446CC")
    ), null, Shader.TileMode.CLAMP)

    private val linePaint = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeWidth = 10f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
//        shader = lineShader
    }

    private val selectionPaint = Paint().apply {
        isAntiAlias = true
        strokeWidth = 6f
    }


    init {
        if (isInEditMode) {
            data = DataSet.random(count = 7, randomX = true)
            selectedIndex = -1
        }
    }

    override fun reload() {
        super.reload()
        updatePath()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        updatePath()
    }


    private fun updatePath() {
        points.clear()
        repeat(data.size) {
            points.add(null)
        }
//        linePath.reset()
        linePath.rewind()

        val nonNaNIndices = data.nonNaNIndices()

        if (nonNaNIndices.isEmpty()) {
            return
        }

        var lastPoint = inCoordSystem(nonNaNIndices[0])
        var control = lastPoint
        linePath.moveTo(lastPoint.x, lastPoint.y)
        for (i in nonNaNIndices) {
            val nextPoint = inCoordSystem(i)
            points[i] = nextPoint

            if (smoothedLine) {
                control = nextPoint.plus(lastPoint)
                linePath.quadTo(lastPoint.x, lastPoint.y,
                    control.x / 2, control.y / 2)
            } else
                linePath.lineTo(nextPoint.x, nextPoint.y)

            lastPoint = nextPoint
        }

        if (smoothedLine)
            linePath.quadTo(control.x / 2, control.y / 2, lastPoint.x, lastPoint.y)
    }

    override fun onSelectionUpdate() {
        if (selectedIndex == -1) return

        selectionInfo.title = if (showSelectionInfoTitle) data.xString(selectedIndex) else ""
        selectionInfo.description = data[selectedIndex].yString()
        selectionInfo.update()
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        linePaint.color = colorManager.getGraphColor(0)
        drawLines(canvas)
        drawPoints(canvas)
        drawSelection(canvas)
    }

    private fun drawLines(canvas: Canvas) {
        if (linePath.isEmpty) {
            return
        }

        val appr = linePath.approximate(0.5f)
        val points = appr.filterIndexed { index, _ -> index % 3 != 0 }
        val fractions = appr.filterIndexed { index, _ -> index % 3 == 0 }
        val lines = ArrayList<Float>()
        for (i in 0 until points.size / 2) {
            val x = points[2 * i]
            val y = points[2 * i + 1]
            lines.add(x)
            lines.add(y)
            if (i != 0 && i != points.size / 2 - 1) {
                lines.add(x)
                lines.add(y)
            }
            if (fractions[i] > shownLineFraction)
                break
        }
        canvas.drawLines(lines.toFloatArray(), linePaint)
    }

    private fun drawPoints(canvas: Canvas) {
        for (p in points) {
            if (p != null) {
                canvas.drawCircle(p.x, p.y, 5f, linePaint)
            }
        }
    }

    private fun drawSelection(canvas: Canvas) {
        if (selectedIndex == -1) return

        selectionPaint.color = colorManager.selectionHighlighter

        val p = inCoordSystem(selectedIndex)
        canvas.drawLine(coordPixelRect.left, p.y, coordPixelRect.right, p.y, selectionPaint)
        canvas.drawLine(p.x, coordPixelRect.top, p.x, coordPixelRect.bottom, selectionPaint)

        canvas.save()
        canvas.translate(p.x, p.y)
        selectionInfo.draw(canvas)
        canvas.restore()
    }


    override fun getTouchedIndex(x: Float, y: Float) : Int {
        var index = -1
        var minSqDistance = Float.MAX_VALUE
        for ((i, p) in points.withIndex()) {
            if (p == null) {
                continue
            }
            val dx = x - p.x
            val dy = y - p.y
            val dist = dx * dx + dy * dy
            if (dist < minSqDistance) {
                index = i
                minSqDistance = dist
            }
        }

        if (minSqDistance <= TOUCH_RADIUS * TOUCH_RADIUS)
            return index

        return -1
    }
}
