package com.nrlm.baselinesurvey.activity.domain.repository

import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.network.interfaces.BaseLineApiService
import javax.inject.Inject

class MainActivityRepositoryImpl @Inject constructor(
    val prefRepo: PrefRepo,
    val baseLineApiService: BaseLineApiService
): MainActivityRepository {

    override fun isLoggedIn(): Boolean {
        return (prefRepo.getAccessToken()?.isNotEmpty() == true)
    }


}