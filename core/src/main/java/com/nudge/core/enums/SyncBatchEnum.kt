package com.nudge.core.enums

enum class SyncBatchEnum(
    val batchSize: Int,
    val maxPayloadSizeInkb: Long,
    val maxClientIdsForStatus: Int
) {
    POOR(batchSize = 5, maxPayloadSizeInkb = 30, maxClientIdsForStatus = 25),
    MODERATE(batchSize = 10, maxPayloadSizeInkb = 60, maxClientIdsForStatus = 40),
    GOOD(batchSize = 15, maxPayloadSizeInkb = 90, maxClientIdsForStatus = 50),
    EXCELLENT(batchSize = 20, maxPayloadSizeInkb = 150, maxClientIdsForStatus = 60),
    UNKNOWN(batchSize = 3, maxPayloadSizeInkb = 20, maxClientIdsForStatus = 10)

}