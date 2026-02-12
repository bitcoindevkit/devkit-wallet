/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.domain

private const val TAG = "ElectrumServer"

class ElectrumServer {
    // private var useDefaultElectrum: Boolean = true
    // private var default: Blockchain
    // private val esploraClient: EsploraClient = EsploraClient("https://esplora.testnet.kuutamo.cloud/")
    // private var custom: Blockchain? = null
    // private var customElectrumURL: String
    // private val defaultElectrumURL = "tcp://10.0.2.2:60401"
    // private val defaultElectrumURL = "ssl://electrum.blockstream.info:60002"
    // private val defaultElectrumURL = "tcp://127.0.0.1:60401"

    // init {
    //     val blockchainConfig = BlockchainConfig.Electrum(ElectrumConfig(
    //         url = defaultElectrumURL,
    //         socks5 = null,
    //         retry = 5u,
    //         timeout = null,
    //         stopGap = 10u,
    //         validateDomain = true
    //     ))
    //     customElectrumURL = ""
    //     default = Blockchain(blockchainConfig)
    // }
    //
    // val server: Blockchain
    //     get() = if (useDefaultElectrum) this.default else this.custom!!

    // if you're looking to test different public Electrum servers we recommend these 3:
    // ssl://electrum.blockstream.info:60002
    // tcp://electrum.blockstream.info:60001
    // tcp://testnet.aranguren.org:51001
    // fun createCustomElectrum(electrumURL: String) {
    //     customElectrumURL = electrumURL
    //     val blockchainConfig = BlockchainConfig.Electrum(ElectrumConfig(
    //         url = customElectrumURL,
    //         socks5 = null,
    //         retry = 5u,
    //         timeout = null,
    //         stopGap = 10u,
    //         validateDomain = true
    //     ))
    //     custom = Blockchain(blockchainConfig)
    //     useCustomElectrum()
    //     Log.i(TAG, "New Electrum Server URL : $customElectrumURL")
    // }

    // fun useCustomElectrum() {
    //     useDefaultElectrum = false
    // }
    //
    // fun useDefaultElectrum() {
    //     useDefaultElectrum = true
    // }
    //
    // fun isElectrumServerDefault(): Boolean {
    //     return useDefaultElectrum
    // }
    //
    // fun getElectrumURL(): String {
    //     return if (useDefaultElectrum) {
    //         defaultElectrumURL
    //     } else {
    //         customElectrumURL
    //     }
    // }
}
