package com.nrlm.baselinesurvey.ui.start_screen.domain.repository

import androidx.core.net.toUri
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.dao.LanguageListDao
import com.nrlm.baselinesurvey.database.dao.QuestionEntityDao
import com.nrlm.baselinesurvey.database.dao.SectionEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.network.interfaces.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class StartScreenRepositoryImpl @Inject constructor(
    val prefRepo: PrefRepo,
    val surveyeeEntityDao: SurveyeeEntityDao,
): StartScreenRepository {
    override suspend fun getSurveyeeDetails(didiId: Int): SurveyeeEntity {
        return surveyeeEntityDao.getDidi(didiId)
    }

    override suspend fun saveImageLocalPathForSurveyee(surveyeeEntity: SurveyeeEntity, finalPathWithCoordinates: String) {
        surveyeeEntity.didiId?.let {
            surveyeeEntityDao.saveLocalImagePath(
                path = finalPathWithCoordinates,
                didiId = it
            )
        }
    }


}