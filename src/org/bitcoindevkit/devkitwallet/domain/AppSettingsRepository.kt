/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.bitcoindevkit.devkitwallet.domain

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.first
import org.bitcoindevkit.devkitwallet.data.datastore.AppSettings

/**
 * Repository that reads and writes global app preferences via a [DataStore].
 *
 * All operations are suspendable and run on the IO dispatcher implicitly via DataStore.
 */
class AppSettingsRepository(private val store: DataStore<AppSettings>) {
    /** Reads the current dark-theme preference. */
    suspend fun fetchDarkTheme() = store.data.first().darkTheme

    /** Persists the dark-theme preference. */
    suspend fun setDarkTheme(isDark: Boolean) = store.updateData { it.copy(darkTheme = isDark) }

    /** Reads whether the onboarding flow has already been completed. */
    suspend fun fetchIntroDone() = store.data.first().introDone

    /** Marks the onboarding flow as completed. */
    suspend fun setIntroDone() = store.updateData { it.copy(introDone = true) }
}
