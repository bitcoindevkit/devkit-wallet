package org.bitcoindevkit.devkitwallet.domain

import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

object DwLogger {
    private const val MAX_LOGS = 5000
    private val logEntries = ArrayDeque<String>(MAX_LOGS)
    private val lock = Any()

    fun log(
        tag: LogLevel,
        message: String,
    ) {
        synchronized(lock) {
            if (logEntries.size >= MAX_LOGS) {
                logEntries.removeLast()
            }
            val millis = System.currentTimeMillis()
            val dateTime = Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .truncatedTo(ChronoUnit.SECONDS)

            logEntries.addFirst("$dateTime $tag $message")
        }
    }

    fun getLogs(): List<String> {
        synchronized(lock) {
            return logEntries.toList()
        }
    }

    enum class LogLevel {
        INFO,
        WARN,
        ERROR;

        override fun toString(): String {
            return when (this) {
                INFO ->  "[INFO] "
                WARN ->  "[WARN] "
                ERROR -> "[ERROR]"
            }
        }
    }
}
