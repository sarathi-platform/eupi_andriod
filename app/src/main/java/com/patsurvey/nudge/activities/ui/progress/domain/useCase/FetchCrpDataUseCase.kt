package com.patsurvey.nudge.activities.ui.progress.domain.useCase

import com.nudge.core.CRP_USER_TYPE
import com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces.FetchSelectionUserDataRepository
import com.sarathi.dataloadingmangement.repository.UserPropertiesRepository
import javax.inject.Inject

class FetchCrpDataUseCase @Inject constructor(
    private val userPropertiesRepository: UserPropertiesRepository,
    private val fetchSelectionUserDataRepository: FetchSelectionUserDataRepository,
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

        val localLanguageList = fetchSelectionUserDataRepository.fetchLanguage()
        val userViewApiRequest = createMultiLanguageVillageRequest(localLanguageList)

        val isUserDetailsFetched =
            fetchSelectionUserDataRepository.fetchAndSaveUserDetailsAndVillageListFromNetwork(
                userViewApiRequest
            )
        if (isUserDetailsFetched) {
            fetchPatQuestionUseCase.invoke(isRefresh)
            fetchCasteListUseCase.invoke(isRefresh)
            onComplete(true)
        } else {
            onComplete(false)
        }
    }


}
