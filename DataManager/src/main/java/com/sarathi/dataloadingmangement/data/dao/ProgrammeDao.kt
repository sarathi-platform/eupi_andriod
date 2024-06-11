package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.data.entities.ProgrammeEntity


@Dao
interface ProgrammeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProgramme(programmeEntity: ProgrammeEntity)

    @Query("Delete from programme_table ")
    fun deleteProgramme()


}