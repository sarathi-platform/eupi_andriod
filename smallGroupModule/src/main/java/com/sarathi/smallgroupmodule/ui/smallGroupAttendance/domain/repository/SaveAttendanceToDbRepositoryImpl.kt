package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.repository

import com.nudge.core.ATTENDANCE_TAG_ID
import com.nudge.core.enums.AttributesType
import com.nudge.core.enums.SubjectType
import com.nudge.core.enums.ValueTypes
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.value
import com.sarathi.dataloadingmangement.data.dao.AttributeValueReferenceDao
import com.sarathi.dataloadingmangement.data.dao.SubjectAttributeDao
import com.sarathi.dataloadingmangement.data.dao.SubjectEntityDao
import com.sarathi.dataloadingmangement.data.entities.AttributeValueReferenceEntity
import com.sarathi.dataloadingmangement.data.entities.SubjectAttributeEntity
import com.sarathi.dataloadingmangement.model.mat.response.TaskData
import com.sarathi.smallgroupmodule.data.model.SubjectAttendanceState
import com.sarathi.smallgroupmodule.utils.getAttendanceFromBoolean
import javax.inject.Inject

class SaveAttendanceToDbRepositoryImpl @Inject constructor(
    val coreSharedPrefs: CoreSharedPrefs,
    val subjectEntityDao: SubjectEntityDao,
    val subjectAttributeDao: SubjectAttributeDao,
    val attributeValueReferenceDao: AttributeValueReferenceDao
) : SaveAttendanceToDbRepository {
    override suspend fun saveFinalAttendanceToDb(finalAttendanceStateList: List<SubjectAttendanceState>) {

        val subjectAttributeRefMap = mutableMapOf<Int, Long>()
        finalAttendanceStateList.forEach { state ->
            val refId = saveAttendanceToSubjectAttributeTable(state)
            subjectAttributeRefMap.put(state.subjectId, refId)
        }

        saveAttendanceAttributeToReferenceTable(finalAttendanceStateList, subjectAttributeRefMap)
    }

    override suspend fun saveAttendanceToSubjectAttributeTable(finalAttendanceState: SubjectAttendanceState): Long {
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

    override suspend fun saveAttendanceAttributeToReferenceTable(
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

    private fun getAllAttendanceAttributeValueReferencesForSubject(
        state: SubjectAttendanceState,
        uniqueUserId: String,
        referenceIdMap: Map<Int, Long>
    ): ArrayList<AttributeValueReferenceEntity> {
        val attributeValueReferenceEntityForSubjectList = ArrayList<AttributeValueReferenceEntity>()

        val parentReferenceId = referenceIdMap[state.subjectId].value()

        val attendanceTaskData = getTaskData(
            AttributesType.ATTRIBUTE_ATTENDANCE,
            state
        )
        val dateTaskData =
            getTaskData(AttributesType.ATTRIBUTE_ATTENDANCE_DATE, state)

        val attendanceAttributeValueReferenceEntity = getAttributeValueReferenceEntity(
            uniqueUserId = uniqueUserId,
            attributesType = AttributesType.ATTRIBUTE_ATTENDANCE,
            taskData = attendanceTaskData,
            referenceId = parentReferenceId
        )
        attributeValueReferenceEntityForSubjectList.add(attendanceAttributeValueReferenceEntity)

        val dateAttributeValueReferenceEntity = getAttributeValueReferenceEntity(
            uniqueUserId = uniqueUserId,
            attributesType = AttributesType.ATTRIBUTE_ATTENDANCE_DATE,
            taskData = dateTaskData,
            referenceId = parentReferenceId
        )

        attributeValueReferenceEntityForSubjectList.add(dateAttributeValueReferenceEntity)
        return attributeValueReferenceEntityForSubjectList
    }

    private fun getAttributeValueReferenceEntity(
        uniqueUserId: String,
        attributesType: AttributesType,
        taskData: TaskData,
        referenceId: Long
    ): AttributeValueReferenceEntity {
        return when (attributesType) {
            AttributesType.ATTRIBUTE_ATTENDANCE -> {
                AttributeValueReferenceEntity
                    .getAttributeValueReferenceEntity(
                        userId = uniqueUserId,
                        parentReferenceId = referenceId.value(),
                        taskData = taskData,
                        valueType = ValueTypes.BOOLEAN.dataType
                    )
            }

            AttributesType.ATTRIBUTE_ATTENDANCE_DATE -> {
                AttributeValueReferenceEntity
                    .getAttributeValueReferenceEntity(
                        userId = uniqueUserId,
                        parentReferenceId = referenceId.value(),
                        taskData = taskData,
                        valueType = ValueTypes.LONG.dataType
                    )
            }
        }
    }

    private fun getTaskData(
        attributesType: AttributesType,
        state: SubjectAttendanceState
    ): TaskData {
        return when (attributesType) {
            AttributesType.ATTRIBUTE_ATTENDANCE -> {
                TaskData(attributesType.attributeType, state.attendance.getAttendanceFromBoolean())
            }

            AttributesType.ATTRIBUTE_ATTENDANCE_DATE -> {
                TaskData(attributesType.attributeType, state.date.toString())
            }
        }
    }


}