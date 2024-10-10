package com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces

interface ChangeUserRepository {

    suspend fun clearDbForUser()
    suspend fun clearPrefsForUser()
    fun logout()

}