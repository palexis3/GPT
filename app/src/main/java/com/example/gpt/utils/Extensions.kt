package com.example.gpt.utils

import android.content.Context
import android.net.Uri
import android.util.Base64
import timber.log.Timber


fun String.mask(): String {
    val numOfCharsToMask = if (this.isNotEmpty() && length > 5) {
        5
    } else {
        0
    }
    return replaceRange(numOfCharsToMask, length, "*".repeat(length - numOfCharsToMask))
}
