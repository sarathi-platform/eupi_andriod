package com.nrlm.baselinesurvey.model.datamodel

import com.google.gson.annotations.SerializedName

data class DidiDetailList(
    @SerializedName("id") var id: Int,
    @SerializedName("castId") var castId: Int,
    @SerializedName("cohortId") var cohortId: Int,
    @SerializedName("name") var name: String,
    @SerializedName("guardianName") var guardianName: String,
    @SerializedName("address") var address: String,
    @SerializedName("relationship") var relationship: String,
    @SerializedName("createdDate") var createdDate: Long,
    @SerializedName("modifiedDate") var modifiedDate: Long,
    @SerializedName("localCreatedDate") var localCreatedDate: Long,
    @SerializedName("localModifiedDate") var localModifiedDate: Long,
    @SerializedName("score") var score: Double,
    @SerializedName("comment") var comment: String,
    @SerializedName("beneficiaryProcessStatus") var beneficiaryProcessStatus: List<BeneficiaryProcessStatusModel>,
    @SerializedName("bpcScore") var bpcScore: Double,
    @SerializedName("bpcComment") var bpcComment: String,
    @SerializedName("crpScore") var crpScore: Double,
    @SerializedName("crpComment") var crpComment: String,
    @SerializedName("crpUploadedImage") var crpUploadedImage: String,
    @SerializedName("rankingEdit") var rankingEdit: Boolean,
    @SerializedName("patEdit") var patEdit: Boolean,
    @SerializedName("shgFlag") var shgFlag: String?,
    @SerializedName("voEndorsementEdit")var voEndorsementEdit: Boolean,
    @SerializedName("ableBodiedFlag") var ableBodiedFlag: String?
)
