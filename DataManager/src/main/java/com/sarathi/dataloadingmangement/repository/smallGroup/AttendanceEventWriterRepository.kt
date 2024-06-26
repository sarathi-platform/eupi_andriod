package com.sarathi.dataloadingmangement.repository.smallGroup

import com.sarathi.dataloadingmangement.data.entities.smallGroup.SmallGroupDidiMappingEntity
import com.sarathi.dataloadingmangement.model.events.SaveAttendanceEventDto
import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupAttendanceHistoryModel
import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupSubTabUiModel

interface AttendanceEventWriterRepository {

    suspend fun getAllActiveAttendanceForUser(): List<SaveAttendanceEventDto>
    suspend fun getAllDeletedAttendanceForUser(): List<SaveAttendanceEventDto>
    suspend fun fetchSubjectIdsForSmallGroup(smallGroupId: Int): List<SmallGroupDidiMappingEntity>

    suspend fun getSmallGroupsForUser(userId: String): List<SmallGroupSubTabUiModel>

    fun getSaveAttendanceEventList(
        smallGroupId: Int,
        historyAttributesForSubjectMapEntry: Map.Entry<Int, List<SmallGroupAttendanceHistoryModel>>
    ): List<SaveAttendanceEventDto>

    fun getDeleteAttendanceEventList(
        smallGroupId: Int,
        historyAttributesForSubjectMapEntry: Map.Entry<Int, List<SmallGroupAttendanceHistoryModel>>
    ): List<SaveAttendanceEventDto>

}
