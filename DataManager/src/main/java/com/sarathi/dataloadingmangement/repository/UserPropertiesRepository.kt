package com.sarathi.dataloadingmangement.repository

interface UserPropertiesRepository {

    fun compareWithPreviousUser(): Boolean
    fun isUserDataLoaded(userType: String): Boolean
    fun getStateId(): Int
    fun isUserBpc(): Boolean
    fun getAppLanguage(): String

}