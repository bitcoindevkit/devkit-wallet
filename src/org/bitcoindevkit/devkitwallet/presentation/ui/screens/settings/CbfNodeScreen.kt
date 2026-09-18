/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.bitcoindevkit.Network
import org.bitcoindevkit.devkitwallet.data.NodePeer
import org.bitcoindevkit.devkitwallet.domain.bundledCheckpoint
import org.bitcoindevkit.devkitwallet.presentation.theme.inter
import org.bitcoindevkit.devkitwallet.presentation.ui.components.RadioButtonWithLabel
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.CbfNodeStatus
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.ScanChoice
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.WalletScreenAction
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.WalletScreenState
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.displayString
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.recoveryScanChoices

/**
 * Settings screen for managing the Kyoto Compact Block Filters (CBF) node.
 *
 * Shows node status, latest known block height, a configurable peer list, and Start/Stop controls that dispatch to
 * [WalletViewModel]. A wallet that has never scanned the chain gets a [ScanTypeDialog] asking where to start; once it
 * has scanned once, starting the node just resumes from the wallet's own checkpoint.
 */
@Composable
internal fun CbfNodeScreen(
    state: WalletScreenState,
    onAction: (WalletScreenAction) -> Unit,
    navController: NavController,
) {
    val colorScheme = MaterialTheme.colorScheme
    val isRunning = state.kyotoNodeStatus == CbfNodeStatus.Running
    var showScanTypeDialog by rememberSaveable { mutableStateOf(false) }

    if (showScanTypeDialog) {
        ScanTypeDialog(
            network = state.network,
            onDismiss = { showScanTypeDialog = false },
            onConfirm = { scanChoice ->
                showScanTypeDialog = false
                onAction(WalletScreenAction.ActivateCbfNode(scanChoice))
            },
        )
    }

    Scaffold(
        topBar = {
            SecondaryScreensAppBar(
                title = "Compact Block Filters Node",
                navigation = { navController.popBackStack() },
            )
        },
        containerColor = colorScheme.surface,
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .padding(vertical = 32.dp, horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "CBF Node Status:",
                    color = colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontFamily = inter,
                    textAlign = TextAlign.Start,
                )
                Text(
                    text = if (isRunning) "Online" else "Offline",
                    color = if (isRunning) Color(0xFF8FD998) else Color(0xFFE76F51),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = inter,
                    textAlign = TextAlign.End,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            ) {
                Text(
                    text = "Latest known block:",
                    color = colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontFamily = inter,
                    textAlign = TextAlign.Start,
                )
                Text(
                    text = "${state.bestBlockHeight}",
                    color = colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontFamily = inter,
                    textAlign = TextAlign.Start,
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))
            HorizontalDivider(color = colorScheme.outline.copy(alpha = 0.30f))
            Spacer(modifier = Modifier.padding(8.dp))

            PeersSection(state = state, onAction = onAction)

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (state.initialRecoveryDone) {
                        onAction(WalletScreenAction.ActivateCbfNode(ScanChoice.Sync))
                    } else {
                        showScanTypeDialog = true
                    }
                },
                enabled = !isRunning,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = colorScheme.secondary,
                        disabledContainerColor = colorScheme.secondary.copy(alpha = 0.4f),
                    ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp),
            ) {
                Text(
                    text = "Start Node",
                    fontFamily = inter,
                    fontSize = 15.sp,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { onAction(WalletScreenAction.StopKyotoNode) },
                enabled = isRunning,
                shape = RoundedCornerShape(16.dp),
                border =
                    BorderStroke(
                        1.5.dp,
                        if (isRunning) colorScheme.primary else colorScheme.primary.copy(alpha = 0.4f),
                    ),
                modifier = Modifier.fillMaxWidth().height(52.dp),
            ) {
                Text(
                    text = "Stop Node",
                    fontFamily = inter,
                    fontSize = 15.sp,
                    color = if (isRunning) colorScheme.primary else colorScheme.primary.copy(alpha = 0.4f),
                )
            }
        }
    }
}

/**
 * Dialog shown the first time the user starts the node on a wallet that has never scanned the chain, asking whether
 * these keys are new.
 *
 * Fresh keys can have no history before the checkpoint the app ships for [network], so the scan starts there and skips
 * every filter before it. Keys restored from an older recovery phrase may hold coins further back and have to walk the
 * chain from its genesis block.
 *
 * @param network The network the wallet runs on; supplies the checkpoint quoted in the explanation.
 * @param onDismiss Called when the dialog is dismissed without starting the node.
 * @param onConfirm Called with the selected [ScanChoice] when the user confirms.
 */
@Composable
private fun ScanTypeDialog(network: Network, onDismiss: () -> Unit, onConfirm: (ScanChoice) -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    var selectedChoice by rememberSaveable { mutableStateOf(ScanChoice.RecoverFromCheckpoint) }
    val checkpoint = network.bundledCheckpoint

    val explanation =
        if (checkpoint != null) {
            "This wallet has never scanned the chain, so it needs a starting point. A wallet created in this app " +
                "has no history before block ${checkpoint.height} and starts scanning there; keys restored from an " +
                "older recovery phrase may hold coins further back and have to scan the whole chain."
        } else {
            "This wallet has never scanned the chain, so it needs a starting point. This network ships no " +
                "checkpoint, so either option scans from the genesis block."
        }

    AlertDialog(
        containerColor = colorScheme.surface,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Start the node",
                color = colorScheme.onSurface,
                fontFamily = inter,
            )
        },
        text = {
            Column {
                Text(
                    text = explanation,
                    color = colorScheme.onSurface.copy(alpha = 0.7f),
                    fontFamily = inter,
                    fontSize = 14.sp,
                )
                Spacer(modifier = Modifier.height(12.dp))
                recoveryScanChoices.forEach { choice ->
                    RadioButtonWithLabel(
                        label = choice.displayString(),
                        isSelected = choice == selectedChoice,
                        onSelect = { selectedChoice = choice },
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedChoice) }) {
                Text(
                    text = "Start Node",
                    color = colorScheme.primary,
                    fontFamily = inter,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = colorScheme.onSurface.copy(alpha = 0.5f),
                    fontFamily = inter,
                )
            }
        },
    )
}

/**
 * Sub-section of [CbfNodeScreen] that lists the default and custom peers and provides input fields for adding new ones.
 */
@Composable
private fun PeersSection(state: WalletScreenState, onAction: (WalletScreenAction) -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    var ipInput by rememberSaveable { mutableStateOf("") }
    var portInput by rememberSaveable { mutableStateOf("") }
    var showInvalidPeerError by rememberSaveable { mutableStateOf(false) }

    val defaultPeerText =
        when {
            state.customPeers.isNotEmpty() -> "Default peer (unused while custom peers are set):"
            state.defaultPeer != null -> "Default peer:"
            else -> "No default peer for this network. Peers will be discovered automatically."
        }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = defaultPeerText,
            color = colorScheme.onSurface,
            fontSize = 14.sp,
            fontFamily = inter,
            textAlign = TextAlign.Start,
        )
        state.defaultPeer?.let { peer ->
            Text(
                text = peer.toString(),
                color = colorScheme.onSurface,
                fontSize = 14.sp,
                fontFamily = inter,
                textAlign = TextAlign.End,
            )
        }
    }

    state.customPeers.forEach { peer ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            Text(
                text = "Custom peer: $peer",
                color = colorScheme.onSurface,
                fontSize = 14.sp,
                fontFamily = inter,
                textAlign = TextAlign.Start,
            )
            IconButton(onClick = { onAction(WalletScreenAction.RemoveCustomPeer(peer)) }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Remove peer $peer",
                    tint = colorScheme.onSurface,
                )
            }
        }
    }

    val textFieldColors =
        OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorScheme.primary,
            unfocusedBorderColor = colorScheme.outline.copy(alpha = 0.30f),
            cursorColor = colorScheme.primary,
            focusedLabelColor = colorScheme.primary,
            unfocusedLabelColor = colorScheme.onSurface.copy(alpha = 0.5f),
        )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
    ) {
        OutlinedTextField(
            modifier = Modifier.weight(2f),
            value = ipInput,
            onValueChange = {
                ipInput = it
                showInvalidPeerError = false
            },
            label = { Text(text = "Peer IP address", fontFamily = inter) },
            placeholder = {
                Text(
                    text = "192.168.0.1",
                    color = colorScheme.onSurface.copy(alpha = 0.3f),
                    fontFamily = inter,
                )
            },
            singleLine = true,
            isError = showInvalidPeerError,
            colors = textFieldColors,
        )
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = portInput,
            onValueChange = {
                portInput = it
                showInvalidPeerError = false
            },
            label = { Text(text = "Port", fontFamily = inter) },
            placeholder = {
                Text(
                    text = "Default",
                    color = colorScheme.onSurface.copy(alpha = 0.3f),
                    fontFamily = inter,
                )
            },
            singleLine = true,
            isError = showInvalidPeerError,
            colors = textFieldColors,
        )
    }

    // A small text field below the input field if the user attempts to add an invalid peer
    if (showInvalidPeerError) {
        Text(
            text = "Invalid IP address or port",
            color = colorScheme.error,
            fontSize = 12.sp,
            fontFamily = inter,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    val addPeerEnabled = ipInput.isNotBlank()
    OutlinedButton(
        onClick = {
            if (NodePeer.fromInput(ipInput, portInput) != null) {
                onAction(WalletScreenAction.AddCustomPeer(ipInput, portInput))
                ipInput = ""
                portInput = ""
            } else {
                showInvalidPeerError = true
            }
        },
        enabled = addPeerEnabled,
        shape = RoundedCornerShape(16.dp),
        border =
            BorderStroke(
                1.5.dp,
                if (addPeerEnabled) colorScheme.primary else colorScheme.primary.copy(alpha = 0.4f),
            ),
        modifier = Modifier.fillMaxWidth().height(52.dp),
    ) {
        Text(
            text = "Add Peer",
            fontFamily = inter,
            fontSize = 15.sp,
            color = if (addPeerEnabled) colorScheme.primary else colorScheme.primary.copy(alpha = 0.4f),
        )
    }
}
