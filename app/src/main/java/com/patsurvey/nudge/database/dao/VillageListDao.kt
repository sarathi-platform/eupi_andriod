package com.patsurvey.nudge.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.utils.TOLA_TABLE
import com.patsurvey.nudge.utils.VILLAGE_TABLE_NAME

@Dao
interface VillageListDao {

    @Query("SELECT * FROM $VILLAGE_TABLE_NAME where languageId=:languageId")
    fun getAllVillages(languageId:Int): List<VillageEntity>

    @Query("Select * FROM $VILLAGE_TABLE_NAME where id = :id")
    fun getVillage(id: Int): VillageEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVillage(village: VillageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(villages: List<VillageEntity>)

    @Query("UPDATE $VILLAGE_TABLE_NAME SET steps_completed = :stepId where id = :villageId")
    fun updateLastCompleteStep(villageId: Int, stepId: List<Int>)

    @Query("Select * from $VILLAGE_TABLE_NAME where id = :villageId AND languageId = :languageId")
    fun fetchVillageDetailsForLanguage(villageId: Int, languageId: Int):VillageEntity
    @Query("DELETE from $VILLAGE_TABLE_NAME")
    fun deleteAllVilleges()

}