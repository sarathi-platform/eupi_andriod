package com.patsurvey.nudge.database.dao

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.utils.STEPS_LIST_TABLE
import com.patsurvey.nudge.utils.StepStatus

@Dao
interface StepsListDao {

    @Query("SELECT * FROM $STEPS_LIST_TABLE ORDER BY orderNumber ASC")
    fun getAllSteps(): List<StepListEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(villages: List<StepListEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(step: StepListEntity)


    @Query("UPDATE $STEPS_LIST_TABLE SET isComplete = :isComplete where id = :stepId AND villageId = :villageId")
    fun markStepAsCompleteOrInProgress(stepId: Int, isComplete: Int = 0,villageId:Int)

    @Query("UPDATE $STEPS_LIST_TABLE SET isComplete = :inProgress where orderNumber = :orderNumber AND villageId = :villageId")
    fun markStepAsInProgress(orderNumber: Int, inProgress: Int = 1,villageId:Int)

    @Query("SELECT isComplete from $STEPS_LIST_TABLE where id = :id AND villageId =:villageId")
    fun isStepComplete(id: Int,villageId: Int): Int

    @Query("SELECT isComplete from $STEPS_LIST_TABLE where id = :id AND villageId = :villageId")
    fun isStepCompleteLiveForBpc(id: Int, villageId: Int) : LiveData<Int>?

    @Query("SELECT isComplete from $STEPS_LIST_TABLE where id = :id AND villageId = :villageId")
    fun isStepCompleteLiveForCrp(id: Int,villageId: Int) : LiveData<Int>

    @Query("DELETE from $STEPS_LIST_TABLE")
    fun deleteAllStepsFromDB()

    @Query("SELECT * FROM $STEPS_LIST_TABLE WHERE villageId = :villageId  ORDER BY orderNumber ASC")
    fun getAllStepsForVillage(villageId: Int): List<StepListEntity>

    @Query("SELECT count(*) FROM $STEPS_LIST_TABLE WHERE villageId = :villageId  and id= :stepId ORDER BY orderNumber ASC")
    fun getStepEntityCountForVillage(villageId: Int, stepId: Int): Int

    @Query("SELECT * FROM $STEPS_LIST_TABLE WHERE orderNumber = :orderNumber ORDER BY orderNumber ASC")
    fun getAllStepsWithOrderNumber(orderNumber: Int): List<StepListEntity>

    @Query("SELECT * FROM $STEPS_LIST_TABLE WHERE villageId = :villageId AND id = :stepId")
    fun getStepForVillage(villageId: Int,stepId: Int): StepListEntity

    @Query("SELECT * FROM $STEPS_LIST_TABLE WHERE villageId = :villageId AND isComplete = :isComplete ORDER BY orderNumber DESC LIMIT 1")
    fun fetchLastInProgressStep(villageId: Int,isComplete: Int): StepListEntity

    @Query("UPDATE $STEPS_LIST_TABLE SET status = :status, workFlowId = :workflowId where id = :stepId AND villageId = :villageId")
    fun updateWorkflowId(stepId: Int, workflowId: Int,villageId:Int,status:String)

    @Query("UPDATE $STEPS_LIST_TABLE SET status = :status, workFlowId = :workflowId where id = :stepId ")
    fun updateWorkflowId(stepId: Int, workflowId: Int,status:String)

    @Query("UPDATE $STEPS_LIST_TABLE SET workFlowId = :workflowId where id = :stepId AND villageId = :villageId")
    fun updateOnlyWorkFlowId(workflowId: Int, villageId: Int, stepId: Int)

    @Query("SELECT * FROM $STEPS_LIST_TABLE WHERE villageId = :villageId AND isComplete = 2 ORDER BY orderNumber ASC")
    fun getAllCompleteStepsForVillage(villageId: Int): List<StepListEntity>

    @Query("UPDATE $STEPS_LIST_TABLE SET needToPost = :needsToPost WHERE id =:id and villageId = :villageId")
    fun updateNeedToPost(id:Int, villageId: Int, needsToPost: Boolean)

    @Query("UPDATE $STEPS_LIST_TABLE SET needToPost = :needsToPost WHERE stepId =:stepId ")
    fun updateNeedToPost(stepId:Int, needsToPost: Boolean)

    @Query("SELECT * FROM $STEPS_LIST_TABLE Where orderNumber = :orderNumber and needToPost = :needToPost")
    fun getAllStepsByOrder(orderNumber: Int,needToPost: Boolean): List<StepListEntity>

    @Query("SELECT * FROM $STEPS_LIST_TABLE Where orderNumber = :orderNumber and villageId = :villageId")
    fun getStepByOrder(orderNumber: Int,villageId: Int): StepListEntity

    @Query("DELETE from $STEPS_LIST_TABLE where villageId = :villageId")
    fun deleteAllStepsForVillage(villageId: Int)

    @Query("DELETE from $STEPS_LIST_TABLE where villageId = :villageId and id = :id")
    fun deleteStepForVillage(villageId: Int, id: Int)

    @Query("UPDATE $STEPS_LIST_TABLE SET needToPost = :needsToPost WHERE orderNumber =:orderNumber and villageId = :villageId")
    fun updateNeedToPostByOrderNumber(orderNumber: Int, villageId: Int, needsToPost: Boolean)

    @Query("SELECT * FROM $STEPS_LIST_TABLE WHERE villageId = :villageId  ORDER BY orderNumber ASC")
    fun getAllStepsForVillageLive(villageId: Int): LiveData<List<StepListEntity>>

    @Transaction
    fun updateStepListForVillage(forceRefresh: Boolean = false, villageId: Int, stepList: List<StepListEntity>) {
        stepList.forEach { step ->

            if (!forceRefresh || getStepEntityCountForVillage(
                    step.villageId,
                    stepId = step.id
                ) == 0
            ) {
                var currentStep = step
                if (TextUtils.isEmpty(step.status)) {
                    currentStep = currentStep.copy(status = StepStatus.NOT_STARTED.name)

                }
                insert(currentStep)

            }
        }
    }

}