package com.nrlm.baselinesurvey.model.datamodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ReferenceIdModel(
    @SerializedName("id")
    @Expose
    var id: Int? = null,

    @SerializedName("name")
    @Expose
    var name: String? = null,

    @SerializedName("federationName")
    @Expose
    var federationName: String? = null,

    @SerializedName("crpName")
    @Expose
    var crpName: String? = null,

    @SerializedName("crpUserId")
    @Expose
    var crpUserId: String? = null,

    @SerializedName("stateId")
    @Expose
    var stateId: Int? = null,

    @SerializedName("languageId")
    @Expose
    var languageId: String? = null,

    @SerializedName("statusId")
    @Expose
    var statusId: Int? = null,

    @SerializedName("stepId")
    @Expose
    var stepId: String? = null
)
