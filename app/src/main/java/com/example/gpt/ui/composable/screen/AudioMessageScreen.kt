package com.example.gpt.ui.composable.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import com.example.gpt.manager.AudioManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gpt.R
import com.example.gpt.ui.theme.MediumPadding
import com.example.gpt.ui.viewmodel.AudioViewModel
import com.example.gpt.utils.createAudioFilePath
import com.example.gpt.utils.fileIsNotEmpty

@Composable
fun AudioMessageScreen(
    audioViewModel: AudioViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val audioFilePath by remember { mutableStateOf(context.createAudioFilePath()) }
    val audioManager by remember { mutableStateOf(AudioManager(context, audioFilePath)) }

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
                audioManager.startRecording()
            } else {
                Toast.makeText(context, R.string.audio_permission_denied, Toast.LENGTH_SHORT).show()
            }
        })

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MediumPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShowPlayButton(
            context = context,
            filePath = audioFilePath,
            startPlaying = startPlaying,
            onPlay = { shouldPlay ->
                startPlaying = shouldPlay

                if (shouldPlay) {
                    val wasPlaybackSuccessful = audioManager.startPlayback()
                    if (!wasPlaybackSuccessful) {
                        Toast.makeText(
                            context,
                            "There was an error with playback",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    audioManager.stopPlayback()
                }
            })

        ShowRecordButton(
            context = context,
            startRecording = startRecording,
            onRecord = { shouldRecord ->
                startRecording = shouldRecord

                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                    if (shouldRecord) {
                        audioManager.startRecording()
                    } else {
                        audioManager.stopRecording()
                    }
                } else {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            })
    }
}

@Composable
fun ShowPlayButton(
    context: Context,
    filePath: String,
    onPlay: (Boolean) -> Unit,
    startPlaying: Boolean
) {
    Button(
        enabled = context.fileIsNotEmpty(filePath),
        onClick = {
            onPlay(!startPlaying)
        }) {
        when (startPlaying) {
            true -> Icon(painterResource(id = R.drawable.pause_icon), "pause")
            false -> Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "play")
        }
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
        when (startRecording) {
            true -> Icon(painterResource(id = R.drawable.pause_icon), "pause")
            false -> Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "play")
        }
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
