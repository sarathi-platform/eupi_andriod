package com.nrlm.baselinesurvey.ui.language.domain.repository

import com.nrlm.baselinesurvey.LANGUAGE_OPEN_FROM_SETTING
import com.nrlm.baselinesurvey.activity.MainActivity
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.dao.LanguageListDao
import com.nrlm.baselinesurvey.database.dao.VillageListDao
import com.nrlm.baselinesurvey.database.entity.LanguageEntity
import com.nrlm.baselinesurvey.database.entity.VillageEntity
import javax.inject.Inject

class LanguageScreenRepositoryImpl @Inject constructor(
    private val prefRepo: PrefRepo,
    private val languageListDao: LanguageListDao,
    private val villageListDao: VillageListDao
): LanguageScreenRepository {

    override suspend fun getAllLanguages(): List<LanguageEntity> {
        return languageListDao.getAllLanguages()
    }

    override fun getSelectedVillage(): VillageEntity {
        return prefRepo.getSelectedVillage()
    }

    override suspend fun fetchVillageDetailsForLanguage(villageId: Int, languageId: Int): VillageEntity {
        return villageListDao.fetchVillageDetailsForLanguage(villageId, languageId)
    }

    override fun saveSelectedVillage(village: VillageEntity) {
        prefRepo.saveSelectedVillage(village)
    }

    override fun saveSelectedLanguageId(id: Int) {
        prefRepo.saveAppLanguageId(id)
    }

    override fun saveSelectedLanguageCode(mainActivity: MainActivity, languageCode: String) {
        prefRepo.saveAppLanguage(languageCode)
        mainActivity.setLanguage(languageCode)
    }

    override fun getLanguageScreenOpenFrom(): Boolean {
        return prefRepo.getPref(LANGUAGE_OPEN_FROM_SETTING, false)
    }

}