package com.sarathi.dataloadingmangement.model.mat.response


import com.google.gson.annotations.SerializedName

data class ActivityConfig(
    @SerializedName("activity_title")
    val activityTitle: List<ActivityTitle>,
    @SerializedName("activity_type")
    val activityType: String,
    @SerializedName("activity_type_id")
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
    @SerializedName("survey_id")
    val surveyId: Int,
    @SerializedName("ui_config")
    val uiConfig: UiConfig,
    @SerializedName("validations")
    val validations: List<Any>
)