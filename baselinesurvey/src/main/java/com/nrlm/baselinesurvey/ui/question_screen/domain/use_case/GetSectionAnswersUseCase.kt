package com.nrlm.baselinesurvey.ui.question_screen.domain.use_case

import com.nrlm.baselinesurvey.database.entity.SectionAnswerEntity
import com.nrlm.baselinesurvey.ui.question_screen.domain.repository.QuestionScreenRepository

class GetSectionAnswersUseCase(
    private val repository: QuestionScreenRepository
) {

    fun getAllAnswersForDidi(didiId: Int): List<SectionAnswerEntity> {
        return repository.getAllAnswersForDidi(didiId)
    }

    fun getSectionAnswerForDidi(sectionId: Int, didiId: Int): List<SectionAnswerEntity> {
        return repository.getSectionAnswerForDidi(sectionId, didiId)
    }



}
