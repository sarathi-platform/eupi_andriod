package com.sarathi.dataloadingmangement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import com.sarathi.dataloadingmangement.data.entities.ConditionsEntity

@Dao
interface ConditionsEntityDao {

    @Insert
    fun addConditionEntity(conditionsEntity: ConditionsEntity)

}