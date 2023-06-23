package com.patsurvey.nudge.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.patsurvey.nudge.database.BpcSelectedDidiEntity
import com.patsurvey.nudge.utils.BPC_SELECTED_DIDI_TABLE

@Dao
interface BpcSelectedDidiDao {

    @Insert
    fun insertDidi(selectedDidiEntity: BpcSelectedDidiEntity)

    @Insert
    fun insertAllDidi(selectedDidiEntityList: List<BpcSelectedDidiEntity>)

    @Query("Select * from $BPC_SELECTED_DIDI_TABLE where activeStatus = 1 and isAlsoSelected = 1 and villageId = :villageId ORDER BY createdDate DESC")
    fun fetchAllSelectedDidiForVillage(villageId: Int): List<BpcSelectedDidiEntity>

    @Query("Select * from $BPC_SELECTED_DIDI_TABLE where activeStatus = 1 and villageId = :villageId ORDER BY createdDate DESC")
    fun fetchAllDidisForVillage(villageId: Int): List<BpcSelectedDidiEntity>

    @Query("SELECT COUNT(*) FROM $BPC_SELECTED_DIDI_TABLE where villageId = :villageId AND patSurveyStatus< 2 AND activeStatus = 1 ORDER BY createdDate DESC")
    fun getAllPendingPATDidisCount(villageId: Int): Int
    @Query("DELETE from $BPC_SELECTED_DIDI_TABLE")
    fun deleteAllDidis()

    @Query("Update $BPC_SELECTED_DIDI_TABLE set isAlsoSelected = :selected where id = :didiId")
    fun markDidiSelected(didiId: Int, selected: Boolean)

    @Query("UPDATE $BPC_SELECTED_DIDI_TABLE SET patSurveyStatus = :patSurveyProgress,needsToPostPAT=0 WHERE id = :didiId")
    fun updateSelDidiPatSurveyStatus(didiId: Int, patSurveyProgress: Int)

    @Query("UPDATE $BPC_SELECTED_DIDI_TABLE SET section1Status = :section1 WHERE id = :didiId")
    fun updateSelDidiPatSection1Status(didiId: Int, section1: Int)

    @Query("UPDATE $BPC_SELECTED_DIDI_TABLE SET section2Status = :section2 WHERE id = :didiId")
    fun updateSelDidiPatSection2Status(didiId: Int, section2: Int)

    @Query("SELECT * FROM $BPC_SELECTED_DIDI_TABLE WHERE id =:didiId")
    fun fetchSelectedDidi(didiId: Int) :BpcSelectedDidiEntity

    @Query("UPDATE $BPC_SELECTED_DIDI_TABLE SET needsToPostPAT=0 WHERE id = :didiId")
    fun updateSelDidiNeedToPostPAT(didiId: Int)

    @Query("UPDATE $BPC_SELECTED_DIDI_TABLE SET patSurveyStatus = :patSurveyProgress WHERE id = :didiId")
    fun updateQuesSectionStatus(didiId: Int, patSurveyProgress: Int)

    @Query("UPDATE $BPC_SELECTED_DIDI_TABLE set patSurveyStatus = :patSurveyStatus,section1Status=:section1Status,section2Status=:section2Status,needsToPostPAT=0 WHERE id =:didiId")
    fun updatePATProgressStatus(patSurveyStatus: Int,section1Status:Int,section2Status:Int,didiId:Int)

    @Query("UPDATE $BPC_SELECTED_DIDI_TABLE set bpcScore =:score, bpcComment=:comment WHERE id=:didiId ")
    fun updateSelDidiScore(score: Double, comment:String, didiId: Int)
}