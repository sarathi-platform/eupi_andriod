package com.patsurvey.nudge.activities.sync.home.domain.use_case

import com.patsurvey.nudge.activities.sync.home.domain.repository.SyncHomeRepository

class GetUserDetailsSyncUseCase(
    private val repository: SyncHomeRepository
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

    fun getLoggedInUserType():String{
        return repository.getLoggedInUserType()
    }

}