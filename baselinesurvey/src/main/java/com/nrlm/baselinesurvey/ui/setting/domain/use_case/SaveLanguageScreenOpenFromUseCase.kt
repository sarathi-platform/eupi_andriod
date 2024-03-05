package com.nrlm.baselinesurvey.ui.setting.domain.use_case

import com.nrlm.baselinesurvey.ui.setting.domain.repository.SettingBSRepository


class SaveLanguageScreenOpenFromUseCase(private val repository: SettingBSRepository) {

    operator fun invoke(){
        repository.saveLanguageScreenOpenFrom()
    }
}