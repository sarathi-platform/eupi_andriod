package com.sarathi.dataloadingmangement.repository.smallGroup

import com.nudge.core.ATTENDANCE_ABSENT
import com.nudge.core.ATTENDANCE_PRESENT
import com.nudge.core.ATTENDANCE_TAG_ID
import com.nudge.core.enums.AttributesType
import com.nudge.core.enums.SubjectType
import com.nudge.core.enums.ValueTypes
import com.nudge.core.enums.ValueTypes.Companion.convertToDataType
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.utils.CoreLogger
import com.nudge.core.value
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.SUCCESS_CODE
import com.sarathi.dataloadingmangement.data.dao.AttributeValueReferenceDao
import com.sarathi.dataloadingmangement.data.dao.SubjectAttributeDao
import com.sarathi.dataloadingmangement.data.dao.SubjectEntityDao
import com.sarathi.dataloadingmangement.data.entities.AttributeValueReferenceEntity
import com.sarathi.dataloadingmangement.data.entities.SubjectAttributeEntity
import com.sarathi.dataloadingmangement.model.mat.response.TaskData
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import com.sarathi.dataloadingmangement.network.request.AttendanceHistoryRequest
import com.sarathi.dataloadingmangement.network.response.AttendanceHistoryResponse
import com.sarathi.dataloadingmangement.network.response.DidiAttendanceDetail
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

class FetchSmallGroupAttendanceHistoryFromNetworkRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val dataLoadingApiService: DataLoadingApiService,
    private val subjectEntityDao: SubjectEntityDao,
    private val subjectAttributeDao: SubjectAttributeDao,
    private val attributeValueReferenceDao: AttributeValueReferenceDao
) : FetchSmallGroupAttendanceHistoryFromNetworkRepository {

    private val TAG =
        FetchSmallGroupAttendanceHistoryFromNetworkRepositoryImpl::class.java.simpleName

    override suspend fun fetchSmallGroupAttendanceHistoryFromNetwork(smallGroupId: Int) {

        try {
            val userId = coreSharedPrefs.getUserNameInInt()
            val request = AttendanceHistoryRequest.getRequest(smallGroupId, userId)
            val response = dataLoadingApiService.getAttendanceHistoryFromNetwork(request)
            if (response.status.equals(SUCCESS_CODE)) {
                response.data?.let { attendanceHistoryResponseList ->
                    attendanceHistoryResponseList.forEach { attendanceHistoryResponse ->
                        saveSmallGroupAttendanceHistoryToDb(attendanceHistoryResponse)
                    }

                }
            }
        } catch (ex: Exception) {
            CoreLogger.e(
                context = CoreAppDetails.getApplicationDetails()?.activity?.applicationContext!!,
                tag = TAG,
                msg = "Exception: ${ex.message}",
                ex = ex,
                stackTrace = true
            )
        }

    }

    override suspend fun saveSmallGroupAttendanceHistoryToDb(attendanceHistoryResponse: AttendanceHistoryResponse) {
        val date = attendanceHistoryResponse.date

        attendanceHistoryResponse.didiAttendanceDetailList.forEach { didiAttendanceDetail ->
            val isHistoryAvailable = checkIfHistoryAvailable(didiAttendanceDetail.id, date)
            if (isHistoryAvailable == 0) {
                val refId = saveAttendanceToAttributeTable(didiAttendanceDetail, date)
                saveAttendanceToAttributeReferenceTable(didiAttendanceDetail, date, refId)
            }
        }
    }

    override suspend fun saveAttendanceToAttributeTable(
        subjectDetails: DidiAttendanceDetail,
        date: Long
    ): Long {
        val uniqueUserId = coreSharedPrefs.getUniqueUserIdentifier()
        val subjectAttributeEntity = SubjectAttributeEntity.getSubjectAttributeEntity(
            userId = uniqueUserId,
            subjectId = subjectDetails.id,
            subjectType = SubjectType.SUBJECT_TYPE_DIDI.subjectName,
            attribute = AttributesType.ATTRIBUTE_ATTENDANCE.attributeType,
            tagId = ATTENDANCE_TAG_ID,
            date = date.toString(),
            missionId = 0,
            activityId = 0,
            taskId = 0
        )
        return subjectAttributeDao.insertSubjectAttribute(subjectAttributeEntity)
    }

    override suspend fun saveAttendanceToAttributeReferenceTable(
        subjectDetails: DidiAttendanceDetail,
        date: Long,
        refId: Long
    ) {
        val uniqueUserId = coreSharedPrefs.getUniqueUserIdentifier()
        val attributeValueReferenceEntityForSubjectList =
            getAllAttendanceAttributeValueReferencesForSubject(
                subjectDetails,
                date,
                uniqueUserId,
                refId
            )

        attributeValueReferenceDao.insertAttributesValueReferences(
            attributeValueReferenceEntityForSubjectList
        )
    }

    override suspend fun checkIfHistoryAvailable(subjectId: Int, date: Long): Int {

        val historyForDidi = subjectAttributeDao.isAttendanceHistoryAvailableForDidi(
            subjectId,
            subjectType = SubjectType.SUBJECT_TYPE_DIDI.subjectName,
            attribute = AttributesType.ATTRIBUTE_ATTENDANCE.attributeType,
        )
        val historyDate = ArrayList<String>()
        historyForDidi.forEach {
            val dateInLong = (it.convertToDataType(ValueTypes.LONG.dataType) as Long)
            historyDate.add(dateInLong.getDate())
        }

        var count = 0
        if (historyDate.contains(date.getDate()))
            count = 1

        count += subjectAttributeDao.isAttendanceHistoryAvailable(
            subjectId = subjectId,
            subjectType = SubjectType.SUBJECT_TYPE_DIDI.subjectName,
            attribute = AttributesType.ATTRIBUTE_ATTENDANCE.attributeType,
            date = date.toString()
        )

        return count
    }

    private fun String.getDateFromResponse(): String {
        val dateStringList = this.split("T")
        return if (dateStringList.isNotEmpty())
            dateStringList[0]
        else
            BLANK_STRING
    }

    private fun getAllAttendanceAttributeValueReferencesForSubject(
        subjectDetails: DidiAttendanceDetail,
        date: Long,
        uniqueUserId: String,
        referenceId: Long
    ): List<AttributeValueReferenceEntity> {
        val attributeValueReferenceEntityForSubjectList = ArrayList<AttributeValueReferenceEntity>()


        val attendanceTaskData = getTaskData(
            AttributesType.ATTRIBUTE_ATTENDANCE,
            subjectDetails,
            date
        )
        val dateTaskData =
            getTaskData(AttributesType.ATTRIBUTE_ATTENDANCE_DATE, subjectDetails, date)

        val attendanceAttributeValueReferenceEntity = getAttributeValueReferenceEntity(
            uniqueUserId = uniqueUserId,
            attributesType = AttributesType.ATTRIBUTE_ATTENDANCE,
            taskData = attendanceTaskData,
            referenceId = referenceId
        )
        attributeValueReferenceEntityForSubjectList.add(attendanceAttributeValueReferenceEntity)

        val dateAttributeValueReferenceEntity = getAttributeValueReferenceEntity(
            uniqueUserId = uniqueUserId,
            attributesType = AttributesType.ATTRIBUTE_ATTENDANCE_DATE,
            taskData = dateTaskData,
            referenceId = referenceId
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
        subjectDetails: DidiAttendanceDetail,
        date: Long
    ): TaskData {
        return when (attributesType) {
            AttributesType.ATTRIBUTE_ATTENDANCE -> {
                TaskData(
                    attributesType.attributeType,
                    subjectDetails.attendanceStatus.getAttendanceFromInt()
                )
            }

            AttributesType.ATTRIBUTE_ATTENDANCE_DATE -> {
                TaskData(attributesType.attributeType, date.toString())
            }
        }
    }

    private fun String.getAttendanceFromInt(): String {
        return if (this == "0")
            ATTENDANCE_ABSENT else ATTENDANCE_PRESENT
    }

    private fun Long?.getDate(pattern: String = "dd/MM/yyyy"): String {
        if (this == null)
            return com.nudge.core.BLANK_STRING

        val formatter = SimpleDateFormat(pattern)
        return formatter.format(Date(this))
    }

}