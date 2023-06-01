package com.patsurvey.nudge.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.patsurvey.nudge.database.NumericAnswerEntity
import com.patsurvey.nudge.utils.NUMERIC_TABLE_NAME

@Dao
interface NumericAnswerDao {

    @Query("SELECT * FROM $NUMERIC_TABLE_NAME")
    fun getAllNumericAnswers(): List<NumericAnswerEntity>

    @Query("SELECT * FROM $NUMERIC_TABLE_NAME where optionId = :optionId AND questionId =:questionId AND didiId =:didiId")
    fun getOptionDetails(optionId:Int,questionId:Int,didiId:Int): NumericAnswerEntity

    @Query("Update $NUMERIC_TABLE_NAME set count = :count where didiId = :didiId AND questionId = :questionId AND didiId =:didiId AND optionId =:optionId")
    fun updateAnswer(didiId: Int,optionId:Int ,questionId: Int,count:Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNumericOption(numericAnswer: NumericAnswerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(numList: List<NumericAnswerEntity>)

    @Query("DELETE from $NUMERIC_TABLE_NAME")
    fun deleteNumericTable()

    @Query("SELECT weight*count AS total_amount FROM $NUMERIC_TABLE_NAME where questionId =:questionId AND didiId =:didiId")
    fun getTotalAssetAmount(questionId: Int,didiId: Int): List<Int>

    @Query("SELECT * FROM $NUMERIC_TABLE_NAME where didiId =:didiId")
    fun getAllAnswersForDidi(didiId:Int): List<NumericAnswerEntity>

    @Query("SELECT * FROM $NUMERIC_TABLE_NAME where questionId =:questionId AND didiId =:didiId")
    fun getSingleQueOptions(questionId:Int,didiId:Int): List<NumericAnswerEntity>
}