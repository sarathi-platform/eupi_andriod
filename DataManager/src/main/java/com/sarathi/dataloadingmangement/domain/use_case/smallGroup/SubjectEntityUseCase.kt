package com.sarathi.dataloadingmangement.domain.use_case.smallGroup

import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import com.sarathi.dataloadingmangement.repository.smallGroup.SubjectEntityRepository
import javax.inject.Inject

class SubjectEntityUseCase @Inject constructor(
    private val subjectEntityRepository: SubjectEntityRepository
) {

    suspend fun getSubjectEntity(subjectId: Int): SubjectEntity {
        return subjectEntityRepository.getSubjectEntity(subjectId)
    }

}