/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.domain

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.first
import org.bitcoindevkit.devkitwallet.data.datastore.AppSettings

class AppSettingsRepository(private val store: DataStore<AppSettings>) {
    suspend fun fetchDarkTheme() = store.data.first().darkTheme

    suspend fun setDarkTheme(isDark: Boolean) = store.updateData { it.copy(darkTheme = isDark) }

    suspend fun fetchIntroDone() = store.data.first().introDone

    suspend fun setIntroDone() = store.updateData { it.copy(introDone = true) }
}
