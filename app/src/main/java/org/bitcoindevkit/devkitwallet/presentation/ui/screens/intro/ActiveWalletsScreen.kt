/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.bitcoindevkit.devkitwallet.data.SingleWallet
import org.bitcoindevkit.devkitwallet.domain.DwLogger
import org.bitcoindevkit.devkitwallet.domain.DwLogger.LogLevel.INFO
import org.bitcoindevkit.devkitwallet.presentation.WalletCreateType
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.presentation.theme.monoRegular
import org.bitcoindevkit.devkitwallet.presentation.theme.quattroRegular
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar

private const val TAG = "ActiveWalletsScreen"

@Composable
internal fun ActiveWalletsScreen(
    activeWallets: List<SingleWallet>,
    navController: NavController,
    onBuildWalletButtonClicked: (WalletCreateType) -> Unit,
) {
    Scaffold(
        topBar = {
            SecondaryScreensAppBar(title = "Choose a Wallet", navigation = { navController.navigateUp() })
        },
        containerColor = DevkitWalletColors.primary,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            activeWallets.forEach {
                ActiveWalletCard(wallet = it, onBuildWalletButtonClicked)
            }
        }
        if (activeWallets.isEmpty()) {
            Text(
                text = "No active wallets.",
                fontSize = 16.sp,
                fontFamily = quattroRegular,
                color = DevkitWalletColors.white,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Composable
fun ActiveWalletCard(wallet: SingleWallet, onBuildWalletButtonClicked: (WalletCreateType) -> Unit) {
    Row(
        Modifier
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .fillMaxWidth()
            .background(
                color = DevkitWalletColors.primaryLight,
                shape = RoundedCornerShape(16.dp),
            ).clickable {
                DwLogger.log(INFO, "Activating existing wallet: ${wallet.name}")
                onBuildWalletButtonClicked(WalletCreateType.LOADEXISTING(wallet))
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
    ) {
        Text(
            "Name: ${wallet.name}\nNetwork: ${wallet.network}\nScript Type: ${wallet.scriptType}",
            fontFamily = monoRegular,
            fontSize = 12.sp,
            lineHeight = 20.sp,
            color = DevkitWalletColors.white,
            modifier = Modifier.padding(16.dp),
        )
    }
}
