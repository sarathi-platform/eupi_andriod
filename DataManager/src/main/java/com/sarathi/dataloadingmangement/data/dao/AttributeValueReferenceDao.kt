package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.ATTRIBUTE_VALUE_REFERENCE_ENTITY_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.AttributeValueReferenceEntity
import com.sarathi.dataloadingmangement.model.uiModel.MarkedDatesUiModel


@Dao
interface AttributeValueReferenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAttributesValueReferences(attributeValueReferenceEntity: AttributeValueReferenceEntity)

    @Query("select value from attribute_value_reference_table where `key`=:attributeKey and userId=:userId")
    fun getAttributeValue(attributeKey: String, userId: String): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAttributesValueReferences(attributeValueReferenceEntity: List<AttributeValueReferenceEntity>)

    @Query(
        "select \n" +
                "attRef.value,\n" +
                "attRef.valueType\n" +
                "from subject_attribute_table subAtt \n" +
                "join attribute_value_reference_table attRef on subAtt.id = attRef.parentReferenceId \n" +
                "where subAtt.userId = :userId \n" +
                "and subAtt.subjectId in (:subjectIds)\n" +
                "and subAtt.attribute = 'Attendance'\n" +
                "and attRef.`key` = 'AttendanceDate'\n" +
                "group by attRef.value"
    )
    suspend fun getMarkedDatesList(userId: String, subjectIds: List<Int>): List<MarkedDatesUiModel>

    @Query("DELETE from $ATTRIBUTE_VALUE_REFERENCE_ENTITY_TABLE_NAME where parentReferenceId in (:parentRefIds)")
    fun removeAttendanceAttributeFromReferenceTable(parentRefIds: List<Int>)

    @Query("Delete from attribute_value_reference_table where userId=:userId")
    fun deleteAttributeValueReferenceForUser(userId: String)
}