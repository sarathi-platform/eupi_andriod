package com.patsurvey.nudge.network.interfaces

import com.patsurvey.nudge.model.response.OrdersListResponse
import com.patsurvey.nudge.network.BaseNetworkConstants.API_ORDERS
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    /**
     * Fetch List of orders using GET API Call on given Url
     * Url would be something like this /v3/b6a30bb0-140f-4966-8608-1dc35fa1fadc
     */
    @GET(API_ORDERS)
    fun getOrdersListV2(): Call<OrdersListResponse>
}