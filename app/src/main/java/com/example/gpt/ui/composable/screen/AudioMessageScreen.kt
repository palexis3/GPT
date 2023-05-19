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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gpt.R
import com.example.gpt.ui.composable.ShowCardHeader
import com.example.gpt.ui.composable.ShowLoading
import com.example.gpt.ui.composable.ShowMessageContent
import com.example.gpt.ui.theme.EIGHT_DP
import com.example.gpt.ui.theme.FOUR_DP
import com.example.gpt.ui.theme.SIX_DP
import com.example.gpt.ui.theme.TWELVE_DP
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
            .padding(TWELVE_DP)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            modifier = Modifier.align(alignment = CenterHorizontally),
            text = stringResource(id = R.string.record_audio),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(SIX_DP))

        ShowRecordButton(
            modifier = Modifier.align(alignment = CenterHorizontally),
            context = context,
            startRecording = startRecording,
            onRecord = { shouldRecord ->
                startRecording = shouldRecord

                if (shouldRecord) {
                    audioViewModel.resetAudioUiFlow()
                }

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

        Spacer(modifier = Modifier.height(30.dp))

        if (fileIsNotEmpty(audioFile) && !startRecording) {
            Text(
                modifier = Modifier.align(alignment = CenterHorizontally),
                text = stringResource(id = R.string.playback_audio),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(SIX_DP))

            ShowPlayButton(
                modifier = Modifier.align(alignment = CenterHorizontally),
                audioFile = audioFile,
                startPlaying = startPlaying,
                onPlay = { shouldPlay ->
                    startPlaying = shouldPlay

                    if (shouldPlay) {
                        val wasPlaybackSuccessful = audioManager.startPlayback()
                        if (!wasPlaybackSuccessful) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.playback_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        audioManager.stopPlayback()
                    }
                })

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(TWELVE_DP),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Button(
                    onClick = {
                        audioViewModel.createTranscription(audioFile)
                    }) {
                    Text(text = stringResource(id = R.string.transcribe_audio))
                }

                Spacer(modifier = Modifier.width(FOUR_DP))

                Button(
                    onClick = {
                        audioViewModel.createTranslation(audioFile)
                    }) {
                    Text(text = stringResource(id = R.string.translate_audio))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            ShowAudioMessageUiState(
                modifier = Modifier.align(End),
                audioMessageUiState
            )
        }
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
            is AudioMessageUiState.Error -> ShowAudioMessageContent(text = stringResource(id = R.string.error))
            is AudioMessageUiState.Success -> {
                ShowAudioMessageContent(text = audioMessageUiState.audioMessageUi.text)
            }
        }
    }
}

@Composable
fun ShowAudioMessageContent(text: String) {
    val cardBackgroundColor = when (text) {
        stringResource(id = R.string.error) -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.secondaryContainer
    }

    val color =  when (text) {
        stringResource(id = R.string.error) -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.secondary
    }

    val messageModifier: Modifier = Modifier.padding(
        start = TWELVE_DP,
        top = FOUR_DP,
        bottom = SIX_DP,
        end = TWELVE_DP
    )

    Card(
        modifier = Modifier
            .widthIn(200.dp, 275.dp)
            .padding(FOUR_DP),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = EIGHT_DP
        ),
        shape = RoundedCornerShape(TWELVE_DP)
    ) {
        ShowCardHeader(text = stringResource(id = R.string.gpt))

        Spacer(modifier = Modifier.height(2.dp))

        ShowMessageContent(
            text = text,
            modifier = messageModifier,
            textStyle = TextStyle(
                color =color,
                fontSize = 18.sp
            )
        )
    }
}

@Composable
fun ShowPlayButton(
    modifier: Modifier,
    audioFile: File,
    onPlay: (Boolean) -> Unit,
    startPlaying: Boolean
) {
    Button(
        modifier = modifier,
        enabled = fileIsNotEmpty(audioFile),
        onClick = {
            onPlay(!startPlaying)
        }) {
        when (startPlaying) {
            true -> Icon(painterResource(id = R.drawable.pause_icon), "pause")
            false -> Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "play")
        }
        Text(
            text = when (startPlaying) {
                true -> stringResource(id = R.string.stop_playback)
                false -> stringResource(id = R.string.start_playback)
            }
        )
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
                true -> stringResource(id = R.string.stop_recording)
                false -> stringResource(id = R.string.start_recording)
            }
        )
    }
}
