package com.nrlm.baselinesurvey.activity.domain.repository

import com.nrlm.baselinesurvey.data.prefs.PrefBSRepo
import com.nrlm.baselinesurvey.network.interfaces.BaseLineApiService
import javax.inject.Inject

class MainActivityRepositoryImpl @Inject constructor(
    val prefBSRepo: PrefBSRepo,
    val baseLineApiService: BaseLineApiService
): MainActivityRepository {

    override fun isLoggedIn(): Boolean {
        return (prefBSRepo.getAccessToken()?.isNotEmpty() == true)
    }


}