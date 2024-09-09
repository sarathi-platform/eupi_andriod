package com.nudge.syncmanager.domain.usecase

import com.nudge.syncmanager.domain.repository.SyncRepository


class GetUserDetailsSyncRepoUseCase(
    private val repository: SyncRepository
) {

    fun getUserMobileNumber(): String {
        return repository.getUserMobileNumber()
    }

    fun getUserID(): String {
        return repository.getUserID()
    }

    fun getUserEmail(): String {
        return repository.getUserEmail()
    }

    fun getUserName(): String {
        return repository.getUserName()
    }

    fun getLoggedInUserType(): String {
        return repository.getLoggedInUserType()
    }
}