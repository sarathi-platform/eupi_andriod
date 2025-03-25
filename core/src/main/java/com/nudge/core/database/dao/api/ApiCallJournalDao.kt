package com.nudge.core.database.dao.api

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nudge.core.ApiCallJournalTable
import com.nudge.core.database.entities.api.ApiCallJournalEntity

@Dao
interface ApiCallJournalDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(apiCallJournalEntity: ApiCallJournalEntity)

    @Query("DELETE FROM $ApiCallJournalTable")
    fun deleteApiCallJournalTable()
}