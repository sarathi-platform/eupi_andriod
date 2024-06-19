package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.model.events.SaveDocumentEventDto
import javax.inject.Inject

class DocumentEventRepositoryImpl @Inject constructor(val coreSharedPrefs: CoreSharedPrefs) :
    IDocumentEventRepository {
    override fun getSaveDocumentEventDto(
        generatedDate: String,
        documentType: String,
        documentName: String
    ): SaveDocumentEventDto {

        return SaveDocumentEventDto(
            generatedDate = generatedDate,
            documentType = documentType,
            documentName = documentName,
            doerId = coreSharedPrefs.getUserName().toInt(),
            doerType = "UPCM"
        )
    }

}