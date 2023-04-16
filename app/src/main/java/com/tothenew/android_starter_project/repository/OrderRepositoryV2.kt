package com.tothenew.android_starter_project.repository

import androidx.lifecycle.MutableLiveData
import com.tothenew.android_starter_project.base.BaseRepository
import com.tothenew.android_starter_project.model.response.ApiResponseModel
import com.tothenew.android_starter_project.network.interfaces.ApiService

class OrderRepositoryV2(private val apiService: ApiService) : BaseRepository() {

    fun getOrdersList(): MutableLiveData<ApiResponseModel> {
        val onResponseSingleLiveData: MutableLiveData<ApiResponseModel> = MutableLiveData<ApiResponseModel>()
        val data = apiService.getOrdersListV2()
        return callApi(data, onResponseSingleLiveData)
    }
}