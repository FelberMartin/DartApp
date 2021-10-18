package com.example.dartapp.ui.dialogs

import android.content.Context
import android.os.Bundle
import com.example.dartapp.R
import com.example.dartapp.util.Strings

class CheckoutDialog(context: Context) : FourChoicesDialog(context) {

    init {
        titleText = Strings.get(R.string.checkout_question)
        infoText = Strings.get(R.string.checkout_explanation)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        numberGrid.disableButtonAt(0)
    }
}