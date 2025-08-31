/*
 * Copyright 2021-2025 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bitcoindevkit.devkitwallet.domain.CurrencyUnit
import org.bitcoindevkit.devkitwallet.domain.Wallet
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.WalletScreenAction
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.WalletScreenState

private const val TAG = "WalletViewModel"

class WalletViewModel(
    private val wallet: Wallet,
) : ViewModel() {
    var state: WalletScreenState by mutableStateOf(WalletScreenState())
        private set

    init {
        updateClientEndpoint()
    }

    fun onAction(action: WalletScreenAction) {
        when (action) {
            WalletScreenAction.UpdateBalance -> updateBalance()
            WalletScreenAction.SwitchUnit -> switchUnit()
        }
    }

    private fun switchUnit() {
        state = when (state.unit) {
            CurrencyUnit.Bitcoin -> state.copy(unit = CurrencyUnit.Satoshi)
            CurrencyUnit.Satoshi -> state.copy(unit = CurrencyUnit.Bitcoin)
        }
    }

    private fun updateBalance() {
        state = state.copy(syncing = true)
        viewModelScope.launch(Dispatchers.IO) {
            wallet.sync()
            withContext(Dispatchers.Main) {
                val newBalance = wallet.getBalance()
                Log.i(TAG, "New balance: $newBalance")
                state = state.copy(balance = newBalance, syncing = false)
            }
        }
    }

    private fun updateClientEndpoint() {
        viewModelScope.launch(Dispatchers.IO) {
            val endpoint = wallet.getClientEndpoint()
            withContext(Dispatchers.Main) {
                state = state.copy(esploraEndpoint = endpoint)
            }
        }
    }
}
