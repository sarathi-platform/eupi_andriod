package com.nrlm.baselinesurvey.activity.domain.repository

import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.network.interfaces.ApiService
import javax.inject.Inject

class MainActivityRepositoryImpl @Inject constructor(
    val prefRepo: PrefRepo,
    val apiService: ApiService
): MainActivityRepository {

    override fun isLoggedIn(): Boolean {
        return (prefRepo.getAccessToken()?.isNotEmpty() == true)
    }


}