package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.repository

import com.sarathi.dataloadingmangement.data.entities.SubjectEntity

interface FetchDidiListForSmallGroupFromDbRepository {

    suspend fun getDidiDetailsForSmallGroup(smallGroupId: Int): List<SubjectEntity>

}
