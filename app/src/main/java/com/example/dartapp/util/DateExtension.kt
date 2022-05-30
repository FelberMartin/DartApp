package com.example.dartapp.util

import java.text.SimpleDateFormat
import java.util.*

fun Date.weekDayString() : String {
    return SimpleDateFormat("EE").format(this)
}

fun Date.timeString() : String {
    return SimpleDateFormat("HH:mm").format(this)
}

fun Date.compactDateString() : String {
    return SimpleDateFormat("dd/MM/YY").format(this)
}

fun Date.dateString() : String {
    return SimpleDateFormat.getDateInstance().format(this)
}

