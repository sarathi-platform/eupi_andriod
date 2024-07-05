package com.sarathi.dataloadingmangement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sarathi.dataloadingmangement.data.entities.TagReferenceEntity

@Dao
interface TagReferenceEntityDao {

    @Insert
    fun addTagReferenceEntity(tagReferenceEntity: List<TagReferenceEntity>)

    @Query("Delete from tag_reference_table where userId=:userId")
    fun deleteTagReferenceEntityForUser(userId: String)

    @Query("Delete from tag_reference_table where userId=:userId and referenceId=:referenceId and referenceType=:referenceType")
    fun deleteTagReferenceEntityForTag(userId: String, referenceId: Int, referenceType: String)


}