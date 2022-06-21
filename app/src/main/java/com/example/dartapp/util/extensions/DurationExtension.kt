package com.example.dartapp.util.extensions

import java.time.Duration

/**
 * Returns a more readable string, compared to the ISO-formatted toString() method.
 * Uses the kotlin.time.Duration#toString method.
 */
fun Duration.toPrettyString(): String {
    return kotlin.time.Duration.parse(this.toString()).toString()
}