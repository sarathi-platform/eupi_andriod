package com.patsurvey.nudge.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.database.LanguageEntity
import com.patsurvey.nudge.utils.BLANK_STRING

data class TransactionResponseModel(
    @SerializedName("transactionId")
    @Expose
    val transactionId:String = BLANK_STRING
)
