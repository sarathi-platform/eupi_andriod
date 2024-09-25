package com.nudge.core.preference

interface CorePrefRepo {

    fun getBackupFileName(mobileNo: String): String
    fun setBackupFileName(fileName: String)

    fun getImageBackupFileName(mobileNo: String): String
    fun setImageBackupFileName(fileName: String)
    fun isFileExported(): Boolean
    fun setFileExported(isExported: Boolean)

    fun getUserId(): String

    fun saveUserId(userId: String)

    fun setMobileNo(mobileNo: String)
    fun getMobileNo(): String
    fun getUserType(): String
    fun setUserType(userTypes: String)
    fun getUniqueUserIdentifier(): String
    fun getAppLanguage(): String?
    fun saveAppLanguage(code: String?)
    fun setUserId(userId: String)

    fun getSelectedLanguageId(): Int

    fun getSelectedLanguageCode(): String

    fun savePref(key: String, value: String)

    fun savePref(key: String, value: Int)

    fun savePref(key: String, value: Boolean)

    fun savePref(key: String, value: Long)

    fun savePref(key: String, value: Float)

    fun getPref(key: String, defaultValue: Int): Int

    fun getPref(key: String, defaultValue: String): String

    fun getPref(key: String, defaultValue: Boolean): Boolean

    fun setDataLoaded(isDataLoaded: Boolean)

    fun isDataLoaded(): Boolean

    fun isDidiTabDataLoaded(): Boolean

    fun setDidiTabDataLoaded(isDataLoaded: Boolean)

    fun getPref(key: String, defaultValue: Long): Long

    fun getPref(key: String, defaultValue: Float): Float

    fun getUserNameInInt(): Int
    fun getSyncBatchSize(): Int
    fun getSyncRetryCount(): Int

    fun saveBuildEnvironment(buildEnv: String)

    fun getBuildEnvironment(): String

    fun getMixPanelToken(): String

    fun saveMixPanelToken(token: String)

}