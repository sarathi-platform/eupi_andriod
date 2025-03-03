package com.patsurvey.nudge.activities.forms.domain.usecase

import com.patsurvey.nudge.activities.forms.domain.repository.SettingFormsRepository
import com.patsurvey.nudge.activities.settings.domain.use_case.GetAllPoorDidiForVillageUseCase
import com.patsurvey.nudge.activities.settings.domain.use_case.GetUserDetailsUseCase
import javax.inject.Inject

class SettingFormsUseCase @Inject constructor(
    val repository: SettingFormsRepository,
    val getUserDetailsUseCase: GetUserDetailsUseCase,
    val getAllPoorDidiForVillageUseCase: GetAllPoorDidiForVillageUseCase
) {
    fun getSelectedVillageId() = repository.getVillageId()
}