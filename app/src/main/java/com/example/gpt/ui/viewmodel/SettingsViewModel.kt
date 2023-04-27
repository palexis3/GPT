package com.example.gpt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gpt.utils.SettingPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingPreferences: SettingPreferences
) : ViewModel() {

    private var _apiKeyState = MutableStateFlow("")
    val apiKeyState
        get() = _apiKeyState.asStateFlow()

    private var _showAndSaveChatHistoryState = MutableStateFlow(false)
    val showAndSaveChatHistoryState
        get() = _showAndSaveChatHistoryState.asStateFlow()

    init {
        viewModelScope.launch {
            settingPreferences.getApiKey()
                .collectLatest { apiKey ->
                    _apiKeyState.update { apiKey ?: "" }
                }

            settingPreferences.saveAndShowChatHistoryState()
                .collectLatest { bool ->
                    _showAndSaveChatHistoryState.update { bool }
                }
        }
    }

    fun setApiKey(apiKey: String) {
        viewModelScope.launch {
            settingPreferences.setApiKey(apiKey)
        }
    }

    fun setSaveAndShowHistory(bool: Boolean) {
        viewModelScope.launch {
            settingPreferences.shouldSaveAndShowChatHistory(bool)
        }
    }
}
