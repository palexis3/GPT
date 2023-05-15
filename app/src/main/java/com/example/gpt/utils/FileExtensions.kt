package com.example.gpt.utils

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Context.createAudioFilePath(): String {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val prefixAudioFileName = timeStamp + "_"

   val audioFile = File.createTempFile(
       prefixAudioFileName,
        ".mp4",
        cacheDir
    ).apply { createNewFile() }

    return audioFile.path
}
