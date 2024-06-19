package com.patsurvey.nudge.activities.settings.domain.use_case

import com.patsurvey.nudge.activities.settings.domain.repository.SettingBSRepository
import com.patsurvey.nudge.database.CasteEntity
import javax.inject.Inject

class GetCasteUseCase @Inject constructor(
    private val repository: SettingBSRepository
) {
    fun getAllCasteForLanguage(languageId:Int): List<CasteEntity> {
        return repository.getAllCasteForLanguage(languageId)
    }

}