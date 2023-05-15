package com.example.gpt.ui.composable.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import com.example.gpt.manager.AudioManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gpt.R
import com.example.gpt.ui.composable.ShowLoading
import com.example.gpt.ui.composable.ShowMessageContentCard
import com.example.gpt.ui.theme.MediumPadding
import com.example.gpt.ui.viewmodel.AudioMessageUiState
import com.example.gpt.ui.viewmodel.AudioViewModel
import com.example.gpt.utils.createAudioFile
import com.example.gpt.utils.fileIsNotEmpty
import com.example.gpt.utils.hasMicrophone
import java.io.File

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun AudioMessageScreen(
    audioViewModel: AudioViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val audioMessageUiState by audioViewModel.audioMessageUiState.collectAsStateWithLifecycle()

    val audioFile by remember { mutableStateOf(context.createAudioFile()) }
    val audioManager by remember { mutableStateOf(AudioManager(context, audioFile.path)) }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MediumPadding)
    ) {
        ShowRecordButton(
            modifier = Modifier.align(alignment = CenterHorizontally),
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

        if (fileIsNotEmpty(audioFile)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MediumPadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShowPlayButton(
                    audioFile = audioFile,
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

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = stringResource(id = R.string.audio_file_transcribe, audioFile.name),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Modifier.height(8.dp)

            Button(
                modifier = Modifier.align(alignment = CenterHorizontally),
                onClick = {
                    audioViewModel.createTranscription(audioFile)
                }) {
                Text(text = stringResource(id = R.string.send))
            }
        }

        ShowAudioMessageUiState(
            modifier = Modifier.align(End),
            audioMessageUiState
        )
    }
}

@Composable
fun ShowAudioMessageUiState(
    modifier: Modifier,
    audioMessageUiState: AudioMessageUiState
) {
    Row(
        modifier = modifier,
    ) {
        when (audioMessageUiState) {
            is AudioMessageUiState.Uninitialized -> {}
            is AudioMessageUiState.Loading -> ShowLoading()
            is AudioMessageUiState.Error -> {
                ShowMessageContentCard(text = stringResource(id = R.string.error))
            }
            is AudioMessageUiState.Success -> {
                ShowMessageContentCard(text = audioMessageUiState.audioMessageUi.text)
            }
        }
    }
}

@Composable
fun ShowPlayButton(
    audioFile: File,
    onPlay: (Boolean) -> Unit,
    startPlaying: Boolean
) {
    Button(
        enabled = fileIsNotEmpty(audioFile),
        onClick = {
            onPlay(!startPlaying)
        }) {
        when (startPlaying) {
            true -> Icon(painterResource(id = R.drawable.pause_icon), "pause")
            false -> Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "play")
        }
    }
}

@Composable
fun ShowRecordButton(
    modifier: Modifier,
    context: Context,
    onRecord: (Boolean) -> Unit,
    startRecording: Boolean
) {

    Button(
        modifier = modifier,
        enabled = context.hasMicrophone(),
        onClick = {
            onRecord(!startRecording)
        }) {
        when (startRecording) {
            true -> Icon(painterResource(id = R.drawable.pause_icon), "pause")
            false -> Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "play")
        }
        Text(
            text = when (startRecording) {
                true -> "Stop Recording"
                false -> "Start Recording"
            }
        )
    }
}
