package com.aibuilt.blackvault.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File

private val Context.settingsStore by preferencesDataStore(name = "blackvault_settings")

class VaultRepository(private val context: Context) {

    private val splashKey = booleanPreferencesKey("splash_enabled")
    private val soundKey = booleanPreferencesKey("sound_enabled")

    val settings: Flow<Settings> = context.settingsStore.data
        .catch { emit(androidx.datastore.preferences.core.emptyPreferences()) }
        .map { prefs ->
            Settings(
                splashEnabled = prefs[splashKey] ?: true,
                soundEnabled = prefs[soundKey] ?: true
            )
        }

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    private val historyFile = File(context.filesDir, "blackvault_history.json")
    private val serializer = ListSerializer(BreachRecord.serializer())
    private val mutex = Mutex()

    private val _history = MutableStateFlow<List<BreachRecord>>(emptyList())
    val history: StateFlow<List<BreachRecord>> = _history.asStateFlow()

    init {
        _history.value = readFromDisk()
    }

    private fun readFromDisk(): List<BreachRecord> = try {
        if (!historyFile.exists()) {
            emptyList()
        } else {
            val parsed = json.decodeFromString(serializer, historyFile.readText())
            parsed.sortedByDescending { it.timestamp }
        }
    } catch (t: Throwable) {
        emptyList()
    }

    private suspend fun writeToDisk(records: List<BreachRecord>) = withContext(Dispatchers.IO) {
        try {
            historyFile.writeText(json.encodeToString(serializer, records))
        } catch (t: Throwable) {
            // storage failure keeps the in-memory state usable
        }
    }

    suspend fun addRecord(record: BreachRecord) {
        mutex.withLock {
            val updated = (_history.value.filterNot { it.id == record.id } + record)
                .sortedByDescending { it.timestamp }
            _history.value = updated
            writeToDisk(updated)
        }
    }

    suspend fun deleteRecord(id: String) {
        mutex.withLock {
            val updated = _history.value.filterNot { it.id == id }
            _history.value = updated
            writeToDisk(updated)
        }
    }

    suspend fun clearHistory() {
        mutex.withLock {
            _history.value = emptyList()
            writeToDisk(emptyList())
        }
    }

    suspend fun setSplashEnabled(enabled: Boolean) {
        context.settingsStore.edit { prefs -> prefs[splashKey] = enabled }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.settingsStore.edit { prefs -> prefs[soundKey] = enabled }
    }
}
