package com.example.gpt.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Base64OutputStream
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.content.FileProvider.getUriForFile
import com.example.gpt.BuildConfig
import com.example.gpt.R
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.experimental.and
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber


private val MAX_FILE_SIZE = 4_194_304L
private const val MAX_IMAGE_DIMENSION = 1024

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


// To get the processed value, we'll convert the uri to bitmap to compress and set the width = height
// then convert the bitmap to an input stream to then return a file
suspend fun processUriToFile(uri: Uri, context: Context): File = withContext(Dispatchers.IO) {
    val file = context.createImageFile()

//    val inputStream = context.contentResolver.openInputStream(uri)

//    val byteArray: ByteArray = bitmapToRgba(bitmap)
//    val rgbaBitmap = bitmapFromRgba(MAX_IMAGE_DIMENSION, MAX_IMAGE_DIMENSION, byteArray)
//    rgbaBitmap?.setHasAlpha(true)

    val bitmap: Bitmap = convertImageUriToBitmap(context, uri)
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(CompressFormat.PNG, 0, outputStream)
    val bitmapData = outputStream.toByteArray()
    val inputStream = ByteArrayInputStream(bitmapData)

    Log.d(
        "XXX-processUriToFile",
        "bitmap config: ${bitmap.config}"
    )

    inputStream.use { input ->
        FileOutputStream(file).use { output ->
            val buffer = ByteArray(1024)
            var read: Int = input.read(buffer)

            while (read != -1) {
                try {
                    output.write(buffer, 0, read)
                    read = input.read(buffer)
                } catch (e: CancellationException) {
                    file.deleteRecursively()
                }
            }
        }
    }

//    val compressedFile = Compressor.compress(context, file) {
//        format(Bitmap.CompressFormat.PNG)
//        size(maxFileSize = MAX_FILE_SIZE)
//        resolution(1024, 1024)
//    }

    val tempBitmap = file.toMaskedBitmap()

    Log.d(
        "XXX-processUriToFile",
        "tempBitmap width: ${tempBitmap?.width} height: ${tempBitmap?.height} config: ${tempBitmap?.config}"
    )

    return@withContext file
}

private fun convertImageUriToBitmap(context: Context, imageUri: Uri): Bitmap {
    val bitmap = when {
        Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
            context.contentResolver,
            imageUri
        )
        else -> {
            val source = ImageDecoder.createSource(context.contentResolver, imageUri)
            ImageDecoder.decodeBitmap(source)
        }
    }

    // Note: The image must be scaled where the height and width are the same but there's a
    // limit on the size of the image.
//    val height = bitmap.height
//    val width = bitmap.width
//    val maxOfTwo = height.coerceAtLeast(width)
//    val dimension = MAX_IMAGE_DIMENSION.coerceAtMost(maxOfTwo)

    return Bitmap.createScaledBitmap(bitmap, MAX_IMAGE_DIMENSION, MAX_IMAGE_DIMENSION, true)
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
//        options.inPreferredConfig = if (Build.VERSION.SDK_INT >= 33) {
//            Bitmap.Config.RGBA_1010102
//        } else if (Build.VERSION.SDK_INT >= 26) {
//            Bitmap.Config.RGBA_F16
//        } else Bitmap.Config.RGB_565

        bitmap = BitmapFactory.decodeStream(FileInputStream(this), null, options)
    } catch (ex: Exception) {
        Timber.tag("XXX-toBitmap").d("exception: $ex")
    }
    return bitmap
}

fun bitmapFromRgba(width: Int, height: Int, bytes: ByteArray): Bitmap? {
    val pixels = IntArray(bytes.size / 4)
    var j = 0
    for (i in pixels.indices) {
        val R: Int = (bytes[j++] and 0xff.toByte()).toInt()
        val G: Int = (bytes[j++] and 0xff.toByte()).toInt()
        val B: Int = (bytes[j++] and 0xff.toByte()).toInt()
        val A: Int = (bytes[j++] and 0xff.toByte()).toInt()
        val pixel = A shl 24 or (R shl 16) or (G shl 8) or B
        pixels[i] = pixel
    }
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return bitmap
}

fun bitmapToRgba(bitmap: Bitmap): ByteArray {
//    require(bitmap.config == Bitmap.Config.ARGB_8888) { "Bitmap must be in ARGB_8888 format" }
    val pixels = IntArray(bitmap.width * bitmap.height)
    val bytes = ByteArray(pixels.size * 4)
    bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
    var i = 0
    for (pixel in pixels) {
        // Get components assuming is ARGB
        val A = pixel shr 24 and 0xff
        val R = pixel shr 16 and 0xff
        val G = pixel shr 8 and 0xff
        val B = pixel and 0xff
        bytes[i++] = R.toByte()
        bytes[i++] = G.toByte()
        bytes[i++] = B.toByte()
        bytes[i++] = A.toByte()
    }
    return bytes
}


fun bitmapToFile(file: File?, bitmap: Bitmap?): File? {
    val outputStream = ByteArrayOutputStream()
    bitmap?.compress(CompressFormat.PNG, 0, outputStream)
    val bitmapData = outputStream.toByteArray()
    val inputStream = ByteArrayInputStream(bitmapData)

    inputStream.use { input ->
        FileOutputStream(file).use { output ->
            val buffer = ByteArray(1024)
            var read: Int = input.read(buffer)

            while (read != -1) {
                try {
                    output.write(buffer, 0, read)
                    read = input.read(buffer)
                } catch (e: CancellationException) {
                    file?.deleteRecursively()
                }
            }
        }
    }

    return file
}

// Can't allow an image file to be larger than 4MB or null
fun File?.fileTooLargeOrNull(): Boolean {
    return this?.let {
        val size = it.length().toDouble() / 1024 / 1024
        Log.d("XXX-FileExtension", "imageSize: ${it.length()} sizeCalculated: $size")
        size > 4
    } ?: true
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

fun File?.toBase64String(): String {
    return ByteArrayOutputStream().use { outputStream ->
        Base64OutputStream(outputStream, Base64.DEFAULT).use { base64FilterStream ->
            this?.inputStream().use { inputStream ->
                inputStream?.copyTo(base64FilterStream) ?: ""
            }
        }
        return@use outputStream.toString()
    }
}
