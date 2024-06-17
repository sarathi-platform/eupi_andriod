package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.data.entities.FormUiConfigEntity
import com.sarathi.dataloadingmangement.repository.FormConfigRepositoryImpl
import javax.inject.Inject


class GetFormUiConfigUseCase @Inject constructor(private val formConfigRepositoryImpl: FormConfigRepositoryImpl) {

    suspend fun getFormUiConfig(): List<FormUiConfigEntity> =
        formConfigRepositoryImpl.getFormUiConfig()

}