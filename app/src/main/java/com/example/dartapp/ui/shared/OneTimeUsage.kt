package com.example.dartapp.ui.shared

class OneTimeUsage<T> (private val oneTimeObject: T){

    var used = false
        private set

    fun use(usage: (T) -> Unit) {
        if (!used) {
            usage.invoke(oneTimeObject)
            used = true
        }
    }
}