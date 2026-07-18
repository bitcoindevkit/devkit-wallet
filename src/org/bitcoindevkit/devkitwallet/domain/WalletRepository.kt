/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.bitcoindevkit.devkitwallet.domain

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.first
import org.bitcoindevkit.devkitwallet.data.datastore.StoredWallet
import org.bitcoindevkit.devkitwallet.data.datastore.WalletData

/**
 * Repository that reads and writes wallet metadata via a [DataStore].
 *
 * All operations are suspendable and run on the IO dispatcher implicitly via DataStore.
 */
class WalletRepository(private val store: DataStore<WalletData>) {
    /** Returns the list of all previously persisted wallets. */
    suspend fun fetchWallets() = store.data.first().wallets

    /** Appends a new wallet to the persisted collection. */
    suspend fun addWallet(wallet: StoredWallet) = store.updateData { it.copy(wallets = it.wallets + wallet) }
}
