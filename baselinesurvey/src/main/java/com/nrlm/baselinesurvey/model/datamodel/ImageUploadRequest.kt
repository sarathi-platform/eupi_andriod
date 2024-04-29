package com.nrlm.baselinesurvey.model.datamodel

import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.database.entity.SectionEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.ui.Constants.QuestionType

data class ImageUploadRequest(
    @SerializedName("subjectId")
    val subjectId: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("userType")
    val userType: String,
    @SerializedName("filePath")
    val filePath: String,
    @SerializedName("questionId")
    val questionId: Int,
    @SerializedName("questionType")
    val questionType: String,
    @SerializedName("tag")
    val tag: Int,
    @SerializedName("dateCreated")
    val dateCreated: Long,
    @SerializedName("languageId")
    val languageId: Int,
    @SerializedName("sectionId")
    val sectionId: Int,
    @SerializedName("subjectType")
    val subjectType: String,
    @SerializedName("surveyId")
    val surveyId: Int,
    @SerializedName("optionId")
    val optionId: Int,
    @SerializedName("referenceId")
    val referenceId: String = "",
    @SerializedName("optionTag")
    val optionTag: Int = 0,
    @SerializedName("localTaskId")
    val localTaskId: String?
) {
    companion object {
        fun getRequestObjectForUploadImage(
            didi: SurveyeeEntity,
            subjectType: String,
            filePath: String,
            location: String,
            userType: String,
            referenceId: String,
            sectionDetails: SectionListItem,
            questionId: Int,
            localTaskId: String
        ): ImageUploadRequest {
            val question = sectionDetails.questionList.find { it.questionId == questionId }
            val option =
                sectionDetails.optionsItemMap[questionId]?.find { it.optionType == QuestionType.Image.name }
            return ImageUploadRequest(
                subjectId = didi.didiId.toString(),
                filePath = filePath,
                userType = userType,
                location = location,
                dateCreated = System.currentTimeMillis(),
                languageId = sectionDetails.languageId,
                subjectType = subjectType,
                surveyId = sectionDetails.surveyId,
                sectionId = sectionDetails.sectionId,
                questionId = questionId,
                questionType = question?.type ?: QuestionType.Form.name,
                tag = question?.tag ?: 0,
                optionId = option?.optionId ?: 0,
                referenceId = referenceId,
                optionTag = option?.optionTag ?: 0,
                localTaskId = localTaskId
            )
        }

        fun getRequestObjectForUploadImage(
            didi: SurveyeeEntity,
            subjectType: String,
            filePath: String,
            location: String,
            userType: String,
            referenceId: String,
            questionEntity: QuestionEntity?,
            optionItemEntity: OptionItemEntity?,
            sectionDetails: SectionEntity,
            questionId: Int,
            localTaskId: String?
        ): ImageUploadRequest {
            return ImageUploadRequest(
                subjectId = didi.didiId.toString(),
                filePath = filePath,
                userType = userType,
                location = location,
                dateCreated = System.currentTimeMillis(),
                languageId = sectionDetails.languageId,
                subjectType = subjectType,
                surveyId = sectionDetails.surveyId,
                sectionId = sectionDetails.sectionId,
                questionId = questionId,
                questionType = questionEntity?.type ?: QuestionType.Form.name,
                tag = questionEntity?.tag ?: 0,
                optionId = optionItemEntity?.optionId ?: 0,
                referenceId = referenceId,
                optionTag = optionItemEntity?.optionTag ?: 0,
                localTaskId = localTaskId
            )
        }

    }
}
