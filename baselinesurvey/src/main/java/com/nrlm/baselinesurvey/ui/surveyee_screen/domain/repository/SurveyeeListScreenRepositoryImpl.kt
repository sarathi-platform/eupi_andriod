package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository

import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.dao.DidiDao
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.network.interfaces.ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SurveyeeListScreenRepositoryImpl @Inject constructor(
    private val prefRepo: PrefRepo,
    private val apiService: ApiService,
    private val didiDao: DidiDao
): SurveyeeListScreenRepository {

    override fun getSurveyeeList(): Flow<List<SurveyeeEntity>> {
        return didiDao.getAllDidis()
    }




}