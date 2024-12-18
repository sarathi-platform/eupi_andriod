package com.nudge.core.usecase

import android.text.TextUtils
import com.nudge.core.BASELINE_MISSION_NAME
import com.nudge.core.DEFAULT_BASELINE_V1_IDS
import com.nudge.core.data.repository.BaselineV1CheckRepository
import com.nudge.core.parseStringToList
import javax.inject.Inject

class BaselineV1CheckUseCase @Inject constructor(
    private val baselineV1CheckRepository: BaselineV1CheckRepository
) {

    operator fun invoke(missionName: String): Boolean {
        val baselineV1Ids = getBaselineV1Ids()

        return baselineV1Ids
            .parseStringToList()
            .contains(
                baselineV1CheckRepository.getStateId()
            ) && missionName.contains(
            BASELINE_MISSION_NAME,
            true
        )
    }

    fun getBaselineV1Ids(): String {
        var baselineV1Ids =
            baselineV1CheckRepository.getBaselineV1Ids()

        if (TextUtils.isEmpty(baselineV1Ids)) {
            baselineV1Ids = DEFAULT_BASELINE_V1_IDS
        }

        return baselineV1Ids
    }

    fun isBaselineV2(stateId: String): Boolean {
        return !getBaselineV1Ids().contains(stateId)
    }

}