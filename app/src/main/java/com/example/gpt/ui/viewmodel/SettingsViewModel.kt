package com.example.gpt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gpt.utils.MySettingPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingPreferences: MySettingPreferences
) : ViewModel() {

    val apiKeyState: StateFlow<String> = settingPreferences.apiKey.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        ""
    )

    val showAndSaveChatHistoryState: StateFlow<Boolean> =
        settingPreferences.saveAndShowChatHistoryState.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            false
        )

    fun setApiKey(apiKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            settingPreferences.setApiKey(apiKey)
        }
    }

    fun setSaveAndShowHistory(bool: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            settingPreferences.shouldSaveAndShowChatHistory(bool)
        }
    }
}
