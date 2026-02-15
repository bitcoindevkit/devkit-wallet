/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DevkitDarkColorScheme = darkColorScheme(
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    outline = Color(0xFFCAC4D0),
    outlineVariant = Color(0xFF49454F),
    primary = Color(0xFFF2D2B6),
    onPrimary = Color(0xFF1C1B1F),
    secondary = Color(0xFFC6B2E0),
    onSecondary = Color(0xFF1C1B1F),
    tertiary = Color(0xFF8FD998),
    onTertiary = Color(0xFF1C1B1F),
)

@Composable
fun DevkitTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DevkitDarkColorScheme,
        typography = devkitTypography,
        content = content,
    )
}

// NOTES ON THE UI
// - The standard padding is 32dp for start/end, 16dp for top/bottom
