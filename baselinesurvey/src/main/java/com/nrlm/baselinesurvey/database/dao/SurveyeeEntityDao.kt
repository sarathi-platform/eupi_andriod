package com.nrlm.baselinesurvey.database.dao

import androidx.room.*
import com.nrlm.baselinesurvey.SURVEYEE_TABLE
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SurveyeeEntityDao {

    @Query("SELECT * FROM $SURVEYEE_TABLE ORDER BY id DESC")
    suspend fun getAllDidis(): List<SurveyeeEntity>

    @Query("SELECT * FROM $SURVEYEE_TABLE where villageId = :villageId  ORDER BY didiId DESC")
    fun getAllDidisForVillage(villageId: Int): List<SurveyeeEntity>

    @Query("Select * FROM $SURVEYEE_TABLE where didiId = :didiId")
    fun getDidi(didiId: Int): SurveyeeEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDidi(didi: SurveyeeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(didis: List<SurveyeeEntity>)

    @Query("DELETE FROM $SURVEYEE_TABLE")
    fun deleteSurveyees()

    @Query("UPDATE $SURVEYEE_TABLE SET crpImageLocalPath = :crpImageLocalPath WHERE didiId = :didiId")
    fun updateImageLocalPath(didiId: Int, crpImageLocalPath: String)

}