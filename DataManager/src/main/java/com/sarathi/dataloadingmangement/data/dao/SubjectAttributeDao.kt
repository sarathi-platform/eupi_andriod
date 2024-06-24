package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.data.entities.SubjectAttributeEntity
import com.sarathi.dataloadingmangement.model.uiModel.SubjectAttributes


@Dao
interface SubjectAttributeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubjectAttribute(subjectAttributeEntity: SubjectAttributeEntity): Long

    @Query("select attribute_value_reference_table.`key`, attribute_value_reference_table.value from subject_attribute_table inner join attribute_value_reference_table on subject_attribute_table.id = attribute_value_reference_table.parentReferenceId where subject_attribute_table.taskId =:taskId")
    fun getSubjectAttributes(taskId: Int): List<SubjectAttributes>


    @Query("Delete from subject_attribute_table where userId=:userId")
    fun deleteSubjectAttributes(userId: String)

}