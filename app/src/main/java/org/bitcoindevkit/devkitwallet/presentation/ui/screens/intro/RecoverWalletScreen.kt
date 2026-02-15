/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.bitcoindevkit.Descriptor
import org.bitcoindevkit.DescriptorSecretKey
import org.bitcoindevkit.KeychainKind
import org.bitcoindevkit.Mnemonic
import org.bitcoindevkit.Network
import org.bitcoindevkit.devkitwallet.data.ActiveWalletScriptType
import org.bitcoindevkit.devkitwallet.data.RecoverWalletConfig
import org.bitcoindevkit.devkitwallet.domain.DwLogger
import org.bitcoindevkit.devkitwallet.domain.DwLogger.LogLevel.INFO
import org.bitcoindevkit.devkitwallet.domain.bip39WordList
import org.bitcoindevkit.devkitwallet.domain.createScriptAppropriateDescriptor
import org.bitcoindevkit.devkitwallet.domain.supportedNetworks
import org.bitcoindevkit.devkitwallet.presentation.WalletCreateType
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.presentation.theme.inter
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar

@Composable
internal fun RecoverWalletScreen(onAction: (WalletCreateType) -> Unit, navController: NavController) {
    val colorScheme = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Recovery Phrase", "Descriptor")

    var walletName by remember { mutableStateOf("") }
    val selectedNetwork: MutableState<Network> = remember { mutableStateOf(Network.SIGNET) }
    val selectedScriptType: MutableState<ActiveWalletScriptType> =
        remember { mutableStateOf(ActiveWalletScriptType.P2TR) }
    val scriptTypes = listOf(ActiveWalletScriptType.P2TR, ActiveWalletScriptType.P2WPKH)

    var recoveryPhrase by remember { mutableStateOf("") }
    var descriptorString by remember { mutableStateOf("") }
    var changeDescriptorString by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            SecondaryScreensAppBar(title = "Recover a Wallet", navigation = { navController.navigateUp() })
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(20.dp))

            // Tab selector
            FormLabel("Recovery Method")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.5.dp,
                        color = colorScheme.outline.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(20.dp),
                    )
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                tabs.forEachIndexed { index, label ->
                    val isSelected = selectedTab == index
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f)
                            .selectable(selected = isSelected, onClick = { selectedTab = index })
                            .then(
                                if (isSelected) {
                                    Modifier.border(
                                        width = 1.5.dp,
                                        color = colorScheme.primary.copy(alpha = 0.30f),
                                        shape = RoundedCornerShape(16.dp),
                                    )
                                } else {
                                    Modifier
                                }
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                    ) {
                        Text(
                            text = label,
                            fontFamily = inter,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                            color = if (isSelected) colorScheme.primary else colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Wallet name (always shown)
            FormLabel("Wallet Name")
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = walletName,
                onValueChange = { walletName = it },
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

            if (selectedTab == 0) {
                // Recovery Phrase tab
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

                Spacer(Modifier.height(24.dp))

                // Recovery phrase input
                FormLabel("Recovery Phrase")
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = recoveryPhrase,
                    onValueChange = { recoveryPhrase = it },
                    placeholder = {
                        Text(
                            text = "Enter your 12-word recovery phrase",
                            color = colorScheme.outlineVariant,
                            fontFamily = inter,
                            fontSize = 15.sp,
                        )
                    },
                    singleLine = false,
                    minLines = 3,
                    textStyle = TextStyle(fontFamily = inter, color = colorScheme.onSurface, fontSize = 14.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        cursorColor = colorScheme.primary,
                        focusedBorderColor = colorScheme.primary.copy(alpha = 0.40f),
                        unfocusedBorderColor = colorScheme.outline.copy(alpha = 0.15f),
                    ),
                    shape = RoundedCornerShape(16.dp),
                )
            } else {
                // Descriptor tab
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

                // Descriptor input
                FormLabel("Descriptor")
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = descriptorString,
                    onValueChange = { descriptorString = it },
                    placeholder = {
                        Text(
                            text = "Input your descriptor here",
                            color = colorScheme.outlineVariant,
                            fontFamily = inter,
                            fontSize = 15.sp,
                        )
                    },
                    singleLine = false,
                    minLines = 4,
                    textStyle = TextStyle(fontFamily = inter, color = colorScheme.onSurface, fontSize = 13.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        cursorColor = colorScheme.primary,
                        focusedBorderColor = colorScheme.primary.copy(alpha = 0.40f),
                        unfocusedBorderColor = colorScheme.outline.copy(alpha = 0.15f),
                    ),
                    shape = RoundedCornerShape(16.dp),
                )

                Spacer(Modifier.height(16.dp))

                // Change descriptor input
                FormLabel("Change Descriptor")
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = changeDescriptorString,
                    onValueChange = { changeDescriptorString = it },
                    placeholder = {
                        Text(
                            text = "Input your change descriptor here",
                            color = colorScheme.outlineVariant,
                            fontFamily = inter,
                            fontSize = 15.sp,
                        )
                    },
                    singleLine = false,
                    minLines = 4,
                    textStyle = TextStyle(fontFamily = inter, color = colorScheme.onSurface, fontSize = 13.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        cursorColor = colorScheme.primary,
                        focusedBorderColor = colorScheme.primary.copy(alpha = 0.40f),
                        unfocusedBorderColor = colorScheme.outline.copy(alpha = 0.15f),
                    ),
                    shape = RoundedCornerShape(16.dp),
                )
            }

            Spacer(Modifier.weight(1f))

            // Recover button
            Button(
                onClick = {
                    if (selectedTab == 0) {
                        // Recovery phrase flow
                        if (recoveryPhrase.isEmpty()) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    "You must provide a recovery phrase to recover a wallet.",
                                )
                            }
                            return@Button
                        }
                        Log.i("RecoverWalletScreen", "Recovering wallet with recovery phrase")
                        val parsingResult = parseRecoveryPhrase(recoveryPhrase)

                        if (parsingResult is RecoveryPhraseValidationResult.Invalid) {
                            scope.launch {
                                snackbarHostState.showSnackbar(parsingResult.reason)
                            }
                        } else if (parsingResult is RecoveryPhraseValidationResult.ProbablyValid) {
                            val mnemonic = Mnemonic.fromString(parsingResult.recoveryPhrase)
                            val bip32ExtendedRootKey = DescriptorSecretKey(selectedNetwork.value, mnemonic, null)
                            val descriptor =
                                createScriptAppropriateDescriptor(
                                    scriptType = selectedScriptType.value,
                                    bip32ExtendedRootKey = bip32ExtendedRootKey,
                                    network = selectedNetwork.value,
                                    keychain = KeychainKind.EXTERNAL,
                                )
                            val changeDescriptor =
                                createScriptAppropriateDescriptor(
                                    scriptType = selectedScriptType.value,
                                    bip32ExtendedRootKey = bip32ExtendedRootKey,
                                    network = selectedNetwork.value,
                                    keychain = KeychainKind.INTERNAL,
                                )
                            val recoverWalletConfig =
                                RecoverWalletConfig(
                                    name = walletName,
                                    network = selectedNetwork.value,
                                    scriptType = selectedScriptType.value,
                                    descriptor = descriptor,
                                    changeDescriptor = changeDescriptor,
                                    recoveryPhrase = parsingResult.recoveryPhrase,
                                )
                            DwLogger.log(INFO, "Recovering wallet with recovery phrase (name: $walletName)")
                            onAction(WalletCreateType.RECOVER(recoverWalletConfig))
                        }
                    } else {
                        // Descriptor flow
                        if (descriptorString.isEmpty() || changeDescriptorString.isEmpty()) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    "You must provide both a descriptor and a change descriptor.",
                                )
                            }
                            return@Button
                        }
                        Log.i("RecoverWalletScreen", "Recovering wallet with descriptors")
                        val descriptor = Descriptor(descriptorString, selectedNetwork.value)
                        val changeDescriptor = Descriptor(changeDescriptorString, selectedNetwork.value)
                        val recoverWalletConfig =
                            RecoverWalletConfig(
                                name = walletName,
                                network = selectedNetwork.value,
                                scriptType = null,
                                descriptor = descriptor,
                                changeDescriptor = changeDescriptor,
                                recoveryPhrase = null,
                            )
                        DwLogger.log(INFO, "Recovering wallet with descriptors (name: $walletName)")
                        onAction(WalletCreateType.RECOVER(recoverWalletConfig))
                    }
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
                    text = "Recover Wallet",
                    fontFamily = inter,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

private fun parseRecoveryPhrase(recoveryPhrase: String): RecoveryPhraseValidationResult {
    val words = recoveryPhrase.trim().split(" ")
    if (words.size != 12) {
        return RecoveryPhraseValidationResult.Invalid("Recovery phrase must have 12 words")
    }
    if (words.any { it !in bip39WordList }) {
        return RecoveryPhraseValidationResult.Invalid("Invalid word in recovery phrase")
    }
    return RecoveryPhraseValidationResult.ProbablyValid(recoveryPhrase)
}

sealed class RecoveryPhraseValidationResult {
    data class ProbablyValid(val recoveryPhrase: String) : RecoveryPhraseValidationResult()

    data class Invalid(val reason: String) : RecoveryPhraseValidationResult()
}
