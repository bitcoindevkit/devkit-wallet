/*
 * Copyright 2021-2025 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi

import org.bitcoindevkit.devkitwallet.domain.CurrencyUnit

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
