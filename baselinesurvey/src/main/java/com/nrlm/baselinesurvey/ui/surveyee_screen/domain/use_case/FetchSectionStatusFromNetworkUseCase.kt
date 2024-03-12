package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

import com.nrlm.baselinesurvey.model.request.SectionStatusRequest
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.DataLoadingScreenRepository
import com.nrlm.baselinesurvey.utils.BaselineLogger

class FetchSectionStatusFromNetworkUseCase(private val repository: DataLoadingScreenRepository) {

    suspend operator fun invoke() {

        try {
            val sectionStatusRequest = SectionStatusRequest(sectionId = 0, surveyId = 0)
            repository.getSectionStatus(sectionStatusRequest)

        } catch (ex: Exception) {
            BaselineLogger.e("FetchCastesFromNetworkUseCase", "invoke", ex)
        }
    }

}