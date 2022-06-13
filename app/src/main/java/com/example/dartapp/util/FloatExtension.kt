package com.example.dartapp.util

fun Float.replaceNaN(with: Float): Float {
    return if (this.isNaN()) with else this
}