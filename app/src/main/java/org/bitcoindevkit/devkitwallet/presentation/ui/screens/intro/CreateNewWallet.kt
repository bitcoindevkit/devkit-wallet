/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro

import androidx.compose.foundation.background
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.bitcoindevkit.Network
import org.bitcoindevkit.devkitwallet.data.ActiveWalletScriptType
import org.bitcoindevkit.devkitwallet.data.NewWalletConfig
import org.bitcoindevkit.devkitwallet.domain.DwLogger
import org.bitcoindevkit.devkitwallet.domain.DwLogger.LogLevel.INFO
import org.bitcoindevkit.devkitwallet.domain.supportedNetworks
import org.bitcoindevkit.devkitwallet.presentation.WalletCreateType
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.presentation.theme.inter
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar

@Composable
internal fun CreateNewWalletScreen(
    navController: NavController,
    onBuildWalletButtonClicked: (WalletCreateType) -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme

    val walletName = remember { mutableStateOf("") }
    val selectedNetwork: MutableState<Network> = remember { mutableStateOf(Network.SIGNET) }
    val selectedScriptType: MutableState<ActiveWalletScriptType> =
        remember { mutableStateOf(ActiveWalletScriptType.P2TR) }
    val scriptTypes = listOf(ActiveWalletScriptType.P2TR, ActiveWalletScriptType.P2WPKH)

    Scaffold(
        topBar = {
            SecondaryScreensAppBar(title = "Create a New Wallet", navigation = { navController.navigateUp() })
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
        ) {
            Spacer(Modifier.height(20.dp))

            // Wallet name
            FormLabel("Wallet Name")
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = walletName.value,
                onValueChange = { walletName.value = it },
                placeholder = {
                    Text(
                        text = "Give your wallet a name",
                        color = colorScheme.outlineVariant,
                        fontFamily = inter,
                        fontSize = 15.sp,
                    )
                },
                singleLine = true,
                textStyle = TextStyle(fontFamily = inter, color = colorScheme.onSurface, fontSize = 15.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = colorScheme.primary,
                    focusedBorderColor = colorScheme.primary.copy(alpha = 0.40f),
                    unfocusedBorderColor = colorScheme.outline.copy(alpha = 0.15f),
                ),
                shape = RoundedCornerShape(16.dp),
            )

            Spacer(Modifier.height(24.dp))

            // Network
            FormLabel("Network")
            OptionGroup {
                supportedNetworks.forEach { network ->
                    ThemedRadioOption(
                        label = network.displayString(),
                        isSelected = selectedNetwork.value == network,
                        onSelect = { selectedNetwork.value = network },
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Script Type
            FormLabel("Script Type")
            OptionGroup {
                scriptTypes.forEach { scriptType ->
                    ThemedRadioOption(
                        label = scriptType.displayString(),
                        isSelected = selectedScriptType.value == scriptType,
                        onSelect = { selectedScriptType.value = scriptType },
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Create button
            Button(
                onClick = {
                    val newWalletConfig = NewWalletConfig(
                        name = walletName.value,
                        network = selectedNetwork.value,
                        scriptType = selectedScriptType.value,
                    )
                    DwLogger.log(INFO, "Creating new wallet named ${newWalletConfig.name}")
                    onBuildWalletButtonClicked(WalletCreateType.FROMSCRATCH(newWalletConfig))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary,
                ),
            ) {
                Text(
                    text = "Create Wallet",
                    fontFamily = inter,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
internal fun FormLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontFamily = inter,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = DevkitWalletColors.subtle,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(bottom = 10.dp),
    )
}

@Composable
internal fun OptionGroup(content: @Composable () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.5.dp,
                color = colorScheme.outline.copy(alpha = 0.10f),
                shape = RoundedCornerShape(20.dp),
            ).padding(8.dp),
    ) {
        content()
    }
}

@Composable
internal fun ThemedRadioOption(label: String, isSelected: Boolean, onSelect: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = isSelected, onClick = onSelect)
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .border(
                    width = 2.dp,
                    color = if (isSelected) colorScheme.primary else colorScheme.outlineVariant,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(colorScheme.primary, CircleShape),
                )
            }
        }
        Text(
            text = label,
            fontFamily = inter,
            fontSize = 14.sp,
            color = colorScheme.onSurface,
        )
    }
}

fun ActiveWalletScriptType.displayString(): String {
    return when (this) {
        ActiveWalletScriptType.P2TR -> "P2TR (Taproot, BIP-86)"
        ActiveWalletScriptType.P2WPKH -> "P2WPKH (Native Segwit, BIP-84)"
        ActiveWalletScriptType.UNKNOWN -> TODO()
        ActiveWalletScriptType.UNRECOGNIZED -> TODO()
    }
}

fun Network.displayString(): String {
    return when (this) {
        Network.TESTNET -> "Testnet 3"
        Network.TESTNET4 -> "Testnet 4"
        Network.REGTEST -> "Regtest"
        Network.SIGNET -> "Signet"
        Network.BITCOIN -> TODO()
    }
}
