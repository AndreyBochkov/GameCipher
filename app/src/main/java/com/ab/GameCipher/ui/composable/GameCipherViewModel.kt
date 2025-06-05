package com.ab.GameCipher.ui.composable

import androidx.lifecycle.ViewModel
import com.ab.GameCipher.data.PageType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class GameCipherViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameCipherUiState())
    val uiState: StateFlow<GameCipherUiState> = _uiState

    init {
        initializeUIState()
    }

    private fun initializeUIState() {
        // TODO: Get DecipherStateMap from LocalDB
        _uiState.value =
            GameCipherUiState()
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
    }

    fun updateCurrentPage(pageType: PageType) {
        _uiState.update {
            it.copy(
                currentPage = pageType
            )
        }
    }
}