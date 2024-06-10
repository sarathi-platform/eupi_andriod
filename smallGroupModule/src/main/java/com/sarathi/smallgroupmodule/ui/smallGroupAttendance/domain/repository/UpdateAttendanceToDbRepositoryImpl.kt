package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.repository

import com.nudge.core.ATTENDANCE_TAG_ID
import com.nudge.core.enums.AttributesType
import com.nudge.core.enums.SubjectType
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.AttributeValueReferenceDao
import com.sarathi.dataloadingmangement.data.dao.SubjectAttributeDao
import com.sarathi.dataloadingmangement.data.dao.SubjectEntityDao
import com.sarathi.dataloadingmangement.data.entities.SubjectAttributeEntity
import com.sarathi.smallgroupmodule.data.model.SubjectAttendanceState
import com.sarathi.smallgroupmodule.utils.getAllAttendanceAttributeValueReferencesForSubject
import javax.inject.Inject

class UpdateAttendanceToDbRepositoryImpl @Inject constructor(
    val coreSharedPrefs: CoreSharedPrefs,
    val subjectEntityDao: SubjectEntityDao,
    val subjectAttributeDao: SubjectAttributeDao,
    val attributeValueReferenceDao: AttributeValueReferenceDao
) : UpdateAttendanceToDbRepository {
    override suspend fun updateFinalAttendanceToDb(
        finalAttendanceStateList: List<SubjectAttendanceState>,
        selectedDate: Long
    ) {
        removeOldAttendanceForDate(finalAttendanceStateList, selectedDate)

        val subjectAttributeRefMap = mutableMapOf<Int, Long>()
        finalAttendanceStateList.forEach { state ->
            val refId = updateAttendanceToSubjectAttributeTable(state)
            subjectAttributeRefMap.put(state.subjectId, refId)
        }

        updateAttendanceAttributeToReferenceTable(finalAttendanceStateList, subjectAttributeRefMap)


    }

    override suspend fun getOldRefForAttendanceAttribute(state: SubjectAttendanceState): Int {
        return subjectAttributeDao.getOldRefForAttendanceAttribute(
            state.subjectId,
            state.date.toString()
        )
    }

    override suspend fun updateAttendanceToSubjectAttributeTable(finalAttendanceState: SubjectAttendanceState): Long {
        val uniqueUserId = coreSharedPrefs.getUniqueUserIdentifier()
        val subjectAttributeEntity = SubjectAttributeEntity.getSubjectAttributeEntity(
            userId = uniqueUserId,
            subjectId = finalAttendanceState.subjectId,
            subjectType = SubjectType.SUBJECT_TYPE_DIDI.subjectName,
            attribute = AttributesType.ATTRIBUTE_ATTENDANCE.attributeType,
            tagId = ATTENDANCE_TAG_ID,
            date = finalAttendanceState.date.toString(),
            missionId = 0,
            activityId = 0,
            taskId = 0
        )
        return subjectAttributeDao.insertSubjectAttribute(subjectAttributeEntity)
    }

    override suspend fun updateAttendanceAttributeToReferenceTable(
        finalAttendanceStateList: List<SubjectAttendanceState>,
        referenceIdMap: Map<Int, Long>
    ) {
        val uniqueUserId = coreSharedPrefs.getUniqueUserIdentifier()

        finalAttendanceStateList.forEach { state ->
            val attributeValueReferenceEntityForSubjectList =
                getAllAttendanceAttributeValueReferencesForSubject(
                    state,
                    uniqueUserId,
                    referenceIdMap
                )

            attributeValueReferenceDao.insertAttributesValueReferences(
                attributeValueReferenceEntityForSubjectList
            )
        }
    }

    override suspend fun removeOldAttendanceForDate(
        finalAttendanceStateList: List<SubjectAttendanceState>,
        selectedDate: Long
    ) {
        val oldSubjectAttributeRefMap = mutableMapOf<Int, Int>()
        finalAttendanceStateList.forEach { state ->
            val oldRef = getOldRefForAttendanceAttribute(state)
            oldSubjectAttributeRefMap.put(state.subjectId, oldRef)
        }
        removeAttendanceFromSubjectAttributeTable(
            finalAttendanceStateList,
            selectedDate
        ) //TODO Check if this is required
        removeAttendanceAttributeFromReferenceTable(
            finalAttendanceStateList,
            oldSubjectAttributeRefMap
        ) //TODO Check if this is required

    }

    override suspend fun removeAttendanceFromSubjectAttributeTable(
        finalAttendanceStateList: List<SubjectAttendanceState>,
        selectedDate: Long
    ) {
        val subjectIds = finalAttendanceStateList.map { it.subjectId }
        subjectAttributeDao.removeAttendanceFromSubjectAttributeTable(
            subjectIds = subjectIds,
            subjectType = SubjectType.SUBJECT_TYPE_DIDI.subjectName,
            attributeType = AttributesType.ATTRIBUTE_ATTENDANCE.attributeType,
            date = selectedDate.toString()
        )
    }

    override suspend fun removeAttendanceAttributeFromReferenceTable(
        finalAttendanceStateList: List<SubjectAttendanceState>,
        referenceIdMap: Map<Int, Int>
    ) {
        attributeValueReferenceDao.removeAttendanceAttributeFromReferenceTable(referenceIdMap.values.map { it })
    }


}