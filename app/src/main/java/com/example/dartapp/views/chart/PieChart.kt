package com.example.dartapp.views.chart

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import com.example.dartapp.R
import com.example.dartapp.views.chart.util.DataSet
import com.example.dartapp.views.chart.util.InfoTextBox
import com.example.dartapp.views.chart.util.getAttrColor
import com.google.android.material.color.MaterialColors
import kotlin.math.*

// How much the selected circle segment should stand out from the center
private const val SELECTION_PROTRUDE = 20f

// Where the first segment begins
private const val STARTING_ANGLE_GRAD = -90f
private const val STARTING_ANGLE= - PI / 2


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

    private var info = InfoTextBox(this)
    private lateinit var textBoxTranslation: PointF


    private val backgroundColor = MaterialColors.getColor(this, R.attr.backgroundColor)

    private val paint: Paint = Paint().apply {
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 14f
    }

    var shownMaxAngle = STARTING_ANGLE_GRAD + 360f
    override var enterAnimation = ObjectAnimator.ofFloat(
        this, "shownMaxAngle", STARTING_ANGLE_GRAD, 360f + STARTING_ANGLE_GRAD
    ). apply {
        duration = enterAnimationDuration
        interpolator = AccelerateDecelerateInterpolator()
        addUpdateListener(this@PieChart)
        if (!isInEditMode) start()
    }


    init {
        data = DataSet.Generator.random(type = DataSet.Type.STRING, count=3)
        if (isInEditMode) {
            selectedIndex = 1
        }


    }



    override fun reload() {
        super.reload()

        data.forEach{ dp -> assert(dp.y.toDouble() >= 0) }
        dataSum = data.sumOf { dp -> dp.y.toDouble() }.toFloat()

        fractions = ArrayList()
        data.forEach { dp -> fractions.add(dp.y.toFloat() / dataSum) }

        recalculatePoints()
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

    // Updates the Selection TextBox's title and description text
    override fun onSelectionUpdate() {
        if (selectedIndex == -1) return

        info.title = data[selectedIndex].xString(data.dataPointXType)

        val percent = fractions[selectedIndex] * 100f
        val value = data[selectedIndex].yString()
        info.description = String.format("%s (%.0f%%)", value, percent)

        info.update()
        updateTextBoxTranslation()
        info.fitInto(RectF(0f, 0f, width.toFloat(), height.toFloat()), textBoxTranslation)
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
            val animatedSweepAngle = max(0f, shownMaxAngle - startAngle)
            val shownSweepAngle = min(sweepAngle, animatedSweepAngle)
            paint.color = colorManager.get(index)

            // let the selected arc protrude from the center
            if (index == selectedIndex) {
                canvas.save()
                val dx = middlePoints[index].x * SELECTION_PROTRUDE
                val dy = middlePoints[index].y * SELECTION_PROTRUDE
                canvas.translate(dx, dy)
            }

            canvas.drawArc(circleRect, startAngle, shownSweepAngle, true, paint)

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

        info.draw(canvas)

        canvas.restore()
    }



    /**
     * Checks on which segment the point lays and returns its index.
     * If the point is outside of the segments returns -1.
     */
    override fun getTouchedIndex(x: Float, y: Float) : Int {
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