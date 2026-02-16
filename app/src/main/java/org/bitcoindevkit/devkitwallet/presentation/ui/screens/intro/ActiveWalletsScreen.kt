/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lucide
import org.bitcoindevkit.devkitwallet.data.SingleWallet
import org.bitcoindevkit.devkitwallet.domain.DwLogger
import org.bitcoindevkit.devkitwallet.domain.DwLogger.LogLevel.INFO
import org.bitcoindevkit.devkitwallet.presentation.WalletCreateType
import org.bitcoindevkit.devkitwallet.presentation.theme.NightGlowSubtle
import org.bitcoindevkit.devkitwallet.presentation.theme.inter
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar

private const val TAG = "ActiveWalletsScreen"

@Composable
internal fun ActiveWalletsScreen(
    activeWallets: List<SingleWallet>,
    navController: NavController,
    onBuildWalletButtonClicked: (WalletCreateType) -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            SecondaryScreensAppBar(title = "Choose a Wallet", navigation = { navController.navigateUp() })
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (activeWallets.isEmpty()) {
                Text(
                    text = "No active wallets.",
                    fontFamily = inter,
                    fontSize = 14.sp,
                    color = NightGlowSubtle,
                )
            } else {
                activeWallets.forEach { wallet ->
                    OutlinedCard(
                        onClick = {
                            DwLogger.log(INFO, "Activating existing wallet: ${wallet.name}")
                            onBuildWalletButtonClicked(WalletCreateType.LOADEXISTING(wallet))
                        },
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.5.dp, colorScheme.outline.copy(alpha = 0.12f)),
                        colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = wallet.name,
                                    fontFamily = inter,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colorScheme.onSurface,
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(top = 4.dp),
                                ) {
                                    WalletChip(text = wallet.network.name)
                                    WalletChip(text = wallet.scriptType.name)
                                }
                            }
                            Icon(
                                imageVector = Lucide.ChevronRight,
                                contentDescription = "Open",
                                tint = colorScheme.outlineVariant,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WalletChip(text: String) {
    val colorScheme = MaterialTheme.colorScheme
    Text(
        text = text,
        fontFamily = inter,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = colorScheme.onSurfaceVariant,
        modifier = Modifier
            .padding(0.dp),
    )
}
