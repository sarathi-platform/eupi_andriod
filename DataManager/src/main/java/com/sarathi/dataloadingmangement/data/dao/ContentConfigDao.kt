package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.data.entities.ContentConfigEntity


@Dao
interface ContentConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContentConfig(contentConfigEntity: ContentConfigEntity)

    @Query("Select * from content_config_table where userId=:userId ")
    fun getAllContentKey(userId: String): List<ContentConfigEntity>

    @Query("Select `key` from content_config_table where userId=:userId and languageCode =:languageCode and contentCategory=:referenceType and matId=:referenceID")
    fun getAllContentKey(
        referenceID: Int,
        referenceType: Int,
        userId: String,
        languageCode: String
    ): List<String>

    @Query("Delete from content_config_table where matId=:missionId and contentCategory=:contentCategory and userId=:uniqueUserIdentifier")
    fun deleteContentConfig(missionId: Int, contentCategory: Int, uniqueUserIdentifier: String)

    @Query("Delete from content_config_table where  userId=:uniqueUserIdentifier")
    fun deleteContentConfigForUser(uniqueUserIdentifier: String)

}