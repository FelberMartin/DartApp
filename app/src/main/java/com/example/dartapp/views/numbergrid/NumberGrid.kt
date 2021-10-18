package com.example.dartapp.views.numbergrid


import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TableLayout
import androidx.annotation.RequiresApi
import com.example.dartapp.R
import com.example.dartapp.util.Strings
import com.google.android.material.button.MaterialButton


@RequiresApi(Build.VERSION_CODES.M)
class NumberGrid @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var config: NumberGridConfig

    private val spaceHorizontally = 5
    private val spaceVertically = 5

    private lateinit var rowLayouts: List<LinearLayout>
    private lateinit var buttons: List<Button>
    private var confirmTile: ConfirmNoScoreTile? = null


    var number = 0
        set(value) {
            field = value
            numberUpdated()
        }

    var delegate: NumberGridDelegate? = null


    init {
        config = parseConfig()
        initLayout()
    }

    private fun parseConfig() : NumberGridConfig {
        val default = 1
        var enumValue = default

        if (attrs != null) {
            val a = context
                .obtainStyledAttributes(
                    attrs,
                    R.styleable.NumberGrid,
                    0, 0
                )

            enumValue = a.getInt(R.styleable.NumberGrid_layoutConfig, default)
            a.recycle()
        }

        return when (enumValue) {
            1 -> NumberGridConfig.default3x4()
            2 -> NumberGridConfig.simple2x2()
            else -> NumberGridConfig.default3x4()
        }
    }

    private fun initLayout() {
        val rows = config.rows
        val columns = config.columns

        this.orientation = TableLayout.VERTICAL
        this.weightSum = rows * 1f

        for (rowIndex in 0 until rows) {
            val rowLayout = LinearLayout(context).apply { id = generateViewId() }
            rowLayout.orientation = LinearLayout.HORIZONTAL
            rowLayout.weightSum = columns * 1f

            for (columnIndex in 0 until columns) {
                val tile = config.tileAt(rowIndex, columnIndex)
                val button = MaterialButton(context)
                button.text = tile.text
                button.id = generateViewId()
                rowLayout.addView(button)

                if (tile is ConfirmNoScoreTile) {
                    confirmTile = tile
                    confirmTile!!.button = button
                }

                button.setOnClickListener { onButtonPress(tile) }
                applyButtonLayout(button)
            }

            this.addView(rowLayout)
            var params = rowLayout.layoutParams as LinearLayout.LayoutParams
            params.weight = 1f
            params.height = LayoutParams.MATCH_PARENT
        }
    }


    private fun onButtonPress(tile: Tile) {
        if (tile is DigitTile)
            onNumberPress((tile as DigitTile).digit)
        else if (tile is ActionTile)
            onActionPress((tile as ActionTile).action)
    }

    private fun onNumberPress(digit: Int) {
        val newNumber = number * 10 + digit

        // avoid overflows
        if (newNumber >= number)
            number = newNumber
    }

    private fun onActionPress(action: ActionTile.Action) {
        if (action == ActionTile.Action.CONFIRM) {
            delegate?.onConfirmPressed(number)
        } else if (action == ActionTile.Action.CLEAR) {
            number = 0
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

    private fun numberUpdated() {
        confirmTile?.updateDynamicText(number)
        delegate?.numberUpdated(number)
    }

    /**
     * Save the number typed into the grid on e.g. screen rotations or theme changes.
     * See: https://stackoverflow.com/a/8127813/13366254
     */
    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable("superState", super.onSaveInstanceState())
        bundle.putInt("number", this.number) // ... save stuff

        return bundle
    }


    /**
     * Restore the number entered before recreating the view
     */
    override fun onRestoreInstanceState(state: Parcelable?) {
        var superState = state
        if (state is Bundle) { // implicit null check
            val bundle = state
            this.number = bundle.getInt("number") // ... load stuff
            superState = bundle.getParcelable("superState")
        }
        super.onRestoreInstanceState(superState)
    }



}