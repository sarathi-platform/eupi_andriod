package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.model.uiModel.SectionUiModel
import com.sarathi.dataloadingmangement.repository.SectionListRepository
import javax.inject.Inject

class GetSectionListUseCase @Inject constructor(
    private val sectionListRepository: SectionListRepository
) {

    suspend operator fun invoke(surveyId: Int): List<SectionUiModel> {

        return sectionListRepository.getSectionListForSurvey(surveyId)

    }

}