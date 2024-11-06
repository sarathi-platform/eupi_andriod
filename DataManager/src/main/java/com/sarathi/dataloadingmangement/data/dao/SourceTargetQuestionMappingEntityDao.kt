package com.sarathi.dataloadingmangement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sarathi.dataloadingmangement.data.entities.SourceTargetQuestionMappingEntity
import com.sarathi.dataloadingmangement.model.uiModel.ConditionsUiModel

@Dao
interface SourceTargetQuestionMappingEntityDao {

    @Insert
    fun addSourceTargetQuestionMapping(sourceTargetQuestionMappingEntity: SourceTargetQuestionMappingEntity): Long

    @Insert
    fun addSourceTargetQuestionMappingList(sourceTargetQuestionMappingEntity: List<SourceTargetQuestionMappingEntity>): List<Long>


    @Query(
        "SELECT source_target_question_mapping_table.id AS sourceTargetQuestionIdRef, " +
                "source_target_question_mapping_table.sourceQuestionId, " +
                "source_target_question_mapping_table.targetQuestionId, " +
                "source_target_question_mapping_table.conditionOperator, " +
                "conditions_table.conditions AS condition " +
                "FROM source_target_question_mapping_table " +
                "LEFT JOIN conditions_table ON source_target_question_mapping_table.id = conditions_table.sourceTargetQuestionRefId " +
                "WHERE source_target_question_mapping_table.surveyId = :surveyId " +
                "AND source_target_question_mapping_table.sectionId = :sectionId " +
                "AND source_target_question_mapping_table.sourceQuestionId IN (:sourceQuestionIdList) " +
                "AND source_target_question_mapping_table.userId = :userId"
    )
    fun getConditionsForQuestion(
        userId: String,
        surveyId: Int,
        sectionId: Int,
        sourceQuestionIdList: List<Int>
    ): List<ConditionsUiModel>

    @Query("DELETE FROM source_target_question_mapping_table where userId = :userId and surveyId = :surveyId and sectionId = :sectionId")
    fun clearAllSourceTargetQuestionMappingForUser(userId: String, surveyId: Int, sectionId: Int)

    @Query("DELETE FROM source_target_question_mapping_table where userId = :userId")
    fun deleteSourceTargetQuestionMappingForUser(userId: String)

}