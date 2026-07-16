/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.bitcoindevkit.AddressInfo
import org.bitcoindevkit.devkitwallet.domain.DwLogger
import org.bitcoindevkit.devkitwallet.domain.DwLogger.LogLevel.INFO
import org.bitcoindevkit.devkitwallet.domain.Wallet
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.ReceiveScreenAction
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.ReceiveScreenState

internal class AddressViewModel(private val wallet: Wallet) : ViewModel() {
    val state: StateFlow<ReceiveScreenState>
        field = MutableStateFlow(ReceiveScreenState())

    fun onAction(action: ReceiveScreenAction) {
        when (action) {
            is ReceiveScreenAction.UpdateAddress -> updateAddress()
        }
    }

    private fun updateAddress() {
        val newAddress: AddressInfo = wallet.getNewAddress()
        DwLogger.log(INFO, "Revealing new address at index ${newAddress.index}")

        state.update {
            ReceiveScreenState(
                address = newAddress.address.toString(),
                addressIndex = newAddress.index,
            )
        }
    }
}
