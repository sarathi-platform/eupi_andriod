package com.patsurvey.nudge.model.response

import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.network.model.ErrorDataModel


class ApiResponseModel<T>(
    @SerializedName("status")
    @Expose val status: String,

    @SerializedName("message")
    @Expose
     val message: String,

    @SerializedName("data")
    @Expose
     val data: T? = null ){

}