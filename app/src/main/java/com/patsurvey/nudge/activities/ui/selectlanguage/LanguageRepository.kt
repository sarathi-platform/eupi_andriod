package com.patsurvey.nudge.activities.ui.selectlanguage

import com.nudge.core.preference.CoreSharedPrefs
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.LanguageEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.LanguageListDao
import com.patsurvey.nudge.database.dao.VillageListDao
import javax.inject.Inject

class LanguageRepository @Inject constructor(
    val prefRepo: PrefRepo,
    val corePrefRepo: CoreSharedPrefs,
    val languageListDao: LanguageListDao,
    val villageListDao: VillageListDao
) : BaseRepository() {


    fun getAllLanguages(): List<LanguageEntity> {
        return languageListDao.getAllLanguages();
    }

    fun getSelectedVillage(): VillageEntity {
        return prefRepo.getSelectedVillage();
    }

    fun fetchVillageDetailsForLanguage(languageId: Int) {
        saveSelectedVillage(
            villageListDao.fetchVillageDetailsForLanguage(
                getSelectedVillage().id,
                languageId
            )
        );
    }

    private fun saveSelectedVillage(village: VillageEntity) {
        prefRepo.saveSelectedVillage(village)
    }

}