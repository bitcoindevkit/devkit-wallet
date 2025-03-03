package org.bitcoindevkit.devkitwallet.domain

object DwLogger {
    private const val MAX_LOGS = 5000
    private val logEntries = ArrayDeque<String>(MAX_LOGS)
    private val lock = Any()

    fun log(
        tag: String,
        message: String,
    ) {
        synchronized(lock) {
            if (logEntries.size >= MAX_LOGS) {
                logEntries.removeLast()
            }
            logEntries.addFirst("${System.currentTimeMillis()} [$tag]: $message")
        }
    }

    fun getLogs(): List<String> {
        synchronized(lock) {
            return logEntries.toList()
        }
    }
}
