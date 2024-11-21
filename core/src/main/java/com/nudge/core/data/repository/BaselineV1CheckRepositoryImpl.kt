package com.nudge.core.data.repository

import com.nudge.core.BLANK_STRING
import com.nudge.core.enums.AppConfigKeysEnum
import com.nudge.core.preference.CoreSharedPrefs
import javax.inject.Inject

class BaselineV1CheckRepositoryImpl @Inject constructor(
    val coreSharedPrefs: CoreSharedPrefs
) : BaselineV1CheckRepository {

    override fun getBaselineV1Ids(): String {
        return coreSharedPrefs.getPref(AppConfigKeysEnum.USE_BASELINE_V1.name, BLANK_STRING)
    }

    override fun getStateId(): Int {
        return coreSharedPrefs.getStateId()
    }


}