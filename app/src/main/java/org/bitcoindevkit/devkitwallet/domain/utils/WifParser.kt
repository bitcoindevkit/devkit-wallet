/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.domain.utils

import android.util.Log
import org.bitcoindevkit.devkitwallet.domain.DwLogger
import org.bitcoindevkit.devkitwallet.domain.DwLogger.LogLevel.INFO
import org.bitcoindevkit.devkitwallet.domain.DwLogger.LogLevel.WARN

private const val TAG = "WifParser"

/**
 * Utility for detecting and extracting WIF-encoded private keys from various input formats.
 */
object WifParser {
    private val WIF_FIRST_CHARS = setOf('5', 'K', 'L', '9', 'c')

    private val BASE58_CHARSET: Set<Char> =
        "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toSet()

    private val WIF_PARAM_KEYS = setOf("wif", "privkey", "private_key", "privatekey")

    fun extract(value: String): String? {
        val trimmed = value.trim()
        val candidates = mutableListOf<String>()

        candidates.add(trimmed)

        if (trimmed.lowercase().startsWith("wif:")) {
            candidates.add(trimmed.drop(4).trim())
        }

        if (trimmed.lowercase().startsWith("bitcoin:")) {
            try {
                val queryPart = trimmed.substringAfter("?", "")
                if (queryPart.isNotEmpty()) {
                    queryPart.split("&").forEach { param ->
                        val kv = param.split("=", limit = 2)
                        if (kv.size == 2 && kv[0].lowercase() in WIF_PARAM_KEYS) {
                            candidates.add(kv[1].trim())
                        }
                    }
                }
            } catch (e: Throwable) {
                Log.i(TAG, "WIF Parsing error: ${e.message}", e)
            }
        }

        val result = candidates.firstOrNull { isLikelyWif(it) }
        if (result != null) {
            DwLogger.log(
                INFO,
                "WifParser: extracted WIF (length=${result.length}) from input (length=${trimmed.length})"
            )
        } else if (candidates.size > 1) {
            DwLogger.log(
                WARN,
                "WifParser: no valid WIF found in ${candidates.size} candidates from input (length=${trimmed.length})"
            )
            candidates.forEach { c ->
                DwLogger.log(
                    WARN,
                    "WifParser: candidate length=${c.length}, first='${c.firstOrNull()}', likelyWif=${isLikelyWif(c)}"
                )
            }
        }
        return result
    }

    fun isLikelyWif(value: String): Boolean {
        if (value.length != 51 && value.length != 52) return false

        val first = value.firstOrNull() ?: return false
        if (first !in WIF_FIRST_CHARS) return false

        return value.all { it in BASE58_CHARSET }
    }
}
