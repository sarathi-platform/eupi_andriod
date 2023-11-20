package com.nrlm.baselinesurvey.ui.question_screen.domain.use_case

import com.nrlm.baselinesurvey.ui.question_screen.domain.repository.QuestionScreenRepository
import com.nrlm.baselinesurvey.utils.SectionStatus

class UpdateSectionProgressUseCase(
    private val repository: QuestionScreenRepository
) {

    operator fun invoke(surveyId: Int,
                        sectionId: Int,
                        didiId: Int,
                        sectionStatus: SectionStatus
    ) {
        repository.updateSectionProgress(surveyId, sectionId, didiId, sectionStatus)
    }

}
