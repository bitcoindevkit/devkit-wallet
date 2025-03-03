/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.domain.utils

import org.bitcoindevkit.devkitwallet.data.ActiveWalletNetwork
import org.bitcoindevkit.Network

fun Network.intoProto(): ActiveWalletNetwork {
    return when (this) {
        Network.TESTNET -> ActiveWalletNetwork.TESTNET
        Network.TESTNET4 -> throw IllegalArgumentException("Bitcoin testnet 4 network is not supported")
        Network.SIGNET -> ActiveWalletNetwork.SIGNET
        Network.REGTEST -> ActiveWalletNetwork.REGTEST
        Network.BITCOIN -> throw IllegalArgumentException("Bitcoin mainnet network is not supported")
    }
}

fun ActiveWalletNetwork.intoDomain(): Network {
    return when (this) {
        ActiveWalletNetwork.TESTNET -> Network.TESTNET
        ActiveWalletNetwork.SIGNET -> Network.SIGNET
        ActiveWalletNetwork.REGTEST -> Network.REGTEST
        ActiveWalletNetwork.UNRECOGNIZED -> throw IllegalArgumentException("Unrecognized network")
    }
}
