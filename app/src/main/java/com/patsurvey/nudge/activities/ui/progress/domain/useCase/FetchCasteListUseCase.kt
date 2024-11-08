package com.patsurvey.nudge.activities.ui.progress.domain.useCase

import com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces.FetchCasteListRepository
import com.patsurvey.nudge.utils.SUCCESS
import javax.inject.Inject

class FetchCasteListUseCase @Inject constructor(
    private val fetchCasteListRepository: FetchCasteListRepository,
    private val languageListUseCase: LanguageListUseCase
) {

    suspend operator fun invoke(isRefresh: Boolean) {
        languageListUseCase.invoke().map { it.id }.distinct().apply {
            this.forEach { languageId ->
                val response = fetchCasteListRepository.fetchCasteListFromNetwork(languageId)
                response?.let {

                    if (response.status.equals(SUCCESS, true)) {

                        response.data?.let { casteList ->
                            if (isRefresh) {
                                fetchCasteListRepository.deleteCasteForLanguage(languageId)
                            }

                            casteList.forEach { casteEntity ->
                                casteEntity.languageId = languageId
                            }

                            fetchCasteListRepository.saveCasteListToDb(casteList)
                        }
                    }

                }
            }
        }
    }

}