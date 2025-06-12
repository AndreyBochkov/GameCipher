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

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/*
 * Concrete class implementation to access data store
 */
class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val decipherStateMapSaved = stringPreferencesKey("decipher_state_map_saved")
        const val TAG = "UserPreferencesRepo"
    }

    val decipherStateMapFlow: Flow<Map<Char, Char>> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            val cached = preferences[decipherStateMapSaved]
            if (cached == null) {
                mapOf()
            } else {
                val result = mutableMapOf<Char, Char>()
                for (i in cached.indices.step(2)) {
                    result[cached[i]] = cached[i + 1]
                }
                result.toMap()
            }
        }

    suspend fun saveLayoutPreference(decipherStateMap: Map<Char, Char>) {
        dataStore.edit { preferences ->
            val result = StringBuilder()
            for (k in decipherStateMap.keys) {
                result.append(k.toString() + (decipherStateMap[k] ?: ""))
            }
            preferences[decipherStateMapSaved] = result.toString()
        }
    }
}
