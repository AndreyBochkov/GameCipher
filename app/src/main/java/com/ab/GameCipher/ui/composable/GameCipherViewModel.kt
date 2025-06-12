package com.ab.GameCipher.ui.composable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ab.GameCipher.GameCipherApplication
import com.ab.GameCipher.data.PageType
import com.ab.GameCipher.data.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class GameCipherViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(GameCipherUiState())
    val uiState: StateFlow<GameCipherUiState> = _uiState

    init {
        initializeUIState()
    }

    private fun initializeUIState() {
        _uiState.value = runBlocking {
            GameCipherUiState(
                decipherStateMap = userPreferencesRepository.decipherStateMapFlow.first()
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
                val result: MutableMap<Char, Char> = it.decipherStateMap.toMutableMap()
                result[it.encryptedChar] = it.decryptedChar
                it.copy(
                    decipherStateMap = result.toMap(),
                    encryptedChar = '-',
                    decryptedChar = '-'
                )
            }
            saveDecipherUiState()
        }
    }

    fun updateDeleteDecipherStateMap(char: Char) {
        _uiState.update {
            val result: MutableMap<Char, Char> = it.decipherStateMap.toMutableMap()
            result.remove(char)
            it.copy(
                decipherStateMap = result
            )
        }
        saveDecipherUiState()
    }

    private fun saveDecipherUiState() {
        viewModelScope.launch {
            userPreferencesRepository.saveLayoutPreference(_uiState.value.decipherStateMap)
        }
    }

    fun updateCurrentPage(pageType: PageType) {
        _uiState.update {
            it.copy(
                currentPage = pageType
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GameCipherApplication)
                GameCipherViewModel(application.userPreferencesRepository)
            }
        }
    }
}