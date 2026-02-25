/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.data

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.bitcoindevkit.CbfBuilder
import org.bitcoindevkit.CbfClient
import org.bitcoindevkit.CbfNode
import org.bitcoindevkit.Info
import org.bitcoindevkit.IpAddress
import org.bitcoindevkit.Network
import org.bitcoindevkit.Peer
import org.bitcoindevkit.ScanType
import org.bitcoindevkit.Transaction
import org.bitcoindevkit.Update
import org.bitcoindevkit.Wallet
import org.bitcoindevkit.Warning
import org.bitcoindevkit.Wtxid
import kotlin.collections.listOf

private const val TAG = "KyotoClient"

// TODO: Document this class well
class Kyoto private constructor(
    private val kyotoNode: CbfNode,
    private val kyotoClient: CbfClient,
) {
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun start(): Flow<Update> {
        kyotoNode.run()

        return flow {
            // Set this to stop under certain circumstances
            while (true) {
                val update = kyotoClient.update()
                emit(update)
            }
        }
    }

    fun infoLog(): SharedFlow<Info> {
        val sharedFlow = MutableSharedFlow<Info>(replay = 0)
        scope.launch {
            while (true) {
                val info = kyotoClient.nextInfo()
                sharedFlow.emit(info)
            }
        }
        return sharedFlow
    }

    fun warningLog(): SharedFlow<Warning> {
        val sharedFlow = MutableSharedFlow<Warning>(replay = 0)
        scope.launch {
            while (true) {
                val warning = kyotoClient.nextWarning()
                sharedFlow.emit(warning)
            }
        }
        return sharedFlow
    }

    fun logToLogcat() {
        scope.launch {
            infoLog().collect {
                Log.i(TAG, it.toString())
            }
        }
        scope.launch {
            warningLog().collect {
                Log.i(TAG, it.toString())
            }
        }
    }

    fun lookupHost(hostname: String): List<IpAddress> {
        return kyotoClient.lookupHost(hostname)
    }

    suspend fun broadcast(transaction: Transaction): Wtxid {
        return kyotoClient.broadcast(transaction)
    }

    fun connect(peer: Peer) {
        kyotoClient.connect(peer)
    }

    fun isRunning(): Boolean {
        return kyotoClient.isRunning()
    }

    fun shutdown() {
        kyotoClient.shutdown()
    }

    companion object {
        private var instance: Kyoto? = null

        fun getInstance(): Kyoto = instance ?: throw KyotoNotInitialized()

        fun create(wallet: Wallet, dataDir: String, network: Network): Kyoto {
            Log.i(TAG, "Starting Kyoto node")
            val peers: List<Peer> = when(network) {
                Network.REGTEST -> {
                    val ip: IpAddress = IpAddress.fromIpv4(10u, 0u, 2u, 2u)
                    val peer1: Peer = Peer(ip, 18444u, false)
                    listOf(peer1)
                }
                Network.SIGNET -> {
                    val ip: IpAddress = IpAddress.fromIpv4(68u, 47u, 229u, 218u)
                    val peer1: Peer = Peer(ip, null, false)
                    listOf(peer1)
                }
                else -> { listOf() }
            }

            val (client, node) =
                CbfBuilder()
                    .dataDir(dataDir)
                    .peers(peers)
                    .connections(1u)
                    .scanType(ScanType.Sync)
                    .build(wallet)

            return Kyoto(node, client).also { instance = it }
        }
    }
}

class KyotoNotInitialized : Exception()
