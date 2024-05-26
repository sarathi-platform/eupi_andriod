package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.sarathi.dataloadingmangement.data.entities.ActivityConfigLanguageAttributesEntity


@Dao
interface ActivityLanguageAttributeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActivityLanguageAttribute(activityConfigLanguageAttributesEntity: ActivityConfigLanguageAttributesEntity)


}