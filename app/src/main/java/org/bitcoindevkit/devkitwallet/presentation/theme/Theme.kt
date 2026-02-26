/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NightGlowColorScheme = darkColorScheme(
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

private val DayGlowColorScheme = lightColorScheme(
    surface = Color(0xFFFFF8F4),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFEDE5DF),
    onSurfaceVariant = Color(0xFF49454F),
    background = Color(0xFFFFF8F4),
    onBackground = Color(0xFF1C1B1F),
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),
    primary = Color(0xFF7D5260),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF625B71),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF2E6A3C),
    onTertiary = Color(0xFFFFFFFF),
)

// Returns the surface color for the given theme as an ARGB int for use outside of Compose (e.g.
// setting the window background from the Activity). Keeps the values co-located with the color
// schemes above so there is a single source of truth for each theme's surface color.
fun themeSurfaceColor(darkTheme: Boolean): Int = if (darkTheme) 0xFF1C1B1F.toInt() else 0xFFFFF8F4.toInt()

@Composable
fun DevkitTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) NightGlowColorScheme else DayGlowColorScheme,
        typography = devkitTypography,
        content = content,
    )
}

// NOTES ON THE UI
// - The standard padding is 32dp for start/end, 16dp for top/bottom
