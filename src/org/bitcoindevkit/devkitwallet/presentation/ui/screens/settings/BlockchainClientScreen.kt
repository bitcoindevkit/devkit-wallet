/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.settings

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.bitcoindevkit.devkitwallet.data.NodePeer
import org.bitcoindevkit.devkitwallet.presentation.theme.inter
import org.bitcoindevkit.devkitwallet.presentation.ui.components.NeutralButton
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.CbfNodeStatus
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.WalletScreenAction
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.WalletScreenState

@Composable
internal fun BlockchainClientScreen(
    state: WalletScreenState,
    onAction: (WalletScreenAction) -> Unit,
    navController: NavController,
) {
    val colorScheme = MaterialTheme.colorScheme

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
                val status = if (state.kyotoNodeStatus == CbfNodeStatus.Running) "Online" else "Offline"
                Text(
                    text = "CBF Node Status: $status",
                    color = colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontFamily = inter,
                    textAlign = TextAlign.Start,
                )
                Box(
                    modifier =
                        Modifier.padding(horizontal = 8.dp)
                            .size(size = 21.dp)
                            .clip(shape = CircleShape)
                            .background(
                                if (state.kyotoNodeStatus == CbfNodeStatus.Running) {
                                    Color(0xFF8FD998)
                                } else {
                                    Color(0xFFE76F51)
                                }
                            )
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

            Spacer(modifier = Modifier.padding(16.dp))

            NeutralButton(
                text = "Start Node",
                enabled = state.kyotoNodeStatus == CbfNodeStatus.Stopped,
                onClick = { onAction(WalletScreenAction.ActivateCbfNode) },
            )
            NeutralButton(
                text = "Stop Node",
                enabled = state.kyotoNodeStatus == CbfNodeStatus.Running,
                onClick = { onAction(WalletScreenAction.StopKyotoNode) },
            )
        }
    }
}

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

    Spacer(modifier = Modifier.padding(8.dp))

    NeutralButton(
        text = "Add Peer",
        enabled = ipInput.isNotBlank(),
        onClick = {
            if (NodePeer.fromInput(ipInput, portInput) != null) {
                onAction(WalletScreenAction.AddCustomPeer(ipInput, portInput))
                ipInput = ""
                portInput = ""
            } else {
                showInvalidPeerError = true
            }
        },
    )
}
