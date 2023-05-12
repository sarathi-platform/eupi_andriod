package com.patsurvey.nudge.database

import android.text.TextUtils
import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.patsurvey.nudge.model.dataModel.StepsListModal
import com.patsurvey.nudge.utils.STEPS_LIST_TABLE
import com.patsurvey.nudge.utils.StepStatus

@Entity(tableName = STEPS_LIST_TABLE)
data class StepListEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "orderNumber")
    var orderNumber: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "isComplete")
    var isComplete: Int = 0,

    @ColumnInfo(name = "needToPost")
    var needToPost: Boolean = true
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
                stepListEntity.add(StepListEntity(id = step.id, orderNumber = step.orderNumber, name = step.name, isComplete = StepStatus.NOT_STARTED.ordinal, needToPost = true))
            }
            return stepListEntity
        }
    }

}
