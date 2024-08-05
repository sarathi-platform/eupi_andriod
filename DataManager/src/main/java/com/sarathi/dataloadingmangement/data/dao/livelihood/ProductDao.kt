package com.sarathi.dataloadingmangement.data.dao.livelihood

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.PRODUCT_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.livelihood.ProductEntity


@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLivelihood(productEntity: ProductEntity)

    @Query("DELETE FROM $PRODUCT_TABLE_NAME where userId=:userId ")
    fun deleteProductForUser(userId: String)
}