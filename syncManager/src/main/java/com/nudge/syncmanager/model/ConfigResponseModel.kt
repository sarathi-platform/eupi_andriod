package com.nudge.syncmanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ConfigResponseModel(
    @SerializedName("languageList")
    @Expose
    val languageList:List<LanguageEntity>,

    @SerializedName("questionImageUrlList")
    @Expose
    val image_profile_link:List<String>,

    )

@Entity(tableName = "language")
data class LanguageEntity(
    @PrimaryKey
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int,

    @SerializedName("orderNumber")
    @Expose
    @ColumnInfo(name = "orderNumber")
    var orderNumber: Int,

    @SerializedName("language")
    @Expose
    @ColumnInfo(name = "language")
    var language : String,

    @SerializedName("langCode")
    @Expose
    @ColumnInfo(name = "langCode")
    val langCode : String?,

    @SerializedName("localName")
    @Expose
    @ColumnInfo(name = "localName")
    val localName : String?,
)

