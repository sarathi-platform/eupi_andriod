package com.sarathi.dataloadingmangement.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DidiDetailList(
    @SerializedName("comment")
    var comment: String? = null,
    @SerializedName("userId")
    var userId: String? = null,
    @SerializedName("villageId")
    var villageId: Int? = null,
    @SerializedName("didiId")
    var didiId: Int? = null,
    @SerializedName("villageName")
    var villageName: String? = null,
    @SerializedName("didiName")
    var didiName: String? = null,
    @SerializedName("dadaName")
    var dadaName: String? = null,
    @SerializedName("cohortId")
    var cohortId: Int? = null,
    @SerializedName("cohortName")
    var cohortName: String? = null,
    @SerializedName("houseNo")
    var houseNo: String? = null,
    @SerializedName("score")
    var score: String? = null,
    @SerializedName("crpImageName")
    var crpImageName: String? = null,
    @SerializedName("ableBodied")
    var ableBodied: String? = null,
    @SerializedName("casteId")
    var casteId: Int? = null,
    @SerializedName("relationship")
    var relationship: String? = null,
    @SerializedName("voName")
    var voName: String? = null,

    @SerializedName("shgVerificationStatus")
    @Expose
    var shgVerificationStatus: String?,

    @SerializedName("shgVerificationDate")
    @Expose
    var shgVerificationDate: Long?,

    @SerializedName("shgName")
    @Expose
    var shgName: String?,

    @SerializedName("shgCode")
    @Expose
    var shgCode: String?,

    @SerializedName("shgMemberId")
    @Expose
    var shgMemberId: String?
) {


}