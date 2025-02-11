package com.sarathi.dataloadingmangement.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.utils.CoreLogger
import com.nudge.core.value
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
import com.sarathi.dataloadingmangement.data.dao.SurveyConfigEntityDao
import com.sarathi.dataloadingmangement.data.dao.TaskDao
import com.sarathi.dataloadingmangement.data.dao.UiConfigDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.LivelihoodDao
import com.sarathi.dataloadingmangement.data.dao.revamp.MissionConfigEntityDao
import com.sarathi.dataloadingmangement.data.dao.revamp.MissionLivelihoodConfigEntityDao
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
import com.sarathi.dataloadingmangement.data.entities.SurveyConfigEntity
import com.sarathi.dataloadingmangement.data.entities.UiConfigEntity
import com.sarathi.dataloadingmangement.data.entities.revamp.MissionConfigEntity
import com.sarathi.dataloadingmangement.data.entities.revamp.MissionLivelihoodConfigEntity
import com.sarathi.dataloadingmangement.domain.ActivityRequest
import com.sarathi.dataloadingmangement.model.mat.response.ActivityConfig
import com.sarathi.dataloadingmangement.model.mat.response.ActivityResponse
import com.sarathi.dataloadingmangement.model.mat.response.ActivityTitle
import com.sarathi.dataloadingmangement.model.mat.response.AttributeResponse
import com.sarathi.dataloadingmangement.model.mat.response.ContentResponse
import com.sarathi.dataloadingmangement.model.mat.response.FormConfigResponse
import com.sarathi.dataloadingmangement.model.mat.response.GrantConfigResponse
import com.sarathi.dataloadingmangement.model.mat.response.MissionConfig
import com.sarathi.dataloadingmangement.model.mat.response.MissionResponse
import com.sarathi.dataloadingmangement.model.mat.response.ProgrameResponse
import com.sarathi.dataloadingmangement.model.mat.response.SurveyConfigAttributeResponse
import com.sarathi.dataloadingmangement.model.mat.response.TaskData
import com.sarathi.dataloadingmangement.model.mat.response.TaskResponse
import com.sarathi.dataloadingmangement.model.survey.response.OptionsItem
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
    val grantConfigDao: GrantConfigDao,
    val surveyConfigEntityDao: SurveyConfigEntityDao,
    val missionConfigEntityDao: MissionConfigEntityDao,
    val missionLivelihoodConfigEntityDao: MissionLivelihoodConfigEntityDao,
    val livelihoodDao: LivelihoodDao
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
                    missionId = mission.id,
                    userId = sharedPrefs.getUniqueUserIdentifier(),
                    missionOrder = mission.order ?: 1
                )
            }
            mission.missionConfig?.let {
                deleteMissionConfig(missionId = mission.id)
                saveMissionConfig(
                    missionId = mission.id,
                    missionName = mission.name,
                    missionConfig = mission.missionConfig
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
                    missionId = missionId,
                    activityId = missionActivityModel.id
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
                        missionId = missionId,
                        userId = sharedPrefs.getUniqueUserIdentifier(),
                        isActive = 1,
                        activityId = missionActivityModel.id,
                        order = missionActivityModel.order ?: 1
                    )
                }

            }
        } catch (exception: Exception) {
            CoreAppDetails.getContext()
                ?.let {
                    CoreLogger.e(
                        context = it,
                        "Exception",
                        exception.localizedMessage ?: BLANK_STRING
                    )
                }
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

        if (!missionActivityModel.activityConfig?.surveyConfig.isNullOrEmpty()) {
            deleteSurveyConfig(activityId = missionActivityModel.id, missionId)
            saveSurveyConfig(
                activityId = missionActivityModel.id,
                missionId = missionId,
                surveyId = missionActivityModel.activityConfig?.surveyId.value(0),
                surveyConfig = missionActivityModel.activityConfig?.surveyConfig ?: listOf()
            )
        }
    }



    override suspend fun isMissionLoaded(missionId: Int, programId: Int): Int {

        return missionDao.isMissionDataLoaded(
            userId = sharedPrefs.getUniqueUserIdentifier(),
            missionId = missionId,
            programId = programId
        )
    }

    override suspend fun setMissionLoaded(missionId: Int, programId: Int) {
        missionDao.updateMissionDataLoaded(
            userId = sharedPrefs.getUniqueUserIdentifier(),
            missionId = missionId,
            programId = programId
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
                    taskId = task.id,
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
        grantConfig.forEach { grantConfig ->
            grantConfigDao.insertGrantActivityConfig(
                GrantConfigEntity.getGrantConfigEntity(
                    userId = sharedPrefs.getUniqueUserIdentifier(),
                    activityConfigId = activityTypeId,
                    surveyId = surveyId,
                    grantConfigResponse = getGrantConfigForLanguageThatMappedToState(grantConfig)
                )
            )
        }
    }

    private fun getGrantConfigForLanguageThatMappedToState(grantConfigResponse: GrantConfigResponse): GrantConfigResponse {
        try {


            val grantType =
                object : TypeToken<List<OptionsItem?>?>() {}.type
            val grantModeOptions = Gson().fromJson<List<OptionsItem>>(
                grantConfigResponse.grantMode,
                grantType
            )

            val grantNatureOptions = Gson().fromJson<List<OptionsItem>>(
                grantConfigResponse.grantNature,
                grantType
            )

            grantModeOptions.forEach {
                checkLanguageAndSaveForCurrentState(it)
            }
            grantNatureOptions.forEach {
                checkLanguageAndSaveForCurrentState(it)
            }
            grantConfigResponse.grantMode = Gson().toJson(grantModeOptions)
            grantConfigResponse.grantNature = Gson().toJson(grantNatureOptions)

            return grantConfigResponse
        } catch (exception: Exception) {
            CoreLogger.e(tag = "GrantConfigSave", msg = exception.stackTraceToString())
            return grantConfigResponse
        }
    }

    private fun checkLanguageAndSaveForCurrentState(it: OptionsItem) {
        it.surveyLanguageAttributes?.forEach { languageAttribute ->
            val stateCode = sharedPrefs.getStateCode().lowercase()
            val languageCodeParts = languageAttribute.languageCode.lowercase().split("_")
            val updatedLanguageCode =
                if (languageCodeParts.size > 1 && languageCodeParts[1].lowercase() == stateCode) {
                    languageCodeParts.firstOrNull()?.lowercase() ?: com.nudge.core.BLANK_STRING
                } else {
                    languageAttribute.languageCode
                }

            languageAttribute.languageCode = updatedLanguageCode
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

            if (rowUpdated == 0) {
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


    override suspend fun getActivityTypesForMission(missionId: Int): List<String> {
        return activityConfigDao.getActivityType(
            missionId = missionId,
            userId = sharedPrefs.getUniqueUserIdentifier()
        )
    }

    override suspend fun saveActivityOrderStatus(missionId: Int, activityId: Int, order: Int) {
        missionActivityDao.updateActivityActiveStatus(
            missionId = missionId,
            userId = sharedPrefs.getUniqueUserIdentifier(),
            isActive = 1,
            activityId = activityId,
            order = order
        )
    }

    private fun saveSurveyConfig(
        activityId: Int,
        missionId: Int,
        surveyId: Int,
        surveyConfig: List<SurveyConfigAttributeResponse>,
    ) {

        surveyConfig.forEach { attribute ->
            surveyConfigEntityDao.insertUiConfig(
                SurveyConfigEntity.getSurveyConfigEntity(
                    missionId = missionId,
                    activityId = activityId,
                    surveyId = surveyId,
                    userId = sharedPrefs.getUniqueUserIdentifier(),
                    attributes = attribute
                )
            )
        }

    }
    private fun saveMissionConfig(
        missionId: Int,
        missionName: String,
        missionConfig: MissionConfig,
    ) {
        val userId = sharedPrefs.getUniqueUserIdentifier()
        missionConfigEntityDao.insertMissionConfig(
            MissionConfigEntity.getMissionConfigEntity(
                missionId = missionId,
                missionName = missionName,
                missionType = missionConfig.missionType ?: BLANK_STRING,
                userId = userId,
            )
        )
        missionConfig.livelihoodConfig?.let { livelihoodList ->
            deleteLivelihoodConfig(missionId = missionId)
            missionLivelihoodConfigEntityDao.insertLivelihoodConfigs(
                MissionLivelihoodConfigEntity.getLivelihoodConfigEntityList(
                    missionId = missionId,
                    missionType = missionConfig.missionType ?: BLANK_STRING,
                    livelihoodType = livelihoodList?.livelihoodType ?: BLANK_STRING,
                    livelihoodOrder = livelihoodList?.livelihoodOrder ?: 0,
                    languages = livelihoodList.languages,
                    userId = userId,
                    programLivelihoodReferenceId = livelihoodList.program_livelihood_reference_id
                )
            )
        }
    }


    private fun deleteSurveyConfig(activityId: Int, missionId: Int) {
        surveyConfigEntityDao.deleteSurveyConfig(
            missionId = missionId,
            activityId = activityId,
            uniqueUserIdentifier = sharedPrefs.getUniqueUserIdentifier()
        )
    }

    private fun deleteMissionConfig(missionId: Int) {
        missionConfigEntityDao.deleteMissionConfig(
            missionId = missionId,
            uniqueUserIdentifier = sharedPrefs.getUniqueUserIdentifier()
        )
    }

    private fun deleteLivelihoodConfig(missionId: Int) {
        missionLivelihoodConfigEntityDao.deleteLivelihoodConfig(
            missionId = missionId,
            uniqueUserIdentifier = sharedPrefs.getUniqueUserIdentifier()
        )
    }
}