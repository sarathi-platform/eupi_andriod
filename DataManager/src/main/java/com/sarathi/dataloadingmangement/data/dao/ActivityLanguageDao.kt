package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.data.entities.ActivityLanguageAttributesEntity


@Dao
interface ActivityLanguageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActivityLanguage(activityLanguageAttributesEntity: ActivityLanguageAttributesEntity)

    @Query("delete from activity_language_attribute_table where userId=:userId")
    fun deleteActivityLanguageAttributeForUser(userId: String)

}