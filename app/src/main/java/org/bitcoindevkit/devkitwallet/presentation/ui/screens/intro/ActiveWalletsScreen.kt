/*
 * Copyright 2021-2025 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import org.bitcoindevkit.devkitwallet.presentation.WalletCreateType
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.presentation.theme.quattroRegular
import org.bitcoindevkit.devkitwallet.presentation.theme.standardText
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
        containerColor = DevkitWalletColors.primary
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            activeWallets.forEach {
                ActiveWalletCard(wallet = it, onBuildWalletButtonClicked)
            }
            if (activeWallets.isEmpty()) {
                Text(
                    text = "No active wallets.",
                    fontSize = 16.sp,
                    fontFamily = quattroRegular,
                    color = DevkitWalletColors.white,
                    modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun ActiveWalletCard(wallet: SingleWallet, onBuildWalletButtonClicked: (WalletCreateType) -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            // Padding outside the card
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(
                color = DevkitWalletColors.primaryLight,
                shape = RoundedCornerShape(16.dp)
            )
            // Padding inside the card
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .clickable { onBuildWalletButtonClicked(WalletCreateType.LOADEXISTING(wallet)) },
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start
    ) {
        DataField("Name", wallet.name)
        DataField("Network", wallet.network.toString())
        DataField("Script Type", wallet.scriptType.toString())
    }
}

@Composable
fun DataField(name: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Text(
            text = name,
            style = standardText,
            lineHeight = 18.sp,
        )
        Text(
            text = value,
            style = standardText,
            lineHeight = 18.sp,
        )
    }
}
