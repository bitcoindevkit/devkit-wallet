/*
 * Copyright 2021-2025 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import org.bitcoindevkit.FeeRate
import org.bitcoindevkit.Psbt
import org.bitcoindevkit.devkitwallet.domain.Wallet

private const val TAG = "SendViewModel"

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

internal class SendViewModel(private val wallet: Wallet) : ViewModel() {
    fun onAction(action: SendScreenAction) {
        when (action) {
            is SendScreenAction.Broadcast -> broadcast(action.txDataBundle)
        }
    }

    private fun broadcast(txInfo: TxDataBundle) {
        try {
            // Create, sign, and broadcast
            val psbt: Psbt =
                when (txInfo.transactionType) {
                    TransactionType.STANDARD -> {
                        wallet.createTransaction(
                            recipientList = txInfo.recipients,
                            feeRate = FeeRate.fromSatPerVb(txInfo.feeRate),
                        )
                    }

                    // TransactionType.SEND_ALL -> Wallet.createSendAllTransaction(recipientList[0].address, FeeRate.fromSatPerVb(feeRate), rbfEnabled, opReturnMsg)
                    TransactionType.SEND_ALL -> {
                        throw NotImplementedError("Send all not implemented")
                    }
                }
            val isSigned = wallet.sign(psbt)
            if (isSigned) {
                val txid: String = wallet.broadcast(psbt)
                Log.i(TAG, "Transaction was broadcast! txid: $txid")
            } else {
                Log.i(TAG, "Transaction not signed.")
            }
        } catch (e: Throwable) {
            Log.i(TAG, "Broadcast error: ${e.message}")
        }
    }
}
