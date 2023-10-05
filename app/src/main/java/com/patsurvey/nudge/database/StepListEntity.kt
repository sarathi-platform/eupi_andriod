package com.patsurvey.nudge.database

import android.text.TextUtils
import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.model.dataModel.StepsListModal
import com.patsurvey.nudge.utils.STEPS_LIST_TABLE
import com.patsurvey.nudge.utils.StepStatus

@Entity(tableName = STEPS_LIST_TABLE)
data class StepListEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("stepId")
    @Expose
    @ColumnInfo(name = "stepId")
    var stepId: Int,

    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int,

    @SerializedName("orderNumber")
    @Expose
    @ColumnInfo(name = "orderNumber")
    var orderNumber: Int,

    @SerializedName("name")
    @Expose
    @ColumnInfo(name = "name")
    val name: String,

    @SerializedName("status")
    @Expose
    @ColumnInfo(name = "status")
    val status: String,

    @SerializedName("isComplete")
    @Expose
    @ColumnInfo(name = "isComplete")
    var isComplete: Int = 0,

    @SerializedName("needToPost")
    @Expose
    @ColumnInfo(name = "needToPost")
    var needToPost: Boolean = false,

    @SerializedName("villageId")
    @Expose
    @ColumnInfo(name = "villageId")
    var villageId: Int = 0,

    @SerializedName("programId")
    @Expose
    @ColumnInfo(name = "programId")
    var programId: Int = 0,

    @SerializedName("workFlowId")
    @Expose
    @ColumnInfo(name = "workFlowId")
    var workFlowId: Int = 0,

    @SerializedName("localModifiedDate")
    @Expose
    @ColumnInfo(name = "localModifiedDate")
    var localModifiedDate: Long ?= 0L,
) {

    fun compare(other: StepListEntity, ignoreIds: Boolean = false): Boolean {
        var same = (ignoreIds || id == other.id) &&
                TextUtils.equals(name, other.name) &&
                orderNumber == other.orderNumber

        return same
    }
    companion object {
        fun same(l1: List<StepListEntity>, l2: List<StepListEntity>, ignoreIds: Boolean = false): Boolean {
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

        fun convertFromModelToEntity(stepList: List<StepsListModal>): List<StepListEntity> {
            val stepListEntity = mutableListOf<StepListEntity>()
            stepList.forEach {step ->
                stepListEntity.add(StepListEntity(id = step.id, orderNumber = step.orderNumber,
                    name = step.name, isComplete = StepStatus.NOT_STARTED.ordinal, needToPost = true, villageId = step.villageId, status = "", stepId = 0))
            }
            return stepListEntity
        }
    }

    fun getUpdatedStep(newStep: StepListEntity): StepListEntity {
        return StepListEntity(stepId = stepId, id = id, orderNumber = orderNumber, name = name, status = newStep.status, isComplete = isComplete, needToPost = needToPost, villageId = villageId, programId = programId, workFlowId = newStep.workFlowId, localModifiedDate = localModifiedDate)
    }

}
