package com.nrlm.baselinesurvey.ui.mission_screen.domain.use_case

import com.nrlm.baselinesurvey.database.entity.SurveyEntity
import com.nrlm.baselinesurvey.ui.mission_screen.domain.repository.MissionScreenRepository
import javax.inject.Inject

class GetSectionsListUseCase @Inject constructor(private val repository: MissionScreenRepository) {
    suspend operator fun invoke(): List<SurveyEntity> {
        return repository.getSectionsList()
    }
}