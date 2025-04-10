package com.nrlm.baselinesurvey.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.VILLAGE_TABLE_NAME
import com.nrlm.baselinesurvey.database.converters.IntConverter

@Entity(tableName = VILLAGE_TABLE_NAME)
data class VillageEntity(

    @PrimaryKey(autoGenerate = true)
    @SerializedName("localVillageId")
    @Expose
    @ColumnInfo(name = "localVillageId")
    var localVillageId: Int,

    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int,

    @SerializedName("name")
    @Expose
    @ColumnInfo(name = "name")
    var name : String,

    @SerializedName("federationName")
    @Expose
    @ColumnInfo(name = "federationName")
    var federationName: String,

    @SerializedName("stateId")
    @Expose
    @ColumnInfo(name = "stateId")
    val stateId: Int,

    @SerializedName("languageId")
    @Expose
    @ColumnInfo(name = "languageId")
    val languageId: Int,

    @SerializedName("steps_completed")
    @Expose
    @TypeConverters(IntConverter::class)
    @ColumnInfo(name = "steps_completed")
    var steps_completed: List<Int>?,

    @SerializedName("needsToPost")
    @Expose
    @ColumnInfo(name = "needsToPost")
    var needsToPost: Boolean = false,

    @SerializedName("statusId")
    @Expose
    @ColumnInfo(name = "statusId")
    val statusId: Int=0,

    @SerializedName("stepId")
    @Expose
    @ColumnInfo(name = "stepId")
    val stepId: Int=0,

)