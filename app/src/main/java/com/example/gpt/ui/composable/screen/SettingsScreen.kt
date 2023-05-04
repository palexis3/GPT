package com.example.gpt.ui.composable.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.gpt.R
import com.example.gpt.ui.theme.MediumPadding
import com.example.gpt.utils.mask

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val apiKey by settingsViewModel.apiKeyState.collectAsStateWithLifecycle()
    val showAndSaveChatHistory by settingsViewModel.showAndSaveChatHistoryState.collectAsStateWithLifecycle()

    var chatHistoryChecked by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(showAndSaveChatHistory) {
        chatHistoryChecked = showAndSaveChatHistory
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MediumPadding)
    ) {
        Text(
            text = stringResource(id = R.string.settings),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(48.dp))

        Card(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 20.dp, start = 10.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(id = R.string.api_key),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

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

                Icon(
                    painter = painterResource(id = R.drawable.navigate_forward_icon),
                    contentDescription = "Navigate Forward"
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 20.dp, start = 10.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.show_and_save_history),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )

                Switch(
                    checked = chatHistoryChecked,
                    onCheckedChange = { bool ->
                        chatHistoryChecked = bool
                        settingsViewModel.setSaveAndShowHistory(bool)
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
    Text(
        text = apiKey.mask(),
        modifier = Modifier.clickable { shouldShowApiKeyText(false) },
        fontStyle = FontStyle.Italic,
        fontWeight = FontWeight.ExtraBold,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary
    )
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
