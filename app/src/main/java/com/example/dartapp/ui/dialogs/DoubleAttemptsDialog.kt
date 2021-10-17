package com.example.dartapp.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.LinearLayout
import com.example.dartapp.R
import com.example.dartapp.views.numbergrid.NumberGrid
import com.example.dartapp.views.numbergrid.NumberGridDelegate

class DoubleAttemptsDialog(context: Context) : Dialog(context), NumberGridDelegate {

    private lateinit var numberGrid: NumberGrid
    private var listener: ((Int) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setContentView(R.layout.dialog_double_attempts)

        numberGrid = findViewById(R.id.numberGrid)
        numberGrid.delegate = this

    }

    fun setClickListener(listener: (Int) -> Unit) {
        this.listener = listener
    }

    override fun numberUpdated(value: Int) {
        listener?.invoke(value)
    }

}