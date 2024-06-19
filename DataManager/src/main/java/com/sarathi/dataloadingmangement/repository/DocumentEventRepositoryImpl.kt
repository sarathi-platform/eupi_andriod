package com.sarathi.dataloadingmangement.repository

import com.nudge.core.generateUUID
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.model.events.SaveDocumentEventDto
import javax.inject.Inject

class DocumentEventRepositoryImpl @Inject constructor(val coreSharedPrefs: CoreSharedPrefs) :
    IDocumentEventRepository {
    override fun getSaveDocumentEventDto(
        generatedDate: String,
        documentType: String,
        documentName: String,
        activityId: Int,

    ): SaveDocumentEventDto {

        return SaveDocumentEventDto(
            generatedDate = generatedDate,
            documentType = documentType,
            documentName = documentName,
            doerId = coreSharedPrefs.getUserName().toInt(),
            doerType = "UPCM",
            activityId = activityId,
            documentId = generateUUID()

        )
    }

}