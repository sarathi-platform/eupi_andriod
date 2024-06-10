package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.data.entities.AttributeValueReferenceEntity


@Dao
interface AttributeValueReferenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAttributesValueReferences(attributeValueReferenceEntity: AttributeValueReferenceEntity)

    @Query("select value from attribute_value_reference_table where `key`=:attributeKey and userId=:userId")
    fun getAttributeValue(attributeKey: String, userId: String): List<String>
}