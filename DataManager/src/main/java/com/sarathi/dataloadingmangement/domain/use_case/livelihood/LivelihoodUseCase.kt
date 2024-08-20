package com.sarathi.dataloadingmangement.domain.use_case.livelihood

import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.enums.LivelihoodLanguageReferenceType
import com.sarathi.dataloadingmangement.model.response.LivelihoodResponse
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.repository.liveihood.ICoreLivelihoodRepository
import javax.inject.Inject

class LivelihoodUseCase @Inject constructor(
    private val coreLivelihoodRepositoryImpl: ICoreLivelihoodRepository
) {
    suspend operator fun invoke(): Boolean {


        try {
            val apiResponse = coreLivelihoodRepositoryImpl.getLivelihoodConfigFromNetwork()
            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let {
                    coreLivelihoodRepositoryImpl.deleteLivelihoodCoreDataForUser()
                    saveLivelihoodConfigInDb(it)
                }
                return true
            } else {
                return false
            }

        } catch (apiException: ApiException) {
            throw apiException
        } catch (ex: Exception) {
            throw ex
        }


    }

    private suspend fun saveLivelihoodConfigInDb(livelihoodResponses: List<LivelihoodResponse>) {
        livelihoodResponses.forEach { livelihoodResponse ->
            livelihoodResponse.livelihood?.let { livelihood ->
                coreLivelihoodRepositoryImpl.saveLivelihoodItemToDB(
                    livelihood,
                    LivelihoodLanguageReferenceType.Livelihood.name
                )
            }
            livelihoodResponse.assets?.let { assets ->
                livelihoodResponse.livelihood?.id?.let {
                    coreLivelihoodRepositoryImpl.saveLivelihoodItemListToDB(
                        assets,
                        it
                    )
                }
            }
            livelihoodResponse.products?.let { products ->
                livelihoodResponse.livelihood?.id?.let {
                    coreLivelihoodRepositoryImpl.saveLivelihoodItemListToDB(
                        products,
                        it
                    )
                }
            }
            livelihoodResponse.events?.let { events ->
                livelihoodResponse.livelihood?.id?.let {
                    coreLivelihoodRepositoryImpl.saveLivelihoodItemListToDB(
                        events,
                        it
                    )
                }
            }

        }
    }
}