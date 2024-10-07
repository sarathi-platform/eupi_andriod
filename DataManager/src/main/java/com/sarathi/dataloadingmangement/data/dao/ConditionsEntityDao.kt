package com.sarathi.dataloadingmangement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sarathi.dataloadingmangement.data.entities.ConditionsEntity

@Dao
interface ConditionsEntityDao {

    @Insert
    fun addConditionEntity(conditionsEntity: ConditionsEntity)

    @Query("DELETE FROM conditions_table WHERE userId = :userId")
    fun clearAllConditionsForUser(userId: String)

}