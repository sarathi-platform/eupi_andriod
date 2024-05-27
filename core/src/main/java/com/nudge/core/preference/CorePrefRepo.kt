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

}