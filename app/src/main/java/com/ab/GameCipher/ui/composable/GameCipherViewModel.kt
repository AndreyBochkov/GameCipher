package com.ab.GameCipher.ui.composable

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ab.GameCipher.data.PageType
import com.ab.GameCipher.data.UserPreferencesRepository
import com.ab.GameCipher.utils.deserialize
import com.ab.GameCipher.utils.serialize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Base64

const val levelsMaxNumConst = 5

class GameCipherViewModel(
    application: Application
): AndroidViewModel(application) {
    private val userPreferencesRepository = UserPreferencesRepository(application)
    private val _uiState = MutableStateFlow(GameCipherUiState())
    val uiState: StateFlow<GameCipherUiState> = _uiState

    init {
        initializeUIState()
    }

    private fun initializeUIState() {
        _uiState.value = runBlocking {
            val states = userPreferencesRepository.loadStates()
            GameCipherUiState(
                level = userPreferencesRepository.loadLevel(),
                cipherStateMap = if (states.first.size == levelsMaxNumConst) states.first else {
                    val default = mutableListOf<Map<Char, Char>>()
                    for (i in 0..<levelsMaxNumConst) {
                        default += ('a'..'z').zip(('a'..'z').shuffled()).toMap()
                    }
                    default.toList()
                },
                decipherStateMap = if (states.second.size == levelsMaxNumConst) states.second else {
                    val default = mutableListOf<Map<Char, Char>>()
                    for (i in 0..<levelsMaxNumConst) {
                        default += mapOf()
                    }
                    default.toList()
                }
            )
        }
        saveCipherState()
    }

    fun updateEncryptedChar(newChar: Char) {
        _uiState.update {
            it.copy(
                encryptedChar = newChar
            )
        }
    }

    fun updateDecryptedChar(newChar: Char) {
        _uiState.update {
            it.copy(
                decryptedChar = newChar
            )
        }
    }

    fun updatePutDecipherStateMap() {
        if (_uiState.value.encryptedChar != '-' && _uiState.value.decryptedChar != '-') {
            _uiState.update {
                val result: MutableList<Map<Char, Char>> = it.decipherStateMap.toMutableList()
                val levelMap = result[it.level].toMutableMap()
                levelMap[it.cipherStateMap[it.level][it.encryptedChar]!!] = it.decryptedChar
                result[it.level] = levelMap.toMap()
                it.copy(
                    decipherStateMap = result.toList(),
                    encryptedChar = '-',
                    decryptedChar = '-'
                )
            }
            saveDecipherState()
        }
    }

    fun updateDeleteDecipherStateMap(char: Char) {
        _uiState.update {
            val result: MutableList<Map<Char, Char>> = it.decipherStateMap.toMutableList()
            val levelMap = result[it.level].toMutableMap()
            levelMap.remove(char)
            result[it.level] = levelMap.toMap()
            it.copy(
                decipherStateMap = result
            )
        }
        saveDecipherState()
    }

    fun updateLevel(newLevel: Int) {
        _uiState.update {
            it.copy(
                level = newLevel
            )
        }
        viewModelScope.launch {
            userPreferencesRepository.saveLevel(newLevel)
        }
    }

    fun deleteProgress() {
        _uiState.update {
            val result: MutableList<Map<Char, Char>> = it.decipherStateMap.toMutableList()
            result[it.level] = mapOf()
            it.copy(
                decipherStateMap = result
            )
        }
        saveDecipherState()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getExportableString(): String {
        return Base64.getEncoder().encodeToString((serialize(_uiState.value.cipherStateMap) + "|" + serialize(_uiState.value.decipherStateMap)).toByteArray())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun importString(exported: String, exception: () -> Unit, success: () -> Unit) {
        val serializedMaps: List<String>
        try {
            serializedMaps = String(Base64.getDecoder().decode(exported)).split("|")
        } catch (e: Throwable) {
            exception()
            return
        }
        if (serializedMaps.size != 2) {
            exception()
            return
        }
        val states = deserialize(serializedMaps[0]) to deserialize(serializedMaps[1])
        if (states.first.size != levelsMaxNumConst || states.second.size != levelsMaxNumConst) {
            exception()
            return
        }
        _uiState.update {
            it.copy(
                level = 0,
                cipherStateMap = states.first,
                decipherStateMap = states.second,
                encryptedChar = '-',
                decryptedChar = '-'
            )
        }
        saveCipherState()
        saveDecipherState()
        success()
    }

    private fun saveDecipherState() {
        viewModelScope.launch {
            userPreferencesRepository.saveDecipherState(_uiState.value.decipherStateMap)
        }
    }

    private fun saveCipherState() {
        viewModelScope.launch {
            userPreferencesRepository.saveCipherState(_uiState.value.cipherStateMap)
        }
    }

    fun updateCurrentPage(pageType: PageType) {
        _uiState.update {
            it.copy(
                currentPage = pageType
            )
        }
    }
}