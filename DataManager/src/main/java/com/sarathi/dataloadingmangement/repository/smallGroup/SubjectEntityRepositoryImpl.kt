package com.sarathi.dataloadingmangement.repository.smallGroup

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.SubjectEntityDao
import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import javax.inject.Inject

class SubjectEntityRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val subjectEntityDao: SubjectEntityDao
) : SubjectEntityRepository {

    override suspend fun getSubjectEntity(subjectId: Int): SubjectEntity {
        return subjectEntityDao.getSubjectForId(
            subjectId,
            coreSharedPrefs.getUniqueUserIdentifier()
        )
    }


}