package com.tothenew.android_starter_project.module.orders

import androidx.lifecycle.MutableLiveData
import com.tothenew.android_starter_project.base.BaseViewModel
import com.tothenew.android_starter_project.model.response.ApiResponseModel
import com.tothenew.android_starter_project.model.response.OrdersListResponse
import com.tothenew.android_starter_project.model_ui.OrderedItem
import com.tothenew.android_starter_project.repository.OrderRepositoryV2
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(private val orderRepo: OrderRepositoryV2)  : BaseViewModel(){

    fun fetchOrders(): MutableLiveData<ApiResponseModel> {
       return orderRepo.getOrdersList()
    }


    /**
     * return list or items using response
     */
    fun getOrdersList(items: List<OrdersListResponse.Data.Item?>?): List<OrderedItem> {
         return items?.mapIndexedNotNull { index, item ->
            OrderedItem(
                extra = item?.extra,
                name = item?.name,
                price = item?.price,
                imageId = index
            )
        } ?: emptyList()
    }

}