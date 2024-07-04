package com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.domain.repository

import com.nudge.core.enums.AttributesType
import com.nudge.core.enums.ValueTypes.Companion.convertToDataType
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.value
import com.sarathi.dataloadingmangement.data.dao.AttributeValueReferenceDao
import com.sarathi.dataloadingmangement.data.dao.SubjectAttributeDao
import com.sarathi.dataloadingmangement.data.dao.SubjectEntityDao
import com.sarathi.dataloadingmangement.data.dao.smallGroup.SmallGroupDidiMappingDao
import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupAttendanceHistoryModel
import com.sarathi.smallgroupmodule.data.model.SubjectAttendanceHistoryState
import com.sarathi.smallgroupmodule.utils.getBooleanValueFromAttendance
import javax.inject.Inject

class FetchSmallGroupAttendanceHistoryFromDbRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val smallGroupDidiMappingDao: SmallGroupDidiMappingDao,
    private val subjectEntityDao: SubjectEntityDao,
    private val subjectAttributeDao: SubjectAttributeDao,
    private val attributeValueReferenceDao: AttributeValueReferenceDao
) : FetchSmallGroupAttendanceHistoryFromDbRepository {
    override suspend fun fetchSmallGroupAttendanceHistoryFromDb(
        smallGroupId: Int,
        dateRange: Pair<Long, Long>
    ): List<SubjectAttendanceHistoryState> {
        val subjectIds = fetchSubjectIdsForSmallGroup(smallGroupId)
        val subjectEntityList = fetchSubjectDetailsForSmallGroup(subjectIds)
        val subjectEntityListMap = subjectEntityList.associateBy { it.subjectId }

        val smallGroupAttendanceHistory =
            fetchSmallGroupHistory(smallGroupId, subjectIds, dateRange)
        val historyAttributesForSubjects = smallGroupAttendanceHistory.groupBy { it.subjectId }

        val subjectAttendanceHistoryStateList = ArrayList<SubjectAttendanceHistoryState>()

        historyAttributesForSubjects.forEach { historyAttributesForSubject ->

            subjectAttendanceHistoryStateList.addAll(
                getSubjectAttendanceHistoryState(
                    subjectEntityListMap,
                    historyAttributesForSubject
                )
            )
        }

        return subjectAttendanceHistoryStateList
    }

    private fun getSubjectAttendanceHistoryState(
        subjectEntityListMap: Map<Int?, SubjectEntity>,
        historyAttributesForSubject: Map.Entry<Int, List<SmallGroupAttendanceHistoryModel>>
    ): List<SubjectAttendanceHistoryState> {
        val completeSubjectHistory = ArrayList<SubjectAttendanceHistoryState>()
        val dateMap = historyAttributesForSubject.value.groupBy { it.date }

        dateMap.forEach { dateMapItem ->
            val attendanceAttribute =
                dateMapItem.value.find { it.key == AttributesType.ATTRIBUTE_ATTENDANCE.attributeType }
            val attendance = attendanceAttribute?.value?.getBooleanValueFromAttendance().value()

            val dateAttribute =
                dateMapItem.value.find { it.key == AttributesType.ATTRIBUTE_ATTENDANCE_DATE.attributeType }
            val date = dateAttribute?.value?.convertToDataType(dateAttribute.valueType) as Long

            completeSubjectHistory.add(
                SubjectAttendanceHistoryState(
                    subjectId = historyAttributesForSubject.key,
                    subjectEntity = subjectEntityListMap[historyAttributesForSubject.key]!!,
                    attendance = attendance,
                    date = date
                )
            )
        }

        return completeSubjectHistory
    }

    override suspend fun fetchSmallGroupHistory(
        smallGroupId: Int,
        subjectIds: List<Int>,
        dateRange: Pair<Long, Long>
    ): List<SmallGroupAttendanceHistoryModel> {
        val uniqueUserId = coreSharedPrefs.getUniqueUserIdentifier()
        return subjectAttributeDao.getSmallGroupAttendanceHistoryForRange(
            uniqueUserId,
            subjectIds,
            dateRange.first,
            dateRange.second
        )
    }

    override suspend fun fetchSubjectDetailsForSmallGroup(subjectIds: List<Int>): List<SubjectEntity> {
        val uniqueUserId = coreSharedPrefs.getUniqueUserIdentifier()
        return subjectEntityDao.getAllSubjectForIds(uniqueUserId, subjectIds)
    }

    override suspend fun fetchSubjectIdsForSmallGroup(smallGroupId: Int): List<Int> {
        val uniqueUserId = coreSharedPrefs.getUniqueUserIdentifier()
        return smallGroupDidiMappingDao.getAllLatestMappingForSmallGroup(
            userId = uniqueUserId,
            smallGroupId = smallGroupId
        ).map { it.didiId }
    }


}