package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.patsurvey.nudge.utils.CohortType
import com.patsurvey.nudge.utils.EMPTY_TOLA_NAME
import com.patsurvey.nudge.utils.TOLA_TABLE
import com.patsurvey.nudge.utils.Tola

@Entity(tableName = TOLA_TABLE)
data class TolaEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int,
    @ColumnInfo(name = "name")
    var name : String,
    @ColumnInfo(name = "type")
    var type: String,
    @ColumnInfo(name = "latitude")
    var latitude: Double,
    @ColumnInfo(name = "longitude")
    var longitude: Double,
    @ColumnInfo(name = "villageId")
    var villageId: Int,
    @ColumnInfo(name = "status")
    val status: Int = 1,
    @ColumnInfo(name = "needsToPost")
    var needsToPost: Boolean = true
) {
    companion object {
        fun createEmptyTolaForVillageId(villageId: Int): TolaEntity {
            return TolaEntity(
                id = 0,
                name = EMPTY_TOLA_NAME,
                type = CohortType.TOLA.type,
                latitude = 0.0,
                longitude = 0.0,
                villageId = villageId
            )
        }
    }
}
