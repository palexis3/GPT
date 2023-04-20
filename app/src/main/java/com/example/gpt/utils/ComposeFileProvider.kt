package com.example.gpt.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.gpt.BuildConfig
import com.example.gpt.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

class ComposeFileProvider : FileProvider(
    R.xml.file_paths
) {
    companion object {
        fun getFilUri(context: Context): Uri {
            val file = context.createImageFile()

            return getUriForFile(
                Objects.requireNonNull(context),
                BuildConfig.APPLICATION_ID + ".provider",
                file
            )
        }
    }
}

fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "PNG_" + timeStamp + "_"

    return File.createTempFile(
        imageFileName,
        ".png",
        externalCacheDir
    )
}
