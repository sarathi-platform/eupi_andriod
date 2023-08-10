package com.patsurvey.nudge.database

import android.text.TextUtils
import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.patsurvey.nudge.MyApplication
import com.patsurvey.nudge.model.response.GetCohortResponseModel
import com.patsurvey.nudge.utils.CohortType
import com.patsurvey.nudge.utils.EMPTY_TOLA_NAME
import com.patsurvey.nudge.utils.TOLA_TABLE
import com.patsurvey.nudge.utils.getUniqueIdForEntity


@Entity(tableName = TOLA_TABLE)
data class TolaEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int,
    @ColumnInfo(name = "localUniqueId")
    var localUniqueId : String? = "",
    @ColumnInfo(name = "serverId")
    var serverId: Int = 0,
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
    val status: Int,
    @ColumnInfo(name = "createdDate")
    var createdDate: Long?=0,
    @ColumnInfo(name = "modifiedDate")
    var modifiedDate: Long?=0,
    @ColumnInfo(name = "localCreatedDate")
    var localCreatedDate: Long?=0,
    @ColumnInfo(name = "localModifiedDate")
    var localModifiedDate: Long?=0,
    @ColumnInfo(name = "needsToPost")
    var needsToPost: Boolean = true,
    @ColumnInfo(name = "transactionId")
    var transactionId: String? = ""
) {

    fun compare(other: GetCohortResponseModel, ignoreIds: Boolean = false): Boolean {
        return (ignoreIds || id == other.id) &&
                TextUtils.equals(name, other.name)
    }
    companion object {
        fun createEmptyTolaForVillageId(villageId: Int): TolaEntity {
            return TolaEntity(
                id = 0,
                name = EMPTY_TOLA_NAME,
                type = CohortType.TOLA.type,
                latitude = 0.0,
                longitude = 0.0,
                villageId = villageId,
                status = 1,
                localCreatedDate = System.currentTimeMillis(),
                localModifiedDate = System.currentTimeMillis(),
                transactionId = "",
                localUniqueId = getUniqueIdForEntity(MyApplication.applicationContext())
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
