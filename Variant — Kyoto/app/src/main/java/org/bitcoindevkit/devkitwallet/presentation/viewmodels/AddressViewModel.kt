/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import org.bitcoindevkit.AddressInfo
import org.bitcoindevkit.devkitwallet.domain.DwLogger
import org.bitcoindevkit.devkitwallet.domain.DwLogger.LogLevel.INFO
import org.bitcoindevkit.devkitwallet.domain.Wallet
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.ReceiveScreenAction
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.ReceiveScreenState

internal class AddressViewModel(private val wallet: Wallet) : ViewModel() {
    var state: ReceiveScreenState by mutableStateOf(ReceiveScreenState())
        private set

    fun onAction(action: ReceiveScreenAction) {
        when (action) {
            is ReceiveScreenAction.UpdateAddress -> updateAddress()
        }
    }

    private fun updateAddress() {
        val newAddress: AddressInfo = wallet.getNewAddress()
        DwLogger.log(INFO, "Revealing new address at index ${newAddress.index}")

        state =
            ReceiveScreenState(
                address = newAddress.address.toString(),
                addressIndex = newAddress.index,
            )
    }
}
