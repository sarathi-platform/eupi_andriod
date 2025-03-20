package com.sarathi.dataloadingmangement.repository.smallGroup

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.SubjectEntityDao
import com.sarathi.dataloadingmangement.model.events.ShgVerificationEventPayloadModel
import javax.inject.Inject

class VerificationEventWriterRepositoryImpl @Inject constructor(
    val coreSharedPrefs: CoreSharedPrefs,
    val subjectEntityDao: SubjectEntityDao
) : VerificationEventWriterRepository {
    override suspend fun getShgVerificationPayloadModel(subjectId: Int): ShgVerificationEventPayloadModel {

        val subjectEntity =
            subjectEntityDao.getSubjectForId(subjectId, coreSharedPrefs.getUniqueUserIdentifier())

        val shgVerificationEventPayloadModel = ShgVerificationEventPayloadModel(
            subjectId = subjectId,
            shgVerificationStatus = subjectEntity.shgVerificationStatus,
            shgVerificationDate = subjectEntity.shgVerificationDate,
            shgMemberId = subjectEntity.shgMemberId,
            shgName = subjectEntity.shgCode,
            shgCode = subjectEntity.shgCode
        )

        return shgVerificationEventPayloadModel
    }
}