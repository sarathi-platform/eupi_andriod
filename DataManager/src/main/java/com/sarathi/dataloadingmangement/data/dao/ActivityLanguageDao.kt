package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.sarathi.dataloadingmangement.data.entities.ActivityLanguageAttributesEntity


@Dao
interface ActivityLanguageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActivityLanguage(activityLanguageAttributesEntity: ActivityLanguageAttributesEntity)


}