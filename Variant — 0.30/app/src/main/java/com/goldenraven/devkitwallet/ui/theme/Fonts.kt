/*
 * Copyright 2020-2023 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package com.goldenraven.devkitwallet.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.goldenraven.devkitwallet.R

val jetBrainsMonoLight = FontFamily(
    Font(
        resId = R.font.jetbrains_mono_light,
        weight = FontWeight.Light,
        style = FontStyle.Normal
    )
)

val jetBrainsMonoRegular = FontFamily(
    Font(
        resId = R.font.jetbrains_mono_regular,
        weight = FontWeight.Normal,
        style = FontStyle.Normal
    )
)

val jetBrainsMonoSemiBold = FontFamily(
    Font(
        resId = R.font.jetbrains_mono_semibold,
        weight = FontWeight.SemiBold,
        style = FontStyle.Normal
    )
)
