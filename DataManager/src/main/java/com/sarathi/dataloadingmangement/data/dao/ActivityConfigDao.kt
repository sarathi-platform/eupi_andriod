package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.sarathi.dataloadingmangement.data.entities.ActivityConfigEntity


@Dao
interface ActivityConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActivityConfig(activityConfigEntity: ActivityConfigEntity)


}