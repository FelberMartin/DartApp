package com.development_felber.dartapp.util

fun Float.replaceNaN(with: Float): Float {
    return if (this.isNaN()) with else this
}