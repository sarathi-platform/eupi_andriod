package com.patsurvey.nudge.activities.backup.domain.repository

import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel

interface ExportImportRepository {
  fun clearLocalData()
  fun setAllDataSynced()

  fun getUserMobileNumber(): String
  fun getUserID(): String
  fun getUserEmail(): String
  fun getUserName(): String
  fun clearSelectionLocalDB()
  fun clearAPIStatusTableData()
  fun getLoggedInUserType():String
  fun getStateId(): Int
  suspend fun fetchMissionsForUser(): List<MissionUiModel>

}