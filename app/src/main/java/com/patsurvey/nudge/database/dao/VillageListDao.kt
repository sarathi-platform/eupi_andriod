package com.patsurvey.nudge.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.utils.VILLAGE_TABLE_NAME

@Dao
interface VillageListDao {

    @Query("SELECT * FROM $VILLAGE_TABLE_NAME where languageId=:languageId")
    fun getAllVillages(languageId:Int): List<VillageEntity>

    @Query("SELECT * FROM $VILLAGE_TABLE_NAME")
    fun getAllLanguageVillages(): List<VillageEntity>

    @Query("Select * FROM $VILLAGE_TABLE_NAME where id = :id")
    fun getVillage(id: Int): VillageEntity

    @Query("Select * FROM $VILLAGE_TABLE_NAME where id = :id and languageId=:languageId")
    fun getVillageFromId(id: Int, languageId: Int): VillageEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVillage(village: VillageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(villages: List<VillageEntity>)

    @Transaction()
    fun insertOnlyNewData(villages: List<VillageEntity>, userBPC: Boolean) {
        villages.forEach {
            val localVillage = getVillageFromId(it.id, it.languageId)
            if (localVillage == null) {
                insertVillage(it)
            } else if (localVillage.isDataLoadTriedOnce != 1 && userBPC) {
                deleteVillageById(localVillage.localVillageId)
                insertVillage(it)
            }
        }
    }

    @Query("DELETE from $VILLAGE_TABLE_NAME where localVillageId= :localVillageId")
    fun deleteVillageById(localVillageId: Int)
    @Query("UPDATE $VILLAGE_TABLE_NAME SET steps_completed = :stepId where id = :villageId")
    fun updateLastCompleteStep(villageId: Int, stepId: List<Int>)

    @Query("Select * from $VILLAGE_TABLE_NAME where id = :villageId AND languageId = :languageId")
    fun fetchVillageDetailsForLanguage(villageId: Int, languageId: Int):VillageEntity
    @Query("DELETE from $VILLAGE_TABLE_NAME")
    fun deleteAllVilleges()

    @Query("SELECT COUNT(*) FROM $VILLAGE_TABLE_NAME where languageId=:languageId")
    fun getAllVillagesCount(languageId:Int): Long

    @Query("SELECT stateId from $VILLAGE_TABLE_NAME LIMIT 1")
    fun getStateId(): Int

    @Query("UPDATE $VILLAGE_TABLE_NAME SET stepId=:stepId, statusId=:statusId where id = :villageId")
    fun updateStepAndStatusId(villageId: Int, stepId: Int,statusId:Int)

    @Query("UPDATE $VILLAGE_TABLE_NAME SET isDataLoadTriedOnce=:isDataLoadTriedOnce where id = :villageId")
    fun updateVillageDataLoadStatus(villageId: Int, isDataLoadTriedOnce: Int)

}