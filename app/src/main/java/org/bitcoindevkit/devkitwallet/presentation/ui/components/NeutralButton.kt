/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.bitcoindevkit.devkitwallet.presentation.theme.inter

@Composable
fun NeutralButton(text: String, enabled: Boolean, modifier: Modifier? = null, onClick: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    Button(
        onClick = onClick,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = colorScheme.secondary,
                disabledContainerColor = colorScheme.secondary.copy(alpha = 0.4f),
            ),
        shape = RoundedCornerShape(16.dp),
        enabled = enabled,
        modifier =
            modifier ?: Modifier
                .height(60.dp)
                .fillMaxWidth(0.9f)
                .padding(vertical = 4.dp, horizontal = 8.dp),
    ) {
        Text(
            text = text,
            fontFamily = inter,
        )
    }
}
