package com.example.gpt.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.foundation.lazy.LazyListState
import java.io.ByteArrayOutputStream


fun LazyListState.isScrolledToEnd() =
    layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 2

fun LazyListState.lastItemPosition() : Float =
    layoutInfo.visibleItemsInfo.lastOrNull()?.size?.toFloat() ?: 0F

fun String.mask() =
    if (length <= 3) {
        replaceRange(1, length, "*".repeat(length - 1))
    } else {
        replaceRange(3, length, "*".repeat(length - 3))
    }


fun Uri.toBase64String(context: Context): String {
    val inputStream = context.contentResolver.openInputStream(this)
    val outputStream = ByteArrayOutputStream()
    val bitmapOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }

    val bitmap = BitmapFactory.decodeStream(inputStream, null, bitmapOptions)
    bitmap?.compress(Bitmap.CompressFormat.PNG, 0, outputStream)
    val byteArray: ByteArray = outputStream.toByteArray()

    inputStream?.close()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}