package com.nrlm.baselinesurvey.ui.language.domain.repository

import android.content.Context
import com.nrlm.baselinesurvey.database.entity.LanguageEntity
import com.nrlm.baselinesurvey.database.entity.VillageEntity

interface LanguageScreenRepository {

    suspend fun getAllLanguages(): List<LanguageEntity>

    fun getSelectedVillage(): VillageEntity

    suspend fun fetchVillageDetailsForLanguage(villageId: Int, languageId: Int):VillageEntity

    fun saveSelectedVillage(village: VillageEntity)

    fun saveSelectedLanguageId(id: Int)

    fun saveSelectedLanguageCode(mainActivity: Context, languageCode: String)

    fun getLanguageScreenOpenFrom():Boolean

}