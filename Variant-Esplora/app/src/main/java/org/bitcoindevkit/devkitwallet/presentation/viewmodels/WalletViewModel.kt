/*
 * Copyright 2021-2025 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bitcoindevkit.devkitwallet.domain.CurrencyUnit
import org.bitcoindevkit.devkitwallet.domain.Wallet

private const val TAG = "WalletViewModel"

data class WalletScreenState(
    val balance: ULong = 0u,
    val syncing: Boolean = false,
    val unit: CurrencyUnit = CurrencyUnit.Bitcoin,
    val esploraEndpoint: String = "",
)

sealed interface WalletScreenAction {
    data object UpdateBalance : WalletScreenAction
    data object SwitchUnit : WalletScreenAction
}

class WalletViewModel(
    private val wallet: Wallet,
) : ViewModel() {
    private val _state: MutableStateFlow<WalletScreenState> = MutableStateFlow(WalletScreenState())
    val state: StateFlow<WalletScreenState> = _state.asStateFlow()

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
        _state.update { state ->
            when (state.unit) {
                CurrencyUnit.Bitcoin -> state.copy(unit = CurrencyUnit.Satoshi)
                CurrencyUnit.Satoshi -> state.copy(unit = CurrencyUnit.Bitcoin)
            }
        }
    }

    private fun updateBalance() {
        _state.update { it.copy(syncing = true) }

        viewModelScope.launch(Dispatchers.IO) {
            wallet.sync()
            withContext(Dispatchers.Main) {
                val newBalance = wallet.getBalance()
                Log.i(TAG, "New balance: $newBalance")
                _state.update { it.copy(balance = newBalance, syncing = false) }
            }
        }
    }

    private fun updateClientEndpoint() {
        viewModelScope.launch(Dispatchers.IO) {
            val endpoint = wallet.getClientEndpoint()
            withContext(Dispatchers.Main) {
                _state.update { it.copy(esploraEndpoint = endpoint) }
            }
        }
    }
}
