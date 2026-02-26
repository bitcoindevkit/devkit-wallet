/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.domain

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.first
import org.bitcoindevkit.devkitwallet.data.SingleWallet
import org.bitcoindevkit.devkitwallet.data.UserPreferences

class UserPreferencesRepository(
    private val userPreferencesStore: DataStore<UserPreferences>,
) {
    suspend fun fetchIntroDone(): Boolean {
        return userPreferencesStore.data.first().introDone
    }

    suspend fun setIntroDone() {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setIntroDone(true).build()
        }
    }

    suspend fun fetchActiveWallets(): List<SingleWallet> {
        return userPreferencesStore.data.first().walletsList
    }

    suspend fun updateActiveWallets(singleWallet: SingleWallet) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().addWallets(singleWallet).build()
        }
    }

    suspend fun fetchDarkTheme(): Boolean {
        return userPreferencesStore.data.first().darkTheme
    }

    suspend fun setDarkTheme(isDark: Boolean) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setDarkTheme(isDark).build()
        }
    }

    suspend fun setFullScanCompleted(walletId: String) {
        userPreferencesStore.updateData { currentPreferences ->
            val updatedWalletsList =
                currentPreferences.walletsList.map { wallet ->
                    if (wallet.id == walletId) {
                        wallet.toBuilder().setFullScanCompleted(true).build()
                    } else {
                        wallet
                    }
                }
            currentPreferences
                .toBuilder()
                .clearWallets()
                .addAllWallets(updatedWalletsList)
                .build()
        }
    }
}
