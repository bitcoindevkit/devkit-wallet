/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
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
    var state: WalletScreenState by
        mutableStateOf(WalletScreenState(network = wallet.network, defaultPeer = Kyoto.defaultPeer(wallet.network)))
        private set

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
        if (peer !in state.customPeers) {
            state = state.copy(customPeers = state.customPeers + peer)
        }
    }

    private fun removeCustomPeer(peer: NodePeer) {
        state = state.copy(customPeers = state.customPeers - peer)
    }

    private fun switchUnit() {
        state =
            when (state.unit) {
                CurrencyUnit.Bitcoin -> state.copy(unit = CurrencyUnit.Satoshi)
                CurrencyUnit.Satoshi -> state.copy(unit = CurrencyUnit.Bitcoin)
            }
    }

    private fun updateLatestBlock(blockHeight: UInt) {
        state = state.copy(bestBlockHeight = blockHeight)
    }

    private fun updateBalance() {
        viewModelScope.launch(Dispatchers.IO) {
            val newBalance = wallet.getBalance()
            Log.i("Kyoto", "New balance: $newBalance")
            DwLogger.log(INFO, "New balance: $newBalance")

            state = state.copy(balance = newBalance)
            Log.i("Kyoto", "New state object: $state")
            DwLogger.log(INFO, "New state object: $state")
        }
    }

    private fun activateKyoto() {
        val peers = state.customPeers.ifEmpty { listOfNotNull(state.defaultPeer) }
        if (peers.isEmpty()) {
            Log.w(TAG, "No peers available for network ${wallet.network}: add a custom peer first")
            return
        }

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

                val newHeight = state.bestBlockHeight
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
        state = state.copy(bestBlockHeight = bestBlockHeight)
    }
}
