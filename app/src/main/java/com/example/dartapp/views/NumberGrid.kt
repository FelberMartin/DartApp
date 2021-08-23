package com.example.dartapp.views


import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TableLayout
import com.example.dartapp.R

class NumberGrid @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    val rows = 4
    val columns = 3

    private lateinit var rowLayouts: List<LinearLayout>
    private lateinit var buttons: List<Button>

    init {
        this.orientation = TableLayout.VERTICAL
        for (rowIndex in 0 until rows) {
            val rowLayout = LinearLayout(context)
            rowLayout.orientation = LinearLayout.HORIZONTAL

            for (columnIndex in 0 until columns) {
                val button = Button(context, attrs)
                button.text = getButtonText(rowIndex, columnIndex)
                rowLayout.addView(button)
            }

            this.addView(rowLayout)
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