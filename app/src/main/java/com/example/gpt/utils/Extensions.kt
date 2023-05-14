package com.example.gpt.utils

fun String.mask(): String {
    val numOfCharsToMask = if (this.isNotEmpty() && length > 5) {
        5
    } else {
        0
    }
    return replaceRange(numOfCharsToMask, length, "*".repeat(length - numOfCharsToMask))
}
