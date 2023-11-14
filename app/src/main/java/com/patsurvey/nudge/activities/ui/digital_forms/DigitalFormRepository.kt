package com.patsurvey.nudge.activities.ui.digital_forms

import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.CasteEntity
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.PoorDidiEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.CasteListDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.PoorDidiListDao
import javax.inject.Inject

class DigitalFormRepository @Inject constructor(
    val prefRepo: PrefRepo,
    val poorDidiListDao: PoorDidiListDao,
    val casteListDao: CasteListDao
) : BaseRepository() {

    fun getSelectedVillage(): VillageEntity {
        return this.prefRepo.getSelectedVillage()
    }

    fun getAppLanguageId(): Int? {
        return this.prefRepo.getAppLanguageId()
    }

    fun isUserBPC(): Boolean {
        return this.prefRepo.isUserBPC()
    }

    fun getPref(key: String, defaultValue: Long): Long {
        return this.prefRepo.getPref(key, defaultValue)
    }

    fun getAllDidisForVillage(villageId: Int): List<DidiEntity> {
        return this.didiDao.getAllDidisForVillage(villageId)
    }

    fun getAllPoorDidisForVillage(villageId: Int): List<PoorDidiEntity> {
        return this.poorDidiListDao.getAllPoorDidisForVillage(villageId)
    }

    fun getAllCasteForLanguage(languageId: Int): List<CasteEntity> {
        return this.casteListDao.getAllCasteForLanguage(languageId)
    }


}