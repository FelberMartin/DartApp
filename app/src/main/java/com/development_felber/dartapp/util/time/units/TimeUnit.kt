package com.development_felber.dartapp.util.time

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

abstract class TimeUnit(
    private val uniquePattern: String,
    private val uiPattern: String
 ) {

    private val uiFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(uiPattern)
    private val uniqueFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(uniquePattern)


    open fun toUiString(dateTime: LocalDateTime): String {
        return dateTime.format(uiFormatter)
    }

    open fun toUniqueString(dateTime: LocalDateTime): String {
        return dateTime.format(uniqueFormatter)
    }

    abstract fun afterGoingBack(unitCount: Int, dateTime: LocalDateTime = LocalDateTime.now()): LocalDateTime
}



