package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.utils.CoreLogger
import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.SUCCESS_CODE
import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.model.mapper.ContentMapper
import com.sarathi.dataloadingmangement.network.SUB_PATH_CONTENT_MANAGER
import com.sarathi.dataloadingmangement.repository.IContentRepository
import javax.inject.Inject


class FetchContentDataFromNetworkUseCase @Inject constructor(
    private val repository: IContentRepository,
    private val coreSharedPrefs: CoreSharedPrefs,
) {
    suspend fun invoke(): Boolean {
        try {
            val startTime = System.currentTimeMillis()


            val contentEntities = mutableListOf<Content>()
            val apiContentResponse =
                repository.fetchContentsFromServer(repository.getAllContentRequest())
            CoreLogger.d(
                tag = "LazyLoadAnalysis",
                msg = "FetchContentDataFromNetworkUseCase :$SUB_PATH_CONTENT_MANAGER  : ${System.currentTimeMillis() - startTime}"
            )

            if (apiContentResponse.status.equals(
                    SUCCESS_CODE,
                    true
                ) || apiContentResponse.status.equals(SUCCESS, true)
            ) {
                apiContentResponse.data?.let { contentResponse ->
                    repository.deleteContentFromDB()
                    for (content in contentResponse) {
                        contentEntities.add(
                            ContentMapper.getContent(
                                content,
                                coreSharedPrefs.getUniqueUserIdentifier()
                            )
                        )
                    }
                    repository.saveContentToDB(contentEntities)
                    CoreLogger.d(
                        tag = "LazyLoadAnalysis",
                        msg = "FetchContentDataFromNetworkUseCase: ${System.currentTimeMillis() - startTime}"
                    )
                    return true
                }
                CoreLogger.d(
                    tag = "LazyLoadAnalysis",
                    msg = "FetchContentDataFromNetworkUseCase: ${System.currentTimeMillis() - startTime}"
                )
                return false
            } else {
                CoreLogger.d(
                    tag = "LazyLoadAnalysis",
                    msg = "FetchContentDataFromNetworkUseCase: ${System.currentTimeMillis() - startTime}"
                )
                return false
            }

        } catch (ex: Exception) {
            throw ex
        }
    }


}