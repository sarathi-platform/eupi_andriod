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
import com.nrlm.baselinesurvey.SUCCESS
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.dao.ActivityTaskDao
import com.nrlm.baselinesurvey.database.dao.LanguageListDao
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.database.entity.ActivityTaskEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.network.interfaces.ApiService
import com.nrlm.baselinesurvey.utils.createMultiLanguageVillageRequest
import javax.inject.Inject

class SurveyeeListScreenRepositoryImpl @Inject constructor(
    private val prefRepo: PrefRepo,
    private val apiService: ApiService,
    private val surveyeeEntityDao: SurveyeeEntityDao,
    private val languageListDao: LanguageListDao,
    private val activityTaskDao: ActivityTaskDao
): SurveyeeListScreenRepository {

    override suspend fun getSurveyeeList(
        missionId: Int,
        activityName: String
    ): List<SurveyeeEntity> {
        val didiList = mutableListOf<SurveyeeEntity>()
        getActivityTasks(missionId = missionId, activityName = activityName).forEach { task ->
            if (surveyeeEntityDao.isDidiExist(task.didiId)) {
                didiList.add(surveyeeEntityDao.getDidi(task.didiId))
            }
        }
        return didiList
    }






    override suspend fun getSurveyeeListFromNetwork(): Boolean {
        try {
            val localLanguageList = languageListDao.getAllLanguages()
            val userViewApiRequest = createMultiLanguageVillageRequest(localLanguageList)
            val userApiResponse = apiService.userAndVillageListAPI(languageId = userViewApiRequest)
            if (userApiResponse.status.equals(SUCCESS, true)) {
                userApiResponse.data?.let {
                    prefRepo.savePref(PREF_KEY_USER_NAME, it.username ?: "")
                    prefRepo.savePref(PREF_KEY_NAME, it.name ?: "")
                    prefRepo.savePref(PREF_KEY_EMAIL, it.email ?: "")
                    prefRepo.savePref(PREF_KEY_IDENTITY_NUMBER, it.identityNumber ?: "")
                    prefRepo.savePref(PREF_KEY_PROFILE_IMAGE, it.profileImage ?: "")
                    prefRepo.savePref(PREF_KEY_ROLE_NAME, it.roleName ?: "")
                    prefRepo.savePref(PREF_KEY_TYPE_NAME, it.typeName ?: "")
                }
                val apiResponse = userApiResponse.data?.username?.toInt()
                    ?.let { apiService.getDidisFromNetwork(userId = it) }
                if (apiResponse?.status?.equals(SUCCESS, false) == true) {
                    if (apiResponse?.data?.didiList != null) {
                        surveyeeEntityDao.deleteSurveyees()
                        apiResponse?.data?.didiList.forEach {
                            val surveyeeEntity = SurveyeeEntity(
                                id = 0,
                                userId = it.userId,
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
        activityName: String
    ): List<ActivityTaskEntity> {
        return activityTaskDao.getActivityTask(missionId, activityName)
    }
}