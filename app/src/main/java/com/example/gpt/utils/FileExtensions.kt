package com.example.gpt.utils

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "mp3" + timeStamp + "_"

    return File.createTempFile(
        imageFileName,
        ".mp3",
        externalCacheDir
    )
}
