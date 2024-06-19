package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.FormUiConfigDao
import com.sarathi.dataloadingmangement.data.entities.FormUiConfigEntity
import javax.inject.Inject

class FormConfigRepositoryImpl @Inject constructor(
    private val formUiConfigDao: FormUiConfigDao,
    private val coreSharedPrefs: CoreSharedPrefs
) : IFormConfigRepository {

    override suspend fun getFormUiConfig(): List<FormUiConfigEntity> {
        return formUiConfigDao.getActivityFormUiConfig(uniqueUserIdentifier = coreSharedPrefs.getUniqueUserIdentifier())
    }


}
