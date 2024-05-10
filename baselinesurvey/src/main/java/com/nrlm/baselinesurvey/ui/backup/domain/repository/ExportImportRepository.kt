package com.nrlm.baselinesurvey.ui.backup.domain.repository

interface ExportImportRepository {
  fun clearLocalData()
  fun setAllDataSynced()

    fun getUserMobileNumber():String
    fun getUserID():String
    fun getUserEmail():String
  fun getUserName(): String

}