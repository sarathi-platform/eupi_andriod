package com.sarathi.smallgroupmodule.utils

import com.nudge.core.enums.AttributesType
import com.nudge.core.enums.ValueTypes
import com.nudge.core.value
import com.sarathi.dataloadingmangement.data.entities.AttributeValueReferenceEntity
import com.sarathi.dataloadingmangement.model.mat.response.TaskData
import com.sarathi.smallgroupmodule.data.model.SubjectAttendanceState

fun getAllAttendanceAttributeValueReferencesForSubject(
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

fun getAttributeValueReferenceEntity(
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

fun getTaskData(
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