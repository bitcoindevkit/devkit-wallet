/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.bitcoindevkit.devkitwallet.domain

import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * In-memory ring-buffer logger used for the in-app **LogsScreen**.
 *
 * Stores up to [MAX_LOGS] entries as formatted strings. Thread-safe via `synchronized`.
 */
object DwLogger {
    private const val MAX_LOGS = 5000
    private val logEntries = ArrayDeque<String>(MAX_LOGS)
    private val lock = Any()

    /**
     * Records a new log entry with the current local time.
     *
     * If the buffer exceeds [MAX_LOGS], the oldest entry is dropped.
     */
    fun log(tag: LogLevel, message: String) {
        synchronized(lock) {
            if (logEntries.size >= MAX_LOGS) {
                logEntries.removeLast()
            }
            val millis = System.currentTimeMillis()
            val dateTime =
                Instant.ofEpochMilli(millis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                    .truncatedTo(ChronoUnit.SECONDS)

            logEntries.addFirst("$dateTime $tag $message")
        }
    }

    /** Returns an immutable snapshot of the current log buffer. */
    fun getLogs(): List<String> {
        synchronized(lock) {
            return logEntries.toList()
        }
    }

    /** Severity levels for log entries. */
    enum class LogLevel {
        INFO,
        WARN,
        ERROR;

        override fun toString(): String {
            return when (this) {
                INFO -> "[INFO] "
                WARN -> "[WARN] "
                ERROR -> "[ERROR]"
            }
        }
    }
}
