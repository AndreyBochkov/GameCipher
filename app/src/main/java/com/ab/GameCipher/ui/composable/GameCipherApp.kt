package com.ab.GameCipher.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ab.GameCipher.R
import com.ab.GameCipher.data.PageType

@Composable
fun GameCipherApp(
    modifier: Modifier = Modifier
) {
    val viewModel: GameCipherViewModel = viewModel()
    val gameCipherUiState = viewModel.uiState.collectAsState().value

    val navigationItemContentList = listOf(
        NavigationItemContent(
            pageType = PageType.Cipher,
            icon = Icons.Default.Home,
            text = stringResource(R.string.tab_cipher)
        ),
        NavigationItemContent(
            pageType = PageType.Edit,
            icon = Icons.Default.Edit,
            text = stringResource(R.string.tab_edit)
        ),
        NavigationItemContent(
            pageType = PageType.LevelSelect,
            icon = Icons.Default.Menu,
            text = stringResource(R.string.tab_level_select)
        ),
    )

    val onTabPressed = { pageType: PageType ->
        if (viewModel.uiState.value.currentPage == pageType) {
            viewModel.updateCurrentPage(pageType = PageType.FirstLaunch)
        } else {
            viewModel.updateCurrentPage(pageType = pageType)
        }
    }

    val updateEncryptedChar = {newChar: Char ->
        viewModel.updateEncryptedChar(newChar)
    }

    val updateDecryptedChar = {newChar: Char ->
        viewModel.updateDecryptedChar(newChar)
    }

    val onEditOptionPressed = {
        viewModel.updatePutDecipherStateMap()
    }

    val onDeleteOptionPressed = { encryptedChar: Char ->
        viewModel.updateDeleteDecipherStateMap(encryptedChar)
    }

    val onLevelOptionPressed = { newLevel: Int ->
        viewModel.updateLevel(newLevel)
    }

    val paddingPageContent = 18.dp

    println(gameCipherUiState.cipherStateMap)
    println(gameCipherUiState.decipherStateMap)

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            val pageModifier = Modifier
                .weight(1f)
                .padding(
                    start = paddingPageContent,
                    top = paddingPageContent,
                    end = paddingPageContent
                )

            when (gameCipherUiState.currentPage) {
                PageType.Cipher -> {
                    GameCipherCipherPageContent(
                        gameCipherUiState = gameCipherUiState,
                        modifier = pageModifier
                    )
                }
                PageType.Edit -> {
                    GameCipherEditPageContent(
                        gameCipherUiState = gameCipherUiState,
                        onEditOptionPressed = onEditOptionPressed,
                        onDeleteOptionPressed = onDeleteOptionPressed,
                        updateEncryptedChar = updateEncryptedChar,
                        updateDecryptedChar = updateDecryptedChar,
                        modifier = pageModifier
                    )
                }
                PageType.LevelSelect -> {
                    GameCipherLevelSelectPageContent(
                        levelsPreviews = stringResource(R.string.cipher_text).split("$$").map { text ->
                            text.slice(0..20)
                        },
                        gameCipherUiState = gameCipherUiState,
                        onLevelOptionPressed = onLevelOptionPressed,
                        modifier = pageModifier
                    )
                }
                else -> {
                    GameCipherFirstLaunchPageContent(
                        modifier = pageModifier
                    )
                }
            }

            GameCipherBottomNavigationBar(
                currentTab = gameCipherUiState.currentPage,
                onTabPressed = onTabPressed,
                navigationItemContentList = navigationItemContentList,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(stringResource(R.string.navigation_bottom))
            )
        }
    }
}

@Composable
private fun GameCipherBottomNavigationBar(
    currentTab: PageType,
    onTabPressed: ((PageType) -> Unit),
    navigationItemContentList: List<NavigationItemContent>,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        for (navItem in navigationItemContentList) {
            NavigationBarItem(
                selected = currentTab == navItem.pageType,
                onClick = { onTabPressed(navItem.pageType) },
                icon = {
                    Icon(
                        imageVector = navItem.icon,
                        contentDescription = navItem.text,
                        modifier = Modifier.scale(1.25f)
                    )
                },
            )
        }
    }
}