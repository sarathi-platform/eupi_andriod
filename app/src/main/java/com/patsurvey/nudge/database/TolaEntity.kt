package com.patsurvey.nudge.database

import android.text.TextUtils
import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.patsurvey.nudge.model.response.GetCohortResponseModel
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
    @ColumnInfo(name = "date_created")
    val date_created: Long = System.currentTimeMillis() / 1000L,
    @ColumnInfo(name = "needsToPost")
    var needsToPost: Boolean = true
) {

    fun compare(other: GetCohortResponseModel, ignoreIds: Boolean = false): Boolean {
        var same = (ignoreIds || id == other.id) &&
                TextUtils.equals(name, other.name) /*&&
                orderNumber == other.orderNumber*/

        return same
    }
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

        fun same(l1: List<TolaEntity>, l2: List<GetCohortResponseModel>, ignoreIds: Boolean = false): Boolean {
            try {
                if (l1.size != l2.size)
                    return false

                for (i in l1.indices) {
                    if (!l1[i].compare(l2[i], ignoreIds))
                        return false
                }
                return true
            } catch (ex: Exception) {
                Log.d("StepListEntity", ex.localizedMessage!!)
            }
            return false
        }
    }
}
