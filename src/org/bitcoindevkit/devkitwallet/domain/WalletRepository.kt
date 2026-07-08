/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.domain

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.first
import org.bitcoindevkit.devkitwallet.data.datastore.StoredWallet
import org.bitcoindevkit.devkitwallet.data.datastore.WalletData

class WalletRepository(private val store: DataStore<WalletData>) {
    suspend fun fetchWallets() = store.data.first().wallets

    suspend fun addWallet(wallet: StoredWallet) = store.updateData { it.copy(wallets = it.wallets + wallet) }

    suspend fun setFullScanCompleted(walletId: String) = store.updateData { data ->
        data.copy(
            wallets =
                data.wallets.map {
                    if (it.id == walletId) it.copy(fullScanCompleted = true) else it
                }
        )
    }
}
