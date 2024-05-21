package com.patsurvey.nudge.model.mapper

import com.sarathi.contentmodule.model.ContentResponse
import com.sarathi.missionactivitytask.data.entities.Content

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