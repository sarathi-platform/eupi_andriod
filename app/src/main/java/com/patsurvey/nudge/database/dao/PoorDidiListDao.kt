package com.patsurvey.nudge.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.PoorDidiEntity
import com.patsurvey.nudge.utils.POOR_DIDI_TABLE

@Dao
interface PoorDidiListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPoorDidi(poorDidi: PoorDidiEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPoorDidiList(poorDidiList: List<PoorDidiEntity>)

    @Query("Select * from $POOR_DIDI_TABLE where villageId = :villageId and activeStatus = 1  ORDER BY localCreatedDate DESC")
    fun getAllPoorDidisForVillage(villageId: Int): List<PoorDidiEntity>

    @Query("Delete from $POOR_DIDI_TABLE")
    fun deleteAllDidis()


    @Query("DELETE from $POOR_DIDI_TABLE where villageId = :villageId")
    fun deleteAllDidisForVillage(villageId: Int)

    @Query("DELETE from $POOR_DIDI_TABLE where id = :didiId")
    fun deleteDidi(didiId: Int)

    @Transaction
    fun updatePoorDidiAfterRefresh(forceRefresh: Boolean = false, didiId: Int, didi: PoorDidiEntity) {
        if (forceRefresh)
            deleteDidi(didiId)
        insertPoorDidi(didi)
    }

}