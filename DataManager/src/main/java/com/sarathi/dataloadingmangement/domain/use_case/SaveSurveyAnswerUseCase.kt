package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.data.entities.SurveyAnswerEntity
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.model.uiModel.SurveyAnswerFormSummaryUiModel
import com.sarathi.dataloadingmangement.repository.ISurveySaveRepository

class SaveSurveyAnswerUseCase(private val repository: ISurveySaveRepository) {
    suspend fun saveSurveyAnswer(
        questionUiModel: QuestionUiModel,
        subjectId: Int,
        taskId: Int,
        referenceId: String,
        grantId: Int,
        grantType: String
    ) {
        return repository.saveSurveyAnswer(
            questionUiModel,
            subjectId,
            taskId,
            referenceId = referenceId,
            grantId = grantId,
            grantType = grantType
        )
    }

    fun getAnswerForTag(taskId: Int, subjectId: Int, tagId: String): String {
        return repository.getSurveyAnswerForTag(taskId, subjectId, tagId)
    }

    suspend fun getAnswerForFormTag(
        taskId: Int,
        subjectId: Int,
        tagId: String,
        activityConfigId: Int,
        referenceId: String
    ): String {
        return repository.getSurveyAnswerForFormTag(
            taskId = taskId,
            subjectId = subjectId,
            tagId = tagId,
            activityConfigId = activityConfigId,
            referenceId = referenceId
        )
    }

    suspend fun getAllSaveAnswer(
        activityConfigId: Int,
        surveyId: Int,
        taskId: Int,
        sectionId: Int,
        grantId: Int
    ): List<SurveyAnswerFormSummaryUiModel> {
        return repository.getAllSaveAnswer(
            taskId = taskId,
            surveyId = surveyId,
            sectionId = sectionId,
            activityConfigId = activityConfigId,
            grantId = grantId
        )
    }

    fun getUserIdentifier(): String = repository.getUserIdentifier()

    suspend fun deleteSurveyAnswer(
        sectionId: Int,
        surveyId: Int,
        referenceId: String,
        taskId: Int
    ): Int {
        return repository.deleteSurveyAnswer(
            sectionId = sectionId,
            surveyId = surveyId,
            taskId = taskId,
            referenceId = referenceId
        )
    }

    suspend fun getSurveyAnswerImageKeys(
        questionType: String,
    ): List<SurveyAnswerEntity>? {
        return repository.getSurveyAnswerImageKeys(questionType = questionType)
    }

    suspend fun getTotalSavedFormResponsesCount(
        surveyId: Int,
        taskId: Int,
        sectionId: Int,
        formQuestionMap: Map<Int, List<Int>>
    ): Map<Int, Int> {
        val map = mutableMapOf<Int, Int>()

        formQuestionMap.forEach { mapEntry ->
            repository.getTotalSavedFormResponsesCount(
                surveyId,
                taskId,
                sectionId,
                mapEntry.value,
                mapEntry.key
            )
                .apply {
                    map.put(mapEntry.key, this.size)
                }

        }

        return map
    }


    suspend fun isAnswerAvailableInDb(
        questionUiModel: QuestionUiModel,
        subjectId: Int,
        referenceId: String,
        taskId: Int,
        grantId: Int,
        grantType: String
    ): Boolean {
        return repository.isAnswerAvailableInDb(
            questionUiModel,
            subjectId,
            referenceId,
            taskId,
            grantId,
            grantType
        )
    }

    fun getFormResponseMap(
        surveyId: Int,
        sectionId: Int,
        taskId: Int,
        formQuestionMap: MutableMap<Int, List<Int>>
    ): Map<Int, List<SurveyAnswerEntity>> {
        val map = mutableMapOf<Int, List<SurveyAnswerEntity>>()

        formQuestionMap.forEach { mapEntry ->
            repository.getFormResponseMap(surveyId, taskId, sectionId, mapEntry.value).let {
                map[mapEntry.key] = it
            }
        }
        return map
    }

    suspend fun checkAndUpdateNonVisibleQuestionResponseInDb(
        question: QuestionUiModel,
        subjectId: Int,
        taskId: Int, referenceId: String,
        grantId: Int,
        grantType: String
    ) {
        repository.checkAndUpdateNonVisibleQuestionResponseInDb(
            question,
            subjectId,
            taskId,
            referenceId,
            grantId,
            grantType
        )
    }

}