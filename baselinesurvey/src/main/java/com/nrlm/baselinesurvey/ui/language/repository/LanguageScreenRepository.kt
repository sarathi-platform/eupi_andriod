package com.nrlm.baselinesurvey.ui.language.repository

import com.nrlm.baselinesurvey.MainActivity
import com.nrlm.baselinesurvey.database.entity.LanguageEntity
import com.nrlm.baselinesurvey.database.entity.VillageEntity
import kotlinx.coroutines.flow.Flow

interface LanguageScreenRepository {

    suspend fun getAllLanguages(): List<LanguageEntity>

    fun getSelectedVillage(): VillageEntity

    suspend fun fetchVillageDetailsForLanguage(villageId: Int, languageId: Int):VillageEntity

    fun saveSelectedVillage(village: VillageEntity)

    fun saveSelectedLanguageId(id: Int)

    fun saveSelectedLanguageCode(mainActivity: MainActivity, languageCode: String)

}