package com.tothenew.android_starter_project.model.response

import com.google.gson.annotations.SerializedName
import com.tothenew.android_starter_project.base.BaseResponseModel


data class OrdersListResponse(
    @SerializedName("data")
    val data: Data?,
    @SerializedName("error")
    val error: String?,
    @SerializedName("status")
    val status: String?
) : BaseResponseModel() {
    data class Data(
        @SerializedName("items")
        val items: List<Item?>?
    ) {
        data class Item(
            @SerializedName("extra")
            val extra: String?,
            @SerializedName("name")
            val name: String?,
            @SerializedName("price")
            val price: String?
        )
    }
}