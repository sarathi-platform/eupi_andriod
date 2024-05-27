package com.sarathi.dataloadingmangement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.sarathi.dataloadingmangement.data.entities.UiConfigEntity


@Dao
interface UiConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUiConfig(uiConfigEntity: UiConfigEntity)


}