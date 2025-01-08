package com.patsurvey.nudge.activities.forms.domain.repository

import com.patsurvey.nudge.data.prefs.PrefRepo

class SettingFormsRepositoryImpl(
    private val prefRepo: PrefRepo,
) : SettingFormsRepository {
    override fun getVillageId(): Int {
        return prefRepo.getSelectedVillage().id
    }
}