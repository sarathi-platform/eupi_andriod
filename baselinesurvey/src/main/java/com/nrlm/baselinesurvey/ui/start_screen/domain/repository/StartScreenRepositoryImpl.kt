package com.nrlm.baselinesurvey.ui.start_screen.domain.repository

import androidx.lifecycle.LiveData
import com.nrlm.baselinesurvey.PREF_STATE_ID
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
        return surveyeeEntityDao.getDidi(didiId)
    }

    override suspend fun getDidiInfoDetails(didiId: Int): DidiInfoEntity {
        return didiInfoDao.getDidiInfo(didiId)
    }

    override suspend fun saveImageLocalPathForSurveyee(
        surveyeeEntity: SurveyeeEntity,
        finalPathWithCoordinates: String
    ) {
        surveyeeEntity.didiId?.let {
            surveyeeEntityDao.saveLocalImagePath(
                path = finalPathWithCoordinates,
                didiId = it
            )
        }
    }

    override suspend fun getDidiInfoObjectLive(didiId: Int): LiveData<List<DidiInfoEntity>> {
        return didiInfoDao.getDidiInfoLive(didiId)
    }

    override fun getStateId(): Int {
        return prefRepo.getPref(PREF_STATE_ID, -1)
    }


}