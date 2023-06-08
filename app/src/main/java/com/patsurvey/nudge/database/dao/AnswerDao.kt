package com.patsurvey.nudge.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.model.dataModel.PATDidiStatusModel
import com.patsurvey.nudge.utils.ANSWER_TABLE
import com.patsurvey.nudge.utils.DIDI_TABLE

@Dao
interface AnswerDao {

    @Query("SELECT * FROM $ANSWER_TABLE")
    fun getAllAnswer(): List<SectionAnswerEntity>

    @Query("SELECT * FROM $ANSWER_TABLE where didiId = :didiId")
    fun getAllAnswerForDidi(didiId: Int): List<SectionAnswerEntity>

    @Query("Select * FROM $ANSWER_TABLE where didiId = :didiId AND actionType = :actionType")
    fun getAnswerForDidi(actionType: String,didiId:Int): List<SectionAnswerEntity>

    @Query("Select * FROM $ANSWER_TABLE where didiId = :didiId AND questionId = :questionId AND actionType = :actionType")
    fun isAlreadyAnswered(didiId: Int, questionId: Int,actionType:String): SectionAnswerEntity

    @Query("Update $ANSWER_TABLE set optionValue = :optionValue, answerValue = :answerValue,weight=:weight, optionId = :optionId,type=:type,totalAssetAmount =:totalAssetAmount,summary=:summary where didiId = :didiId AND questionId = :questionId AND actionType = :actionType")
    fun updateAnswer(didiId: Int,optionId:Int ,questionId: Int,actionType:String,optionValue:Int,weight:Int,answerValue:String,type:String,totalAssetAmount:Int,summary:String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAnswer(Answer: SectionAnswerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(Answers: List<SectionAnswerEntity>)

    @Query("DELETE from $ANSWER_TABLE")
    fun deleteAnswerTable()

    @Query("Select COUNT(*) FROM $ANSWER_TABLE where didiId = :didiId AND type = :type AND actionType = :actionType AND optionValue=1")
    fun fetchOptionYesCount(didiId: Int, type: String,actionType:String): Int

    @Query("Select optionId FROM $ANSWER_TABLE where didiId = :didiId AND actionType = :actionType AND questionId = :questionId")
    fun fetchOptionID(didiId: Int, questionId: Int,actionType:String): Int

    @Query("Select COUNT(*) FROM $ANSWER_TABLE where didiId = :didiId AND actionType = :actionType AND questionId = :questionId AND optionId=:optionId")
    fun countOfOptionId(didiId: Int, questionId: Int,actionType:String,optionId:Int): Int

    @Query("Select * FROM $ANSWER_TABLE where didiId = :didiId AND questionId = :questionId AND type=:type")
    fun getNumTypeAnswer(didiId: Int, questionId: Int,type:String): SectionAnswerEntity

    @Query("Select * FROM $ANSWER_TABLE where villageId = :villageId AND needsToPost = 1")
    fun getAllNeedToPostQues(villageId: Int): List<SectionAnswerEntity>

    @Query("Select * FROM $ANSWER_TABLE where didiId = :didiId AND needsToPost = 1")
    fun getAllNeedToPostQuesForDidi(didiId: Int): List<SectionAnswerEntity>


    @Query("select $DIDI_TABLE.id,$DIDI_TABLE.name,$DIDI_TABLE.serverId,$DIDI_TABLE.patSurveyStatus,$DIDI_TABLE.section1Status,$DIDI_TABLE.section2Status from $DIDI_TABLE LEFT join $ANSWER_TABLE on $ANSWER_TABLE.didiId = $DIDI_TABLE.id where $DIDI_TABLE.villageId = :villageId AND $DIDI_TABLE.needsToPostPAT=1 AND $DIDI_TABLE.wealth_ranking = 'POOR' GROUP BY $DIDI_TABLE.id")
    fun fetchPATSurveyDidiList(villageId: Int): List<PATDidiStatusModel>
    @Query("Select * FROM $ANSWER_TABLE where didiId = :didiId AND questionId = :questionId")
    fun getQuestionAnswerForDidi(didiId: Int, questionId: Int): SectionAnswerEntity
    @Query("select  d.* from $DIDI_TABLE d  INNER join $ANSWER_TABLE q  on q.didiId = d.id where d.villageId =:villageId AND d.forVoEndorsement = 1 GROUP BY d.id ORDER BY d.createdDate DESC")
    fun fetchAllDidisForVO(villageId: Int): List<DidiEntity>

    @Query("Update $ANSWER_TABLE set needsToPost = :needsToPost where didiId = :didiId AND questionId = :questionId ")
    fun updateNeedToPost(didiId: Int,questionId: Int,needsToPost:Boolean)

    @Query("Select * FROM $ANSWER_TABLE where didiId = :didiId AND actionType = 'INCLUSION'")
    fun getAllInclusiveQues(didiId: Int): List<SectionAnswerEntity>

    @Query("SELECT SUM(weight)FROM ques_answer_table where didiId=:didiId AND type !='Numeric_Field'")
    fun getTotalWeightWithoutNumQues(didiId: Int): Double

}