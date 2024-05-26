package com.sarathi.dataloadingmangement.model.mapper

import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.network.response.ContentResponse

object ContentMapper {
    fun getContent(
        contentResponse: ContentResponse
    ): Content {
        return Content(
            contentKey = contentResponse.contentKey,
            contentId = 0,
            contentValue = contentResponse.contentValue,
            contentType = contentResponse.contentType,
            languageCode = contentResponse.languageCode,
            isDownload = 1,
            thumbUrl = ""
        )
    }
}