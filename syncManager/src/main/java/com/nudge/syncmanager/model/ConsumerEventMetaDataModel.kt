package com.nudge.syncmanager.model

import com.nudge.core.analytics.mixpanel.CommonEventParams

data class ConsumerEventMetaDataModel(
    val selectedSyncType: Int,
    val commonEventParams: CommonEventParams,
    val success: Boolean,
    val message: String
)
