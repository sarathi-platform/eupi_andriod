package com.patsurvey.nudge.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.utils.STEPS_LIST_TABLE

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
}