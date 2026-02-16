/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.X
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.presentation.theme.inter

@Composable
fun CustomSnackbar(data: SnackbarData) {
    Snackbar(
        modifier = Modifier.padding(12.dp),
        action = {
            IconButton(
                onClick = { data.performAction() },
            ) {
                Icon(
                    imageVector = Lucide.X,
                    contentDescription = "Ok",
                    tint = DevkitWalletColors.white,
                )
            }
        },
        containerColor = DevkitWalletColors.primaryLight,
    ) {
        Text(
            text = data.visuals.message,
            fontFamily = inter,
            fontSize = 14.sp,
        )
    }
}
