package com.sarathi.dataloadingmangement.repository.smallGroup

import com.nudge.core.ATTENDANCE_DELETED
import com.nudge.core.ATTENDANCE_TAG_ID
import com.nudge.core.YYYY_MM_DD
import com.nudge.core.enums.AttributesType
import com.nudge.core.enums.PayloadType
import com.nudge.core.enums.SubjectType
import com.nudge.core.enums.ValueTypes.Companion.convertToDataType
import com.nudge.core.getAttendanceFromBoolean
import com.nudge.core.getBooleanValueFromAttendance
import com.nudge.core.getCurrentTimeInMillis
import com.nudge.core.getDate
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.SubjectAttributeDao
import com.sarathi.dataloadingmangement.data.dao.SubjectEntityDao
import com.sarathi.dataloadingmangement.data.dao.smallGroup.SmallGroupDidiMappingDao
import com.sarathi.dataloadingmangement.data.entities.smallGroup.SmallGroupDidiMappingEntity
import com.sarathi.dataloadingmangement.model.events.PayloadData
import com.sarathi.dataloadingmangement.model.events.SaveAttendanceEventDto
import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupAttendanceHistoryModel
import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupSubTabUiModel
import javax.inject.Inject

class AttendanceEventWriterRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val smallGroupDidiMappingDao: SmallGroupDidiMappingDao,
    private val subjectEntityDao: SubjectEntityDao,
    private val subjectAttributeDao: SubjectAttributeDao
) : AttendanceEventWriterRepository {

    override suspend fun getAllActiveAttendanceForUser(): List<SaveAttendanceEventDto> {
        val eventList = ArrayList<SaveAttendanceEventDto>()
        val userId = coreSharedPrefs.getUniqueUserIdentifier()
        val smallGroupList = getSmallGroupsForUser(userId)
        if (smallGroupList.isNotEmpty()) {
            smallGroupList.forEach { smallGroupItem ->
                val smallGroupDidiMapping =
                    fetchSubjectIdsForSmallGroup(smallGroupItem.smallGroupId)
                getAttendanceEvent(
                    smallGroupDidiMapping = smallGroupDidiMapping,
                    userId = userId,
                    eventList = eventList,
                    smallGroupItem = smallGroupItem, isDeleteEvent = false
                )
            }
        }
        return eventList
    }

    private fun getAttendanceEvent(
        smallGroupDidiMapping: List<SmallGroupDidiMappingEntity>,
        userId: String,
        eventList: ArrayList<SaveAttendanceEventDto>,
        smallGroupItem: SmallGroupSubTabUiModel,
        isDeleteEvent: Boolean
    ) {
        val subjectIds = smallGroupDidiMapping.map { it.didiId }
        val smallGroupAttendanceHistory =
            if (isDeleteEvent)
                subjectAttributeDao.getDeletedAttendanceListForSubjects(userId, subjectIds)
            else
                subjectAttributeDao.getAllActiveSmallGroupAttendanceHistory(userId, subjectIds)
        val historyAttributesForSubjectMap = smallGroupAttendanceHistory.groupBy { it.subjectId }
        historyAttributesForSubjectMap.forEach { historyAttributesForSubjectMapEntry ->
            eventList.addAll(
                if (isDeleteEvent) {
                    getDeleteAttendanceEventList(
                        smallGroupId = smallGroupItem.smallGroupId,
                        historyAttributesForSubjectMapEntry = historyAttributesForSubjectMapEntry
                    )
                } else {
                    getSaveAttendanceEventList(
                        smallGroupId = smallGroupItem.smallGroupId,
                        historyAttributesForSubjectMapEntry = historyAttributesForSubjectMapEntry
                    )
                }
            )
        }
    }

    override suspend fun getAllDeletedAttendanceForUser(): List<SaveAttendanceEventDto> {
        val eventList = ArrayList<SaveAttendanceEventDto>()
        val userId = coreSharedPrefs.getUniqueUserIdentifier()
        val smallGroupList = getSmallGroupsForUser(userId)
        if (smallGroupList.isNotEmpty()) {
            smallGroupList.forEach { smallGroupItem ->
                val smallGroupDidiMapping =
                    fetchSubjectIdsForSmallGroup(smallGroupItem.smallGroupId)
                getAttendanceEvent(
                    smallGroupDidiMapping = smallGroupDidiMapping,
                    userId = userId,
                    eventList = eventList,
                    smallGroupItem = smallGroupItem, isDeleteEvent = true
                )
            }
        }
        return eventList

    }

    override suspend fun getSmallGroupsForUser(userId: String): List<SmallGroupSubTabUiModel> {
        return smallGroupDidiMappingDao.getAllMappingForUserByDate(userId = userId)
    }

    override suspend fun fetchSubjectIdsForSmallGroup(smallGroupId: Int): List<SmallGroupDidiMappingEntity> {
        val uniqueUserId = coreSharedPrefs.getUniqueUserIdentifier()
        return smallGroupDidiMappingDao.getAllLatestMappingForSmallGroup(
            userId = uniqueUserId,
            smallGroupId
        )
    }

    override fun getSaveAttendanceEventList(
        smallGroupId: Int,
        historyAttributesForSubjectMapEntry: Map.Entry<Int, List<SmallGroupAttendanceHistoryModel>>
    ): List<SaveAttendanceEventDto> {
        val eventList = ArrayList<SaveAttendanceEventDto>()
        val dateMap = historyAttributesForSubjectMapEntry.value.groupBy { it.date }

        dateMap.forEach { dateMapItem ->
            val saveAttendanceEventDto = getSaveAttendanceEventDto(
                dateMapItem,
                smallGroupId,
                historyAttributesForSubjectMapEntry,
                isDeleteEvent = false
            )
            eventList.add(saveAttendanceEventDto)
        }

        return eventList
    }

    private fun getSaveAttendanceEventDto(
        dateMapItem: Map.Entry<String, List<SmallGroupAttendanceHistoryModel>>,
        smallGroupId: Int,
        historyAttributesForSubjectMapEntry: Map.Entry<Int, List<SmallGroupAttendanceHistoryModel>>,
        isDeleteEvent: Boolean
    ): SaveAttendanceEventDto {
        val attendanceAttribute =
            dateMapItem.value.find { it.key == AttributesType.ATTRIBUTE_ATTENDANCE.attributeType }
        val attendance = attendanceAttribute?.value.getBooleanValueFromAttendance()

        val dateAttribute =
            dateMapItem.value.find { it.key == AttributesType.ATTRIBUTE_ATTENDANCE_DATE.attributeType }
        val date = dateAttribute?.value?.convertToDataType(dateAttribute.valueType) as Long

        val payloadData = PayloadData(
            date = date.getDate(YYYY_MM_DD),
            id = smallGroupId.toString(),
            value = if (isDeleteEvent) ATTENDANCE_DELETED else attendance.getAttendanceFromBoolean()
        )

        return SaveAttendanceEventDto(
            dateCreated = getCurrentTimeInMillis(),
            languageId = coreSharedPrefs.getSelectedLanguageId(),
            subjectId = historyAttributesForSubjectMapEntry.key,
            subjectType = SubjectType.SUBJECT_TYPE_DIDI.subjectName,
            tagId = ATTENDANCE_TAG_ID,
            payloadType = PayloadType.PAYLOAD_TYPE_ATTENDANCE.payloadType,
            payloadData = payloadData
        )
    }

    override fun getDeleteAttendanceEventList(
        smallGroupId: Int,
        historyAttributesForSubjectMapEntry: Map.Entry<Int, List<SmallGroupAttendanceHistoryModel>>
    ): List<SaveAttendanceEventDto> {
        val eventList = ArrayList<SaveAttendanceEventDto>()
        val dateMap = historyAttributesForSubjectMapEntry.value.groupBy { it.date }

        dateMap.forEach { dateMapItem ->
            val saveAttendanceEventDto = getSaveAttendanceEventDto(
                dateMapItem = dateMapItem,
                smallGroupId = smallGroupId,
                historyAttributesForSubjectMapEntry = historyAttributesForSubjectMapEntry,
                isDeleteEvent = true
            )
            eventList.add(saveAttendanceEventDto)
        }

        return eventList
    }


}