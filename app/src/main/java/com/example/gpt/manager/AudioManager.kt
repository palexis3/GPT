package com.example.gpt.manager

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import java.io.File
import java.io.IOException
import timber.log.Timber

class AudioManager(private var context: Context, private var filePath: String) {

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null

    fun startRecording(): Boolean {
        return try {
            mediaRecorder =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    MediaRecorder(context)
                } else MediaRecorder()

            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder?.setAudioSamplingRate(48000)
            mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder?.setOutputFile(filePath)
            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)

            mediaRecorder?.prepare()
            mediaRecorder?.start()
            true
        } catch (e: IOException) {
            Timber.d("startRecording failed IOException: $e}")
            false
        } catch (e: IllegalStateException) {
            Timber.d("startRecording failed IllegalStateException: $e")
            false
        }
    }

    fun stopRecording() {
        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaRecorder = null
    }

    fun startPlayback(): Boolean {
        return try {
            if (File(filePath).exists()) {
                val audioUri = Uri.parse(filePath)
                mediaPlayer = MediaPlayer.create(context, audioUri).apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    start()
                }
                true
            } else {
                Timber.d("startPlayback filePath non-existent: filePath: $filePath")
                false
            }
        } catch (e: Exception) {
            Timber.d("startPlayback failed message: $e exception: ${e.message}")
            false
        }
    }

    fun stopPlayback() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
