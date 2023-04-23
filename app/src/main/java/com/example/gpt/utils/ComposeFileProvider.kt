package com.example.gpt.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.content.FileProvider.getUriForFile
import com.example.gpt.BuildConfig
import com.example.gpt.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import timber.log.Timber

class ComposeFileProvider : FileProvider(
    R.xml.file_paths
)

fun Context.getFileUri(file: File): Uri {
    return getUriForFile(
        this,
        BuildConfig.APPLICATION_ID + ".provider",
        file
    )
}

fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "PNG_" + timeStamp + "_"

    return File.createTempFile(
        imageFileName,
        ".png",
        cacheDir
    ).apply { createNewFile() }
}

fun Context.convertUriToFile(uri: Uri): File {
    var inputStream: InputStream? = null
    var outputStream: OutputStream? = null

    try {
        val file = createImageFile()
        inputStream = this.contentResolver.openInputStream(uri)
        outputStream = FileOutputStream(file)

        val buffer = ByteArray(1024)
        var length = inputStream?.read() ?: 0

        while (length > 0) {
            outputStream.write(buffer, 0, length)
            length = inputStream?.read() ?: 0
        }

        return file
    } catch (ex: Exception) {
        Timber.tag("XXX-convertUriToFile").d("exception: $ex")
        return File("")
    } finally {
        outputStream?.close()
        inputStream?.close()
    }
}

fun File?.toMaskedBitmap(): Bitmap? {
    var bitmap: Bitmap? = null
    try {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        bitmap = BitmapFactory.decodeStream(FileInputStream(this), null, options)
        bitmap?.setHasAlpha(true)
    } catch (ex: Exception) {
        Timber.tag("XXX-toBitmap").d("exception: $ex")
    }
    return bitmap
}

fun Context.bitmapToFile(bitmap: Bitmap?): File? {
    return try {
        val file = createImageFile()
        val outputStream = FileOutputStream(file)

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 0, bos)
        val byteArray = bos.toByteArray()

        outputStream.write(byteArray)
        outputStream.flush()
        outputStream.close()

        file
    } catch (ex: Exception) {
        Timber.tag("XXX-convertUriToFile").d("exception: $ex")
        null
    }
}

// Can't allow an image file to be larger than 4MB or null
fun File?.fileTooLargeOrNull(): Boolean {
    return this?.let {
        val size = it.length().toDouble() / 1024 / 1024
        Log.d("XXX-FileExtension", "imageSize: ${it.length()} sizeCalculated: $size")
        size > 4
    } ?: true
}
