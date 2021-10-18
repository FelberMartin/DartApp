package com.example.dartapp.ui.dialogs

import android.content.Context
import com.example.dartapp.R
import com.example.dartapp.util.Strings

class DoubleAttemptsDialog(context: Context) : FourChoicesDialog(context) {

    init {
        titleText = Strings.get(R.string.double_attempts_question)
        infoText = Strings.get(R.string.double_attempts_explanation)
    }
}