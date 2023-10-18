package com.nrlm.baselinesurvey.data.prefs


interface PrefRepo {

    fun getLoginStatus(): Boolean

    fun getAccessToken(): String?

    fun saveAccessToken(token: String)

}