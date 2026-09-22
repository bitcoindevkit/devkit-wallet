/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi

import org.bitcoindevkit.Network
import org.bitcoindevkit.devkitwallet.data.NodePeer
import org.bitcoindevkit.devkitwallet.domain.CurrencyUnit

/**
 * UI state snapshot for the wallet home screen.
 *
 * @property balance Total wallet balance in satoshis.
 * @property unit Current display unit (BTC or sats).
 * @property network The active Bitcoin network.
 * @property bestBlockHeight Chain tip known to the wallet.
 * @property kyotoNodeStatus Whether the Kyoto CBF node is running or stopped.
 * @property connectedPeerCount Number of peers the running node is currently connected to.
 * @property defaultPeer Hard-coded default peer for the current network, if any.
 * @property customPeers User-added peers for the Kyoto node.
 * @property initialRecoveryDone Whether this wallet has ever completed a scan of the chain.
 */
data class WalletScreenState(
    val balance: ULong = 0u,
    val unit: CurrencyUnit = CurrencyUnit.Bitcoin,
    val network: Network = Network.SIGNET,
    val bestBlockHeight: UInt = 0u,
    val kyotoNodeStatus: CbfNodeStatus = CbfNodeStatus.Stopped,
    val connectedPeerCount: Int = 0,
    val defaultPeer: NodePeer? = null,
    val customPeers: List<NodePeer> = emptyList(),
    val initialRecoveryDone: Boolean = false,
)

/** One-way actions that the wallet home screen can dispatch to its [WalletViewModel]. */
sealed interface WalletScreenAction {
    /** Refresh the on-chain balance from the underlying wallet. */
    data object UpdateBalance : WalletScreenAction

    /** Toggle between BTC and satoshi display. */
    data object SwitchUnit : WalletScreenAction

    /** Start the Kyoto CBF node with the chosen scan strategy and begin listening for chain updates. */
    data class ActivateCbfNode(val scanChoice: ScanChoice) : WalletScreenAction

    /** Shut down the Kyoto CBF node gracefully. */
    data object StopKyotoNode : WalletScreenAction

    /** Add a custom peer to the Kyoto node configuration. */
    data class AddCustomPeer(val ip: String, val port: String) : WalletScreenAction

    /** Remove a previously-added custom peer. */
    data class RemoveCustomPeer(val peer: NodePeer) : WalletScreenAction
}

/** Lifecycle states of the Kyoto (CBF) node. */
enum class CbfNodeStatus {
    Running,
    Stopped,
}

/**
 * How far back the Kyoto node should scan the chain when it starts.
 *
 * [Sync] resumes from the wallet's own last stored checkpoint. It is what a wallet that has already scanned the chain
 * once always uses, and it needs no input from the user.
 *
 * A wallet that has never scanned has no checkpoint of its own and has to be told where to start, which comes down to
 * whether its keys are new: [RecoverFromCheckpoint] starts from the checkpoint the app ships for the network, which is
 * everything a wallet created in this app can have history for, while [RecoverFromGenesis] walks every filter the
 * network ever produced, which is what keys restored from an older recovery phrase may need.
 */
enum class ScanChoice {
    Sync,
    RecoverFromCheckpoint,
    RecoverFromGenesis,
}

/** The starting points offered to a wallet that has never scanned the chain. */
val recoveryScanChoices: List<ScanChoice> = listOf(ScanChoice.RecoverFromCheckpoint, ScanChoice.RecoverFromGenesis)

/** Human-readable label for the scan strategy. */
fun ScanChoice.displayString(): String {
    return when (this) {
        ScanChoice.Sync -> "Sync from last known block"
        ScanChoice.RecoverFromCheckpoint -> "Fresh wallet (no past history)"
        ScanChoice.RecoverFromGenesis -> "Existing wallet (recover from genesis block)"
    }
}
