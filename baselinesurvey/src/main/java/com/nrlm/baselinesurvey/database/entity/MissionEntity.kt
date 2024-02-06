package com.nrlm.baselinesurvey.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.MISSION_TABLE_NAME
import com.nrlm.baselinesurvey.database.converters.MissionActivityConverter
import com.nrlm.baselinesurvey.model.datamodel.MissionActivityModel

@Entity(tableName = MISSION_TABLE_NAME)
data class MissionEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int,
    @Expose
    @TypeConverters(MissionActivityConverter::class)
    val activities: List<MissionActivityModel>,
    val endDate: String,
    val missionId: Int,
    val missionName: String,
    val startDate: String
)