package com.nrlm.baselinesurvey.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.nrlm.baselinesurvey.SURVEYEE_TABLE
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.utils.states.SurveyState

@Dao
interface SurveyeeEntityDao {

    @Query("SELECT * FROM $SURVEYEE_TABLE ORDER BY id DESC")
    suspend fun getAllDidis(): List<SurveyeeEntity>

    @Query("SELECT * FROM $SURVEYEE_TABLE where villageId = :villageId  ORDER BY didiId DESC")
    fun getAllDidisForVillage(villageId: Int): List<SurveyeeEntity>

    @Query("Select * FROM $SURVEYEE_TABLE where didiId = :didiId")
    fun getDidi(didiId: Int): SurveyeeEntity

    @Query("Select * FROM $SURVEYEE_TABLE where didiId in(:didiId)")
    fun isDidiExist(didiId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDidi(didi: SurveyeeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(didis: List<SurveyeeEntity>)

    @Query("DELETE FROM $SURVEYEE_TABLE where userId=:userId ")
    fun deleteSurveyees(userId:String)

    @Query("UPDATE $SURVEYEE_TABLE SET crpImageLocalPath = :crpImageLocalPath WHERE didiId = :didiId")
    fun updateImageLocalPath(didiId: Int, crpImageLocalPath: String)

    @Query("Select villageId from $SURVEYEE_TABLE where didiId = :didiId")
    fun getVillageIdForDidi(didiId: Int): Int

    @Query("UPDATE $SURVEYEE_TABLE SET surveyStatus = :didiSurveyStatus where didiId = :didiId")
    fun updateDidiSurveyStatus(didiSurveyStatus: Int, didiId: Int)

    @Query("UPDATE $SURVEYEE_TABLE SET movedToThisWeek = :moveDidisToNextWeek where didiId in (:didiIdList)")
    fun moveSurveyeesToThisWeek(didiIdList: List<Int>, moveDidisToNextWeek: Boolean)

    @Query("UPDATE $SURVEYEE_TABLE SET movedToThisWeek = :moveDidisToNextWeek where didiId = :didiId")
    fun moveSurveyeeToThisWeek(didiId: Int, moveDidisToNextWeek: Boolean)

    @Query("UPDATE $SURVEYEE_TABLE SET crpImageLocalPath = :path WHERE didiId = :didiId")
    fun saveLocalImagePath(path: String, didiId: Int)

    @Transaction
    fun updateDidiSurveyStatusAfterCheck(didiId: Int, didiSurveyStatus: Int) {
        val didi = getDidi(didiId)
        if (didi.surveyStatus != SurveyState.COMPLETED.ordinal) {
            updateDidiSurveyStatus(didiSurveyStatus, didiId)
        }
    }

    @Query("SELECT * FROM $SURVEYEE_TABLE  where userId=:userId ORDER BY id DESC")
    suspend fun getAllDidiForQNA(
        userId: String
    ): List<SurveyeeEntity>
}