package com.example.dartapp.views.chart

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import kotlin.math.*

private const val SELECTION_PROTRUDE = 20f
private const val STARTING_ANGLE_GRAD = -90f
private const val STARTING_ANGLE= - PI / 2

private const val TEXT_BOX_PADDING = 5f

private const val TAG = "PieChart"

class PieChart @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Chart(context, attrs, defStyleAttr) {

    override var data: DataSet = DataSet()
        set(value) {
            field = value
            dataChanged()
        }

    private var dataSum: Float = 0.0f
    private lateinit var fractions: ArrayList<Float>
    private lateinit var startPoints: ArrayList<PointF>
    private lateinit var middlePoints: ArrayList<PointF>
    private lateinit var center: PointF

    private lateinit var circleRect: RectF

    private var selectedIndex = -1


    private val paint: Paint = Paint().apply {
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 8f
    }

    private val selectionTitlePaint = Paint().apply {
        isAntiAlias = true
        textSize = 30f
        isFakeBoldText = true
        textAlign = Paint.Align.CENTER
        color = Color.WHITE
    }
    private val selectionDescPaint = Paint().apply {
        isAntiAlias = true
        textSize = 24f
        textAlign = Paint.Align.CENTER
        color = Color.LTGRAY
    }
    private val selectionTextBoxPaint = Paint().apply {
        color = Color.BLACK
        alpha = 100
    }


    init {
        if (true) {
            selectedIndex = 1
            data = DataSet.Generator.random(type = DataSet.Type.STRING, count=3)
        }
    }

    private fun dataChanged() {
        data.forEach{ dp -> assert(dp.y.toDouble() >= 0) }
        dataSum = data.sumOf { dp -> dp.y.toDouble() }.toFloat()

        fractions = ArrayList()
        data.forEach { dp -> fractions.add(dp.y.toFloat() / dataSum) }

        recalculatePoints()
    }

    private fun recalculatePoints() {
        startPoints = ArrayList()
        middlePoints = ArrayList()

        var startAngle = STARTING_ANGLE
        for (fraction in fractions) {
            startPoints.add(PointF(cos(startAngle).toFloat(), sin(startAngle).toFloat()))
            val fractionAngle = fraction * 2 * PI
            val middleAngle = startAngle + fractionAngle / 2
            middlePoints.add(PointF(cos(middleAngle).toFloat(), sin(middleAngle).toFloat()))
            startAngle += fractionAngle
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val requestedWidth = MeasureSpec.getSize(widthMeasureSpec)
        val requestedHeight = MeasureSpec.getSize(heightMeasureSpec)
        val desiredWidth = requestedWidth + paddingLeft + paddingRight
        val desiredHeight = requestedHeight + paddingTop + paddingBottom

        val size = min(desiredWidth, desiredHeight)

        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val size = min(w, h).toFloat()
        val offset = SELECTION_PROTRUDE
        circleRect = RectF(offset, offset, size - offset, size - offset)

        center = PointF(size / 2, size / 2)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawPie(canvas)
        drawFractionSpacers(canvas)
        drawSelectionInfo(canvas)
    }

    private fun drawPie(canvas: Canvas) {
        var startAngle = STARTING_ANGLE_GRAD

        for (index in 0 until data.size) {

            val sweepAngle = fractions[index] * 360
            paint.color = colorManager.get(index)

            // let the selected arc protrude from the center
            if (index == selectedIndex) {
                canvas.save()
                val dx = middlePoints[index].x * SELECTION_PROTRUDE
                val dy = middlePoints[index].y * SELECTION_PROTRUDE
                canvas.translate(dx, dy)
            }

            canvas.drawArc(circleRect, startAngle, sweepAngle, true, paint)

            if (index == selectedIndex)
                canvas.restore()

            startAngle += sweepAngle
        }
    }

    // Spaces between fractions
    private fun drawFractionSpacers(canvas: Canvas) {
        paint.color = Color.WHITE
        for (point in startPoints) {
            val radius = width / 2f
            val relX = point.x * radius
            val relY = point.y * radius
            canvas.drawLine(center.x, center.y,
                center.x + relX, center.y + relY, paint)
        }
    }

    private fun drawSelectionInfo(canvas: Canvas) {
        if (selectedIndex == -1) return

        val titleText = data[selectedIndex].xString(data.dataPointXType)

        val percent = fractions[selectedIndex] * 100f
        val value = data[selectedIndex].yString()
        val descText = String.format("%s (%.0f%%)", value, percent)

        val textBoxWidth = 2 * TEXT_BOX_PADDING +
                max(selectionTitlePaint.measureText(titleText),
                    selectionDescPaint.measureText(descText))
        val textBoxHeight = getTextBoxHeight()

        val radius = 0.6f * center.x
        val textBoxCenter = PointF(
            center.x + middlePoints[selectedIndex].x * radius,
            center.y + middlePoints[selectedIndex].y * radius
        )

        val textBoxRect = RectF(
            textBoxCenter.x - textBoxWidth / 2,
            textBoxCenter.y - textBoxHeight / 2,
            textBoxCenter.x + textBoxWidth / 2,
            textBoxCenter.y + textBoxHeight / 2
        )

        canvas.drawRoundRect(textBoxRect, 5f, 5f, selectionTextBoxPaint)

        canvas.drawText(titleText, textBoxCenter.x, textBoxCenter.y, selectionTitlePaint)
        canvas.drawText(descText, textBoxCenter.x, textBoxCenter.y + 24, selectionDescPaint)
    }

    private fun getTextBoxHeight() : Float {
        val titleFM = selectionTitlePaint.fontMetrics
        val descFM = selectionDescPaint.fontMetrics
        val titleHeight = titleFM.bottom - titleFM.top
        val descHeight = descFM.bottom - descFM.top
        return 2 * TEXT_BOX_PADDING + titleHeight + descHeight + titleFM.leading
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_UP)
            return true

        val index = getTouchedFractionIndex(event.x, event.y)
        if (index == -1 || index == selectedIndex)
            selectedIndex = -1
        else
            selectedIndex = index

        invalidate()
        return true
    }

    private fun getTouchedFractionIndex(x: Float, y: Float) : Int {
        val index = getTouchedDirectionIndex(x, y)
        val dx = center.x - x
        val dy = center.y - y
        val unselectedRadius = center.x - SELECTION_PROTRUDE

        // inside
        if (dx * dx + dy * dy <= unselectedRadius * unselectedRadius)
            return index
        // outside
        else if (selectedIndex != index)
            return -1

        // check whether inside magnified selection
        val selectedRadius = center.x
        if (dx * dx + dy * dy <= selectedRadius * selectedRadius)
            return index

        return -1
    }

    private fun getTouchedDirectionIndex(x: Float, y: Float) : Int{
        val dx = center.x - x
        val dy = center.y - y
        var touchAngle = atan(dy / dx)

        // atan only returns -PI/2 to PI/2
        if (dx > 0)
            touchAngle += PI.toFloat()

        // finding the fraction within which the touched angle lays
        var startAngle = STARTING_ANGLE
        for (index in 0 until data.size) {
            val fractionAngle = fractions[index] * 2 * PI.toFloat()
            if (startAngle <= touchAngle &&
                touchAngle <= startAngle + fractionAngle) {
                return index
            }
            startAngle += fractionAngle
        }

        return -1
    }




}