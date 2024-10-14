package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.entities.ActivityEntity
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.data.entities.DocumentEntity
import com.sarathi.dataloadingmangement.data.entities.FormEntity
import com.sarathi.dataloadingmangement.data.entities.MissionEntity
import com.sarathi.dataloadingmangement.data.entities.SurveyAnswerEntity

interface IRegenerateGrantEventRepository {
    suspend fun getAllMissionsForUser(): List<MissionEntity>
    suspend fun getAllActivityForUser(): List<ActivityEntity>
    suspend fun getAllTaskForUser(): List<ActivityTaskEntity>
    suspend fun getSubjectTypeForActivity(activityId: Int, missionId: Int): String
    suspend fun getAllSurveyAnswerForUSer(): List<SurveyAnswerEntity>
    suspend fun getAllFormData(): List<FormEntity>
    suspend fun getDocumentData(): List<DocumentEntity>
    suspend fun getTaskEntity(taskId: Int): ActivityTaskEntity?


}