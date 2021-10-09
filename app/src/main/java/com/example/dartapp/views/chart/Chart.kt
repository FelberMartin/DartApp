package com.example.dartapp.views.chart

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View


abstract class Chart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var colorManager = ColorManager()

    var data: DataSet = DataSet()
        set(value) {
            field = value
            dataChanged()
        }

    abstract fun dataChanged()



}