package com.nrlm.baselinesurvey.ui.profile.domain.repository

interface ProfileBSRepository {
    fun getUserName():String
    fun getUserEmail():String
    fun getUserMobileNumber():String
    fun getUserIdentityNumber():String
}