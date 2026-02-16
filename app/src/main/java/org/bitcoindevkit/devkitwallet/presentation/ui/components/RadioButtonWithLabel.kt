/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.bitcoindevkit.devkitwallet.presentation.theme.inter

@Composable
fun RadioButtonWithLabel(label: String, isSelected: Boolean, onSelect: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier =
            Modifier
                .padding(0.dp)
                .selectable(
                    selected = isSelected,
                    onClick = onSelect,
                ),
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            colors =
                RadioButtonDefaults.colors(
                    selectedColor = colorScheme.primary,
                    unselectedColor = colorScheme.outline,
                ),
            modifier =
                Modifier
                    .padding(start = 8.dp)
                    .size(40.dp),
        )
        Text(
            text = label,
            color = colorScheme.onSurface,
            fontFamily = inter,
            fontSize = 12.sp,
            modifier =
                Modifier
                    .clickable(onClick = onSelect)
                    .padding(0.dp),
        )
    }
}
