package com.nrlm.baselinesurvey.ui.setting.domain.use_case

import com.nrlm.baselinesurvey.ui.setting.domain.repository.SettingBSRepository
import com.nudge.core.model.SettingOptionModel
import javax.inject.Inject

class GetSettingOptionListUseCase @Inject constructor(
    private val repository :SettingBSRepository
) {
    suspend operator fun invoke():List<SettingOptionModel>{
        return repository.getSettingOptionList()
    }
}