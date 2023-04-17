package com.patsurvey.nudge.repository

import androidx.lifecycle.MutableLiveData
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.network.interfaces.ApiService

class OrderRepositoryV2(private val apiService: ApiService) : BaseRepository() {

    fun getOrdersList(): MutableLiveData<ApiResponseModel> {
        val onResponseSingleLiveData: MutableLiveData<ApiResponseModel> = MutableLiveData<ApiResponseModel>()
        val data = apiService.getOrdersListV2()
        return callApi(data, onResponseSingleLiveData)
    }
}