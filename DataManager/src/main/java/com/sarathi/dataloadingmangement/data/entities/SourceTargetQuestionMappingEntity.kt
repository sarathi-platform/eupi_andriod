package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.model.survey.response.Conditions

@Entity("source_target_question_mapping_table")
data class SourceTargetQuestionMappingEntity(

    @PrimaryKey(true)
    @ColumnInfo("id")
    val id: Int = 0,
    val userId: String,
    val surveyId: Int,
    val sectionId: Int,
    val sourceQuestionId: Int,
    val targetQuestionId: Int,
    val conditionOperator: String

) {

    companion object {

        fun getSourceTargetQuestionMappingEntity(
            sectionId: Int,
            surveyId: Int,
            userId: String,
            targetQuestionId: Int,
            conditions: Conditions,
            conditionOperator: String? = BLANK_STRING
        ): SourceTargetQuestionMappingEntity? {
            var sourceTargetQuestionMappingEntity: SourceTargetQuestionMappingEntity? = null

            var mConditionOperator = BLANK_STRING

            if (conditionOperator != null)
                mConditionOperator = conditionOperator

            conditions.sourceQuestion?.let {
                sourceTargetQuestionMappingEntity = SourceTargetQuestionMappingEntity(
                    userId = userId,
                    surveyId = surveyId,
                    sectionId = sectionId,
                    sourceQuestionId = it,
                    targetQuestionId = targetQuestionId,
                    conditionOperator = mConditionOperator
                )
            }

            return sourceTargetQuestionMappingEntity
        }

        fun getSourceTargetQuestionMappingEntityList(
            sectionId: Int,
            surveyId: Int,
            userId: String,
            targetQuestionId: Int,
            conditionsList: List<Conditions>?,
            conditionOperator: String? = BLANK_STRING
        ): List<SourceTargetQuestionMappingEntity> {
            val sourceTargetQuestionMappingEntityList: ArrayList<SourceTargetQuestionMappingEntity> =
                ArrayList()
            var mConditionOperator = BLANK_STRING

            if (conditionsList == null)
                return sourceTargetQuestionMappingEntityList

            if (conditionOperator != null)
                mConditionOperator = conditionOperator

            conditionsList.forEach {

                getSourceTargetQuestionMappingEntity(
                    userId = userId,
                    surveyId = surveyId,
                    sectionId = sectionId,
                    targetQuestionId = targetQuestionId,
                    conditions = it,
                    conditionOperator = mConditionOperator
                )?.let {
                    sourceTargetQuestionMappingEntityList.add(it)
                }

            }

            return sourceTargetQuestionMappingEntityList
        }

    }

}
