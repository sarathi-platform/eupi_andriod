package com.nrlm.baselinesurvey.ui.start_screen.domain.repository

import androidx.lifecycle.LiveData
import com.nrlm.baselinesurvey.PREF_STATE_ID
import com.nrlm.baselinesurvey.PREF_USER_TYPE
import com.nrlm.baselinesurvey.data.prefs.PrefBSRepo
import com.nrlm.baselinesurvey.database.dao.DidiInfoDao
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.database.entity.DidiInfoEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import javax.inject.Inject

class StartScreenRepositoryImpl @Inject constructor(
    val prefBSRepo: PrefBSRepo,
    val surveyeeEntityDao: SurveyeeEntityDao,
    val didiInfoDao: DidiInfoDao
): StartScreenRepository {
    override suspend fun getSurveyeeDetails(didiId: Int): SurveyeeEntity {
        return surveyeeEntityDao.getDidi(didiId)
    }

    override suspend fun getDidiInfoDetails(didiId: Int): DidiInfoEntity? {
        return didiInfoDao.getDidiInfo(userId = getBaseLineUserId(), didiId)
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
        return didiInfoDao.getDidiInfoLive(userId = getBaseLineUserId(), didiId = didiId)
    }

    override fun getStateId(): Int {
        return prefBSRepo.getPref(PREF_STATE_ID, -1)
    }

    override fun getUserType(): String? {
        return prefBSRepo.getPref(PREF_USER_TYPE, "")
    }

    override fun getBaseLineUserId(): String {
        return prefBSRepo.getUniqueUserIdentifier()
    }

    override fun saveTempImagePath(imagePath: String) {
        prefRepo.savePref("temp_image_path", imagePath)


    }

    override fun getTempImagePath(): String {
        return prefRepo.getPref("temp_image_path", "")!!
    }


}