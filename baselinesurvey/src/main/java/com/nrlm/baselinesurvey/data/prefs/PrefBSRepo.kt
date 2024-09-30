package com.nrlm.baselinesurvey.data.prefs

import com.nrlm.baselinesurvey.database.entity.VillageEntity


interface PrefBSRepo {

    fun getLoginStatus(): Boolean

    fun getAccessToken(): String?

    fun saveAccessToken(token: String)

    fun getAppLanguage():String?

    fun saveAppLanguage(code: String?)

    fun saveSelectedVillage(village: VillageEntity)

    fun getSelectedVillage():VillageEntity

    fun getAppLanguageId():Int?

    fun saveAppLanguageId(languageId: Int?)

    fun settingOpenFrom(): Int
    fun saveSettingOpenFrom(pageFrom: Int)

    fun getFromPage():String

    fun saveFromPage(pageFrom:String)

    fun savePref(key: String, value: String)

    fun savePref(key: String, value: Int)

    fun savePref(key: String, value: Boolean)

    fun savePref(key: String, value: Long)

    fun savePref(key: String, value: Float)

    fun getPref(key: String, defaultValue: Int): Int

    fun getPref(key: String, defaultValue: String): String?

    fun getPref(key: String, defaultValue: Boolean): Boolean

    fun getPref(key: String, defaultValue: Long): Long

    fun getPref(key: String, defaultValue: Float): Float

    fun saveMobileNumber(mobileNumber: String)

    fun getMobileNumber(): String?

    fun getUserId(): String

    fun clearSharedPreference()

    fun setDataSyncStatus(status: Boolean)
    fun getDataSyncStatus(): Boolean
    fun setPreviousUserMobile(mobileNumber: String)
    fun getPreviousUserMobile(): String
    fun getUniqueUserIdentifier(): String
}