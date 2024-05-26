package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.sarathi.dataloadingmangement.data.entities.AttributeValueReferenceEntity


@Dao
interface AttributeValueReferenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAttributesValueReferences(attributeValueReferenceEntity: AttributeValueReferenceEntity)


}