package com.example.gpt.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

interface SettingPreferences {
    suspend fun setApiKey(apiKey: String)
    suspend fun getApiKey(): Flow<String?>

    suspend fun setSaveChatMessages(shouldStore: Boolean)
    suspend fun getSaveChatMessages(): Flow<Boolean>
}

class MySettingPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingPreferences {

    override suspend fun setApiKey(apiKey: String) {
        dataStore.edit { preferences ->
            preferences[API_KEY] = apiKey
        }
    }

    override suspend fun getApiKey(): Flow<String?> =
        dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[API_KEY]
            }

    override suspend fun setSaveChatMessages(shouldStore: Boolean) {
        dataStore.edit { preferences ->
            preferences[SAVE_CHAT_MESSAGES] = shouldStore
        }
    }

    override suspend fun getSaveChatMessages(): Flow<Boolean> =
        dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { preferences ->
                preferences[SAVE_CHAT_MESSAGES] ?: false
            }

    private companion object {
        val API_KEY = stringPreferencesKey(
            name = "api_key"
        )

        val SAVE_CHAT_MESSAGES = booleanPreferencesKey(
            name = "save_chat_messages"
        )
    }
}
