package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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


}