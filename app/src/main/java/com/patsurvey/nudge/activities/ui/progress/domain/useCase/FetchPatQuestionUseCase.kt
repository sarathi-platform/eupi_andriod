package com.patsurvey.nudge.activities.ui.progress.domain.useCase

import com.patsurvey.nudge.activities.ui.progress.domain.repository.FetchPatQuestionRepository
import javax.inject.Inject

class FetchPatQuestionUseCase @Inject constructor(
    private val fetchPatQuestionRepository: FetchPatQuestionRepository
) {

    suspend operator fun invoke() {

    }

}