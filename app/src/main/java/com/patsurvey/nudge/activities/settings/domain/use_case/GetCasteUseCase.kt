package com.patsurvey.nudge.activities.settings.domain.use_case

import com.nudge.core.database.entities.CasteEntity
import com.patsurvey.nudge.activities.settings.domain.repository.SettingBSRepository
import javax.inject.Inject

class GetCasteUseCase @Inject constructor(
    private val repository: SettingBSRepository
) {
    fun getAllCasteForLanguage(languageId:Int): List<CasteEntity> {
        return repository.getAllCasteForLanguage(languageId)
    }

}