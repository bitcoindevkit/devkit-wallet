/*
 * Copyright 2021-2025 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.domain.utils

import org.bitcoindevkit.Network
import org.bitcoindevkit.devkitwallet.data.ActiveWalletNetwork

fun Network.intoProto(): ActiveWalletNetwork {
    return when (this) {
        Network.REGTEST -> ActiveWalletNetwork.REGTEST
        Network.TESTNET -> ActiveWalletNetwork.TESTNET3
        Network.TESTNET4 -> ActiveWalletNetwork.TESTNET4
        Network.SIGNET -> ActiveWalletNetwork.SIGNET
        Network.BITCOIN -> throw IllegalArgumentException("Bitcoin mainnet network is not supported")
    }
}

fun ActiveWalletNetwork.intoDomain(): Network {
    return when (this) {
        ActiveWalletNetwork.REGTEST      -> Network.REGTEST
        ActiveWalletNetwork.SIGNET       -> Network.SIGNET
        ActiveWalletNetwork.TESTNET3     -> Network.TESTNET
        ActiveWalletNetwork.TESTNET4     -> Network.TESTNET4
        ActiveWalletNetwork.UNRECOGNIZED -> throw IllegalArgumentException("Unrecognized network")
    }
}
