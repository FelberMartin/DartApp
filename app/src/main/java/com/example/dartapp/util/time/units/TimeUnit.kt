package com.example.dartapp.util.time

abstract class TimeUnit(
 ) {
    abstract fun toUiString(milliseconds: Long): String
    abstract fun toIndex(milliseconds: Long): Int
    abstract fun toStartOfUnit(milliseconds: Long): Long
    abstract fun millisAfterGoingBack(unitCount: Int, milliseconds: Long = System.currentTimeMillis()): Long
}



