package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.sarathi.dataloadingmangement.data.entities.TaskAttributesEntity


@Dao
interface TaskAttributeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTaskAttribute(taskAttributesEntity: TaskAttributesEntity)


}