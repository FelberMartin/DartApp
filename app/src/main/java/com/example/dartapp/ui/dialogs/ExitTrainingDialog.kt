package com.example.dartapp.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import com.example.dartapp.R

class ExitTrainingDialog(context: Context) : Dialog(context) {

    private lateinit var cancelButton: Button
    private lateinit var exitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_exit_training)

        cancelButton = findViewById(R.id.cancelButton)
        exitButton = findViewById(R.id.exitButton)

        cancelButton.setOnClickListener { this.dismiss() }
    }


    fun setOnExitClickedListener(listener: (View) -> Unit) {
        exitButton.setOnClickListener {
            this.dismiss()
            listener.invoke(it)
        }
    }
}