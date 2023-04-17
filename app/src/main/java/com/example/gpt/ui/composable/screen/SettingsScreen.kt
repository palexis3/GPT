package com.example.gpt.ui.composable.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gpt.ui.viewmodel.SettingsViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import com.example.gpt.R
import com.example.gpt.ui.theme.MediumPadding
import com.example.gpt.utils.mask

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val apiKey by settingsViewModel.apiKeyState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MediumPadding)
    ) {

        Text(
            text = stringResource(id = R.string.settings),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = stringResource(id = R.string.api_key),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                painter = painterResource(id = R.drawable.key_icon),
                contentDescription = "API key icon"
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        when (apiKey) {
            "" -> ShowEnterApiKey(newKeySelected = { apiKey ->
                settingsViewModel.setApiKey(apiKey)
            })
            else -> {
                ShowCurrentApiKey(
                    apiKey = apiKey,
                    newKeySelected = { apiKey ->
                        settingsViewModel.setApiKey(apiKey)
                    }
                )
            }
        }
    }
}

@Composable
fun ShowCurrentApiKey(apiKey: String, newKeySelected: (String) -> Unit) {
    var shouldShowApiKey by rememberSaveable { mutableStateOf(true) }

    if (shouldShowApiKey) {
        ApiKeyText(apiKey = apiKey, shouldShowApiKeyText = { value ->
            shouldShowApiKey = value
        })
    } else {
        ApiKeyInputLayout(
            newKeySelected = newKeySelected,
            onDismissRequested = {
                shouldShowApiKey = true
            }
        )
    }
}

@Composable
fun ShowEnterApiKey(newKeySelected: (String) -> Unit) {
    var shouldOpenInputLayout by rememberSaveable { mutableStateOf(false) }

    if (shouldOpenInputLayout) {
        ApiKeyInputLayout(
            newKeySelected = newKeySelected,
            onDismissRequested = {
                shouldOpenInputLayout = false
            }
        )
    } else {
        Button(onClick = {
            shouldOpenInputLayout = true
        }) {
            Text(text = stringResource(id = R.string.enter_api_key))
        }
    }
}

@Composable
fun ApiKeyText(apiKey: String, shouldShowApiKeyText: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(start = 4.dp),
            text = apiKey.mask(),
            fontStyle = FontStyle.Italic,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.width(4.dp))

        Button(
            onClick = { shouldShowApiKeyText(false) }
        ) {
            Text(
                text = stringResource(id = R.string.edit),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiKeyInputLayout(
    newKeySelected: (String) -> Unit,
    onDismissRequested: () -> Unit
) {
    var inputString by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(MediumPadding)) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RectangleShape
                ),
            value = inputString,
            textStyle = MaterialTheme.typography.titleMedium,
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            onValueChange = { value ->
                inputString = value
            },
            placeholder = {
                Text(
                    stringResource(id = R.string.enter_api_key),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.DarkGray,
                    fontStyle = FontStyle.Italic
                )
            }
        )

        Spacer(modifier = Modifier.height(36.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    onDismissRequested()
                },
            ) {
                Text(stringResource(id = R.string.dismiss))
            }

            Spacer(modifier = Modifier.width(6.dp))

            Button(
                onClick = {
                    newKeySelected(inputString)
                    onDismissRequested()
                },
                enabled = inputString.isNotEmpty()
            ) {
                Text(stringResource(id = R.string.confirm))
            }
        }
    }
}


/**
 * NOTE: There's an ongoing issue where the keyboard is covered by the ModalBottomSheet in Material3
 * when using version `1.1.0-alpha08`
 * Link: https://issuetracker.google.com/issues/268380384
 */
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ShowModelBottomSheet(
//    newKeySelected: (String) -> Unit,
//    onDismissRequested: () -> Unit
//) {
//    val coroutineScope = rememberCoroutineScope()
//    val bottomSheetState = rememberModalBottomSheetState(
//        skipPartiallyExpanded = false
//    )
//    var inputString by remember { mutableStateOf("") }
//
//    ModalBottomSheet(
//        onDismissRequest = {
//            coroutineScope.launch { bottomSheetState.hide() }
//            onDismissRequested()
//        },
//        sheetState = bottomSheetState
//    ) {
//        Column(modifier = Modifier.padding(MediumPadding)) {
//            TextField(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(
//                        color = MaterialTheme.colorScheme.background,
//                        shape = RectangleShape
//                    ),
//                value = inputString,
//                textStyle = MaterialTheme.typography.titleMedium,
//                colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
//                onValueChange = { value ->
//                    inputString = value
//                },
//                placeholder = {
//                    Text(
//                        stringResource(id = R.string.enter_api_key),
//                        style = MaterialTheme.typography.labelMedium,
//                        color = Color.DarkGray,
//                        fontStyle = FontStyle.Italic
//                    )
//                }
//            )
//
//            Spacer(modifier = Modifier.height(36.dp))
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.End
//            ) {
//                Button(
//                    onClick = {
//                        coroutineScope.launch { bottomSheetState.hide() }
//                        onDismissRequested()
//                    },
//                ) {
//                    Text(stringResource(id = R.string.dismiss))
//                }
//
//                Spacer(modifier = Modifier.width(6.dp))
//
//                Button(
//                    onClick = { newKeySelected(inputString) },
//                    enabled = inputString.isNotEmpty()
//                ) {
//                    Text(stringResource(id = R.string.confirm))
//                }
//            }
//        }
//    }
//}
