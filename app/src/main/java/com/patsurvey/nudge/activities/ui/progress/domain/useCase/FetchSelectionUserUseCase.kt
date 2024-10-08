package com.patsurvey.nudge.activities.ui.progress.domain.useCase

import com.sarathi.dataloadingmangement.repository.UserPropertiesRepository

abstract class FetchSelectionUserUseCase(
    private val userPropertiesRepository: UserPropertiesRepository
) {

    abstract suspend fun invoke(
        onComplete: (isSuccess: Boolean) -> Unit,
        isRefresh: Boolean = true
    )

    fun compareWithPreviousUser(): Boolean {
        return userPropertiesRepository.compareWithPreviousUser()
    }

    fun isUserDataLoaded(userType: String): Boolean {
        return userPropertiesRepository.isUserDataLoaded(userType)
    }

    fun getStateId(): Int {
        return userPropertiesRepository.getStateId()
    }
}