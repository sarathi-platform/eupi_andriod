package com.patsurvey.nudge.activities.settings.domain.repository

import android.content.Context
import com.patsurvey.nudge.database.CasteEntity
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.model.response.ApiResponseModel


interface SettingBSRepository {
    suspend fun performLogout(): ApiResponseModel<String>

    fun clearSharedPref()

    fun saveLanguageScreenOpenFrom()

    fun clearLocalData()

    fun getUserType():String?

    fun getVillageId():Int
    fun getSettingOpenFrom():Int

    suspend fun getAllPoorDidiForVillage(villageId:Int):List<DidiEntity>
    suspend fun getAllDidiForVillage(villageId:Int):List<DidiEntity>
    suspend fun getAllStepsForVillage(villageId:Int):List<StepListEntity>

    suspend fun exportAllFiles(context: Context)

    fun setAllDataSynced()

    fun getUserMobileNumber():String
    fun getUserID():String
    fun getUserEmail():String

    fun getUserName(): String
    fun getAllCasteForLanguage(languageId:Int):List<CasteEntity>
    fun clearSelectionLocalDB()
    fun isSyncEnable(): Boolean

}