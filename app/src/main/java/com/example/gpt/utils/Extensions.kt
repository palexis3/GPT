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


fun Uri.toBase64String(context: Context): String {
    return try {
        val bytes = context.contentResolver.openInputStream(this)?.readBytes()
        Base64.encodeToString(bytes, Base64.DEFAULT)
    } catch (ex: Exception) {
        Timber.tag("XXX-toBase64String").d("exception: %s", ex.printStackTrace())
        ""
    }
}
