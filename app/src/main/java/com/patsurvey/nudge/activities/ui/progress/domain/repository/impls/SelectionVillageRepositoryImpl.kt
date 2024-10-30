package com.patsurvey.nudge.activities.ui.progress.domain.repository.impls

import com.nudge.core.DEFAULT_LANGUAGE_ID
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.value
import com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces.SelectionVillageRepository
import com.patsurvey.nudge.data.prefs.SharedPrefs
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.VillageListDao
import javax.inject.Inject

class SelectionVillageRepositoryImpl @Inject constructor(
    private val selectionSharedPrefs: SharedPrefs,
    private val coreSharedPrefs: CoreSharedPrefs,
    private val villageListDao: VillageListDao
) : SelectionVillageRepository {
    override fun setSelectedVillage(villageEntity: VillageEntity) {
        selectionSharedPrefs.saveSelectedVillage(villageEntity)
    }

    override fun getSelectedVillage(): VillageEntity {
        return selectionSharedPrefs.getSelectedVillage()
    }

    override suspend fun getVillageListFromDb(): List<VillageEntity> {
        val finalResult = ArrayList<VillageEntity>()
        val selectedLanguage = selectionSharedPrefs.getAppLanguageId().value(DEFAULT_LANGUAGE_ID)
        val villageListInSelectedLanguage = villageListDao.getAllVillages(selectedLanguage)
        val villageListInDefaultLanguage = villageListDao.getAllVillages(DEFAULT_LANGUAGE_ID)
        finalResult.addAll(villageListInSelectedLanguage)
        if (villageListInDefaultLanguage.size != villageListInSelectedLanguage.size) {
            val missingVillages = villageListInSelectedLanguage.filterNot { selectedVillage ->
                villageListInDefaultLanguage.any { defaultVillage ->
                    defaultVillage.id == selectedVillage.id
                }
            }
            finalResult.addAll(missingVillages)
        }
        return finalResult
    }
}