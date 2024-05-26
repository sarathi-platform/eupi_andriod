package com.sarathi.dataloadingmangement.domain


import android.util.Log
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.ActivityConfigDao
import com.sarathi.dataloadingmangement.data.dao.ActivityDao
import com.sarathi.dataloadingmangement.data.dao.ActivityLanguageAttributeDao
import com.sarathi.dataloadingmangement.data.dao.ActivityLanguageDao
import com.sarathi.dataloadingmangement.data.dao.AttributeValueReferenceDao
import com.sarathi.dataloadingmangement.data.dao.ContentConfigDao
import com.sarathi.dataloadingmangement.data.dao.ContentDao
import com.sarathi.dataloadingmangement.data.dao.MissionDao
import com.sarathi.dataloadingmangement.data.dao.MissionLanguageAttributeDao
import com.sarathi.dataloadingmangement.data.dao.SubjectAttributeDao
import com.sarathi.dataloadingmangement.data.dao.TaskAttributeDao
import com.sarathi.dataloadingmangement.data.dao.TaskDao
import com.sarathi.dataloadingmangement.data.dao.UiConfigDao
import com.sarathi.dataloadingmangement.data.entities.ActivityConfigEntity
import com.sarathi.dataloadingmangement.data.entities.ActivityEntity
import com.sarathi.dataloadingmangement.data.entities.ActivityLanguageAttributesEntity
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.data.entities.AttributeValueReferenceEntity
import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.data.entities.ContentConfigEntity
import com.sarathi.dataloadingmangement.data.entities.MissionEntity
import com.sarathi.dataloadingmangement.data.entities.MissionLanguageEntity
import com.sarathi.dataloadingmangement.data.entities.SubjectAttributeEntity
import com.sarathi.dataloadingmangement.data.entities.UiConfigEntity
import com.sarathi.dataloadingmangement.model.ApiResponseModel
import com.sarathi.dataloadingmangement.model.mat.response.ActivityConfig
import com.sarathi.dataloadingmangement.model.mat.response.ActivityResponse
import com.sarathi.dataloadingmangement.model.mat.response.ActivityTitle
import com.sarathi.dataloadingmangement.model.mat.response.ContentResponse
import com.sarathi.dataloadingmangement.model.mat.response.MissionResponse
import com.sarathi.dataloadingmangement.model.mat.response.TaskData
import com.sarathi.dataloadingmangement.model.mat.response.TaskResponse
import com.sarathi.dataloadingmangement.model.mat.response.UiConfig
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import com.sarathi.dataloadingmangement.network.request.ContentRequest
import com.sarathi.dataloadingmangement.repository.IDataLoadingScreenRepository
import javax.inject.Inject

class DataLoadingScreenRepositoryImpl @Inject constructor(
    val apiInterface: DataLoadingApiService,
    val missionDao: MissionDao,
    val missionActivityDao: ActivityDao,
    val taskDao: TaskDao,
    val activityConfigDao: ActivityConfigDao,
    val activityLanguageAttributeDao: ActivityLanguageAttributeDao,
    val activityLanguageDao: ActivityLanguageDao,
    val attributeValueReferenceDao: AttributeValueReferenceDao,
    val contentConfigDao: ContentConfigDao,
    val missionLanguageAttributeDao: MissionLanguageAttributeDao,
    val subjectAttributeDao: SubjectAttributeDao,
    val taskAttributeDao: TaskAttributeDao,
    val uiConfigDao: UiConfigDao,
    val contentDao: ContentDao,
    val sharedPrefs: CoreSharedPrefs
) : IDataLoadingScreenRepository {
    override suspend fun fetchMissionDataFromServer(
        languageCode: String,
        missionName: String
    ): ApiResponseModel<List<MissionResponse>> {
        val missionRequest = MissionRequest(languageCode, missionName)
        return apiInterface.getMissions(missionRequest)
    }

    override suspend fun saveMissionToDB(missions: List<MissionResponse>) {
        missionDao.softDeleteMission(sharedPrefs.getUniqueUserIdentifier())
        missions.forEach { mission ->
            val missionCount = missionDao.getMissionCount(
                userId = sharedPrefs.getUniqueUserIdentifier(),
                missionId = mission.id
            )
            if (missionCount == 0) {
                saveMissionsLanguageAttributes(mission)
                saveContentConfig(mission.id, mission.missionConfig.contents, 1)
                missionDao.insertMission(
                    MissionEntity.getMissionEntity(
                        userId = sharedPrefs.getUniqueUserIdentifier(),
                        activityTaskSize = mission.activities.size,
                        mission = mission,

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
                            missionActivityModel.taskResponses.size,
                            missionActivityModel
                        )
                    )
                    saveContentConfig(
                        missionActivityModel.id,
                        missionActivityModel.activityConfig.content,
                        2
                    )
                    saveActivityConfig(
                        missionActivityModel.activityConfig,
                        missionActivityModel.id,
                        missionId
                    )
                    saveActivityUiConfig(
                        missionActivityModel.id,
                        missionId,
                        missionActivityModel.activityConfig.uiConfig
                    )
                    saveActivityLanguageAttributes(
                        missionId,
                        missionActivityModel.id,
                        missionActivityModel.activityConfig.activityTitle
                    )
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

    private suspend fun saveActivityConfig(
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

    private suspend fun saveActivityUiConfig(
        activityId: Int,
        missionId: Int,
        uiConfig: UiConfig,
    ) {
        uiConfig.languageAttributes.forEach { language ->
            language.attributes.forEach {

                uiConfigDao.insertUiConfig(
                    UiConfigEntity.getUiConfigEntity(
                        missionId = missionId,
                        activityId = activityId,
                        userId = sharedPrefs.getUniqueUserIdentifier(),
                        language = language.languageId,
                        attributes = it
                    )
                )
            }

        }

    }

    private suspend fun saveContentConfig(
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
                    taskId = task.id ?: 0
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
                    task.taskData,
                    task.subjectId,
                    subject
                )
            } else {
                taskDao.updateActiveTaskStatus(
                    1,
                    task.id ?: 0,
                    sharedPrefs.getUniqueUserIdentifier()
                )
            }

        }
    }

    override suspend fun fetchContentsFromServer(contentMangerRequest: ContentRequest): ApiResponseModel<List<ContentResponse>> {
        return apiInterface.fetchContentData(contentMangerRequest)
    }

    override suspend fun saveContentToDB(contents: List<Content>) {
        contentDao.insertContent(contents)
    }

    override suspend fun deleteContentFromDB() {
        contentDao.deleteContent()
    }

    override suspend fun getContentData(): List<Content> {
        return contentDao.getContentData()
    }

}