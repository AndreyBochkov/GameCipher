package com.ab.GameCipher.ui.composable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ab.GameCipher.R
import com.ab.GameCipher.utils.appropriateDecryptedCharList
import com.ab.GameCipher.utils.appropriateEncryptedCharList
import com.ab.GameCipher.utils.decipherUsingMap

@Composable
fun GameCipherCipherPageContent(
    gameCipherUiState: GameCipherUiState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxHeight(),
        contentPadding = PaddingValues(
            top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding(),
        )
    ) {
        item {
            LayoutText(
                text = decipherUsingMap(stringResource(R.string.cipher_text), gameCipherUiState.decipherStateMap),
                setCustomFont = true
            )
        }
    }
}

@Composable
fun GameCipherEditPageContent(
    gameCipherUiState: GameCipherUiState,
    onEditOptionPressed: () -> Unit,
    onDeleteOptionPressed: (Char) -> Unit,
    updateEncryptedChar: (Char) -> Unit,
    updateDecryptedChar: (Char) -> Unit,
    modifier: Modifier = Modifier,
) {
    var encryptedExpanded by remember { mutableStateOf(false) }
    var decryptedExpanded by remember { mutableStateOf(false) }
    LazyColumn(
        modifier = modifier.fillMaxHeight(),
        contentPadding = PaddingValues(
            top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding(),
        )
    ) {
        item {
            LayoutText(stringResource(R.string.map_symbol))
        }

        item {
            Button(
                onClick = { encryptedExpanded = !encryptedExpanded },
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    disabledContentColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                LayoutText("${gameCipherUiState.encryptedChar}", setCustomFont = true)
            }
            DropdownMenu(
                expanded = encryptedExpanded,
                onDismissRequest = { encryptedExpanded = false },
            ) {
                for (char in appropriateEncryptedCharList) {
                    if (!gameCipherUiState.decipherStateMap.containsKey(char)) {
                        DropdownMenuItem(
                            text = { LayoutText("$char", setCustomFont = true) },
                            onClick = {
                                updateEncryptedChar(char)
                                encryptedExpanded = false
                            }
                        )
                    }
                }
            }
        }

        item {
            LayoutText(stringResource(R.string.to_letter))
        }

        item {
            Button(
                onClick = { decryptedExpanded = !decryptedExpanded },
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    disabledContentColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                LayoutText("${gameCipherUiState.decryptedChar}", setCustomFont = true)
            }
            DropdownMenu(
                expanded = decryptedExpanded,
                onDismissRequest = { decryptedExpanded = false },
            ) {
                for (char in appropriateDecryptedCharList) {
                    if (!gameCipherUiState.decipherStateMap.containsValue(char)) {
                        DropdownMenuItem(
                            text = { LayoutText("$char", setCustomFont = true) },
                            onClick = {
                                updateDecryptedChar(char)
                                decryptedExpanded = false
                            }
                        )
                    }
                }
            }
        }

        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
        }

        item {
            Button(
                onClick = {
                    onEditOptionPressed()
                },
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    disabledContentColor = MaterialTheme.colorScheme.tertiaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            ) {
                LayoutText(stringResource(R.string.map))
            }
        }

        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
        }

        items(gameCipherUiState.decipherStateMap.entries.toList()) {
            Button(
                onClick = {
                    onDeleteOptionPressed(it.key)
                },
                colors = if (it.key == it.value.lowercaseChar()) ButtonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer, contentColor = MaterialTheme.colorScheme.onTertiaryContainer, disabledContentColor = MaterialTheme.colorScheme.tertiaryContainer, disabledContainerColor = MaterialTheme.colorScheme.onTertiaryContainer)
                else ButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer, disabledContentColor = MaterialTheme.colorScheme.errorContainer, disabledContainerColor = MaterialTheme.colorScheme.onErrorContainer),
                modifier = Modifier.padding(bottom = 3.dp)
            ) {
                LayoutText(
                    text = it.key + " >>> " + it.value,
                    setCustomFont = true
                )
            }
        }
    }
}

@Composable
fun GameCipherFirstLaunchPageContent(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxHeight(),
        contentPadding = PaddingValues(
            top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding(),
        )
    ) {
        item {
            LayoutText(
                text = stringResource(R.string.first_launch_greeting_text)
            )
        }
    }
}