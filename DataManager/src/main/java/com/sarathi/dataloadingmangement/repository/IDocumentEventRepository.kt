package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.model.events.SaveDocumentEventDto

interface IDocumentEventRepository {

    fun getSaveDocumentEventDto(
        generatedDate: String,
        documentType: String,
        documentName: String,
        activityId: Int,
    ): SaveDocumentEventDto

}