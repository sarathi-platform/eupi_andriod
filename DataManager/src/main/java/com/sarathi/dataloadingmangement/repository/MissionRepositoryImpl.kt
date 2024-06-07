package com.sarathi.dataloadingmangement.repository

import android.util.Log
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.ActivityConfigDao
import com.sarathi.dataloadingmangement.data.dao.ActivityDao
import com.sarathi.dataloadingmangement.data.dao.ActivityLanguageDao
import com.sarathi.dataloadingmangement.data.dao.AttributeValueReferenceDao
import com.sarathi.dataloadingmangement.data.dao.ContentConfigDao
import com.sarathi.dataloadingmangement.data.dao.MissionDao
import com.sarathi.dataloadingmangement.data.dao.MissionLanguageAttributeDao
import com.sarathi.dataloadingmangement.data.dao.ProgrammeDao
import com.sarathi.dataloadingmangement.data.dao.SubjectAttributeDao
import com.sarathi.dataloadingmangement.data.dao.TaskDao
import com.sarathi.dataloadingmangement.data.dao.UiConfigDao
import com.sarathi.dataloadingmangement.data.entities.ActivityConfigEntity
import com.sarathi.dataloadingmangement.data.entities.ActivityEntity
import com.sarathi.dataloadingmangement.data.entities.ActivityLanguageAttributesEntity
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.data.entities.AttributeValueReferenceEntity
import com.sarathi.dataloadingmangement.data.entities.ContentConfigEntity
import com.sarathi.dataloadingmangement.data.entities.MissionEntity
import com.sarathi.dataloadingmangement.data.entities.MissionLanguageEntity
import com.sarathi.dataloadingmangement.data.entities.ProgrammeEntity
import com.sarathi.dataloadingmangement.data.entities.SubjectAttributeEntity
import com.sarathi.dataloadingmangement.data.entities.UiConfigEntity
import com.sarathi.dataloadingmangement.domain.MissionRequest
import com.sarathi.dataloadingmangement.model.mat.response.ActivityConfig
import com.sarathi.dataloadingmangement.model.mat.response.ActivityResponse
import com.sarathi.dataloadingmangement.model.mat.response.ActivityTitle
import com.sarathi.dataloadingmangement.model.mat.response.AttributeResponse
import com.sarathi.dataloadingmangement.model.mat.response.ContentResponse
import com.sarathi.dataloadingmangement.model.mat.response.MissionResponse
import com.sarathi.dataloadingmangement.model.mat.response.ProgrameResponse
import com.sarathi.dataloadingmangement.model.mat.response.TaskData
import com.sarathi.dataloadingmangement.model.mat.response.TaskResponse
import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import javax.inject.Inject

class MissionRepositoryImpl @Inject constructor(
    val apiInterface: DataLoadingApiService,
    val missionLanguageAttributeDao: MissionLanguageAttributeDao,
    val sharedPrefs: CoreSharedPrefs,
    val contentConfigDao: ContentConfigDao,
    val missionActivityDao: ActivityDao,
    val activityConfigDao: ActivityConfigDao,
    val uiConfigDao: UiConfigDao,
    val activityLanguageDao: ActivityLanguageDao,
    val taskDao: TaskDao,
    val programmeDao: ProgrammeDao,
    val subjectAttributeDao: SubjectAttributeDao,
    val attributeValueReferenceDao: AttributeValueReferenceDao,
    val missionDao: MissionDao
) : IMissionRepository {

    override suspend fun fetchMissionDataFromServer(
    ): ApiResponseModel<List<ProgrameResponse>> {
        val missionRequest = MissionRequest(stateId = 31)
        return apiInterface.getMissions(missionRequest)
    }

    override suspend fun saveMissionToDB(missions: List<MissionResponse>, programmeId: Int) {
        missionDao.softDeleteMission(sharedPrefs.getUniqueUserIdentifier())
        missions.forEach { mission ->
            val missionCount = missionDao.getMissionCount(
                userId = sharedPrefs.getUniqueUserIdentifier(),
                missionId = mission.id
            )
            if (missionCount == 0) {
                saveMissionsLanguageAttributes(mission)
                saveContentConfig(mission.id, mission.missionConfig?.contents ?: listOf(), 1)
                missionDao.insertMission(
                    MissionEntity.getMissionEntity(
                        userId = sharedPrefs.getUniqueUserIdentifier(),
                        activityTaskSize = mission.activities.size,
                        mission = mission,
                        programmeId = programmeId
                    )
                )
            } else {
                missionDao.updateMissionActiveStatus(
                    mission.id,
                    sharedPrefs.getUniqueUserIdentifier()
                )
            }
        }
    }

    private fun saveMissionsLanguageAttributes(mission: MissionResponse) {
        mission.languages.forEach {
            missionLanguageAttributeDao.insertMissionLanguageAttribute(
                MissionLanguageEntity.getMissionLanguageEntity(
                    mission.id,
                    it,
                    sharedPrefs.getUniqueUserIdentifier()
                )
            )
        }
    }

    private fun saveContentConfig(
        id: Int,
        contents: List<ContentResponse>,
        contentCategory: Int
    ) {
        contents.forEach {
            contentConfigDao.insertContentConfig(
                ContentConfigEntity.getContentConfigEntity(
                    userId = sharedPrefs.getUniqueUserIdentifier(),
                    matId = id,
                    it,
                    contentCategory
                )
            )
        }
    }

    override suspend fun saveMissionsActivityToDB(
        activities: List<ActivityResponse>,
        missionId: Int
    ) {
        try {
            activities.forEach { missionActivityModel ->
                val activityCount = missionActivityDao.getActivityCount(
                    userId = sharedPrefs.getUniqueUserIdentifier(),
                    missionActivityModel.id
                )
                if (activityCount == 0) {
                    missionActivityDao.insertMissionActivity(
                        ActivityEntity.getMissionActivityEntity(
                            sharedPrefs.getUniqueUserIdentifier(),
                            missionId,
                            missionActivityModel.taskResponses?.size ?: 0,
                            missionActivityModel
                        )
                    )
                    saveContentConfig(
                        missionActivityModel.id,
                        missionActivityModel.activityConfig?.content ?: listOf(),
                        2
                    )
                    missionActivityModel.activityConfig?.let {
                        saveActivityConfig(
                            it,
                            missionActivityModel.id,
                            missionId
                        )
                    }
                    saveActivityUiConfig(
                        missionActivityModel.id,
                        missionId,
                        missionActivityModel.activityConfig?.uiConfig ?: listOf()
                    )
                    missionActivityModel.activityConfig?.activityTitle?.let {
                        saveActivityLanguageAttributes(
                            missionId,
                            missionActivityModel.id,
                            it
                        )
                    }
                } else {
                    missionActivityDao.updateActivityActiveStatus(
                        missionId,
                        sharedPrefs.getUniqueUserIdentifier(),
                        1,
                        missionActivityModel.id
                    )
                }
            }
        } catch (exception: Exception) {
            Log.e("Exception", exception.localizedMessage)
        }
    }

    override fun saveMissionsActivityTaskToDB(
        missionId: Int,
        activityId: Int,
        subject: String,
        tasks: List<TaskResponse>
    ) {
        taskDao.softDeleteActivityTask(sharedPrefs.getUniqueUserIdentifier(), activityId, missionId)
        tasks.forEach { task ->
            val taskCount =
                taskDao.getTaskByIdCount(
                    userId = sharedPrefs.getUniqueUserIdentifier(),
                    taskId = task.id
                )
            if (taskCount == 0) {
                taskDao.insertActivityTask(
                    ActivityTaskEntity.getActivityTaskEntity(
                        userId = sharedPrefs.getUniqueUserIdentifier(),
                        missionId = missionId,
                        activityId = activityId,
                        task = task,
                    )
                )
                saveTaskAttributes(
                    missionId,
                    activityId,
                    task.id,
                    task.taskData ?: listOf(),
                    task.subjectId,
                    subject
                )
            } else {
                taskDao.updateActiveTaskStatus(
                    1,
                    task.id,
                    sharedPrefs.getUniqueUserIdentifier()
                )
            }

        }
    }

    override suspend fun saveProgrammeToDb(programme: ProgrameResponse) {
        programmeDao.insertProgramme(ProgrammeEntity.getProgrammeEntity(programme))
    }

    override suspend fun getAllMission(): List<MissionUiModel> {
        return missionDao.getMissions(
            userId = sharedPrefs.getUniqueUserIdentifier(),
            languageCode = sharedPrefs.getAppLanguage()
        )
    }


    private fun saveActivityConfig(
        activityConfig: ActivityConfig,
        activityId: Int,
        missionId: Int
    ) {
        activityConfigDao.insertActivityConfig(
            ActivityConfigEntity.getActivityConfigEntity(
                activityId = activityId,
                missionId = missionId,
                activityConfig,
                sharedPrefs.getUniqueUserIdentifier()
            )
        )
    }

    private fun saveActivityUiConfig(
        activityId: Int,
        missionId: Int,
        uiConfig: List<AttributeResponse>,
    ) {
        uiConfig.forEach { attribute ->
            uiConfigDao.insertUiConfig(
                UiConfigEntity.getUiConfigEntity(
                    missionId = missionId,
                    activityId = activityId,
                    userId = sharedPrefs.getUniqueUserIdentifier(),
                    attributes = attribute
                )
            )
        }
    }

    private fun saveActivityLanguageAttributes(
        missionId: Int,
        id: Int,
        activityTitle: List<ActivityTitle>
    ) {
        activityTitle.forEach {
            activityLanguageDao.insertActivityLanguage(
                ActivityLanguageAttributesEntity.getActivityLanguageAttributesEntity(
                    missionId = missionId,
                    activityId = id,
                    userId = sharedPrefs.getUniqueUserIdentifier(),
                    activityTitle = it
                )
            )
        }
    }

    private fun saveTaskAttributes(
        missionId: Int,
        activityId: Int,
        id: Int,
        taskData: List<TaskData>,
        subjectId: Int,
        subject: String
    ) {
        val refrenceId = subjectAttributeDao.insertSubjectAttribute(
            SubjectAttributeEntity.getSubjectAttributeEntity(
                userId = sharedPrefs.getUniqueUserIdentifier(),
                missionId = missionId,
                activityId = activityId,
                taskId = id,
                subjectId = subjectId,
                subjectType = subject,
                attribute = "",
            )
        )
        taskData.forEach {
            attributeValueReferenceDao.insertAttributesValueReferences(
                AttributeValueReferenceEntity.getAttributeValueReferenceEntity(
                    userId = sharedPrefs.getUniqueUserIdentifier(),
                    parentReferenceId = refrenceId,
                    taskData = it,
                )
            )
        }
    }
}