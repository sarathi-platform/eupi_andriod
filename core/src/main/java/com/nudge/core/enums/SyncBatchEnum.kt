package com.nudge.core.enums

enum class SyncBatchEnum(val batchSize: Int, val maxPayloadSize: Long) {
    POOR(batchSize = 5, maxPayloadSize = 150),
    MODERATE(batchSize = 10, maxPayloadSize = 550),
    GOOD(batchSize = 15, maxPayloadSize = 2000),
    EXCELLENT(batchSize = 20, maxPayloadSize = 5000),
    UNKNOWN(batchSize = 3, maxPayloadSize = 100)

}