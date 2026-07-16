/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi

import org.bitcoindevkit.Network
import org.bitcoindevkit.devkitwallet.data.NodePeer
import org.bitcoindevkit.devkitwallet.domain.CurrencyUnit

data class WalletScreenState(
    val balance: ULong = 0u,
    val unit: CurrencyUnit = CurrencyUnit.Bitcoin,
    val network: Network = Network.SIGNET,
    val bestBlockHeight: UInt = 0u,
    val kyotoNodeStatus: CbfNodeStatus = CbfNodeStatus.Stopped,
    val defaultPeer: NodePeer? = null,
    val customPeers: List<NodePeer> = emptyList(),
)

sealed interface WalletScreenAction {
    data object UpdateBalance : WalletScreenAction

    data object SwitchUnit : WalletScreenAction

    data object ActivateCbfNode : WalletScreenAction

    data object StopKyotoNode : WalletScreenAction

    data class AddCustomPeer(val ip: String, val port: String) : WalletScreenAction

    data class RemoveCustomPeer(val peer: NodePeer) : WalletScreenAction
}

enum class CbfNodeStatus {
    Running,
    Stopped,
}
