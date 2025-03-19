package com.nudge.core.model.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.BLANK_STRING
import com.nudge.core.helper.TranslationHelper

data class ShgDetailsFromLokOsResponseModel(
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("shg_code")
    @Expose
    val shgCode: String,
    @SerializedName("shg_members")
    @Expose
    val shgMembers: List<ShgMember>,
    @SerializedName("shg_name")
    @Expose
    val shgName: String
)


data class ShgMember(
    @SerializedName("aadhar_verified")
    @Expose
    val aadharVerified: Boolean,
    @SerializedName("education")
    @Expose
    val education: String,
    @SerializedName("father_husband")
    @Expose
    val fatherHusband: String,
    @SerializedName("gender")
    @Expose
    val gender: String,
    @SerializedName("member_code")
    @Expose
    val memberCode: String,
    @SerializedName("member_id")
    @Expose
    val memberId: Int,
    @SerializedName("member_name")
    @Expose
    val memberName: String,
    @SerializedName("relation_name")
    @Expose
    val relationName: String,
    @SerializedName("social_category")
    @Expose
    val socialCategory: String
) {

    companion object {
        fun getDidiIdNotFoundOption(translationHelper: TranslationHelper): ShgMember {
            return ShgMember(
                aadharVerified = false,
                education = BLANK_STRING,
                fatherHusband = BLANK_STRING,
                gender = BLANK_STRING,
                memberCode = BLANK_STRING,
                memberId = -1,
                memberName = "Didi ID Not Found",
                relationName = BLANK_STRING,
                socialCategory = BLANK_STRING
            )
        }
    }

}