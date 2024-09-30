package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.useCase

import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.repository.FetchDidiListForSmallGroupFromDbRepository
import javax.inject.Inject

class FetchDidiListForSmallGroupFromDbUseCase @Inject constructor(
    private val fetchDidiListForSmallGroupFromDbRepository: FetchDidiListForSmallGroupFromDbRepository,
) {

    suspend operator fun invoke(smallGroupId: Int): List<SubjectEntity> {

        return fetchDidiListForSmallGroupFromDbRepository.getDidiDetailsForSmallGroup(smallGroupId)
            .sortedBy { it.subjectName }

    }

}
