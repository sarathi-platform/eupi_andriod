package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.model.uiModel.SectionUiModel
import com.sarathi.dataloadingmangement.repository.SectionListRepository
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import javax.inject.Inject

class GetSectionListUseCase @Inject constructor(
    private val sectionListRepository: SectionListRepository
) {

    suspend operator fun invoke(surveyId: Int): List<SectionUiModel> {

        return sectionListRepository.getSectionListForSurvey(surveyId)

    }

    suspend fun getSectionStatusMap(missionId: Int, surveyId: Int, taskId: Int): Map<Int, String> {
        val statusMap: MutableMap<Int, String> = HashMap<Int, String>()
        sectionListRepository.getSectionStatusForTask(missionId, surveyId, taskId).forEach {
            statusMap[it.sectionId] = it.sectionStatus ?: SurveyStatusEnum.INPROGRESS.name
        }

        return statusMap
    }

}