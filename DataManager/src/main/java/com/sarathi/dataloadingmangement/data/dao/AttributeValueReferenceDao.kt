package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.data.entities.AttributeValueReferenceEntity
import com.sarathi.dataloadingmangement.model.uiModel.MarkedDatesUiModel


@Dao
interface AttributeValueReferenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAttributesValueReferences(attributeValueReferenceEntity: AttributeValueReferenceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAttributesValueReferences(attributeValueReferenceEntity: List<AttributeValueReferenceEntity>)

    @Query(
        "select \n" +
                "attRef.value,\n" +
                "attRef.valueType\n" +
                "from subject_attribute_table subAtt \n" +
                "join attribute_value_reference_table attRef on subAtt.id = attRef.parentReferenceId \n" +
                "where subAtt.userId = :userId \n" +
                "and subAtt.attribute = 'Attendance'\n" +
                "and attRef.`key` = 'AttendanceDate'\n" +
                "group by attRef.value"
    )
    suspend fun getMarkedDatesList(userId: String): List<MarkedDatesUiModel>


}