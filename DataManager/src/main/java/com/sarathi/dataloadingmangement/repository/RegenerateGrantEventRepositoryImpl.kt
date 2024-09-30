package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.ActivityConfigDao
import com.sarathi.dataloadingmangement.data.dao.ActivityDao
import com.sarathi.dataloadingmangement.data.dao.DocumentDao
import com.sarathi.dataloadingmangement.data.dao.FormDao
import com.sarathi.dataloadingmangement.data.dao.MissionDao
import com.sarathi.dataloadingmangement.data.dao.SurveyAnswersDao
import com.sarathi.dataloadingmangement.data.dao.TaskDao
import com.sarathi.dataloadingmangement.data.entities.ActivityEntity
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.data.entities.DocumentEntity
import com.sarathi.dataloadingmangement.data.entities.FormEntity
import com.sarathi.dataloadingmangement.data.entities.MissionEntity
import com.sarathi.dataloadingmangement.data.entities.SurveyAnswerEntity
import javax.inject.Inject

class RegenerateGrantEventRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val missionDao: MissionDao,
    private val activityDao: ActivityDao,
    private val taskDao: TaskDao,
    private val activityConfigDao: ActivityConfigDao,
    private val surveyAnswersDao: SurveyAnswersDao,
    private val formDao: FormDao,
    private val documentDao: DocumentDao,
) : IRegenerateGrantEventRepository {
    override suspend fun getAllMissionsForUser(): List<MissionEntity> {
        return missionDao.getAllMissionForUser(userId = coreSharedPrefs.getUniqueUserIdentifier())
    }

    override suspend fun getAllActivityForUser(): List<ActivityEntity> {
        return activityDao.getAllActivityForUser(userId = coreSharedPrefs.getUniqueUserIdentifier())
    }

    override suspend fun getAllTaskForUser(): List<ActivityTaskEntity> {
        return taskDao.getAllActivityTask(userId = coreSharedPrefs.getUniqueUserIdentifier())
    }

    override suspend fun getSubjectTypeForActivity(activityId: Int, missionId: Int): String {
        return activityConfigDao.getSubjectTypeForActivity(
            missionId,
            activityId,
            coreSharedPrefs.getUniqueUserIdentifier()
        )
    }

    override suspend fun getAllSurveyAnswerForUSer(): List<SurveyAnswerEntity> {
        return surveyAnswersDao.getAllSurveyAnswerForUser(coreSharedPrefs.getUniqueUserIdentifier())
    }

    override suspend fun getAllFormData(): List<FormEntity> {
        return formDao.getAllFormSummaryDataForUser(userId = coreSharedPrefs.getUniqueUserIdentifier())
    }

    override suspend fun getDocumentData(): List<DocumentEntity> {
        return documentDao.getDocumentSummaryData(userId = coreSharedPrefs.getUniqueUserIdentifier())
    }

    override suspend fun getTaskEntity(taskId: Int): ActivityTaskEntity? {
        return taskDao.getTaskById(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            taskId = taskId
        )
    }


}