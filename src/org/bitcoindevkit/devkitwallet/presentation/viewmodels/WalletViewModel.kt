/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bitcoindevkit.devkitwallet.data.Kyoto
import org.bitcoindevkit.devkitwallet.data.NodePeer
import org.bitcoindevkit.devkitwallet.domain.CurrencyUnit
import org.bitcoindevkit.devkitwallet.domain.DwLogger
import org.bitcoindevkit.devkitwallet.domain.DwLogger.LogLevel.INFO
import org.bitcoindevkit.devkitwallet.domain.Wallet
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.WalletScreenAction
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.WalletScreenState

private const val TAG = "WalletViewModel"

internal class WalletViewModel(private val wallet: Wallet) : ViewModel() {
    val defaultPeer: NodePeer? = Kyoto.defaultPeer(wallet.network)

    val state: StateFlow<WalletScreenState>
        field = MutableStateFlow(WalletScreenState(network = wallet.network, defaultPeer = defaultPeer))

    private val kyotoCoroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private var kyoto: Kyoto? = null

    private val snackbarChannel = Channel<String>(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val snackbarMessages: Flow<String> = snackbarChannel.receiveAsFlow()

    fun onAction(action: WalletScreenAction) {
        when (action) {
            WalletScreenAction.SwitchUnit -> switchUnit()
            WalletScreenAction.UpdateBalance -> updateBalance()
            WalletScreenAction.ActivateCbfNode -> activateKyoto()
            WalletScreenAction.StopKyotoNode -> stopKyotoNode()
            is WalletScreenAction.AddCustomPeer -> addCustomPeer(action.ip, action.port)
            is WalletScreenAction.RemoveCustomPeer -> removeCustomPeer(action.peer)
        }
    }

    private fun addCustomPeer(ip: String, port: String) {
        val peer = NodePeer.fromInput(ip, port) ?: return
        state.update {
            if (peer in it.customPeers) it else it.copy(customPeers = it.customPeers + peer)
        }
    }

    private fun removeCustomPeer(peer: NodePeer) {
        state.update { it.copy(customPeers = it.customPeers - peer) }
    }

    private fun switchUnit() {
        state.update {
            when (it.unit) {
                CurrencyUnit.Bitcoin -> it.copy(unit = CurrencyUnit.Satoshi)
                CurrencyUnit.Satoshi -> it.copy(unit = CurrencyUnit.Bitcoin)
            }
        }
    }

    private fun updateLatestBlock(blockHeight: UInt) {
        state.update { it.copy(bestBlockHeight = blockHeight) }
    }

    private fun updateBalance() {
        viewModelScope.launch(Dispatchers.IO) {
            val newBalance = wallet.getBalance()
            Log.i("Kyoto", "New balance: $newBalance")
            DwLogger.log(INFO, "New balance: $newBalance")

            state.update { it.copy(balance = newBalance) }
            Log.i("Kyoto", "New state object: ${state.value}")
            DwLogger.log(INFO, "New state object: ${state.value}")
        }
    }

    private fun activateKyoto() {
        // An empty list is fine: Kyoto discovers peers on its own if none are provided
        val peers = state.value.customPeers.ifEmpty { listOfNotNull(defaultPeer) }

        val dataDir = wallet.internalAppFilesPath
        this.kyoto = Kyoto.create(wallet.wallet, dataDir, wallet.network, peers)
        val updatesFlow = kyoto!!.start()
        kyotoCoroutineScope.launch {
            var previousHeight: UInt = wallet.bestBlock()

            updatesFlow.collect {
                Log.i(TAG, "Collecting a flow update")
                wallet.applyUpdate(it)
                updateBalance()
                updateBestBlock()

                val newHeight = state.value.bestBlockHeight
                if (newHeight > previousHeight) {
                    snackbarChannel.send("New block: $newHeight")
                }
                previousHeight = newHeight
            }
        }
        kyoto!!.logToLogcat()
    }

    private fun stopKyotoNode() {
        kyoto!!.shutdown()
    }

    private fun updateBestBlock() {
        val bestBlockHeight = wallet.bestBlock()
        state.update { it.copy(bestBlockHeight = bestBlockHeight) }
    }
}
