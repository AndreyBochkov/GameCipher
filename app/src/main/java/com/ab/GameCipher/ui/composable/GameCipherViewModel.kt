package com.ab.GameCipher.ui.composable

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ab.GameCipher.data.PageType
import com.ab.GameCipher.data.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

const val levelsMaxNumConst = 3

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
                    for (i in 0..levelsMaxNumConst) {
                        default += ('a'..'z').zip(('a'..'z').shuffled()).toMap()
                    }
                    println(default)
                    viewModelScope.launch {
                        userPreferencesRepository.saveCipherState(default)
                    }
                    default.toList()
                },
                decipherStateMap = if (states.first.size == levelsMaxNumConst) states.second else listOf(emptyMap(), emptyMap(), emptyMap())
            )
        }
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
                levelMap[it.encryptedChar] = it.decryptedChar
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

    private fun saveDecipherState() {
        viewModelScope.launch {
            userPreferencesRepository.saveDecipherState(_uiState.value.decipherStateMap)
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