package com.patsurvey.nudge.activities.settings.domain.use_case

import com.patsurvey.nudge.activities.settings.domain.repository.SettingBSRepository


class SaveLanguageScreenOpenFromUseCase(private val repository: SettingBSRepository) {

    operator fun invoke(){
        repository.saveLanguageScreenOpenFrom()
    }
}