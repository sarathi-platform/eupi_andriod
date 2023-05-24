package com.patsurvey.nudge.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.utils.ANSWER_TABLE

@Dao
interface AnswerDao {

    @Query("SELECT * FROM $ANSWER_TABLE")
    fun getAllAnswer(): List<SectionAnswerEntity>

    @Query("Select * FROM $ANSWER_TABLE where didiId = :didiId AND actionType = :actionType")
    fun getAnswerForDidi(actionType: String,didiId:Int): List<SectionAnswerEntity>

    @Query("Select * FROM $ANSWER_TABLE where didiId = :didiId AND questionId = :questionId AND actionType = :actionType")
    fun isAlreadyAnswered(didiId: Int, questionId: Int,actionType:String): SectionAnswerEntity

    @Query("Update $ANSWER_TABLE set answerOption = :answerOption, answerValue = :answerValue, optionId = :optionId where didiId = :didiId AND questionId = :questionId AND actionType = :actionType")
    fun updateAnswer(didiId: Int,optionId:Int ,questionId: Int,actionType:String,answerOption:String,answerValue:String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAnswer(Answer: SectionAnswerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(Answers: List<SectionAnswerEntity>)

    @Query("DELETE from $ANSWER_TABLE")
    fun deleteAnswerTable()
}