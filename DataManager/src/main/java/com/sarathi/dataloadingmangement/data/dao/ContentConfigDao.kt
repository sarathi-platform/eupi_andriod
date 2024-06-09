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

    @Query("Select * from content_config_table where userId=:userId  and contentCategory=1 and languageCode=:languageCode")
    fun getAllContentKey(userId: String, languageCode: String): List<ContentConfigEntity>
}