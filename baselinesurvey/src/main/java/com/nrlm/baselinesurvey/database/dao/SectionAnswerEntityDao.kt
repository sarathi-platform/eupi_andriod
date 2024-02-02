package com.nrlm.baselinesurvey.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nrlm.baselinesurvey.ANSWER_TABLE
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.SectionAnswerEntity

@Dao
interface SectionAnswerEntityDao {

    @Query("SELECT * FROM $ANSWER_TABLE")
    fun getAllAnswer(): List<SectionAnswerEntity>

    @Query("SELECT * FROM $ANSWER_TABLE where didiId = :didiId")
    fun getAllAnswerForDidi(didiId: Int): List<SectionAnswerEntity>

    @Query("Select * FROM $ANSWER_TABLE where didiId = :didiId and sectionId = :sectionId")
    fun getSectionAnswerForDidi(sectionId: Int, didiId: Int): List<SectionAnswerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAnswer(answer: SectionAnswerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(answers: List<SectionAnswerEntity>)

    @Query("Update $ANSWER_TABLE set optionItems = :optionItems, questionType=:questionType, questionSummary=:questionSummary where didiId = :didiId AND questionId = :questionId AND sectionId = :sectionId")
    fun updateAnswer(
        didiId: Int,
        sectionId: Int,
        questionId: Int,
        optionItems: List<OptionItemEntity>,
        questionType: String,
        questionSummary: String
    )

    @Query("Select COUNT(*) FROM $ANSWER_TABLE where didiId = :didiId AND questionId = :questionId AND sectionId = :sectionId")
    fun isQuestionAlreadyAnswered(didiId: Int, questionId: Int, sectionId: Int): Int


}