package com.nrlm.baselinesurvey.model.mappers

import com.nrlm.baselinesurvey.database.entity.ContentEntity
import com.nrlm.baselinesurvey.model.response.ContentResponse

object ContentEntityMapper {
    fun getContentEntity(
        contentResponse: ContentResponse
    ): ContentEntity {
        return ContentEntity(
            contentKey = contentResponse.contentKey,
            contentId = 0,
            contentValue = contentResponse.contentValue,
            contentType = contentResponse.contentType,
            languageCode = contentResponse.languageCode
        )
    }
}