package com.nrlm.baselinesurvey.ui.question_screen.domain.use_case

import com.nrlm.baselinesurvey.model.datamodel.Sections
import com.nrlm.baselinesurvey.ui.question_screen.domain.repository.QuestionScreenRepository

class GetSectionUseCase(
    private val repository: QuestionScreenRepository
) {

    operator fun invoke(sectionId: Int): Sections {
        return repository.getSections(sectionId)
    }

}
