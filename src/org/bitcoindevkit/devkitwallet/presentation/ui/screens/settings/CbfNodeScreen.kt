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
import org.bitcoindevkit.devkitwallet.data.NodePeer
import org.bitcoindevkit.devkitwallet.presentation.theme.inter
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.CbfNodeStatus
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.WalletScreenAction
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.WalletScreenState

@Composable
internal fun CbfNodeScreen(
    state: WalletScreenState,
    onAction: (WalletScreenAction) -> Unit,
    navController: NavController,
) {
    val colorScheme = MaterialTheme.colorScheme
    val isRunning = state.kyotoNodeStatus == CbfNodeStatus.Running

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
                onClick = { onAction(WalletScreenAction.ActivateCbfNode) },
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
