package com.sarathi.dataloadingmangement.model.mat.response


import com.google.gson.annotations.SerializedName

data class ActivityConfig(
    @SerializedName("languages")
    val activityTitle: List<ActivityTitle>,
    @SerializedName("activityType")
    val activityType: String,
    @SerializedName("activityTypeId")
    val activityTypeId: Int,
    @SerializedName("conditions")
    val conditions: List<Any>,
    @SerializedName("content")
    val content: List<ContentResponse>,
    @SerializedName("doer")
    val doer: String,
    @SerializedName("reviewer")
    val reviewer: String,
    @SerializedName("subject")
    val subject: String,
    @SerializedName("surveyId")
    val surveyId: Int,
    @SerializedName("uiConfig")
    val uiConfig: List<AttributeResponse>,
    @SerializedName("validations")
    val validations: List<Any>,
    @SerializedName("grantConfig")
    val grantConfig: List<GrantConfigResponse>? = emptyList(),
    @SerializedName("formConfig")
    val formConfig: List<FormConfigResponse?> = emptyList(),
    @SerializedName("taskCompletion")
    val taskCompletion: String?,
    @SerializedName("icon")
    val icon: String?,
)