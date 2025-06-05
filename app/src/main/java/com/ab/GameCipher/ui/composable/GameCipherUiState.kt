package com.ab.GameCipher.ui.composable

import com.ab.GameCipher.data.PageType

data class GameCipherUiState (
    var decipherStateMap: Map<Char, Char> = mapOf(),
    var currentPage: PageType = PageType.FirstLaunch,
    var encryptedChar: Char = '-',
    var decryptedChar: Char = '-'
)