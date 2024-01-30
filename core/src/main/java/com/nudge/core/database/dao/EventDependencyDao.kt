package com.nudge.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.nudge.core.database.entities.EventDependencyEntity

@Dao
interface EventDependencyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(eventDependency: EventDependencyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(eventDependencies: List<EventDependencyEntity>)

}
