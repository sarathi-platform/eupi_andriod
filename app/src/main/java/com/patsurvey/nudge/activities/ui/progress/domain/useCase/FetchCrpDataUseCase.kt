package com.patsurvey.nudge.activities.ui.progress.domain.useCase

import com.nudge.core.CRP_USER_TYPE
import com.sarathi.dataloadingmangement.domain.use_case.FetchUserDetailUseCase
import com.sarathi.dataloadingmangement.repository.UserPropertiesRepository
import javax.inject.Inject

class FetchCrpDataUseCase @Inject constructor(
    private val userPropertiesRepository: UserPropertiesRepository,
    private val fetchUserDetailUseCase: FetchUserDetailUseCase,
    private val fetchPatQuestionUseCase: FetchPatQuestionUseCase,
    private val fetchCasteListUseCase: FetchCasteListUseCase,
) : FetchSelectionUserUseCase(userPropertiesRepository) {

    override suspend fun invoke(
        onComplete: (isSuccess: Boolean) -> Unit,
        isRefresh: Boolean
    ) {
        if (isUserDataLoaded(CRP_USER_TYPE)) {
            onComplete(true)
            return
        }

        val isUserDetailsFetched = fetchUserDetailUseCase.invoke()
        if (isUserDetailsFetched) {
            fetchPatQuestionUseCase.invoke()
            fetchCasteListUseCase.invoke()
            onComplete(true)
        } else {
            onComplete(false)
        }
    }

}
