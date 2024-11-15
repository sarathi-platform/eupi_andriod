package com.patsurvey.nudge.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.utils.TOLA_TABLE

@Dao
interface TolaDao {

    @Query("SELECT * FROM $TOLA_TABLE where status = 1")
    fun getAllTolas(): List<TolaEntity>

    @Query("SELECT * FROM $TOLA_TABLE where id = :id AND status = 1")
    fun getTola(id: Int): TolaEntity

    @Query("SELECT * FROM $TOLA_TABLE where villageId = :villageId and status = 1 ORDER BY localCreatedDate DESC")
    fun getAllTolasForVillage(villageId: Int): List<TolaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(tolas: List<TolaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tola: TolaEntity): Int

    @Query("DELETE from $TOLA_TABLE where id = :id")
    fun removeTola(id: Int)

    @Query("SELECT * from $TOLA_TABLE where id = :id")
    fun fetchSingleTola(id: Int): TolaEntity?

    @Query("UPDATE $TOLA_TABLE SET needsToPost = :needsToPost WHERE id in (:ids)")
    fun setNeedToPost(ids: List<Int>, needsToPost: Boolean)
    @Query("UPDATE $TOLA_TABLE SET needsToPost = :needsToPost WHERE id =:id")
    fun updateNeedToPost(id:Int, needsToPost: Boolean)

    @Query("UPDATE $TOLA_TABLE SET name = :name , latitude = :latitude ,longitude = :longitude, modifiedDate=:modifiedDate WHERE id =:id and villageId =:villageId")
    fun updateTolaNameAndLocation(
        id: Int,
        name: String,
        latitude: String,
        longitude: String,
        modifiedDate: Long,
        villageId: Int
    )
    @Query("DELETE from $TOLA_TABLE where villageId = :villageId")
    fun deleteTolaTable(villageId: Int)

    @Query("UPDATE $TOLA_TABLE SET status = :status, needsToPost = 1 WHERE id = :id")
    fun deleteTolaOffline(id: Int, status: Int)

    @Query("DELETE from $TOLA_TABLE where needsToPost = :needsToPost")
    fun deleteTolaNeedToPost( needsToPost: Boolean)

    @Query("SELECT * from $TOLA_TABLE where needsToPost = :needsToPost and transactionId = :transactionId and serverId = :serverId")
    fun fetchTolaNeedToPost( needsToPost: Boolean,transactionId : String?, serverId: Int) : List<TolaEntity>

    @Query("UPDATE $TOLA_TABLE SET transactionId = :transactionId WHERE id = :id")
    fun updateTolaTransactionId(id: Int, transactionId: String)

    @Query("SELECT * from $TOLA_TABLE where needsToPost = :needsToPost and transactionId != :transactionId")
    fun fetchPendingTola(needsToPost: Boolean,transactionId : String?) : List<TolaEntity>

    @Query("DELETE from $TOLA_TABLE")
    fun deleteAllTola()

    @Query("SELECT * from $TOLA_TABLE where status = :status")
    fun fetchAllTolaNeedToDelete(status: Int) : List<TolaEntity>

    @Query("SELECT * from $TOLA_TABLE where status = :status and transactionId != :transactionId")
    fun fetchAllPendingTolaNeedToDelete(status: Int,transactionId: String?) : List<TolaEntity>

    @Query("SELECT * from $TOLA_TABLE where needsToPost = :needsToPost and transactionId != :transactionId")
    fun fetchAllPendingTolaNeedToUpdate(needsToPost: Boolean,transactionId: String?) : List<TolaEntity>

    @Query("DELETE from $TOLA_TABLE where id = :id")
    fun deleteTola(id: Int)

    @Query("Update $TOLA_TABLE SET serverId = :serverId, needsToPost = :needsToPost, transactionId = :transactionId, createdDate = :createdDate, modifiedDate =:modifiedDate where id = :id")
    fun updateTolaDetailAfterSync(id: Int, serverId: Int, needsToPost: Boolean, transactionId: String, createdDate: Long, modifiedDate: Long)

    @Query("SELECT * from $TOLA_TABLE where needsToPost = :needsToPost and transactionId = :transactionId and serverId != :serverId")
    fun fetchAllTolaNeedToUpdate( needsToPost: Boolean,transactionId : String?,serverId : Int) : List<TolaEntity>

    @Query("SELECT * from $TOLA_TABLE where id = :id")
    fun fetchSingleTolaFromServerId(id: Int): TolaEntity?

    @Query("Select COUNT(*) FROM $TOLA_TABLE where name = :name AND villageId= :villageId and status = 1")
    fun getTolaExist(name:String,villageId:Int):Int

    @Query("DELETE from $TOLA_TABLE where villageId= :villageId")
    fun deleteTolaForVillage(villageId: Int)

    @Query("SELECT * FROM $TOLA_TABLE")
    fun getTolaTableDump(): List<TolaEntity>

    @Transaction
    fun updateTolaData(forceRefresh: Boolean = false, villageId: Int, tolaList: List<TolaEntity>) {
        if (forceRefresh)
            deleteTolaForVillage(villageId)
        insertAll(tolaList)
    }
}