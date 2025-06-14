package com.ab.GameCipher.ui.composable

import com.ab.GameCipher.data.PageType

data class GameCipherUiState (
    var level: Int = 0,
    var decipherStateMap: List<Map<Char, Char>> = emptyList(),
    var cipherStateMap: List<Map<Char, Char>> = emptyList(),
    var currentPage: PageType = PageType.FirstLaunch,
    var encryptedChar: Char = '-',
    var decryptedChar: Char = '-'
)