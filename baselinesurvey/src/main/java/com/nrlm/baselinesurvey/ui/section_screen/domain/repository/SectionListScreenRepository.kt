package com.nrlm.baselinesurvey.ui.section_screen.domain.repository

import com.nrlm.baselinesurvey.database.entity.ActivityTaskEntity
import com.nrlm.baselinesurvey.database.entity.DidiSectionProgressEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nrlm.baselinesurvey.utils.states.SurveyState

interface SectionListScreenRepository {

    fun getSectionsListForDidi(didiId: Int, surveyId: Int, languageId: Int): List<SectionListItem>

    fun getSectionListForSurvey(surveyId: Int, languageId: Int): List<SectionListItem>

    fun getSelectedLanguage(): Int
    fun getSectionProgressForDidi(
        didiId: Int,
        surveyId: Int,
        languageId: Int
    ): List<DidiSectionProgressEntity>

    fun getSurveyeDetails(didiId: Int): SurveyeeEntity

    suspend fun updateSubjectStatus(didiId: Int, surveyState: SurveyState)

    suspend fun updateTaskStatus(didiId: Int, surveyState: SectionStatus)

    suspend fun updateTaskStatus(
        taskId: Int,
        activityId: Int,
        missionId: Int,
        status: String
    )

    suspend fun getTaskForSubjectId(surveyId: Int): ActivityTaskEntity?

    fun getUserId(): String
}
