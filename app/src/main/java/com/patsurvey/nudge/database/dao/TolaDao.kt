package com.patsurvey.nudge.database.dao

import androidx.room.*
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.utils.TOLA_TABLE

@Dao
interface TolaDao {

    @Query("SELECT * FROM $TOLA_TABLE where status = 1")
    fun getAllTolas(): List<TolaEntity>

    @Query("SELECT * FROM $TOLA_TABLE where id = :id AND status = 1")
    fun getTola(id: Int): TolaEntity

    @Query("SELECT * FROM $TOLA_TABLE where villageId = :villageId and status = 1 ORDER BY createdDate DESC")
    fun getAllTolasForVillage(villageId: Int): List<TolaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(tolas: List<TolaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tola: TolaEntity)

    @Query("DELETE from $TOLA_TABLE where id = :id")
    fun removeTola(id: Int)

    @Query("SELECT * from $TOLA_TABLE where id = :id")
    fun fetchSingleTola(id: Int): TolaEntity?

    @Query("UPDATE $TOLA_TABLE SET needsToPost = :needsToPost WHERE id in (:ids)")
    fun setNeedToPost(ids: List<Int>, needsToPost: Boolean)
    @Query("UPDATE $TOLA_TABLE SET needsToPost = :needsToPost WHERE id =:id")
    fun updateNeedToPost(id:Int, needsToPost: Boolean)

    @Query("DELETE from $TOLA_TABLE where villageId = :villageId")
    fun deleteTolaTable(villageId: Int)

    @Query("UPDATE $TOLA_TABLE SET status = :status, needsToPost = 1 WHERE id = :id")
    fun deleteTolaOffline(id: Int, status: Int)

    @Query("DELETE from $TOLA_TABLE where needsToPost = :needsToPost")
    fun deleteTolaNeedToPost( needsToPost: Boolean)

    @Query("SELECT * from $TOLA_TABLE where needsToPost = :needsToPost and transactionId = :transactionId")
    fun fetchTolaNeedToPost( needsToPost: Boolean,transactionId : String?) : List<TolaEntity>

    @Query("UPDATE $TOLA_TABLE SET transactionId = :transactionId WHERE id = :id")
    fun updateTolaTransactionId(id: Int, transactionId: String)

    @Query("SELECT * from $TOLA_TABLE where needsToPost = :needsToPost and transactionId != :transactionId")
    fun fetchPendingTola(needsToPost: Boolean,transactionId : String?) : List<TolaEntity>

    @Query("DELETE from $TOLA_TABLE")
    fun deleteAllTola()

    @Query("SELECT * from $TOLA_TABLE where status = :status")
    fun fetchAllTolaNeedToDelete(status: Int) : List<TolaEntity>

    @Query("SELECT * from $TOLA_TABLE where status = :status and transactionId = :transactionId")
    fun fetchAllPendingTolaNeedToDelete(status: Int,transactionId: String?) : List<TolaEntity>

    @Query("SELECT * from $TOLA_TABLE where needsToPost = :needsToPost and transactionId != :transactionId")
    fun fetchAllPendingTolaNeedToUpdate(needsToPost: Boolean,transactionId: String?) : List<TolaEntity>

    @Query("DELETE from $TOLA_TABLE where id = :id")
    fun deleteTola(id: Int)

    @Query("Update $TOLA_TABLE SET serverId = :serverId, needsToPost = :needsToPost, transactionId = :transactionId, createdDate = :createdDate, modifiedDate =:modifiedDate where id = :id")
    fun updateTolaDetailAfterSync(id: Int, serverId: Int, needsToPost: Boolean, transactionId: String, createdDate: Long, modifiedDate: Long)

    @Query("SELECT * from $TOLA_TABLE where needsToPost = :needsToPost and transactionId = :transactionId and serverId != :serverId")
    fun fetchAllTolaNeedToUpdate( needsToPost: Boolean,transactionId : String?,serverId : Int) : List<TolaEntity>
}