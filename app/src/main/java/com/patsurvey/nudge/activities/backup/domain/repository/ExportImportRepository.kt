package com.patsurvey.nudge.activities.backup.domain.repository

interface ExportImportRepository {
  fun clearLocalData()
  fun setAllDataSynced()

  fun getUserMobileNumber(): String
  fun getUserID(): String
  fun getUserEmail(): String
  fun getUserName(): String
  fun clearSelectionLocalDB()

  fun getLoggedInUserType():String


}