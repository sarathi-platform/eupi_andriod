package com.sarathi.dataloadingmangement.model.uiModel

import android.net.Uri
import com.sarathi.dataloadingmangement.data.entities.SurveyConfigEntity

data class TaskCardModel(
    val label: String,
    var value: String,
    val icon: Uri?
)

data class SurveyCardModel(
    val label: String,
    var value: String,
    var type: String,
    var tagId: Int
) {

    companion object {
        fun getSurveyCarModel(
            surveyConfigEntity: SurveyConfigEntity
        ): SurveyCardModel {

            return SurveyCardModel(
                label = surveyConfigEntity.label,
                value = surveyConfigEntity.value,
                type = surveyConfigEntity.type,
                tagId = surveyConfigEntity.tagId
            )

        }

        fun getSurveyCardModelList(
            surveyConfigEntityList: List<SurveyConfigEntity>
        ): List<SurveyCardModel> {
            val surveyCardModelList = mutableListOf<SurveyCardModel>()
            surveyConfigEntityList.forEach {
                surveyCardModelList.add(getSurveyCarModel(it))
            }
            return surveyCardModelList
        }
    }

}