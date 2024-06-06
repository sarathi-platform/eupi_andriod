package com.patsurvey.nudge.activities

import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.network.interfaces.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VillageScreenViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val apiService: ApiService
): BaseViewModel() {

    fun isUserBpc() = prefRepo.isUserBPC()

    fun getStateId():Int{
        return prefRepo.getStateId()
    }

    override fun onServerError(error: ErrorModel?) {
        TODO("Not yet implemented")
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        TODO("Not yet implemented")
    }
}