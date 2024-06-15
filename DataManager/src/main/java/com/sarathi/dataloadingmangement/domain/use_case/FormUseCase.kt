package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.BLANK_STRING
import com.sarathi.dataloadingmangement.data.entities.FormEntity
import com.sarathi.dataloadingmangement.model.uiModel.DisbursementFormSummaryUiModel
import com.sarathi.dataloadingmangement.model.uiModel.GrantTaskCardSlots
import com.sarathi.dataloadingmangement.model.uiModel.UiConfigAttributeType
import com.sarathi.dataloadingmangement.repository.FormRepositoryImpl
import javax.inject.Inject

class FormUseCase @Inject constructor(private val repository: FormRepositoryImpl) {

    suspend fun saveFormEData(
        subjectId: Int,
        taskId: Int,
        surveyId: Int,
        referenceId: String,
        subjectType: String
    ) {
        return repository.saveFromToDB(
            subjectId = subjectId,
            taskId = taskId,
            surveyId = surveyId,
            referenceId = referenceId,
            subjectType = subjectType
        )
    }

    suspend fun deleteFormE(
        referenceId: String,
        taskId: Int
    ): Int {
        return repository.deleteForm(
            taskId = taskId,
            referenceId = referenceId
        )
    }

    suspend fun getFormData(): List<DisbursementFormSummaryUiModel> {
        val fromData = repository.getFormData()
        val list = ArrayList<DisbursementFormSummaryUiModel>()
        fromData.forEach { form ->
            val _data = getFormAttributeDate(form)
            list.add(
                DisbursementFormSummaryUiModel(
                    subjectType = form.subjectType,
                    date = form.formGenerateDate,
                    noOfDidi = 1,
                    subjectName = _data[GrantTaskCardSlots.GRANT_TASK_TITLE.name] ?: BLANK_STRING,
                    villageName = _data[GrantTaskCardSlots.GRANT_TASK_TITLE.name] ?: BLANK_STRING,
                    mode = _data[GrantTaskCardSlots.GRANT_TASK_TITLE.name] ?: BLANK_STRING,
                    nature = _data[GrantTaskCardSlots.GRANT_TASK_TITLE.name] ?: BLANK_STRING,
                    amount = _data[GrantTaskCardSlots.GRANT_TASK_TITLE.name] ?: BLANK_STRING,
                    didiImage = _data[GrantTaskCardSlots.GRANT_TASK_IMAGE.name] ?: BLANK_STRING
                )
            )
        }
        return list
    }

    private suspend fun getFormAttributeDate(form: FormEntity): HashMap<String, String> {
        return getUiComponentValues(
            taskId = form.taskid,
            missionId = 1,
            activityId = 2,
            subjectId = form.subjectid,
            componentType = "Card"
        )
    }

    private suspend fun getTaskAttributeValue(key: String, taskId: Int): String {
        return repository.getTaskAttributes(taskId).find { it.key == key }?.value
            ?: BLANK_STRING
    }

    private fun getAnswerForTag(taskId: Int, subjectId: Int, tagId: String): String {
        return repository.getSurveyAnswerForTag(taskId, subjectId, tagId)
    }

    private suspend fun getUiComponentValues(
        taskId: Int,
        missionId: Int,
        subjectId: Int,
        activityId: Int,
        componentType: String
    ): HashMap<String, String> {
        val cardAttributesWithValue = HashMap<String, String>()
        val activityConfig = repository.getFormUiConfig(
            missionId = missionId, activityId = activityId
        )
        val cardConfig = activityConfig.filter { it.componentType == componentType }
        cardConfig.forEach { cardAttribute ->
            cardAttributesWithValue[cardAttribute.key] = when (cardAttribute.type.toUpperCase()) {
                UiConfigAttributeType.STATIC.name -> cardAttribute.value
                UiConfigAttributeType.DYNAMIC.name, UiConfigAttributeType.ATTRIBUTE.name -> getTaskAttributeValue(
                    cardAttribute.value, taskId
                )

                UiConfigAttributeType.TAG.name -> getAnswerForTag(
                    taskId,
                    subjectId,
                    getTaskAttributeValue(
                        cardAttribute.value, taskId
                    )
                )

                else -> {
                    BLANK_STRING
                }
            }
        }

        return cardAttributesWithValue
    }
}
