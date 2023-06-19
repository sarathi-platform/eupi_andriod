package com.patsurvey.nudge.activities.settings

import com.google.gson.annotations.SerializedName

data class TransactionIdResponse(@SerializedName("transactionId") var transactionId: String,
                                 @SerializedName("referenceId") var referenceId: Int)
data class TransactionIdResponseForPatStatus(@SerializedName("transactionId") var transactionId: String,
                                 @SerializedName("referenceId") var referenceId: String)
