package com.nrlm.baselinesurvey.ui.language.domain.repository

import android.content.Context
import com.nrlm.baselinesurvey.LANGUAGE_OPEN_FROM_SETTING
import com.nrlm.baselinesurvey.data.prefs.PrefBSRepo
import com.nrlm.baselinesurvey.database.dao.LanguageListDao
import com.nrlm.baselinesurvey.database.dao.VillageListDao
import com.nrlm.baselinesurvey.database.entity.LanguageEntity
import com.nrlm.baselinesurvey.database.entity.VillageEntity
import javax.inject.Inject

class LanguageScreenRepositoryImpl @Inject constructor(
    private val prefBSRepo: PrefBSRepo,
    private val languageListDao: LanguageListDao,
    private val villageListDao: VillageListDao
): LanguageScreenRepository {

    override suspend fun getAllLanguages(): List<LanguageEntity> {
        return languageListDao.getAllLanguages()
    }

    override fun getSelectedVillage(): VillageEntity {
        return prefBSRepo.getSelectedVillage()
    }

    override suspend fun fetchVillageDetailsForLanguage(villageId: Int, languageId: Int): VillageEntity {
        return villageListDao.fetchVillageDetailsForLanguage(villageId, languageId)
    }

    override fun saveSelectedVillage(village: VillageEntity) {
        prefBSRepo.saveSelectedVillage(village)
    }

    override fun saveSelectedLanguageId(id: Int) {
        prefBSRepo.saveAppLanguageId(id)
    }

    override fun saveSelectedLanguageCode(mainActivity: Context, languageCode: String) {
        prefBSRepo.saveAppLanguage(languageCode)
    }

    override fun getLanguageScreenOpenFrom(): Boolean {
        return prefBSRepo.getPref(LANGUAGE_OPEN_FROM_SETTING,false)
    }

}