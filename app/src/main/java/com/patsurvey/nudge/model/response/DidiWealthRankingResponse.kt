package com.patsurvey.nudge.model.response

import com.google.gson.annotations.SerializedName

data class BeneficiaryCount(
    @SerializedName("RICH") val richCount: Int? = null,
    @SerializedName("MEDIUM") val mediumCount: Int? = null,
    @SerializedName("POOR") val poorCount: Int? = null
)

data class ProcessBeneficiaryItem(
    val result: String? = null,
    val type: String? = null
)

data class DidiWealthRankingResponse(
    @SerializedName("data") val data: Data? = null,
    @SerializedName("message")val message: String? = null,
    @SerializedName("status") val status: String? = null
)

data class Data(
    @SerializedName("beneficiaryCount") val beneficiaryCount: BeneficiaryCount? = null,
    @SerializedName("beneficiaryList") val beneficiaryList: BeneficiaryList? = null
)

data class BeneficiaryList(
    @SerializedName("MEDIUM") val mediumDidi: List<DidiDetailList?>? = null,
    @SerializedName("RICH") val richDidi: List<DidiDetailList?>? = null,
    @SerializedName("POOR") val poorDidi: List<DidiDetailList?>? = null
)
data class RICHDidi(
    @SerializedName("cohortId") val cohortId: Int? = null,
    @SerializedName("address") val address: String? = null,
    @SerializedName("processBeneficiary") val processBeneficiary: List<ProcessBeneficiaryItem?>? = null,
    @SerializedName("guardianName") val guardianName: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("relationship") val relationship: String? = null,
    @SerializedName("profileImageUrl") val profileImageUrl: Any? = null,
    @SerializedName("castId") val castId: Int? = null
)
data class MEDIUMDidi(
    @SerializedName("cohortId") val cohortId: Int? = null,
    @SerializedName("address") val address: String? = null,
    @SerializedName("processBeneficiary") val processBeneficiary: List<ProcessBeneficiaryItem?>? = null,
    @SerializedName("guardianName") val guardianName: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("relationship") val relationship: String? = null,
    @SerializedName("profileImageUrl") val profileImageUrl: Any? = null,
    @SerializedName("castId") val castId: Int? = null
)

data class POORDidi(
    @SerializedName("cohortId") val cohortId: Int? = null,
    @SerializedName("address") val address: String? = null,
    @SerializedName("processBeneficiary") val processBeneficiary: List<ProcessBeneficiaryItem?>? = null,
    @SerializedName("guardianName") val guardianName: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("relationship") val relationship: String? = null,
    @SerializedName("profileImageUrl") val profileImageUrl: Any? = null,
    @SerializedName("castId") val castId: Int? = null
)