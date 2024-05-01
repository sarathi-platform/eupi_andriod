package com.patsurvey.nudge.activities.backup.domain.use_case

import com.patsurvey.nudge.activities.backup.domain.repository.ExportImportRepository


class GetUserDetailsExportUseCase(
    private val repository: ExportImportRepository
) {

    fun getUserMobileNumber():String{
        return repository.getUserMobileNumber()
    }
    fun getUserID():String{
        return repository.getUserID()
    }

    fun getUserEmail():String{
        return repository.getUserEmail()
    }

    fun getUserName():String{
        return repository.getUserName()
    }


}