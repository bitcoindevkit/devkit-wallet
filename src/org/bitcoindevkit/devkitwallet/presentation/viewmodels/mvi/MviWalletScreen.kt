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
 * @property defaultPeer Hard-coded default peer for the current network, if any.
 * @property customPeers User-added peers for the Kyoto node.
 */
data class WalletScreenState(
    val balance: ULong = 0u,
    val unit: CurrencyUnit = CurrencyUnit.Bitcoin,
    val network: Network = Network.SIGNET,
    val bestBlockHeight: UInt = 0u,
    val kyotoNodeStatus: CbfNodeStatus = CbfNodeStatus.Stopped,
    val defaultPeer: NodePeer? = null,
    val customPeers: List<NodePeer> = emptyList(),
)

/** One-way actions that the wallet home screen can dispatch to its [WalletViewModel]. */
sealed interface WalletScreenAction {
    /** Refresh the on-chain balance from the underlying wallet. */
    data object UpdateBalance : WalletScreenAction

    /** Toggle between BTC and satoshi display. */
    data object SwitchUnit : WalletScreenAction

    /** Start the Kyoto CBF node and begin listening for chain updates. */
    data object ActivateCbfNode : WalletScreenAction

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
