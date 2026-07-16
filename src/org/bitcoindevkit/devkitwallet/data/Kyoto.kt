/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
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
import org.bitcoindevkit.CbfException
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

private const val TAG = "KyotoClient"

// TODO: Document this class well
class Kyoto
private constructor(
    private val kyotoNode: CbfNode,
    private val kyotoClient: CbfClient,
) {
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun start(): Flow<Update> {
        kyotoNode.run()

        // The client throws an Exception on the `update()` method once the node has stopped (requested shutdown,
        // unreachable peers, lost connections); end the flow instead of crashing the collector
        return flow {
            while (true) {
                val update =
                    try {
                        kyotoClient.update()
                    } catch (e: CbfException) {
                        Log.i(TAG, "Node has stopped, ending the updates flow: ${e.message}")
                        break
                    }
                emit(update)
            }
        }
    }

    fun infoLog(): SharedFlow<Info> {
        val sharedFlow = MutableSharedFlow<Info>(replay = 0)
        scope.launch {
            while (true) {
                val info =
                    try {
                        kyotoClient.nextInfo()
                    } catch (e: CbfException) {
                        break
                    }
                sharedFlow.emit(info)
            }
        }
        return sharedFlow
    }

    fun warningLog(): SharedFlow<Warning> {
        val sharedFlow = MutableSharedFlow<Warning>(replay = 0)
        scope.launch {
            while (true) {
                val warning =
                    try {
                        kyotoClient.nextWarning()
                    } catch (e: CbfException) {
                        break
                    }
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
        try {
            kyotoClient.shutdown()
        } catch (e: CbfException) {
            Log.i(TAG, "Shutdown requested but the node had already stopped: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "KyotoClient"

        private var instance: Kyoto? = null

        fun getInstance(): Kyoto = instance ?: throw KyotoNotInitialized()

        fun defaultPeer(network: Network): NodePeer? =
            when (network) {
                // Default connection point from the emulator with a local regtest setup
                Network.REGTEST -> NodePeer(ip = "10.0.2.2", port = 18444u)
                else -> null
            }

        fun create(wallet: Wallet, dataDir: String, network: Network, nodePeers: List<NodePeer>): Kyoto {
            Log.i(TAG, "Starting Kyoto node with peers: $nodePeers")
            val peers: List<Peer> = nodePeers.map { it.toPeer() }

            val (client, node) =
                CbfBuilder().dataDir(dataDir).peers(peers).connections(1u).scanType(ScanType.Sync).build(wallet)

            return Kyoto(node, client).also { instance = it }
        }
    }
}

class KyotoNotInitialized : Exception()

/**
 * A peer the Kyoto node can connect to, kept as simple displayable values. A null port means the default port for the
 * network is used.
 */
data class NodePeer(val ip: String, val port: UShort?) {
    fun toPeer(): Peer {
        val octets = ip.split(".").map { it.toUByte() }
        val ipAddress: IpAddress = IpAddress.fromIpv4(octets[0], octets[1], octets[2], octets[3])
        return Peer(ipAddress, port, false)
    }

    override fun toString(): String = if (port != null) "$ip:$port" else ip

    companion object {
        /** Parses user input into a [NodePeer], returning null if the IP address or port is invalid. */
        fun fromInput(ip: String, port: String): NodePeer? {
            val octets = ip.trim().split(".")
            if (octets.size != 4 || octets.any { it.toUByteOrNull() == null }) return null

            val trimmedPort = port.trim()
            val parsedPort: UShort? =
                if (trimmedPort.isEmpty()) {
                    null
                } else {
                    trimmedPort.toUShortOrNull() ?: return null
                }

            return NodePeer(ip.trim(), parsedPort)
        }
    }
}
