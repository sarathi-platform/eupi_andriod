package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.repository.SectionStatusUpdateRepository
import javax.inject.Inject

class SectionStatusUpdateUseCase @Inject constructor(
    private val sectionStatusUpdateRepository: SectionStatusUpdateRepository
) {

    suspend operator fun invoke(
        missionId: Int,
        surveyId: Int,
        sectionId: Int,
        taskId: Int,
        status: String
    ) {
        sectionStatusUpdateRepository.addOrUpdateSectionStatusForTask(
            missionId,
            surveyId,
            sectionId,
            taskId,
            status
        )
    }

}