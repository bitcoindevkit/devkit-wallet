/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.bitcoindevkit.devkitwallet.domain.utils

import org.bitcoindevkit.Network
import org.bitcoindevkit.devkitwallet.data.datastore.ActiveWalletNetwork

/**
 * Converts a BDK [Network] into its DataStore-serializable equivalent.
 *
 * @throws IllegalArgumentException for unsupported networks (mainnet, testnet4).
 */
fun Network.intoStored(): ActiveWalletNetwork {
    return when (this) {
        Network.TESTNET -> ActiveWalletNetwork.TESTNET
        Network.TESTNET4 -> throw IllegalArgumentException("Bitcoin testnet 4 network is not supported")
        Network.SIGNET -> ActiveWalletNetwork.SIGNET
        Network.REGTEST -> ActiveWalletNetwork.REGTEST
        Network.BITCOIN -> throw IllegalArgumentException("Bitcoin mainnet network is not supported")
    }
}

/** Converts a DataStore-serializable [ActiveWalletNetwork] into the BDK [Network] used at runtime. */
fun ActiveWalletNetwork.intoDomain(): Network {
    return when (this) {
        ActiveWalletNetwork.TESTNET -> Network.TESTNET
        ActiveWalletNetwork.SIGNET -> Network.SIGNET
        ActiveWalletNetwork.REGTEST -> Network.REGTEST
    // ActiveWalletNetwork.UNRECOGNIZED -> throw IllegalArgumentException("Unrecognized network")
    }
}
