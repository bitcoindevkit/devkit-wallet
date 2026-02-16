/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.composables.icons.lucide.List
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.RotateCcw
import org.bitcoindevkit.devkitwallet.presentation.navigation.ActiveWalletsScreen
import org.bitcoindevkit.devkitwallet.presentation.navigation.CreateNewWalletScreen
import org.bitcoindevkit.devkitwallet.presentation.navigation.WalletRecoveryScreen
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.presentation.theme.inter

@Composable
internal fun WalletChoiceScreen(navController: NavController) {
    val colorScheme = MaterialTheme.colorScheme

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Logo area
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .border(
                        width = 2.dp,
                        color = colorScheme.primary.copy(alpha = 0.20f),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "\u20BF",
                    color = colorScheme.primary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Devkit Wallet",
                fontFamily = inter,
                fontSize = 28.sp,
                fontWeight = FontWeight.Light,
                color = colorScheme.onSurface,
            )
            Text(
                text = "BITCOIN DEVELOPMENT KIT",
                fontSize = 13.sp,
                color = DevkitWalletColors.subtle,
                letterSpacing = 1.sp,
            )

            Spacer(Modifier.height(64.dp))

            // Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                WelcomeButton(
                    icon = Lucide.List,
                    iconTint = colorScheme.primary,
                    title = "Use an Active Wallet",
                    description = "Open an existing wallet on this device",
                    borderColor = colorScheme.outline.copy(alpha = 0.15f),
                    onClick = { navController.navigate(ActiveWalletsScreen) },
                )
                WelcomeButton(
                    icon = Lucide.Plus,
                    iconTint = colorScheme.tertiary,
                    title = "Create a New Wallet",
                    description = "Generate fresh keys and start from scratch",
                    borderColor = colorScheme.outline.copy(alpha = 0.15f),
                    onClick = { navController.navigate(CreateNewWalletScreen) },
                )
                WelcomeButton(
                    icon = Lucide.RotateCcw,
                    iconTint = colorScheme.secondary,
                    title = "Recover an Existing Wallet",
                    description = "Import from descriptor or recovery phrase",
                    borderColor = colorScheme.outline.copy(alpha = 0.15f),
                    onClick = { navController.navigate(WalletRecoveryScreen) },
                )
            }
        }
    }
}

@Composable
private fun WelcomeButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    description: String,
    borderColor: Color,
    onClick: () -> Unit,
) {
    OutlinedCard(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.5.dp, borderColor),
        colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .border(
                        width = 1.dp,
                        color = iconTint.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(14.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp),
                )
            }
            Column {
                Text(
                    text = title,
                    fontFamily = inter,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = description,
                    fontFamily = inter,
                    fontSize = 12.sp,
                    color = DevkitWalletColors.subtle,
                )
            }
        }
    }
}
