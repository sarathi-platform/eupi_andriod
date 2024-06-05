package com.nudge.core.preference

interface CorePrefRepo {

    fun getBackupFileName(mobileNo: String): String
    fun setBackupFileName(fileName: String)

    fun getImageBackupFileName(mobileNo: String): String
    fun setImageBackupFileName(fileName: String)
    fun isFileExported(): Boolean
    fun setFileExported(isExported: Boolean)
    fun setMobileNo(mobileNo: String)
    fun getMobileNo(): String
    fun getUserType(): String
    fun setUserType(userTypes: String)
    fun getUniqueUserIdentifier(): String
    fun getAppLanguage(): String?
    fun saveAppLanguage(code: String?)
    fun getUserId(): String
    fun setUserId(userId: String)

}