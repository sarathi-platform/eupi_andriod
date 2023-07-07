package com.patsurvey.nudge.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.utils.QUESTION_TABLE

@Dao
interface QuestionListDao {

    @Query("SELECT * FROM $QUESTION_TABLE")
    fun getAllQuestions(): List<QuestionEntity>

    @Query("Select * FROM $QUESTION_TABLE where questionId = :id")
    fun getQuestion(id: Int): QuestionEntity

    @Query("Select * FROM $QUESTION_TABLE where actionType = :type AND languageId=:languageId ORDER BY `order`")
    fun getQuestionForType(type: String,languageId:Int): List<QuestionEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQuestion(question: QuestionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(questionList: List<QuestionEntity>)

    @Query("DELETE from $QUESTION_TABLE")
    fun deleteQuestionTable()

    @Query("SELECT surveyPassingMark from $QUESTION_TABLE LIMIT 1")
    fun getPassingScore(): Int

    @Query("SELECT * FROM $QUESTION_TABLE where languageId=:languageId")
    fun getAllQuestionsForLanguage(languageId: Int): List<QuestionEntity>
}