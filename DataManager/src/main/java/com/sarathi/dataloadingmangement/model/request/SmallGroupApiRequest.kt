package com.sarathi.dataloadingmangement.model.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SmallGroupApiRequest(
    @SerializedName("userList")
    @Expose
    val userList: List<Int>
)
