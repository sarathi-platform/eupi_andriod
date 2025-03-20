package com.sarathi.dataloadingmangement.repository.smallGroup

import com.sarathi.dataloadingmangement.model.events.ShgVerificationEventPayloadModel

interface VerificationEventWriterRepository {

    suspend fun getShgVerificationPayloadModel(subjectId: Int): ShgVerificationEventPayloadModel

}