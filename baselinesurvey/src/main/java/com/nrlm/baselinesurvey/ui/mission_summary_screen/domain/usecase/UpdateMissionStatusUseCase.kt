package com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.usecase

import com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.repository.MissionSummaryScreenRepository
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import javax.inject.Inject

class UpdateMissionStatusUseCase @Inject constructor(private val repository: MissionSummaryScreenRepository) {

    suspend operator fun invoke(missionId: Int, status: SectionStatus) {
        repository.updateMissionStatus(missionId, status)
    }

}