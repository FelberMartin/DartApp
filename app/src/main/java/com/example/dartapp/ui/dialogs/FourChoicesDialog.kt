package com.example.dartapp.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import com.example.dartapp.R
import com.example.dartapp.views.numbergrid.NumberGrid
import com.example.dartapp.views.numbergrid.NumberGridDelegate

open class FourChoicesDialog(context: Context) : Dialog(context), NumberGridDelegate {

    protected var titleText = "Title"
    protected var infoText = "Info"

    protected lateinit var numberGrid: NumberGrid
    private var listener: ((Int) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setContentView(R.layout.dialog_four_choices)

        numberGrid = findViewById(R.id.numberGrid)
        numberGrid.delegate = this

        val title = findViewById<TextView>(R.id.titleLabel)
        title.text = titleText

        val info = findViewById<TextView>(R.id.infoLabel)
        info.text = infoText
    }

    fun setClickListener(listener: (Int) -> Unit) {
        this.listener = listener
    }

    override fun numberUpdated(value: Int) {
        listener?.invoke(value)
    }

}

