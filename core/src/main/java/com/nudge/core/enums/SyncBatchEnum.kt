package com.nudge.core.enums

enum class SyncBatchEnum(val batchSize: Int, val maxPayloadSize: Long) {
    POOR(batchSize = 5, maxPayloadSize = 30),
    MODERATE(batchSize = 10, maxPayloadSize = 60),
    GOOD(batchSize = 15, maxPayloadSize = 90),
    EXCELLENT(batchSize = 20, maxPayloadSize = 150),
    UNKNOWN(batchSize = 3, maxPayloadSize = 20)

}