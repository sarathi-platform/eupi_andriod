package com.nudge.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nudge.core.ImageStatusTable
import com.nudge.core.database.entities.ImageStatusEntity

@Dao
interface ImageStatusDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(imageStatusEntity: ImageStatusEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(imageEventList: List<ImageStatusEntity>)

    @Query("SELECT * from $ImageStatusTable")
    fun getAllImageEvent(): List<ImageStatusEntity>

}