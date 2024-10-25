//package com.nrlm.baselinesurvey.database.entity
//
//import androidx.room.ColumnInfo
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//import com.google.gson.annotations.Expose
//import com.google.gson.annotations.SerializedName
//import com.nrlm.baselinesurvey.LANGUAGE_TABLE_NAME
//
//@Entity(tableName = LANGUAGE_TABLE_NAME)
//data class LanguageEntity(
//    @PrimaryKey
//    @SerializedName("id")
//    @Expose
//    @ColumnInfo(name = "id")
//    var id: Int,
//
//    @SerializedName("orderNumber")
//    @Expose
//    @ColumnInfo(name = "orderNumber")
//    var orderNumber: Int,
//
//    @SerializedName("language")
//    @Expose
//    @ColumnInfo(name = "language")
//    var language : String,
//
//    @SerializedName("langCode")
//    @Expose
//    @ColumnInfo(name = "langCode")
//    val langCode : String?,
//
//    @SerializedName("localName")
//    @Expose
//    @ColumnInfo(name = "localName")
//    val localName : String?,
//)
