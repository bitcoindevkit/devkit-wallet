/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun DevkitTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        // colorScheme = devkitColors,
        // shapes = devkitShapes,
        typography = devkitTypography,
        content = content,
    )
}

// NOTES ON THE UI
// - The standard padding is 32dp for start/end, 16dp for top/bottom
