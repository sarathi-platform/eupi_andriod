package com.nrlm.baselinesurvey.ui.start_screen.domain.use_case

import androidx.lifecycle.LiveData
import com.nrlm.baselinesurvey.database.entity.DidiInfoEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.ui.start_screen.domain.repository.StartScreenRepository

class GetSurveyeeDetailsUserCase (private val repository: StartScreenRepository) {

    suspend fun invoke(didiId: Int): SurveyeeEntity {
        return repository.getSurveyeeDetails(didiId)
    }

    suspend fun getDidiIndoDetail(didiId: Int): DidiInfoEntity {
        return repository.getDidiInfoDetails(didiId)
    }

    suspend fun getDidiInfoObjectLive(didiId: Int): LiveData<List<DidiInfoEntity>> {
        return repository.getDidiInfoObjectLive(didiId)
    }

    fun getStateId(): Int {
        return repository.getStateId()
    }

    fun getUserType(): String? {
        return repository.getUserType()
    }

}
