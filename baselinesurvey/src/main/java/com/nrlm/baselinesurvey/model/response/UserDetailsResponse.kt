package com.nrlm.baselinesurvey.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.database.entity.VillageEntity
import com.nrlm.baselinesurvey.model.datamodel.ReferenceIdModel

data class UserDetailsResponse(
    @SerializedName("username")
    @Expose
    val username:String?,

    @SerializedName("email")
    @Expose
    val email:String?,

    @SerializedName("name")
    @Expose
    val name:String?,

    @SerializedName("identityNumber")
    @Expose
    val identityNumber:String?,

    @SerializedName("profileImage")
    @Expose
    val profileImage:String?,

    @SerializedName("roleName")
    @Expose
    val roleName:String?,

    @SerializedName("typeName")
    @Expose
    val typeName:String?,

    @SerializedName("villageList")
    @Expose
    val villageList:List<VillageEntity>?,

    @SerializedName("referenceId")
    @Expose
    val referenceId: List<ReferenceIdModel>
)
