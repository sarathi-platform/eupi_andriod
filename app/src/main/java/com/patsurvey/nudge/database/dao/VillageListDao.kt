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

    @Query("SELECT * FROM $VILLAGE_TABLE_NAME where languageId=:languageId and isActive=1")
    fun getAllVillages(languageId:Int): List<VillageEntity>

    @Query("SELECT * FROM $VILLAGE_TABLE_NAME where isActive=1")
    fun getAllLanguageVillages(): List<VillageEntity>

    @Query("Select * FROM $VILLAGE_TABLE_NAME where id = :id and isActive=1")
    fun getVillage(id: Int): VillageEntity

    @Query("Select * FROM $VILLAGE_TABLE_NAME where id = :id and languageId=:languageId")
    fun getVillageFromId(id: Int, languageId: Int): VillageEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVillage(village: VillageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(villages: List<VillageEntity>)

    @Transaction()
    fun insertOnlyNewData(villages: List<VillageEntity>, userBPC: Boolean) {
        softDeleteVillage()
        villages.forEach {
            val localVillage = getVillageFromId(it.id, it.languageId)
            if (localVillage == null) {
                insertVillage(it.copy(isActive = 1))
            } else if (localVillage.isDataLoadTriedOnce != 1 && userBPC) {
                deleteVillageById(localVillage.localVillageId)
                insertVillage(it.copy(isActive = 1))
            } else {
                updateVillageData(
                    name = it.name,
                    federationName = it.federationName,
                    villageId = it.id,
                    languageId = it.languageId
                )
            }

        }
    }

    @Query("update $VILLAGE_TABLE_NAME set isActive=2")
    fun softDeleteVillage()

    @Query("DELETE from $VILLAGE_TABLE_NAME where localVillageId= :localVillageId")
    fun deleteVillageById(localVillageId: Int)
    @Query("UPDATE $VILLAGE_TABLE_NAME SET steps_completed = :stepId where id = :villageId")
    fun updateLastCompleteStep(villageId: Int, stepId: List<Int>)

    @Query("UPDATE $VILLAGE_TABLE_NAME SET name=:name,federationName=:federationName, isActive=1 where  id = :villageId and languageId=:languageId  ")
    fun updateVillageData(villageId: Int, languageId: Int, name: String, federationName: String)

    @Query("Select * from $VILLAGE_TABLE_NAME where id = :villageId AND languageId = :languageId  and isActive=1")
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