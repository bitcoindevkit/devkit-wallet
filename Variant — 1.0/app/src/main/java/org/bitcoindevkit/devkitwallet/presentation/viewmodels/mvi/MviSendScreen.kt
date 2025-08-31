/*
 * Copyright 2021-2025 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi

// data class SendScreenState(
//     val address: String? = null,
// )

sealed class SendScreenAction {
    data class Broadcast(val txDataBundle: TxDataBundle) : SendScreenAction()
}

data class TxDataBundle(
    val recipients: List<Recipient>,
    val feeRate: ULong,
    val transactionType: TransactionType,
)

data class Recipient(var address: String, var amount: ULong)

enum class TransactionType {
    STANDARD,
    SEND_ALL,
}
