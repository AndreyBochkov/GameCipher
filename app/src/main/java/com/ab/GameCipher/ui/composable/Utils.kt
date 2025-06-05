package com.ab.GameCipher.ui.composable

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import com.ab.GameCipher.R
import com.ab.GameCipher.data.PageType
import androidx.compose.ui.text.font.Font

val customFontFamily: FontFamily = FontFamily(Font(R.font.cipher))

@Composable
fun LayoutText(text: String, modifier: Modifier = Modifier, setCustomFont: Boolean = false) {
    Text(
        text = text,
        fontFamily = if (setCustomFont) customFontFamily else null,
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}

data class NavigationItemContent(
    val pageType: PageType,
    val icon: ImageVector,
    val text: String
)