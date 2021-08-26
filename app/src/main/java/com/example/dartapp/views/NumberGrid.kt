package com.example.dartapp.views


import android.content.Context
import android.net.LinkAddress
import android.os.Build
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TableLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.solver.widgets.Rectangle
import androidx.core.view.marginLeft
import com.example.dartapp.R

@RequiresApi(Build.VERSION_CODES.M)
class NumberGrid @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    val rows = 4
    val columns = 3

    val spaceHorizontally = 5
    val spaceVertically = 5

    private lateinit var rowLayouts: List<LinearLayout>
    private lateinit var buttons: List<Button>

    init {
        this.orientation = TableLayout.VERTICAL
        this.weightSum = rows * 1.0f


        for (rowIndex in 0 until rows) {
            val rowLayout = LinearLayout(context)
            rowLayout.orientation = LinearLayout.HORIZONTAL
            rowLayout.weightSum = columns * 1.0f

            for (columnIndex in 0 until columns) {
                val button = Button(context, attrs)
                button.text = getButtonText(rowIndex, columnIndex)
                rowLayout.addView(button)

                var params = button.layoutParams as LinearLayout.LayoutParams
                params.weight = 1f
                params.height = LayoutParams.MATCH_PARENT

                params.leftMargin = spaceHorizontally
                params.rightMargin = spaceHorizontally
                params.topMargin = spaceVertically
                params.bottomMargin = spaceVertically
            }

            this.addView(rowLayout)
            (rowLayout.layoutParams as LinearLayout.LayoutParams).weight = 1f
        }

    }

    private fun getButtonText(rowIndex: Int, columnIndex: Int) : String {
        if (rowIndex < 3) {
            val number = 7 + columnIndex - rowIndex * 3
            return number.toString()
        }

        if (columnIndex == 0)
            return context.getString(R.string.clear)
        if (columnIndex == 1)
            return "0"

        return context.getString(R.string.confirm)
    }

}