/*
 * Copyright 2021-2025 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.domain.utils

import java.text.DecimalFormat

fun ULong?.formatInBtc(): String {
    val balanceInSats =
        if (this == 0UL || this == null) {
            0F
        } else {
            this.toDouble().div(100_000_000)
        }
    return DecimalFormat("0.00000000").format(balanceInSats)
}
