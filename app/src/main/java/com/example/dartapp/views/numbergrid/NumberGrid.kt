package com.example.dartapp.views.numbergrid


import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TableLayout
import androidx.annotation.RequiresApi
import com.example.dartapp.R
import com.google.android.material.button.MaterialButton

@RequiresApi(Build.VERSION_CODES.M)
class NumberGrid @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val rows = 4
    private val columns = 3

    private val spaceHorizontally = 5
    private val spaceVertically = 5

    private lateinit var rowLayouts: List<LinearLayout>
    private lateinit var buttons: List<Button>


    var number = 0
        set(value) {
            delegate?.numberUpdated(value)
            field = value
        }

    var delegate: NumberGridDelegate? = null


    init {
        this.orientation = TableLayout.VERTICAL
        this.weightSum = rows * 1.0f


        for (rowIndex in 0 until rows) {
            val rowLayout = LinearLayout(context)
            rowLayout.orientation = LinearLayout.HORIZONTAL
            rowLayout.weightSum = columns * 1.0f

            for (columnIndex in 0 until columns) {
                val button = MaterialButton(context, attrs)
                button.text = getButtonText(rowIndex, columnIndex)
                button.id
                rowLayout.addView(button)

                setButtonListener(button)
                applyButtonLayout(button)
            }

            this.addView(rowLayout)
            var params = rowLayout.layoutParams as LinearLayout.LayoutParams
            params.weight = 1f
            params.height = LayoutParams.MATCH_PARENT
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

    private fun setButtonListener(button: Button) {
        val text = button.text

        // digit
        if (text[0].isDigit()) {
            button.setOnClickListener {
                val digit = Integer.parseInt(text.toString())
                val newNumber = number * 10 + digit

                // avoid overflows and leading zeros
                if (newNumber > number)
                    number = newNumber

            }
        }

        // clear
        else if (text == resources.getString(R.string.clear)) {
            button.setOnClickListener { number = 0 }
        }

        //confirm
        else {
            button.setOnClickListener {
                delegate?.onConfirmPressed(number)
            }
        }

    }

    private fun applyButtonLayout(button: Button) {
        var params = button.layoutParams as LinearLayout.LayoutParams
        params.weight = 1f
        params.width = LayoutParams.MATCH_PARENT
        params.height = LayoutParams.MATCH_PARENT

        params.leftMargin = spaceHorizontally
        params.rightMargin = spaceHorizontally
        params.topMargin = spaceVertically
        params.bottomMargin = spaceVertically
    }



}