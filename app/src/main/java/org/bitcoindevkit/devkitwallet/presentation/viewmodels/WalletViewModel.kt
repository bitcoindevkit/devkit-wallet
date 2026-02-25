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
import kotlinx.coroutines.launch
import org.bitcoindevkit.devkitwallet.data.Kyoto
import org.bitcoindevkit.devkitwallet.domain.CurrencyUnit
import org.bitcoindevkit.devkitwallet.domain.DwLogger
import org.bitcoindevkit.devkitwallet.domain.DwLogger.LogLevel.INFO
import org.bitcoindevkit.devkitwallet.domain.Wallet
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.WalletScreenAction
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.WalletScreenState

private const val TAG = "WalletViewModel"

internal class WalletViewModel(
    private val wallet: Wallet,
) : ViewModel() {
    var state: WalletScreenState by mutableStateOf(WalletScreenState())
        private set

    private val kyotoCoroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private var kyoto: Kyoto? = null

    fun onAction(action: WalletScreenAction) {
        when (action) {
            WalletScreenAction.SwitchUnit      -> switchUnit()
            WalletScreenAction.UpdateBalance   -> updateBalance()
            WalletScreenAction.ActivateCbfNode -> activateKyoto()
            WalletScreenAction.StopKyotoNode   -> stopKyotoNode()
            WalletScreenAction.ClearSnackbar   -> clearSnackbar()
        }
    }

    private fun showSnackbar(message: String) {
        state = state.copy(snackbarMessage = message)
    }

    private fun clearSnackbar() {
        state = state.copy(snackbarMessage = null)
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
        val dataDir = wallet.internalAppFilesPath
        this.kyoto = Kyoto.create(wallet.wallet, dataDir, wallet.network)
        val updatesFlow = kyoto!!.start()
        kyotoCoroutineScope.launch {
            updatesFlow.collect {
                Log.i(TAG, "Collecting a flow update")
                wallet.applyUpdate(it)
                updateBalance()
                updateBestBlock()
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
