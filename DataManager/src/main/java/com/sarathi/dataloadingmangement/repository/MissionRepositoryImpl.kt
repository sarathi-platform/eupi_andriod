package com.sarathi.dataloadingmangement.repository

import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.utils.CoreLogger
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
import com.sarathi.dataloadingmangement.data.entities.ActivityEntity
import com.sarathi.dataloadingmangement.data.entities.ActivityLanguageAttributesEntity
import com.sarathi.dataloadingmangement.data.entities.ContentConfigEntity
import com.sarathi.dataloadingmangement.data.entities.MissionEntity
import com.sarathi.dataloadingmangement.data.entities.MissionLanguageEntity
import com.sarathi.dataloadingmangement.data.entities.ProgrammeEntity
import com.sarathi.dataloadingmangement.data.entities.revamp.MissionConfigEntity
import com.sarathi.dataloadingmangement.data.entities.revamp.MissionLivelihoodConfigEntity
import com.sarathi.dataloadingmangement.model.mat.response.ActivityResponse
import com.sarathi.dataloadingmangement.model.mat.response.ActivityTitle
import com.sarathi.dataloadingmangement.model.mat.response.ContentResponse
import com.sarathi.dataloadingmangement.model.mat.response.MissionConfig
import com.sarathi.dataloadingmangement.model.mat.response.MissionResponse
import com.sarathi.dataloadingmangement.model.mat.response.ProgrameResponse
import com.sarathi.dataloadingmangement.model.uiModel.ContentCategoryEnum
import com.sarathi.dataloadingmangement.model.uiModel.MissionInfoUIModel
import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import kotlinx.coroutines.flow.Flow
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

    override suspend fun fetchMissionListFromServer(): ApiResponseModel<List<ProgrameResponse>> {
        return apiInterface.getMissionList()
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

    private fun deleteMissionConfig(missionId: Int) {
        missionConfigEntityDao.deleteMissionConfig(
            missionId = missionId,
            uniqueUserIdentifier = sharedPrefs.getUniqueUserIdentifier()
        )
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
                    programLivelihoodReferenceId = livelihoodList.programLivelihoodReferenceId
                )
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

    private fun deleteLivelihoodConfig(missionId: Int) {
        missionLivelihoodConfigEntityDao.deleteLivelihoodConfig(
            missionId = missionId,
            uniqueUserIdentifier = sharedPrefs.getUniqueUserIdentifier()
        )
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

    override fun getAllMission(): Flow<List<MissionUiModel>> {
        return missionDao.getMissionsFlow(
            userId = sharedPrefs.getUniqueUserIdentifier(),
            languageCode = sharedPrefs.getAppLanguage()
        )
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

    override suspend fun fetchMissionInfo(missionId: Int): MissionInfoUIModel? {
        val missionUIInfoModel = missionLanguageAttributeDao.fetchMissionInfo(
            missionId = missionId,
            userId = sharedPrefs.getUniqueUserIdentifier(),
            languageCode = sharedPrefs.getSelectedLanguageCode()
        )
        val livelihoodName =
            findLivelihoodSubtitle(missionUIInfoModel?.livelihoodType)
        return missionUIInfoModel?.copy(livelihoodName = livelihoodName)
    }

    private fun findLivelihoodSubtitle(livelihoodType: String?): String? {
        val livelihood = livelihoodDao.getLivelihoodList(
            userId = sharedPrefs.getUniqueUserIdentifier(),
            languageCode = sharedPrefs.getSelectedLanguageCode()
        )
        val livelihoodName =
            livelihood.find { it.type.equals(livelihoodType, true) }?.name
                ?: livelihoodType
        return livelihoodName
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


}