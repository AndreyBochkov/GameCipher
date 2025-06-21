package com.ab.GameCipher.data

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ab.GameCipher.utils.deserialize
import com.ab.GameCipher.utils.serialize
import kotlinx.coroutines.flow.firstOrNull

private const val LAYOUT_PREFERENCE_NAME = "layout_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = LAYOUT_PREFERENCE_NAME
)

class UserPreferencesRepository(
    private val context: Context
) {
    init {
        require(context is Application) { "Use Application instead of the Context!" }
    }

    private val decipherStateSaved = stringPreferencesKey("decipher_state_map_saved")
    private val cipherStateSaved = stringPreferencesKey("cipher_state_map_saved")
    private val levelSaved = intPreferencesKey("level_saved")

    suspend fun saveLevel(level: Int) {
        context.dataStore.edit { preferences ->
            preferences[levelSaved] = level
        }
    }

    suspend fun loadLevel(): Int {
        return (context.dataStore.data.firstOrNull()?:(return 0))[levelSaved]?:0
    }

    suspend fun saveDecipherState(decipherState: List<Map<Char, Char>>) {
        context.dataStore.edit { preferences ->
            preferences[decipherStateSaved] = serialize(decipherState)
        }
    }

    suspend fun saveCipherState(cipherState: List<Map<Char, Char>>) {
        context.dataStore.edit { preferences ->
            preferences[cipherStateSaved] = serialize(cipherState)
        }
    }

    suspend fun loadStates(): Pair<List<Map<Char, Char>>, List<Map<Char, Char>>> {
        val preferences = context.dataStore.data.firstOrNull() ?:
            return Pair(emptyList(), emptyList())
        return Pair(
            deserialize(preferences[cipherStateSaved]?:""),
            deserialize(preferences[decipherStateSaved]?:"")
        )
    }

    // 🙂 <- this is Bob, i'll just leave him here ok?
}
