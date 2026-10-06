package com.example.data

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.playbackDataStore: DataStore<Preferences> by preferencesDataStore(name = "jdwave_playback_positions")

/**
 * Robust DataStore-backed repository to persist and retrieve playback positions,
 * ensuring the AudioPlayer initializes at the exact last played timestamp.
 */
class PlaybackDataStoreRepository(private val context: Context) {
    private val dataStore = context.playbackDataStore

    companion object {
        private val KEY_LAST_ACTIVE_TRACK_ID = stringPreferencesKey("last_active_track_id")
        private val KEY_LAST_ACTIVE_STORY_ID = stringPreferencesKey("last_active_story_id")

        fun positionKey(trackId: String) = intPreferencesKey("pos_$trackId")
        fun durationKey(trackId: String) = intPreferencesKey("dur_$trackId")
        fun completedKey(trackId: String) = booleanPreferencesKey("comp_$trackId")
        fun timestampKey(trackId: String) = longPreferencesKey("time_$trackId")
    }

    suspend fun savePlaybackPosition(
        trackId: String,
        positionSec: Int,
        durationSec: Int,
        storyId: String? = null,
        completed: Boolean = false
    ) {
        dataStore.edit { preferences ->
            preferences[positionKey(trackId)] = positionSec
            preferences[durationKey(trackId)] = durationSec
            preferences[completedKey(trackId)] = completed
            preferences[timestampKey(trackId)] = System.currentTimeMillis()
            preferences[KEY_LAST_ACTIVE_TRACK_ID] = trackId
            if (storyId != null) {
                preferences[KEY_LAST_ACTIVE_STORY_ID] = storyId
            }
        }
    }

    fun getPlaybackPositionFlow(trackId: String): Flow<Int> {
        val key = positionKey(trackId)
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[key] ?: 0
            }
    }

    suspend fun getInitialPlaybackPosition(trackId: String): Int {
        val preferences = dataStore.data
            .catch { emit(emptyPreferences()) }
            .first()
        val isCompleted = preferences[completedKey(trackId)] ?: false
        if (isCompleted) {
            return 0
        }
        val savedPosition = preferences[positionKey(trackId)] ?: 0
        return if (savedPosition >= 10) {
            savedPosition
        } else {
            0
        }
    }

    suspend fun getLastActiveTrackInfo(): Pair<String?, String?> {
        val preferences = dataStore.data
            .catch { emit(emptyPreferences()) }
            .first()
        val trackId = preferences[KEY_LAST_ACTIVE_TRACK_ID]
        val storyId = preferences[KEY_LAST_ACTIVE_STORY_ID]
        return Pair(trackId, storyId)
    }

    fun getAllTrackPositions(): Flow<Map<String, Int>> {
        return dataStore.data
            .catch { emit(emptyPreferences()) }
            .map { preferences ->
                val result = mutableMapOf<String, Int>()
                preferences.asMap().forEach { (key, value) ->
                    if (key.name.startsWith("pos_") && value is Int) {
                        val trackId = key.name.removePrefix("pos_")
                        result[trackId] = value
                    }
                }
                result
            }
    }

    suspend fun clearTrackPosition(trackId: String) {
        dataStore.edit { preferences ->
            preferences.remove(positionKey(trackId))
            preferences.remove(durationKey(trackId))
            preferences.remove(completedKey(trackId))
            preferences.remove(timestampKey(trackId))
        }
    }
}
