package com.example.dartapp.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import com.example.dartapp.R

class LegFinishedDialog(context: Context) : Dialog(context) {

    private lateinit var backButton: Button
    private lateinit var restartButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setContentView(R.layout.dialog_game_over)

        backButton = findViewById(R.id.backButton)
        restartButton = findViewById(R.id.restartButton)
    }


    fun setOnBackClickedListener(listener: (View) -> Unit) {
        backButton.setOnClickListener {
            this.dismiss()
            listener.invoke(it)
        }
    }

    fun setOnRestartClickedListener(listener: (View) -> Unit) {
        restartButton.setOnClickListener {
            this.dismiss()
            listener.invoke(it)
        }
    }
}