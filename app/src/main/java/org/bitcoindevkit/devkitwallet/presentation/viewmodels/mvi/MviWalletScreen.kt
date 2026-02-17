/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi

import org.bitcoindevkit.devkitwallet.domain.CurrencyUnit

data class WalletScreenState(
    val balance: ULong = 0u,
    val unit: CurrencyUnit = CurrencyUnit.Bitcoin,
    val latestBlock: UInt = 0u,
    val snackbarMessage: String? = null,
    val kyotoNodeStatus: KyotoNodeStatus = KyotoNodeStatus.Stopped,
)

sealed interface WalletScreenAction {
    data object UpdateBalance : WalletScreenAction

    data object SwitchUnit : WalletScreenAction

    data object StartKyotoNode : WalletScreenAction

    data object StopKyotoNode : WalletScreenAction

    data object ClearSnackbar : WalletScreenAction
}

enum class KyotoNodeStatus {
    Running,
    Stopped,
}
