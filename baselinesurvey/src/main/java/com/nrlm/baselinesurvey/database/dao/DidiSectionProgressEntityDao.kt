package com.nrlm.baselinesurvey.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nrlm.baselinesurvey.DIDI_SECTION_PROGRESS_TABLE
import com.nrlm.baselinesurvey.database.entity.DidiSectionProgressEntity

@Dao
interface DidiSectionProgressEntityDao {

    @Insert
    fun addDidiSectionProgress(didiSectionProgressEntity: DidiSectionProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addDidiSectionProgress(didiSectionProgressEntity: List<DidiSectionProgressEntity>)
    @Query("Select * from $DIDI_SECTION_PROGRESS_TABLE where surveyId = :surveyId and didiId = :didiId")
    fun getAllSectionProgressForDidi(surveyId: Int, didiId: Int): List<DidiSectionProgressEntity>

    @Query("Select * from $DIDI_SECTION_PROGRESS_TABLE where surveyId = :surveyId and sectionId = :sectionId and didiId = :didiId")
    fun getSectionProgressForDidi(surveyId: Int, sectionId: Int, didiId: Int): DidiSectionProgressEntity?

    @Query("Update $DIDI_SECTION_PROGRESS_TABLE set sectionStatus = :sectionStatus where surveyId = :surveyId and sectionId = :sectionId and didiId = :didiId")
    fun updateSectionStatusForDidi(surveyId: Int, sectionId: Int, didiId: Int, sectionStatus: Int)

}