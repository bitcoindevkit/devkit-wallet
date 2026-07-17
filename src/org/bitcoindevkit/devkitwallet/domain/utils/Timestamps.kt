/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.bitcoindevkit.devkitwallet.domain.utils

import android.text.format.DateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Converts a Unix timestamp (seconds since epoch) into a human-readable date-time string.
 *
 * Format: `"MMMM d yyyy HH:mm"` (e.g. `"January 15 2024 09:30"`).
 */
fun ULong.timestampToString(): String {
    val calendar = Calendar.getInstance(Locale.ENGLISH)
    calendar.timeInMillis = (this * 1000u).toLong()
    return DateFormat.format("MMMM d yyyy HH:mm", calendar).toString()
}
