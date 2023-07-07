package com.patsurvey.nudge.database

import androidx.room.*
import com.patsurvey.nudge.database.converters.IntConverter
import com.patsurvey.nudge.utils.VILLAGE_TABLE_NAME

@Entity(tableName = VILLAGE_TABLE_NAME)
data class VillageEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "localVillageId")
    var localVillageId: Int,

    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "name")
    var name : String,

    @ColumnInfo(name = "federationName")
    var federationName: String,

    @ColumnInfo(name = "stateId")
    val stateId: Int,

    @ColumnInfo(name = "languageId")
    val languageId: Int,

    @TypeConverters(IntConverter::class)
    @ColumnInfo(name = "steps_completed")
    var steps_completed: List<Int>?,

    @ColumnInfo(name = "needsToPost")
    var needsToPost: Boolean = false
)
