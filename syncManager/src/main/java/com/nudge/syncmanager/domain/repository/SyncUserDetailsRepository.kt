package com.nudge.syncmanager.domain.repository

interface SyncUserDetailsRepository {
    fun getUserMobileNumber(): String
    fun getUserID(): String
    fun getUserEmail(): String
    fun getUserName(): String
    fun getLoggedInUserType(): String
}