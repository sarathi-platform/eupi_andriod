package com.patsurvey.nudge.database

import android.text.TextUtils
import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.MyApplication
import com.patsurvey.nudge.model.response.GetCohortResponseModel
import com.patsurvey.nudge.utils.CohortType
import com.patsurvey.nudge.utils.EMPTY_TOLA_NAME
import com.patsurvey.nudge.utils.TOLA_TABLE
import com.patsurvey.nudge.utils.getUniqueIdForEntity


@Entity(tableName = TOLA_TABLE)
data class TolaEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int,

    @SerializedName("localUniqueId")
    @Expose
    @ColumnInfo(name = "localUniqueId")
    var localUniqueId : String? = "",

    @SerializedName("serverId")
    @Expose
    @ColumnInfo(name = "serverId")
    var serverId: Int = 0,

    @SerializedName("name")
    @Expose
    @ColumnInfo(name = "name")
    var name : String,

    @SerializedName("type")
    @Expose
    @ColumnInfo(name = "type")
    var type: String,

    @SerializedName("latitude")
    @Expose
    @ColumnInfo(name = "latitude")
    var latitude: Double,

    @SerializedName("longitude")
    @Expose
    @ColumnInfo(name = "longitude")
    var longitude: Double,

    @SerializedName("villageId")
    @Expose
    @ColumnInfo(name = "villageId")
    var villageId: Int,

    @SerializedName("status")
    @Expose
    @ColumnInfo(name = "status")
    val status: Int,

    @SerializedName("createdDate")
    @Expose
    @ColumnInfo(name = "createdDate")
    var createdDate: Long?=0,

    @SerializedName("modifiedDate")
    @Expose
    @ColumnInfo(name = "modifiedDate")
    var modifiedDate: Long?=0,

    @SerializedName("localCreatedDate")
    @Expose
    @ColumnInfo(name = "localCreatedDate")
    var localCreatedDate: Long?=0,

    @SerializedName("localModifiedDate")
    @Expose
    @ColumnInfo(name = "localModifiedDate")
    var localModifiedDate: Long?=0,

    @SerializedName("needsToPost")
    @Expose
    @ColumnInfo(name = "needsToPost")
    var needsToPost: Boolean = true,

    @SerializedName("transactionId")
    @Expose
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
                localUniqueId = getUniqueIdForEntity()
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

    fun getUpdatedTola(tolaEntity: TolaEntity): TolaEntity {
        return TolaEntity(id, localUniqueId, tolaEntity.id, tolaEntity.name, type, tolaEntity.latitude, tolaEntity.longitude, villageId, status, createdDate, modifiedDate, localCreatedDate, localModifiedDate, needsToPost, transactionId)
    }
}
