package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository

import android.util.Log
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.PREF_KEY_EMAIL
import com.nrlm.baselinesurvey.PREF_KEY_IDENTITY_NUMBER
import com.nrlm.baselinesurvey.PREF_KEY_NAME
import com.nrlm.baselinesurvey.PREF_KEY_PROFILE_IMAGE
import com.nrlm.baselinesurvey.PREF_KEY_ROLE_NAME
import com.nrlm.baselinesurvey.PREF_KEY_TYPE_NAME
import com.nrlm.baselinesurvey.PREF_KEY_USER_NAME
import com.nrlm.baselinesurvey.PREF_MOBILE_NUMBER
import com.nrlm.baselinesurvey.SUCCESS
import com.nrlm.baselinesurvey.data.prefs.PrefBSRepo
import com.nrlm.baselinesurvey.database.dao.ActivityTaskDao
import com.nrlm.baselinesurvey.database.dao.LanguageListDao
import com.nrlm.baselinesurvey.database.dao.MissionActivityDao
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.database.entity.ActivityTaskEntity
import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.network.interfaces.BaseLineApiService
import com.nrlm.baselinesurvey.network.interfaces.ApiService
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.createMultiLanguageVillageRequest
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nrlm.baselinesurvey.utils.states.SurveyState
import com.nrlm.baselinesurvey.utils.states.SurveyeeCardState
import com.nudge.core.ENGLISH_LANGUAGE_CODE
import com.nudge.core.toDate
import com.nudge.core.updateCoreEventFileName
import javax.inject.Inject

class SurveyeeListScreenRepositoryImpl @Inject constructor(
    private val prefBSRepo: PrefBSRepo,
    private val baseLineApiService: BaseLineApiService,
    private val surveyeeEntityDao: SurveyeeEntityDao,
    private val languageListDao: LanguageListDao,
    private val activityTaskDao: ActivityTaskDao,
    private val activityDao: MissionActivityDao,
    private val taskDao: ActivityTaskDao
): SurveyeeListScreenRepository {

    override suspend fun getSurveyeeList(
        missionId: Int,
        activityId: Int
    ): List<SurveyeeEntity> {
        val didiList = mutableListOf<SurveyeeEntity>()
        //TODO FIx logic here
        getActivityTasks(missionId = missionId, activityId).forEach { task ->
            if (surveyeeEntityDao.isDidiExist(task.didiId)) {
                didiList.add(
                    surveyeeEntityDao.getDidi(task.didiId).copy(
                        surveyStatus = SurveyState.toInt(
                            task.status ?: SurveyState.NOT_STARTED.name
                        )
                    )
                )
            }
        }
        return didiList
    }






    override suspend fun getSurveyeeListFromNetwork(): Boolean {
        try {
            val localLanguageList = languageListDao.getAllLanguages()
            val userViewApiRequest = createMultiLanguageVillageRequest(localLanguageList)
            val userApiResponse =
                baseLineApiService.userAndVillageListAPI(languageId = userViewApiRequest)
            if (userApiResponse.status.equals(SUCCESS, true)) {
                userApiResponse.data?.let {
                    prefBSRepo.savePref(PREF_KEY_USER_NAME, it.username ?: "")
                    prefBSRepo.savePref(PREF_KEY_NAME, it.name ?: "")
                    prefBSRepo.savePref(PREF_KEY_EMAIL, it.email ?: "")
                    prefBSRepo.savePref(PREF_KEY_IDENTITY_NUMBER, it.identityNumber ?: "")
                    prefBSRepo.savePref(PREF_KEY_PROFILE_IMAGE, it.profileImage ?: "")
                    prefBSRepo.savePref(PREF_KEY_ROLE_NAME, it.roleName ?: "")
                    prefBSRepo.savePref(PREF_KEY_TYPE_NAME, it.typeName ?: "")
                    updateCoreEventFileName(
                        context = BaselineCore.getAppContext(),
                        mobileNo = prefRepo.getPref(PREF_MOBILE_NUMBER, BLANK_STRING) ?: BLANK_STRING
                    )
                }
                val apiResponse = userApiResponse.data?.username?.toInt()
                    ?.let { baseLineApiService.getDidisFromNetwork(userId = it) }
                if (apiResponse?.status?.equals(SUCCESS, false) == true) {
                    if (apiResponse?.data?.didiList != null) {
                        surveyeeEntityDao.deleteSurveyees(getBaseLineUserId())
                        apiResponse?.data?.didiList.forEach {
                            val surveyeeEntity = SurveyeeEntity(
                                id = 0,
                                userId = getBaseLineUserId(),
                                didiId = it.didiId,
                                didiName = it.didiName ?: BLANK_STRING,
                                dadaName = it.dadaName ?: BLANK_STRING,
                                casteId = it.casteId ?: -1,
                                cohortId = it.cohortId ?: -1,
                                cohortName = it.cohortName ?: BLANK_STRING,
                                houseNo = it.houseNo ?: BLANK_STRING,
                                villageId = it.villageId ?: -1,
                                villageName = it.villageName ?: BLANK_STRING,
                                ableBodied = it.ableBodied ?: BLANK_STRING
                            )
                            surveyeeEntityDao.insertDidi(surveyeeEntity)
                        }
                        return true
                    } else {
                        return false
                    }
                } else {
                    return false
                }
            } else {
                return false
            }
        } catch (ex: Exception) {
            Log.e("SurveyeeListScreenRepositoryImpl", "getSurveyeeListFromNetwork: ", ex)
            return false
        }
    }

    override suspend fun moveSurveyeesToThisWeek(
        didiIdList: Set<Int>,
        moveDidisToNextWeek: Boolean
    ) {
        didiIdList.forEach {
            surveyeeEntityDao.moveSurveyeesToThisWeek(didiIdList.toList(), moveDidisToNextWeek)
        }
    }

    override suspend fun moveSurveyeeToThisWeek(
        didiId: Int,
        moveDidisToNextWeek: Boolean
    ) {
        surveyeeEntityDao.moveSurveyeeToThisWeek(didiId, moveDidisToNextWeek)
    }

    override suspend fun getActivityTasks(
        missionId: Int,
        activityId: Int
    ): List<ActivityTaskEntity> {
        return activityTaskDao.getActivityTask(getBaseLineUserId(), missionId, activityId)
    }

    override suspend fun getMissionActivitiesStatusFromDB(
        activityId: Int,
        surveyeeCardState: List<SurveyeeCardState>
    ) {
        var activities = activityDao.getActivitiesFormIds(getBaseLineUserId(), activityId)
        val tasks =
            activityTaskDao.getActivityTaskFromIds(getBaseLineUserId(), activities.activityId)
        activityDao.updateActivityStatus(
            getBaseLineUserId(),
            activityId,
            SurveyState.INPROGRESS.ordinal,
            activities.activityTaskSize
        )
        var activityCompleteInc = 0
        var activityPending = activities.pendingDidi

        surveyeeCardState.forEach { surveyeeCardState ->
            if (surveyeeCardState.surveyState == SurveyState.COMPLETED) {
                ++activityCompleteInc
                activityPending = --activities.pendingDidi
            }

        }
        val complete =
            if (activities.activityTaskSize == activityCompleteInc) SurveyState.COMPLETED.ordinal else SurveyState.INPROGRESS.ordinal
        activityDao.updateActivityStatus(
            getBaseLineUserId(),
            activityId,
            complete,
            activities.activityTaskSize - activityCompleteInc
        )

    }

    override suspend fun updateActivityAllTaskStatus(
        activityId: Int,
        isAllTask: Boolean
    ) {
        activityDao.updateActivityAllTaskStatus(getBaseLineUserId(), activityId, isAllTask)
    }

    override suspend fun updateActivityStatus(
        missionId: Int,
        activityId: Int,
        status: SectionStatus
    ) {
        if (status == SectionStatus.COMPLETED) {
            activityDao.markActivityComplete(
                userId = getBaseLineUserId(),
                missionId = missionId,
                activityId = activityId,
                status = status.name,
                completedDate = System.currentTimeMillis().toDate().toString()
            )
        } else {
            activityDao.markActivityStart(
                userId = getBaseLineUserId(),
                missionId = missionId,
                activityId = activityId,
                status = status.name,
                actualStartDate = System.currentTimeMillis().toDate().toString()
            )
        }
    }

    override suspend fun getActivitiyStatusFromDB(activityId: Int): MissionActivityEntity {
        return activityDao.getActivity(getBaseLineUserId(), activityId)
    }

    override fun getBaseLineUserId(): String {
        return prefBSRepo.getUniqueUserIdentifier()
    }

    override fun getAppLanguage(): String {
        return prefBSRepo.getAppLanguage()?:ENGLISH_LANGUAGE_CODE
    }


}