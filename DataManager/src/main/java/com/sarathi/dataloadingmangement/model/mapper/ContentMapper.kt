package com.sarathi.dataloadingmangement.model.mapper

import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.network.response.ContentResponse

object ContentMapper {
    fun getContent(
        contentResponse: ContentResponse,
        userId: String
    ): Content {
        return Content(
            contentKey = contentResponse.contentKey,
            contentId = contentResponse.contentId,
            contentName = contentResponse.contentKey ?: BLANK_STRING,
            contentValue = contentResponse.contentValue,
            contentType = contentResponse.contentType,
            languageCode = contentResponse.languageCode,
            isDownload = 1,
            thumbUrl = contentResponse.thumbnail ?: BLANK_STRING,
            id = 0,
            userId = userId
        )
    }
}