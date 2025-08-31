/*
 * Copyright 2020-2023 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package com.goldenraven.devkitwallet.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

internal val devkitTypography = Typography(
    labelLarge = TextStyle(
        fontFamily = jetBrainsMonoLight,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp,
        lineHeight = 28.sp
    ),
)

// These are the default text styles used by Material3 components:
// Buttons: labelLarge
