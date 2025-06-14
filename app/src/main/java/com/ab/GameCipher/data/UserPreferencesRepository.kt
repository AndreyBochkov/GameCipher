/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ab.GameCipher.data

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull

private const val LAYOUT_PREFERENCE_NAME = "layout_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = LAYOUT_PREFERENCE_NAME
)

/*
 * Concrete class implementation to access data store
 */
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

    private fun serialize(list: List<Map<Char, Char>>): String {
        return list.joinToString(";") { map ->
            map.entries.joinToString(":") { (key, value) ->
                "${key}${value}"
            }
        }
    }

    private fun deserialize(serialized: String): List<Map<Char, Char>> {
        if (serialized.isEmpty()) return emptyList()
        return serialized.split(";").map { mapStr ->
            if (mapStr.isEmpty()) emptyMap()
            else mapStr.split(":").associate { pairStr ->
                pairStr[0] to pairStr[1]
            }
        }
    }
}
