package com.sarathi.dataloadingmangement.model.events

import com.google.gson.annotations.SerializedName

data class SaveDocumentEventDto(
    @SerializedName("generatedDate")
    var generatedDate: String,
    @SerializedName("documentType")
    var documentType: String,
    @SerializedName("doerId")
    var doerId: Int,
    @SerializedName("doerType")
    var doerType: String,
    @SerializedName("documentName")
    var documentName: String
)