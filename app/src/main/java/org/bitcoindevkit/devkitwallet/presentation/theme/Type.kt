/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

internal val devkitTypography =
    Typography(
        labelLarge =
            TextStyle(
                fontFamily = inter,
                fontWeight = FontWeight.Normal,
                color = DevkitWalletColors.white,
                fontSize = 16.sp,
                lineHeight = 28.sp,
            ),
    )

val introText =
    TextStyle(
        fontFamily = inter,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 28.sp,
    )

// These are the default text styles used by Material3 components:
// Buttons: labelLarge

internal val standardText =
    TextStyle(
        color = DevkitWalletColors.white,
        fontFamily = inter,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
    )
