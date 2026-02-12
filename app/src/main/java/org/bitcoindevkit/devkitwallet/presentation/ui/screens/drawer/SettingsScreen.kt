/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.drawer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.History
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.SatelliteDish
import com.composables.icons.lucide.ScrollText
import org.bitcoindevkit.devkitwallet.presentation.navigation.AboutScreen
import org.bitcoindevkit.devkitwallet.presentation.navigation.BlockchainClientScreen
import org.bitcoindevkit.devkitwallet.presentation.navigation.LogsScreen
import org.bitcoindevkit.devkitwallet.presentation.navigation.RecoveryPhraseScreen
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.presentation.theme.quattroRegular
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar

@Composable
internal fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            SecondaryScreensAppBar(
                title = "Settings",
                navigation = { navController.popBackStack() },
            )
        },
        containerColor = DevkitWalletColors.primary,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            item {
                SettingsItem(
                    icon = { Icon(Lucide.Info, contentDescription = "About", tint = DevkitWalletColors.white) },
                    label = "About",
                    onClick = { navController.navigate(AboutScreen) },
                )
            }
            item {
                SettingsItem(
                    icon = { Icon(Lucide.History, contentDescription = "Wallet Recovery Data", tint = DevkitWalletColors.white) },
                    label = "Wallet Recovery Data",
                    onClick = { navController.navigate(RecoveryPhraseScreen) },
                )
            }
            item {
                SettingsItem(
                    icon = { Icon(Lucide.SatelliteDish, contentDescription = "Compact Block Filters Node", tint = DevkitWalletColors.white) },
                    label = "Compact Block Filters Node",
                    onClick = { navController.navigate(BlockchainClientScreen) },
                )
            }
            item {
                SettingsItem(
                    icon = { Icon(Lucide.ScrollText, contentDescription = "Logs", tint = DevkitWalletColors.white) },
                    label = "Logs",
                    onClick = { navController.navigate(LogsScreen) },
                )
            }
        }
    }
}

@Composable
private fun SettingsItem(
    icon: @Composable () -> Unit,
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon()
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            color = DevkitWalletColors.white,
            fontFamily = quattroRegular,
        )
    }
}
