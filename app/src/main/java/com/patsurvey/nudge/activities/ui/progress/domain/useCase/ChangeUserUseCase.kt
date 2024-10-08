package com.patsurvey.nudge.activities.ui.progress.domain.useCase

import com.patsurvey.nudge.activities.ui.progress.domain.repository.ChangeUserRepository
import javax.inject.Inject

class ChangeUserUseCase @Inject constructor(
    private val changeUserRepository: ChangeUserRepository
) {

    suspend operator fun invoke(result: () -> Unit) {
        changeUserRepository.clearDbForUser()
        changeUserRepository.clearPrefsForUser()
        result()
    }

}