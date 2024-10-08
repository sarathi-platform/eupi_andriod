package com.patsurvey.nudge.activities.ui.progress.domain.useCase

import com.patsurvey.nudge.activities.ui.progress.domain.repository.FetchCasteListRepository
import javax.inject.Inject

class FetchCasteListUseCase @Inject constructor(
    private val fetchCasteListRepository: FetchCasteListRepository
) {

    suspend operator fun invoke() {

    }

}