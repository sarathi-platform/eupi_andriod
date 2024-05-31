package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.sarathi.dataloadingmangement.data.entities.GrantConfigEntity


@Dao
interface GrantConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGrantActivityConfig(grantConfig: GrantConfigEntity)


}