package com.example.dartapp.views.numbergrid

interface NumberGridDelegate {
    fun onConfirmPressed(value: Int)
    fun numberUpdated(value: Int)
}