/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import org.bitcoindevkit.devkitwallet.presentation.WalletCreateType
import org.bitcoindevkit.devkitwallet.data.ActiveWalletScriptType
import org.bitcoindevkit.devkitwallet.data.NewWalletConfig
import org.bitcoindevkit.devkitwallet.presentation.ui.components.NeutralButton
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.presentation.theme.monoRegular
import org.bitcoindevkit.Network
import org.bitcoindevkit.devkitwallet.presentation.theme.standardText
import org.bitcoindevkit.devkitwallet.presentation.ui.components.NeutralButton
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar
import org.bitcoindevkit.devkitwallet.presentation.ui.components.WalletOptionsCard

@Composable
internal fun CreateNewWalletScreen(
    navController: NavController,
    onBuildWalletButtonClicked: (WalletCreateType) -> Unit
) {
    Scaffold(
        topBar = {
            SecondaryScreensAppBar(title = "Create a New Wallet", navigation = { navController.navigateUp() })
        },
        containerColor = DevkitWalletColors.primary
    ) { paddingValues ->

        ConstraintLayout(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(vertical = 16.dp)
        ) {
            val (choices, button) = createRefs()

            val walletName: MutableState<String> = remember { mutableStateOf("") }
            val selectedNetwork: MutableState<Network> = remember { mutableStateOf(Network.SIGNET) }
            val selectedScriptType: MutableState<ActiveWalletScriptType> = remember { mutableStateOf(ActiveWalletScriptType.P2TR) }
            val scriptTypes = listOf(ActiveWalletScriptType.P2TR, ActiveWalletScriptType.P2WPKH)

            Column(
                modifier = Modifier
                    .constrainAs(choices) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .fillMaxSize()
                    .background(color = DevkitWalletColors.primary)
                    .padding(horizontal = 32.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    value = walletName.value,
                    onValueChange = { walletName.value = it },
                    label = {
                        Text(
                            text = "Give your wallet a name",
                            style = standardText,
                            color = DevkitWalletColors.white,
                        )
                    },
                    singleLine = true,
                    textStyle = TextStyle(fontFamily = monoRegular, color = DevkitWalletColors.white),
                    colors = OutlinedTextFieldDefaults.colors(
                        cursorColor = DevkitWalletColors.accent1,
                        focusedBorderColor = DevkitWalletColors.accent1,
                        unfocusedBorderColor = DevkitWalletColors.white,
                    ),
                )

                Spacer(modifier = Modifier.padding(12.dp))
                WalletOptionsCard(scriptTypes, selectedNetwork, selectedScriptType)
                Spacer(modifier = Modifier.padding(16.dp))
            }

            Column(
                modifier = Modifier
                    .constrainAs(button) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                NeutralButton(
                    text = "Create Wallet",
                    enabled = true,
                    modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp)),
                    onClick = {
                        val newWalletConfig = NewWalletConfig(
                            name = walletName.value,
                            network = selectedNetwork.value,
                            scriptType = selectedScriptType.value
                        )
                        onBuildWalletButtonClicked(
                            WalletCreateType.FROMSCRATCH(newWalletConfig)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun NetworkOptionCard(networks: List<Network>, selectedNetwork: MutableState<Network>) {
    Column(
        Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = DevkitWalletColors.secondary,
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = DevkitWalletColors.primaryLight,
                shape = RoundedCornerShape(16.dp)
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Network",
            fontFamily = monoRegular,
            fontSize = 18.sp,
            color = DevkitWalletColors.white,
            modifier = Modifier.padding(top = 8.dp, start = 8.dp, bottom = 8.dp)
        )

        HorizontalDivider(color = DevkitWalletColors.secondary, thickness = 2.dp, modifier = Modifier.padding(bottom = 8.dp))

        networks.forEachIndexed { index, it ->
            RadioButtonWithLabel(
                label = it.displayString(),
                isSelected = selectedNetwork.value == it,
                onSelect = { selectedNetwork.value = it }
            )
            if (index == 2) Spacer(modifier = Modifier.padding(bottom = 8.dp))
        }
    }
}

@Composable
fun ScriptTypeOptionCard(scriptTypes: List<ActiveWalletScriptType>, selectedScriptType: MutableState<ActiveWalletScriptType>) {
    Column(
        Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = DevkitWalletColors.secondary,
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = DevkitWalletColors.primaryLight,
                shape = RoundedCornerShape(16.dp)
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Script Type",
            fontFamily = monoRegular,
            fontSize = 18.sp,
            color = DevkitWalletColors.white,
            modifier = Modifier.padding(top = 8.dp, start = 8.dp, bottom = 8.dp)
        )

        HorizontalDivider(color = DevkitWalletColors.secondary, thickness = 2.dp, modifier = Modifier.padding(bottom = 8.dp))

        scriptTypes.forEachIndexed { index, it ->
            RadioButtonWithLabel(
                label = it.displayString(),
                isSelected = selectedScriptType.value == it,
                onSelect = { selectedScriptType.value = it }
            )
            if (index == 1) Spacer(modifier = Modifier.padding(bottom = 8.dp))
        }
    }
}

@Composable
fun RadioButtonWithLabel(label: String, isSelected: Boolean, onSelect: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier
            .padding(0.dp)
            .selectable(
                selected = isSelected,
                onClick = onSelect
            )
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = DevkitWalletColors.accent1,
                unselectedColor = DevkitWalletColors.accent2
            ),
            modifier = Modifier
                .padding(0.dp)
                .size(40.dp)
        )
        Text(
            text = label,
            color = DevkitWalletColors.white,
            fontFamily = monoRegular,
            fontSize = 14.sp,
            modifier = Modifier
                .clickable(onClick = onSelect)
                .padding(0.dp)
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
