package com.nrlm.baselinesurvey.ui.start_screen.domain.repository

import androidx.lifecycle.LiveData
import com.nrlm.baselinesurvey.PREF_KEY_USER_NAME
import com.nrlm.baselinesurvey.PREF_STATE_ID
import com.nrlm.baselinesurvey.PREF_USER_TYPE
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.dao.DidiInfoDao
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.database.entity.DidiInfoEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import javax.inject.Inject

class StartScreenRepositoryImpl @Inject constructor(
    val prefRepo: PrefRepo,
    val surveyeeEntityDao: SurveyeeEntityDao,
    val didiInfoDao: DidiInfoDao
): StartScreenRepository {
    override suspend fun getSurveyeeDetails(didiId: Int): SurveyeeEntity {
        return surveyeeEntityDao.getDidi(didiId, getUserId())
    }

    override suspend fun getDidiInfoDetails(didiId: Int): DidiInfoEntity {
        return didiInfoDao.getDidiInfo(getUserId(), didiId)
    }

    override suspend fun saveImageLocalPathForSurveyee(
        surveyeeEntity: SurveyeeEntity,
        finalPathWithCoordinates: String
    ) {
        surveyeeEntity.didiId?.let {
            surveyeeEntityDao.saveLocalImagePath(
                path = finalPathWithCoordinates,
                didiId = it,
                userId = getUserId()
            )
        }
    }

    override suspend fun getDidiInfoObjectLive(didiId: Int): LiveData<List<DidiInfoEntity>> {
        return didiInfoDao.getDidiInfoLive(didiId)
    }

    override fun getStateId(): Int {
        return prefRepo.getPref(PREF_STATE_ID, -1)
    }

    override fun getUserType(): String? {
        return prefRepo.getPref(PREF_USER_TYPE, "")
    }

    override fun getUserId(): Int {
        return prefRepo.getPref(PREF_KEY_USER_NAME, "")?.toInt() ?: 0
    }

}