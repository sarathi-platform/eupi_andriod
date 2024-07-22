package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.model.uiModel.SectionUiModel

interface SectionListRepository {
    fun getSectionListForSurvey(surveyId: Int): List<SectionUiModel>
}