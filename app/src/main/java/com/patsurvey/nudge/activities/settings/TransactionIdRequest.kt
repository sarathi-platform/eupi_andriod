package com.patsurvey.nudge.activities.settings

import com.google.gson.annotations.SerializedName

data class TransactionIdRequest(@SerializedName("module") var module: String?,
                                @SerializedName("transactionId") var transactionId: List<String>)
