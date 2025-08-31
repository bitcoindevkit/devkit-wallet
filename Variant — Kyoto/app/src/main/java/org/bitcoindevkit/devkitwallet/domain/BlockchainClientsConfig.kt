/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.domain

import org.bitcoindevkit.Network

class BlockchainClientsConfig {
    private var defaultClient: BlockchainClient? = null
    private val allClients: MutableList<BlockchainClient> = mutableListOf()

    fun getClient(): BlockchainClient? {
        return defaultClient
    }

    fun addClient(client: BlockchainClient, setDefault: Boolean) {
        allClients.forEach {
            if (it.clientId() == client.clientId()) {
                throw IllegalArgumentException(
                    "Client with url ${client.clientId()} already exists"
                )
            }
        }
        if (allClients.size >= 8) throw IllegalArgumentException("Maximum number of clients (8) reached")
        allClients.add(client)
        if (setDefault) {
            defaultClient = client
        }
    }

    fun setDefaultClient(clientId: String) {
        val client = allClients.find { it.clientId() == clientId }
        if (client == null) throw IllegalArgumentException("Client with url $clientId not found")
        defaultClient = client
    }

    companion object {
        fun createDefaultConfig(network: Network): BlockchainClientsConfig {
            val config = BlockchainClientsConfig()
            when (network) {
                Network.REGTEST -> {
                    config.addClient(EsploraClient("http://10.0.2.2:3002"), true)
                }
                Network.TESTNET -> {
                    config.addClient(ElectrumClient("ssl://electrum.blockstream.info:60002"), true)
                }
                Network.TESTNET4 -> throw IllegalArgumentException("This app does not support testnet 4 yet")
                Network.SIGNET -> {
                    config.addClient(ElectrumClient("ssl://mempool.space:60602"), true)
                }
                Network.BITCOIN -> throw IllegalArgumentException("This app does not support mainnet")
            }
            return config
        }
    }
}
