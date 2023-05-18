package com.example.gpt.utils

import android.content.Context
import android.content.pm.PackageManager
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Context.createAudioFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val prefixAudioFileName = timeStamp + "_"

   return File.createTempFile(
       prefixAudioFileName,
        ".mp4",
        cacheDir
    ).apply { createNewFile() }
}

fun fileIsNotEmpty(file: File): Boolean {
    val inputStream = FileInputStream(file)
    return inputStream.channel.size() != 0L
}

fun Context.hasMicrophone(): Boolean {
    val packageManager = this.packageManager
    return packageManager.hasSystemFeature(
        PackageManager.FEATURE_MICROPHONE
    )
}

