/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi

data class ReceiveScreenState(
    val address: String? = null,
    val addressIndex: UInt? = null,
)

sealed interface ReceiveScreenAction {
    data object UpdateAddress : ReceiveScreenAction
}
