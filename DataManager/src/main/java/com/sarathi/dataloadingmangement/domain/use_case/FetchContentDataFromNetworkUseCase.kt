package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.SUCCESS_CODE
import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.model.mapper.ContentMapper
import com.sarathi.dataloadingmangement.repository.IContentRepository
import javax.inject.Inject


class FetchContentDataFromNetworkUseCase @Inject constructor(
    private val repository: IContentRepository,
) {
    suspend fun invoke(): Boolean {
        try {
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
                        contentEntities.add(ContentMapper.getContent(content))
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


}