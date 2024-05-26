package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.sarathi.dataloadingmangement.data.entities.ContentConfigEntity


@Dao
interface ContentConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContentConfig(contentConfigEntity: ContentConfigEntity)


}