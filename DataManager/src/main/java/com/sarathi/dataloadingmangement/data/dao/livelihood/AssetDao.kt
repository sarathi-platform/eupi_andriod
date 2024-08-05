package com.sarathi.dataloadingmangement.data.dao.livelihood

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.sarathi.dataloadingmangement.data.entities.livelihood.AssetEntity


@Dao
interface AssetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAsset(assetEntity: AssetEntity)

//    @Query("DELETE FROM $ASSETS_TABLE_NAME where user=:userId ")
//    fun deleteAssetForUser(userId: String)
}