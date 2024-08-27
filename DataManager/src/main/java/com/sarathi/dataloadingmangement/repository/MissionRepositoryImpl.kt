package com.sarathi.dataloadingmangement.repository

import android.util.Log
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.data.dao.ActivityConfigDao
import com.sarathi.dataloadingmangement.data.dao.ActivityDao
import com.sarathi.dataloadingmangement.data.dao.ActivityLanguageDao
import com.sarathi.dataloadingmangement.data.dao.AttributeValueReferenceDao
import com.sarathi.dataloadingmangement.data.dao.ContentConfigDao
import com.sarathi.dataloadingmangement.data.dao.FormUiConfigDao
import com.sarathi.dataloadingmangement.data.dao.GrantConfigDao
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
import com.sarathi.dataloadingmangement.data.entities.FormUiConfigEntity
import com.sarathi.dataloadingmangement.data.entities.GrantConfigEntity
import com.sarathi.dataloadingmangement.data.entities.MissionEntity
import com.sarathi.dataloadingmangement.data.entities.MissionLanguageEntity
import com.sarathi.dataloadingmangement.data.entities.ProgrammeEntity
import com.sarathi.dataloadingmangement.data.entities.SubjectAttributeEntity
import com.sarathi.dataloadingmangement.data.entities.UiConfigEntity
import com.sarathi.dataloadingmangement.domain.ActivityRequest
import com.sarathi.dataloadingmangement.model.mat.response.ActivityConfig
import com.sarathi.dataloadingmangement.model.mat.response.ActivityResponse
import com.sarathi.dataloadingmangement.model.mat.response.ActivityTitle
import com.sarathi.dataloadingmangement.model.mat.response.AttributeResponse
import com.sarathi.dataloadingmangement.model.mat.response.ContentResponse
import com.sarathi.dataloadingmangement.model.mat.response.FormConfigResponse
import com.sarathi.dataloadingmangement.model.mat.response.GrantConfigResponse
import com.sarathi.dataloadingmangement.model.mat.response.MissionResponse
import com.sarathi.dataloadingmangement.model.mat.response.ProgrameResponse
import com.sarathi.dataloadingmangement.model.mat.response.TaskData
import com.sarathi.dataloadingmangement.model.mat.response.TaskResponse
import com.sarathi.dataloadingmangement.model.uiModel.ContentCategoryEnum
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
    val formUiConfigDao: FormUiConfigDao,
    val activityLanguageDao: ActivityLanguageDao,
    val taskDao: TaskDao,
    val programmeDao: ProgrammeDao,
    val subjectAttributeDao: SubjectAttributeDao,
    val attributeValueReferenceDao: AttributeValueReferenceDao,
    val missionDao: MissionDao,
    val grantConfigDao: GrantConfigDao
) : IMissionRepository {

    override suspend fun fetchActivityDataFromServer(
        programId: Int,
        missionId: Int
    ): ApiResponseModel<List<ActivityResponse>> {
        val activityRequest =
            ActivityRequest(programId = programId, missionId = missionId)

        return apiInterface.getActivityDetails(activityRequest)
    }

    override suspend fun fetchMissionListFromServer(): ApiResponseModel<List<ProgrameResponse>> {
        return apiInterface.getMissionList()
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
            deleteContentConfig(mission.id, ContentCategoryEnum.MISSION.ordinal)
            saveContentConfig(
                mission.id,
                mission.missionConfig?.contents ?: listOf(),
                ContentCategoryEnum.MISSION.ordinal
            )
        }
    }

    private fun deleteContentConfig(missionId: Int, contentCategory: Int) {
        contentConfigDao.deleteContentConfig(
            missionId,
            contentCategory,
            sharedPrefs.getUniqueUserIdentifier()
        )
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


    override suspend fun saveActivityConfig(
        missionActivityModel: ActivityResponse,
        missionId: Int,
    ) {
        missionActivityModel.activityConfig?.activityTitle?.let {
            saveActivityLanguageAttributes(
                missionId,
                missionActivityModel.id,
                it
            )
        }
        deleteContentConfig(missionActivityModel.id, ContentCategoryEnum.ACTIVITY.ordinal)
        saveContentConfig(
            missionActivityModel.id,
            missionActivityModel.activityConfig?.content ?: listOf(),
            ContentCategoryEnum.ACTIVITY.ordinal
        )
        deleteActivityConfig(missionActivityModel.id)


        missionActivityModel.activityConfig?.let {
            val activityConfigId = saveActivityConfig(
                it,
                missionActivityModel.id,

                missionId
            )
            deleteGrantConfig(activityConfigId)
            it.grantConfig?.let { it1 ->
                saveGrantActivityConfig(
                    it1,
                    missionActivityModel.activityConfig.surveyId,
                    activityConfigId
                )


            }
            it.formConfig?.let { it1 ->
                saveFormUiConfig(it1, missionId, missionActivityModel.id)
            }

        }
        deleteActivityUiConfig(activityId = missionActivityModel.id, missionId = missionId)
        saveActivityUiConfig(
            missionActivityModel.id,
            missionId,
            missionActivityModel.activityConfig?.uiConfig ?: listOf()
        )
    }

    private fun deleteActivityUiConfig(missionId: Int, activityId: Int) {
        uiConfigDao.deleteActivityUiConfig(
            missionId = missionId,
            activityId = activityId,
            uniqueUserIdentifier = sharedPrefs.getUniqueUserIdentifier()
        )
    }

    private fun deleteActivityConfig(id: Int) {
        activityConfigDao.deleteActivityConfig(
            activityId = id,
            userId = sharedPrefs.getUniqueUserIdentifier()
        )
    }

    private fun deleteGrantConfig(activityConfigId: Long) {
        grantConfigDao.deleteGrantConfig(
            activityConfigId,
            userId = sharedPrefs.getUniqueUserIdentifier()
        )
    }

    private fun deleteFormConfig(activityId: Int, missionId: Int) {
        formUiConfigDao.deleteActivityFormUiConfig(
            uniqueUserIdentifier = sharedPrefs.getUniqueUserIdentifier(),
            activityId = activityId,
            missionId = missionId
        )
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
                updateTaskAttributes(
                    missionId,
                    activityId,
                    task.id,
                    task.taskData ?: listOf(),
                    task.subjectId,
                    subject
                )
            }

        }
    }

    override suspend fun saveProgrammeToDb(programme: ProgrameResponse) {
        programmeDao.deleteProgramme(sharedPrefs.getUniqueUserIdentifier())
        programmeDao.insertProgramme(
            ProgrammeEntity.getProgrammeEntity(
                programme,
                sharedPrefs.getUniqueUserIdentifier()
            )
        )
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
    ): Long {
        return activityConfigDao.insertActivityConfig(
            ActivityConfigEntity.getActivityConfigEntity(
                activityId = activityId,
                missionId = missionId,
                activityConfig,
                sharedPrefs.getUniqueUserIdentifier()
            )
        )
    }

    private fun saveFormUiConfig(
        uiConfig: List<FormConfigResponse?>,
        missionId: Int,
        activityId: Int
    ) {
        deleteFormConfig(activityId, missionId)

        uiConfig.forEach { attribute ->
            formUiConfigDao.insertFormUiConfig(
                FormUiConfigEntity.getFormUiConfigEntity(
                    userId = sharedPrefs.getUniqueUserIdentifier(),
                    attributes = attribute,
                    activityId = activityId,
                    missionId = missionId
                )
            )
        }
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
        activityLanguageDao.deleteActivityLanguageAttributeForActivity(
            userId = sharedPrefs.getUniqueUserIdentifier(),
            missionId = missionId,
            activityId = id
        )
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

    private fun saveGrantActivityConfig(
        grantConfig: List<GrantConfigResponse>,
        surveyId: Int,
        activityTypeId: Long
    ) {
        grantConfig.forEach {
            grantConfigDao.insertGrantActivityConfig(
                GrantConfigEntity.getGrantConfigEntity(
                    userId = sharedPrefs.getUniqueUserIdentifier(),
                    activityConfigId = activityTypeId,
                    surveyId = surveyId,
                    grantConfigResponse = it
                )
            )
        }
    }

    private fun updateTaskAttributes(
        missionId: Int,
        activityId: Int,
        id: Int,
        taskData: List<TaskData>,
        subjectId: Int,
        subject: String
    ) {
        subjectAttributeDao.updateSubjectAttribute(
                userId = sharedPrefs.getUniqueUserIdentifier(),
                missionId = missionId,
                activityId = activityId,
                taskId = id,
                subjectId = subjectId,
                subjectType = subject,
        )

        val referenceId = subjectAttributeDao.getReferenceId(
            userId = sharedPrefs.getUniqueUserIdentifier(),
            subjectId = subjectId,
            taskId = id,
        )

        taskData.forEach {
            val rowUpdated = attributeValueReferenceDao.updateAttributeValueReference(
                    userId = sharedPrefs.getUniqueUserIdentifier(),
                    parentReferenceId = referenceId.toLong(),
                    key = it.key,
                    value = it.value ?: BLANK_STRING,
                )

            if(rowUpdated == 0) {
                attributeValueReferenceDao.insertAttributesValueReferences(
                    AttributeValueReferenceEntity.getAttributeValueReferenceEntity(
                        userId = sharedPrefs.getUniqueUserIdentifier(),
                        parentReferenceId = referenceId.toLong(),
                        taskData = it,
                    )
                )
            }
        }
    }

}