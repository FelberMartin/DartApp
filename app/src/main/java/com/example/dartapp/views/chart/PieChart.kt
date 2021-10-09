package com.example.dartapp.views.chart

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import com.example.dartapp.R
import com.google.android.material.color.MaterialColors
import kotlin.math.*

// How much the selected circle segment should stand out from the center
private const val SELECTION_PROTRUDE = 20f

// Where the first segment begins
private const val STARTING_ANGLE_GRAD = -90f
private const val STARTING_ANGLE= - PI / 2

// Padding between the edge of the TextBox rectangle and the title and description texts
private const val TEXT_BOX_PADDING = 10f

// for Logcat debugging
private const val TAG = "PieChart"

class PieChart @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Chart(context, attrs, defStyleAttr) {


    private var dataSum: Float = 0.0f
    private lateinit var fractions: ArrayList<Float>
    
    // On the unit circle, where this circle segment starts
    private lateinit var startPoints: ArrayList<PointF>
    // On the unit circle, where this circle segment's middle lays
    private lateinit var middlePoints: ArrayList<PointF>
    
    // Center of the View
    private var center: PointF = PointF(0f, 0f)

    private lateinit var circleRect: RectF

    private var selectedIndex = -1
        set(value) {
            field = value
            if (value != -1) updateTextBox()
        }

    private val backgroundColor = MaterialColors.getColor(this, R.attr.backgroundColor)

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
        alpha = 160
    }

    private var titleText = "title"
    private var descText = "desc"
    private lateinit var textBoxRect: RectF
    private lateinit var textBoxTranslation: PointF
    
    // Text baselines for the selection text
    private var titleBaseLine = 0f
    private var descBaseLine = 0f


    init {
        data = DataSet.Generator.random(type = DataSet.Type.STRING, count=3)
        if (isInEditMode) {
            selectedIndex = 1
        }
    }

    override fun dataChanged() {
        super.dataChanged()
        data.forEach{ dp -> assert(dp.y.toDouble() >= 0) }
        dataSum = data.sumOf { dp -> dp.y.toDouble() }.toFloat()

        fractions = ArrayList()
        data.forEach { dp -> fractions.add(dp.y.toFloat() / dataSum) }

        recalculatePoints()
        selectedIndex = -1
    }

    // Recalculate the points on the unit circle (all the sin and cos values)
    private fun recalculatePoints() {
        startPoints = ArrayList()
        middlePoints = ArrayList()

        var startAngle = STARTING_ANGLE
        for (fraction in fractions) {
            startPoints.add(PointF(cos(startAngle).toFloat(), sin(startAngle).toFloat()))
            val segmentAngle = fraction * 2 * PI
            val middleAngle = startAngle + segmentAngle / 2
            middlePoints.add(PointF(cos(middleAngle).toFloat(), sin(middleAngle).toFloat()))
            startAngle += segmentAngle
        }

        invalidate()
    }

    // Updates the values for the selection TextBox
    private fun updateTextBox() {
        updateText()

        val titleFM = selectionTitlePaint.fontMetrics
        val descFM = selectionDescPaint.fontMetrics
        val titleHeight = titleFM.bottom - titleFM.top
        val descHeight = descFM.bottom - descFM.top

        titleBaseLine = TEXT_BOX_PADDING - titleFM.top
        descBaseLine = TEXT_BOX_PADDING + titleHeight + titleFM.leading - descFM.top

        val textBoxWidth = 2 * TEXT_BOX_PADDING +
                max(selectionTitlePaint.measureText(titleText),
                    selectionDescPaint.measureText(descText))
        val textBoxHeight = 2 * TEXT_BOX_PADDING + titleHeight + descHeight + titleFM.leading

        textBoxRect = RectF(0f, 0f, textBoxWidth, textBoxHeight)

        updateTextBoxTranslation()
    }

    // Updates the Selection TextBox's title and description text
    private fun updateText() {
        titleText = data[selectedIndex].xString(data.dataPointXType)

        val percent = fractions[selectedIndex] * 100f
        val value = data[selectedIndex].yString()
        descText = String.format("%s (%.0f%%)", value, percent)
    }

    // Translation of the canvas for drawing the selection TextBox at origin 0,0
    private fun updateTextBoxTranslation() {
        if (selectedIndex == -1) return
        val radius = 0.6f * center.x

        // Pointing to the middle of the textBox
        textBoxTranslation = PointF(
            center.x + middlePoints[selectedIndex].x * radius,
            center.y + middlePoints[selectedIndex].y * radius
        )

        // moving to the upper left corner
        textBoxTranslation.x -= textBoxRect.right / 2
        textBoxTranslation.y -= textBoxRect.bottom / 2
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
        updateTextBoxTranslation()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawSegments(canvas)
        drawSegmentSpacers(canvas)
        drawSelectionTextBox(canvas)
    }

    // Segments of the circle
    private fun drawSegments(canvas: Canvas) {
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

    // Spaces between segments
    private fun drawSegmentSpacers(canvas: Canvas) {
        paint.color = backgroundColor
        for (point in startPoints) {
            val radius = width / 2f
            val relX = point.x * radius
            val relY = point.y * radius
            canvas.drawLine(center.x, center.y,
                center.x + relX, center.y + relY, paint)
        }
    }

    // The selection info TextBox
    // TODO: move TextBox to the center if it wouldn't fit into the View
    private fun drawSelectionTextBox(canvas: Canvas) {
        if (selectedIndex == -1) return

        canvas.save()
        canvas.translate(textBoxTranslation.x, textBoxTranslation.y)

        canvas.drawRoundRect(textBoxRect, 10f, 10f, selectionTextBoxPaint)
        canvas.drawText(titleText, textBoxRect.right / 2, titleBaseLine, selectionTitlePaint)
        canvas.drawText(descText, textBoxRect.right / 2, descBaseLine, selectionDescPaint)

        canvas.restore()
    }



    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_UP)
            return true

        val index = getSegmentIndex(event.x, event.y)
        if (index == -1 || index == selectedIndex)
            selectedIndex = -1
        else
            selectedIndex = index

        invalidate()
        return true
    }

    /**
     * Checks on which segment the point lays and returns its index.
     * If the point is outside of the segments returns -1.
     */
    private fun getSegmentIndex(x: Float, y: Float) : Int {
        val index = getDirectionIndex(x, y)
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

    /**
     * Returns the segments index in which segments angle the given Point is.
     * Does not check whether the Point is actually in the returned segment.
     */
    private fun getDirectionIndex(x: Float, y: Float) : Int{
        val dx = center.x - x
        val dy = center.y - y
        var touchAngle = atan(dy / dx)

        // atan only returns -PI/2 to PI/2
        if (dx > 0)
            touchAngle += PI.toFloat()

        // finding the segment within which the touched angle lays
        var startAngle = STARTING_ANGLE
        for (index in 0 until data.size) {
            val segmentAngle = fractions[index] * 2 * PI.toFloat()
            if (startAngle <= touchAngle &&
                touchAngle <= startAngle + segmentAngle) {
                return index
            }
            startAngle += segmentAngle
        }

        return -1
    }

}