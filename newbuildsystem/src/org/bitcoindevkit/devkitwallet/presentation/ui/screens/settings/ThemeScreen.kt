/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Moon
import com.composables.icons.lucide.Sun
import org.bitcoindevkit.devkitwallet.presentation.theme.inter
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar

@Composable
internal fun ThemeScreen(useDarkTheme: Boolean, onToggleTheme: () -> Unit, navController: NavController) {
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            SecondaryScreensAppBar(
                title = "Theme",
                navigation = { navController.popBackStack() },
            )
        },
        containerColor = colorScheme.surface,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.5.dp,
                        color = colorScheme.outline.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(20.dp),
                    ).clip(RoundedCornerShape(20.dp)),
            ) {
                ThemeItem(
                    icon = Lucide.Sun,
                    iconTint = colorScheme.primary,
                    title = "DayGlow",
                    description = "Light theme",
                    isSelected = !useDarkTheme,
                    onClick = { if (useDarkTheme) onToggleTheme() },
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = colorScheme.outline.copy(alpha = 0.06f),
                )
                ThemeItem(
                    icon = Lucide.Moon,
                    iconTint = colorScheme.secondary,
                    title = "NightGlow",
                    description = "Dark theme",
                    isSelected = useDarkTheme,
                    onClick = { if (!useDarkTheme) onToggleTheme() },
                )
            }
        }
    }
}

@Composable
private fun ThemeItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(iconTint.copy(alpha = if (isSelected) 0.10f else 0.05f))
                .border(
                    width = 1.dp,
                    color = iconTint.copy(alpha = if (isSelected) 0.15f else 0.07f),
                    shape = RoundedCornerShape(14.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) iconTint else iconTint.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = if (isSelected) colorScheme.onSurface else colorScheme.onSurface.copy(alpha = 0.5f),
                fontFamily = inter,
                fontSize = 15.sp,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = colorScheme.onSurface.copy(alpha = 0.5f),
                fontFamily = inter,
                fontSize = 12.sp,
            )
        }
        if (isSelected) {
            Icon(
                imageVector = Lucide.Check,
                contentDescription = "Selected",
                tint = colorScheme.primary,
                modifier = Modifier.size(18.dp),
            )
        } else {
            Spacer(modifier = Modifier.width(18.dp))
        }
    }
}
