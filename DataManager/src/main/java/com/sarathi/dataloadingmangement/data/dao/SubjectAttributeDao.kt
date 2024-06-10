package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sarathi.dataloadingmangement.SUBJECT_ATTRIBUTE_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.SubjectAttributeEntity
import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupAttendanceHistoryModel
import com.sarathi.dataloadingmangement.model.uiModel.SubjectAttributes


@Dao
interface SubjectAttributeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubjectAttribute(subjectAttributeEntity: SubjectAttributeEntity): Long

    @Query("select attribute_value_reference_table.`key`, attribute_value_reference_table.value from subject_attribute_table inner join attribute_value_reference_table on subject_attribute_table.id = attribute_value_reference_table.parentReferenceId where subject_attribute_table.taskId =:taskId")
    fun getSubjectAttributes(taskId: Int): List<SubjectAttributes>

    @Query("select subject_attribute_table.id, subject_attribute_table.subjectId, subject_attribute_table.subjectType, subject_attribute_table.attribute, subject_attribute_table.date, attribute_value_reference_table.`key`,attribute_value_reference_table.value,attribute_value_reference_table.valueType from subject_attribute_table join attribute_value_reference_table on subject_attribute_table.id = attribute_value_reference_table.parentReferenceId where subject_attribute_table.userId = :userId and subject_attribute_table.subjectId in (:subjectIds) and subject_attribute_table.attribute = 'Attendance'and subject_attribute_table.date BETWEEN :startDate and :endDate")
    fun getSmallGroupAttendanceHistoryForRange(
        userId: String,
        subjectIds: List<Int>,
        startDate: Long,
        endDate: Long
    ): List<SmallGroupAttendanceHistoryModel>

    @Query("select subject_attribute_table.id, subject_attribute_table.subjectId, subject_attribute_table.subjectType, subject_attribute_table.attribute, subject_attribute_table.date, attribute_value_reference_table.`key`,attribute_value_reference_table.value,attribute_value_reference_table.valueType from subject_attribute_table join attribute_value_reference_table on subject_attribute_table.id = attribute_value_reference_table.parentReferenceId where subject_attribute_table.userId = :userId and subject_attribute_table.subjectId in (:subjectIds) and subject_attribute_table.attribute = 'Attendance'and subject_attribute_table.date = :selectedDate")

    fun getSmallGroupAttendanceHistoryForDate(
        userId: String,
        subjectIds: List<Int>,
        selectedDate: Long,
    ): List<SmallGroupAttendanceHistoryModel>

    @Query("select id from $SUBJECT_ATTRIBUTE_TABLE_NAME where subjectId = :subjectId and subjectType = 'Didi' and attribute = 'Attendance' and date = :date")
    fun getOldRefForAttendanceAttribute(subjectId: Int, date: String): Int

    @Query("DELETE from $SUBJECT_ATTRIBUTE_TABLE_NAME where subjectId = :subjectId and subjectType = :subjectType and attribute = :attribute and date = :date")
    fun removeEntryFromSubjectAttributeTable(
        subjectId: Int,
        subjectType: String,
        attribute: String,
        date: String
    )

    @Transaction
    fun removeAttendanceFromSubjectAttributeTable(
        subjectIds: List<Int>,
        attributeType: String,
        subjectType: String,
        date: String
    ) {
        subjectIds.forEach {
            removeEntryFromSubjectAttributeTable(
                subjectId = it,
                subjectType = subjectType,
                attribute = attributeType,
                date = date
            )
        }
    }

}