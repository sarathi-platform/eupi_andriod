package com.nrlm.baselinesurvey.ui.question_screen.domain.use_case

import com.nrlm.baselinesurvey.database.entity.SectionEntity
import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.ui.question_screen.domain.repository.QuestionScreenRepository

class GetSectionsListUseCase(private val repository: QuestionScreenRepository) {

    suspend operator fun invoke(surveyId: Int): List<SectionEntity> {
        val languageId = repository.getSelectedLanguage()
        return repository.getSectionsList(surveyId, languageId)
    }

    fun getSelectedLanguage(): Int {
        return repository.getSelectedLanguage()
    }

}
