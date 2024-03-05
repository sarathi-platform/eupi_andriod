package com.patsurvey.nudge.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.patsurvey.nudge.database.NumericAnswerEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.utils.NUMERIC_TABLE_NAME

@Dao
interface NumericAnswerDao {

    @Query("SELECT * FROM $NUMERIC_TABLE_NAME")
    fun getAllNumericAnswers(): List<NumericAnswerEntity>

    @Query("SELECT * FROM $NUMERIC_TABLE_NAME where optionId = :optionId AND questionId =:questionId AND didiId =:didiId")
    fun getOptionDetails(optionId:Int,questionId:Int,didiId:Int): NumericAnswerEntity

    @Query("Update $NUMERIC_TABLE_NAME set count = :count, optionValue=:optionValue where didiId = :didiId AND questionId = :questionId AND didiId =:didiId AND optionId =:optionId")
    fun updateAnswer(didiId: Int,optionId:Int ,questionId: Int,count:Int,optionValue:Int)

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

    @Query("SELECT SUM(weight * count) FROM $NUMERIC_TABLE_NAME where didiId =:didiId AND questionId = :questionId")
    fun fetchTotalAmount(questionId:Int,didiId:Int):Int


    @Query("DELETE from $NUMERIC_TABLE_NAME")
    fun deleteAllNumericAnswers()
    @Query("DELETE from $NUMERIC_TABLE_NAME where didiId in (:didiIdList)")
    fun deleteAllNumericAnswersForDidis(didiIdList: List<Int>)

    @Query("SELECT COUNT(*) FROM $NUMERIC_TABLE_NAME where optionId = :optionId AND questionId =:questionId AND didiId =:didiId")
    fun isNumericQuestionAnswered(questionId: Int, optionId: Int, didiId: Int): Int

    @Transaction
    fun updateNumericAnswersAfterRefresh(forceRefresh: Boolean = false, didiIdList: List<Int>, numericList: List<NumericAnswerEntity>) {
        if (forceRefresh)
            deleteAllNumericAnswersForDidis(didiIdList)
        insertAll(numericList)
    }
}