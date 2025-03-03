/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.bitcoindevkit.devkitwallet.presentation.navigation.WalletScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.presentation.theme.monoRegular
import org.bitcoindevkit.devkitwallet.presentation.ui.components.NeutralButton
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.KyotoNodeStatus
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.WalletScreenAction
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.WalletScreenState

@Composable
internal fun BlockchainClientScreen(
    state: WalletScreenState,
    onAction: (WalletScreenAction) -> Unit,
    navController: NavController
) {

    Scaffold(
        topBar = {
            SecondaryScreensAppBar(
                title = "Compact Block Filters Node",
                navigation = { navController.navigate(WalletScreen) }
            )
        },
        containerColor = DevkitWalletColors.primary
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(vertical = 32.dp, horizontal = 16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                val status = if (state.kyotoNodeStatus == KyotoNodeStatus.Running) "Online" else "Offline"
                Text(
                    text = "CBF Node Status: $status",
                    color = DevkitWalletColors.white,
                    fontSize = 14.sp,
                    fontFamily = monoRegular,
                    textAlign = TextAlign.Start,
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(size = 21.dp)
                        .clip(shape = CircleShape)
                        .background(if (state.kyotoNodeStatus == KyotoNodeStatus.Running) Color(0xFF2A9D8F) else Color(0xFFE76F51) )
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text(
                    text = "Latest known block:",
                    color = DevkitWalletColors.white,
                    fontSize = 14.sp,
                    fontFamily = monoRegular,
                    textAlign = TextAlign.Start,
                )
                Text(
                    text = "${state.latestBlock}",
                    color = DevkitWalletColors.white,
                    fontSize = 14.sp,
                    fontFamily = monoRegular,
                    textAlign = TextAlign.Start,
                )
            }

            Spacer(modifier = Modifier.padding(16.dp))

            NeutralButton(
                text = "Start Node",
                enabled = state.kyotoNodeStatus == KyotoNodeStatus.Stopped,
                onClick = { onAction(WalletScreenAction.StartKyotoNode) }
            )
            NeutralButton(
                text = "Start Sync",
                enabled = state.kyotoNodeStatus == KyotoNodeStatus.Running,
                onClick = { onAction(WalletScreenAction.StartKyotoSync) }
            )
            NeutralButton(
                text = "Stop Node",
                enabled = state.kyotoNodeStatus == KyotoNodeStatus.Running,
                onClick = { onAction(WalletScreenAction.StopKyotoNode) }
            )
        }
    }
}
