package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.data.entities.SurveyEntity
import com.sarathi.dataloadingmangement.model.uiModel.SectionUiModel
import com.sarathi.dataloadingmangement.repository.ContentRepositoryImpl
import com.sarathi.dataloadingmangement.repository.SectionListRepository
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import javax.inject.Inject

class GetSectionListUseCase @Inject constructor(
    private val sectionListRepository: SectionListRepository,
    private val contentRepositoryImpl: ContentRepositoryImpl,
    private val coreSharedPrefs: CoreSharedPrefs
) {

    suspend operator fun invoke(surveyId: Int): List<SectionUiModel> {
        // Fetch the sections from the repository
        val sections = sectionListRepository.getSectionListForSurvey(surveyId)

        // Map each section to a SectionUiModel
        return sections.map { section ->
            val contentList = section.contentEntities.mapNotNull { contentEntity ->
                val contentKey = contentEntity.contentKey ?: BLANK_STRING
                val languageCode = coreSharedPrefs.getSelectedLanguageCode()

                // Fetch content data from the DAO
                contentRepositoryImpl.getContentList(
                    contentKey = contentKey,
                    languageCode = languageCode
                )
            }
            // Return a copy of the section with updated content entities
            section.copy(contentEntities = contentList)
        }
    }

    suspend operator fun invoke(surveyId: Int, sectionId: Int): SectionUiModel? {

        return invoke(surveyId).find { it.sectionId == sectionId }

    }

    suspend fun getSectionStatusMap(missionId: Int, surveyId: Int, taskId: Int): Map<Int, String> {
        val statusMap: MutableMap<Int, String> = HashMap<Int, String>()
        sectionListRepository.getSectionStatusForTask(missionId, surveyId, taskId).forEach {
            statusMap[it.sectionId] = it.sectionStatus ?: SurveyStatusEnum.INPROGRESS.name
        }

        return statusMap
    }

    suspend fun getSurveyEntity(surveyId: Int): SurveyEntity? {
        return sectionListRepository.getSurveyEntity(surveyId)
    }

}