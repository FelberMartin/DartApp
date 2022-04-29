package com.example.dartapp.util

import java.text.SimpleDateFormat
import java.util.*

fun Date.weekDay() : String {
    return SimpleDateFormat("EE").format(this)
}

fun Date.timeString() : String {
    return SimpleDateFormat("HH:mm").format(this)
}

fun Date.dateString() : String {
    return SimpleDateFormat.getDateInstance().format(this)
}