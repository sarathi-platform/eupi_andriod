package com.sarathi.dataloadingmangement.repository.smallGroup

import com.sarathi.dataloadingmangement.data.entities.SubjectEntity

interface SubjectEntityRepository {
    suspend fun getSubjectEntity(subjectId: Int): SubjectEntity
}