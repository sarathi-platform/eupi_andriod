package com.patsurvey.nudge.activities.ui.progress.domain.repository

interface ChangeUserRepository {

    suspend fun clearDbForUser()
    suspend fun clearPrefsForUser()

}