package com.example.gpt.utils

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.compose.foundation.lazy.LazyListState
import timber.log.Timber


fun LazyListState.isScrolledToEnd() =
    layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 2

fun LazyListState.lastItemPosition(): Float =
    layoutInfo.visibleItemsInfo.lastOrNull()?.size?.toFloat() ?: 0F

fun String.mask() =
    if (length <= 3) {
        replaceRange(1, length, "*".repeat(length - 1))
    } else {
        replaceRange(3, length, "*".repeat(length - 3))
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
