package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.data.entities.FormUiConfigEntity
import com.sarathi.dataloadingmangement.repository.FormConfigRepositoryImpl
import javax.inject.Inject


class GetFormUiConfigUseCase @Inject constructor(private val formConfigRepositoryImpl: FormConfigRepositoryImpl) {

    suspend fun getFormUiConfig(activityId: Int, missionId: Int): List<FormUiConfigEntity> =
        formConfigRepositoryImpl.getFormUiConfig(activityId = activityId, missionId = missionId)


    suspend fun getFormConfigValue(key: String, activityId: Int, missionId: Int): String {
        return formConfigRepositoryImpl.getFormUiValue(
            activityId = activityId,
            missionId = missionId,
            key = key
        )
    }
}