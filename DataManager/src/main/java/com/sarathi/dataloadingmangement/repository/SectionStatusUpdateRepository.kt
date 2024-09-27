package com.sarathi.dataloadingmangement.repository

interface SectionStatusUpdateRepository {

    suspend fun addOrUpdateSectionStatusForTask(
        missionId: Int,
        surveyId: Int,
        sectionId: Int,
        taskId: Int,
        status: String
    )

    suspend fun addSectionStatusForTask(
        missionId: Int,
        surveyId: Int,
        sectionId: Int,
        taskId: Int,
        status: String
    )

    suspend fun updateSectionStatusForTask(
        missionId: Int,
        surveyId: Int,
        sectionId: Int,
        taskId: Int,
        status: String
    )

    suspend fun isStatusAvailableForTaskSection(
        missionId: Int,
        surveyId: Int,
        sectionId: Int,
        taskId: Int
    ): Boolean

}