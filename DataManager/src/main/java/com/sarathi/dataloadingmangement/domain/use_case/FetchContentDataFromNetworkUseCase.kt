package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.preference.CoreSharedPrefs
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
) : BaseApiCallNetworkUseCase() {

    override suspend operator fun invoke(
        screenName: String,
        triggerType: DataLoadingTriggerType,
        moduleName: String,
        customData: Map<String, Any>
    ): Boolean {
        try {
            if (!super.invoke(
                    screenName = screenName,
                    triggerType = triggerType,
                    moduleName = moduleName,
                    customData = customData,
                )
            ) {
                return false
            }
            val contentEntities = mutableListOf<Content>()
            val apiContentResponse =
                repository.fetchContentsFromServer(repository.getAllContentRequest())
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
                    return true
                }
                return false
            } else {
                return false
            }

        } catch (ex: Exception) {
            throw ex
        }
    }

    override fun getApiEndpoint(): String {
        return SUB_PATH_CONTENT_MANAGER
    }


}