package com.nrlm.baselinesurvey.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TransactionResponse(
    @SerializedName("transactionId")
    @Expose
    val transactionId: String?


)
