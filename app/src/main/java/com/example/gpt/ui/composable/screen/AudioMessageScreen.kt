package com.example.gpt.ui.composable.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gpt.R
import com.example.gpt.ui.viewmodel.AudioViewModel
import com.example.gpt.utils.createImageFile
import java.io.IOException
import timber.log.Timber

@Composable
fun AudioMessageScreen(
    audioViewModel: AudioViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val file by remember { mutableStateOf(context.createImageFile()) }
    val dataSource = "android.resource://com.example.gpt/raw/${file.name}"

    Log.d("XXX-AudioMessageScreen", "file: $file filePath: ${file.path} fileName: ${file.name}")

    var player: MediaPlayer? = null
    var recorder: MediaRecorder? = null

    var startPlaying by remember { mutableStateOf(false) }
    var startRecording by remember { mutableStateOf(false) }

    val permissionCheckResult = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { success ->
            if (success) {
                startRecording(
                    recorderOnCalled = { mediaRecorder ->
                        Log.d("XXX-AudioMessageScreen", "startRecording recorderOnCalled: $mediaRecorder recorder: $recorder")
                        if (recorder == null) {
                            recorder = mediaRecorder
                        }
                    },
                    file.path,
                    context
                )
            } else {
                Toast.makeText(context, R.string.audio_permission_denied, Toast.LENGTH_SHORT).show()
            }
        })

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        ShowPlayButton(
            context = context,
            startPlaying = startPlaying,
            onPlay = { shouldPlay ->
                startPlaying = shouldPlay

                if (shouldPlay) {
                    startPlaying(
                        playerOnCalled = { mediaPlayer ->
                            Log.d("XXX-AudioMessageScreen", "startPlaying playerOnCalled: $mediaPlayer player: $player")
                            if (player == null) {
                                player = mediaPlayer
                            }
                        },
                        file.path
                    )
                } else {
                    stopPlaying(player)
                }
            })

        ShowRecordButton(
            context = context,
            startRecording = startRecording,
            onRecord = { shouldRecord ->
                startRecording = shouldRecord

                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                    if (shouldRecord) {
                        startRecording(
                            recorderOnCalled = { mediaRecorder ->
                                if (recorder == null) {
                                    recorder = mediaRecorder
                                }
                                Log.d("XXX-AudioMessageScreen", "startRecording recorderOnCalled: $mediaRecorder recorder: $recorder")
                            },
                            file.path,
                            context
                        )
                    } else {
                        stopRecording(recorder)
                    }
                } else {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            })
    }
}

private fun startPlaying(playerOnCalled: (MediaPlayer) -> Unit, dataSource: String) {
    playerOnCalled(
        MediaPlayer().apply {
            try {
                setDataSource(dataSource)
                prepare()
                start()
            } catch (e: IOException) {
                Timber.d("startPlaying failed exception: $e")
            }
        }
    )
}

private fun stopPlaying(player: MediaPlayer?) {
    player?.stop()
    player?.release()
}

private fun startRecording(recorderOnCalled: (MediaRecorder) -> Unit, dataSource: String, context: Context) {
    recorderOnCalled(
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> MediaRecorder(context)
            else -> MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(dataSource)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Timber.d("startRecording failed exception: $e")
            }

            start()
        }
    )
}

private fun stopRecording(recorder: MediaRecorder?) {
    recorder?.stop()
    recorder?.release()
}

@Composable
fun ShowPlayButton(
    context: Context,
    onPlay: (Boolean) -> Unit,
    startPlaying: Boolean
) {
    Button(
        enabled = hasMicrophone(context),
        onClick = {
            onPlay(!startPlaying)
        }) {
        Icon(
            imageVector = when (startPlaying) {
                true -> Icons.Filled.Done
                false -> Icons.Filled.PlayArrow
            },
            contentDescription = "Play"
        )
        Text(
            text = when (startPlaying) {
                true -> "Stop playing"
                false -> "Start playing"
            }
        )
    }
}

@Composable
fun ShowRecordButton(
    context: Context,
    onRecord: (Boolean) -> Unit,
    startRecording: Boolean
) {

    Button(
        enabled = hasMicrophone(context),
        onClick = {
            onRecord(!startRecording)
        }) {
        Icon(
            imageVector = when (startRecording) {
                true -> Icons.Filled.Done
                false -> Icons.Filled.PlayArrow
            },
            contentDescription = "Play"
        )
        Text(
            text = when (startRecording) {
                true -> "Stop recording"
                false -> "Start recording"
            }
        )
    }
}

private fun hasMicrophone(context: Context): Boolean {
    val packageManager = context.packageManager
    return packageManager.hasSystemFeature(
        PackageManager.FEATURE_MICROPHONE
    )
}
