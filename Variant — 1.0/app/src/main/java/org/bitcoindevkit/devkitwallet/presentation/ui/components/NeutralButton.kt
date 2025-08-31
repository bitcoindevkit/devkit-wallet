/*
 * Copyright 2021-2025 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors

@Composable
fun NeutralButton(text: String, enabled: Boolean = true, modifier: Modifier? = null, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = DevkitWalletColors.secondary,
                disabledContainerColor = DevkitWalletColors.secondary,
            ),
        shape = RoundedCornerShape(16.dp),
        enabled = enabled,
        modifier = modifier ?: Modifier
            .height(80.dp)
            .fillMaxWidth(0.9f)
            .padding(vertical = 8.dp, horizontal = 8.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
    ) {
        Text(
            text = text,
        )
    }
}
