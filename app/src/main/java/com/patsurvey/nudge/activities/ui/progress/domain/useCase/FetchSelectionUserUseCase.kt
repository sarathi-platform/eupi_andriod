package com.patsurvey.nudge.activities.ui.progress.domain.useCase

import com.patsurvey.nudge.database.LanguageEntity
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

    fun isUserBpc(): Boolean {
        return userPropertiesRepository.isUserBpc()
    }

    fun getAppLanguage(): String {
        return userPropertiesRepository.getAppLanguage()
    }

    fun createMultiLanguageVillageRequest(localLanguageList: List<LanguageEntity>): String {
        var requestString: StringBuilder = StringBuilder()
        var request: String = "2"
        if (localLanguageList.isNotEmpty()) {
            localLanguageList.forEach {
                requestString.append("${it.id}-")
            }
        } else request = "2"
        if (requestString.contains("-")) {
            request = requestString.substring(0, requestString.length - 1)
        }
        return request
    }
}