package com.patsurvey.nudge.database.dao

import android.net.Uri
import androidx.room.*
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.converters.BeneficiaryProcessStatusModel
import com.patsurvey.nudge.utils.DIDI_TABLE
import com.patsurvey.nudge.utils.SHGFlag
import com.patsurvey.nudge.utils.WealthRank

@Dao
interface DidiDao {

    @Query("SELECT * FROM $DIDI_TABLE ORDER BY id DESC")
    fun getAllDidis(): List<DidiEntity>

    @Query("SELECT * FROM $DIDI_TABLE where villageId = :villageId ORDER BY createdDate DESC")
    fun getAllDidisForVillage(villageId: Int): List<DidiEntity>

    @Query("Select * FROM $DIDI_TABLE where id = :id")
    fun getDidi(id: Int): DidiEntity

    @Query("Select COUNT(*) FROM $DIDI_TABLE where name = :name AND address=:address AND guardianName=:guardianName AND cohortId=:tolaId AND villageId= :villageId")
    fun getDidiExist(name:String,address:String,guardianName:String,tolaId:Int,villageId:Int):Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDidi(didi: DidiEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(didis: List<DidiEntity>)

    @Update
    fun updateDidi(didi: DidiEntity)

    @Query("DELETE from $DIDI_TABLE")
    fun deleteDidiTable()

    @Query("DELETE from $DIDI_TABLE where villageId = :villageId")
    fun deleteDidiForVillage(villageId: Int)

    @Query("UPDATE $DIDI_TABLE SET needsToPost = :needsToPost WHERE id in (:ids)")
    fun setNeedToPost(ids: List<Int>, needsToPost: Boolean)

    @Query("UPDATE $DIDI_TABLE SET needsToPost = :needsToPost WHERE id =:id")
    fun updateNeedToPost(id:Int, needsToPost: Boolean)
    @Query("UPDATE $DIDI_TABLE SET needsToPostRanking = :needsToPostRanking WHERE id = :id")
    fun setNeedToPostRanking(id:Int, needsToPostRanking: Boolean)

    @Query("UPDATE $DIDI_TABLE SET wealth_ranking = :rank WHERE id = :didiId")
    fun updateDidiRank(didiId: Int, rank: String)
    @Query("SELECT COUNT(id) from $DIDI_TABLE where wealth_ranking = :unRankedStatus and villageId = :villageId")
    fun getUnrankedDidiCount(villageId: Int, unRankedStatus: String = WealthRank.NOT_RANKED.rank): Int

    @Query("SELECT * FROM $DIDI_TABLE where wealth_ranking = :rank and villageId = :villageId")
    fun getAllPoorDidisForVillage(villageId: Int, rank: String = WealthRank.POOR.rank): List<DidiEntity>

    @Query("UPDATE $DIDI_TABLE SET localPath = :path WHERE id = :didiId")
    fun saveLocalImagePath(path: String, didiId: Int)

    @Query("SELECT * FROM $DIDI_TABLE where needsToPostRanking = :needsToPostRanking AND villageId = :villageId")
    fun getAllNeedToPostDidiRanking(needsToPostRanking: Boolean,villageId: Int): List<DidiEntity>

    @Query("DELETE FROM $DIDI_TABLE where cohortId =:tolaId")
    fun deleteDidisForTola(tolaId: Int)

    @Query("UPDATE $DIDI_TABLE SET beneficiaryProcessStatus = :status WHERE id = :didiId")
    fun updateBeneficiaryProcessStatus(didiId: Int, status: List<BeneficiaryProcessStatusModel>)

    @Query("UPDATE $DIDI_TABLE SET patSurveyStatus = :patSurveyProgress WHERE id = :didiId")
    fun updateQuesSectionStatus(didiId: Int, patSurveyProgress: Int)
    @Query("select * from $DIDI_TABLE where cohortId = :tolaId")
    fun getDidisForTola(tolaId: Int): List<DidiEntity>

    @Query("UPDATE $DIDI_TABLE SET section1Status = :section1 WHERE id = :didiId")
    fun updatePatSection1Status(didiId: Int, section1: Int)

    @Query("UPDATE $DIDI_TABLE SET section2Status = :section2 WHERE id = :didiId")
    fun updatePatSection2Status(didiId: Int, section2: Int)

    @Query("select * from $DIDI_TABLE where id = :didiId")
    fun fetchDidiDetails(didiId: Int): DidiEntity

    @Query("update $DIDI_TABLE set shgFlag =:shgFlag where id = :didiId")
    fun updateDidiShgStatus(didiId: Int, shgFlag: Int)
    @Query("update $DIDI_TABLE set cohortName = :newName where cohortId = :id")
    fun updateTolaName(id: Int, newName: String)

    @Query("select COUNT(*) from $DIDI_TABLE where villageId =:villageId AND patSurveyStatus=0 AND wealth_ranking='POOR'")
    fun fetchPendingDidiCount(villageId: Int): Int

    @Query("UPDATE $DIDI_TABLE set voEndorsementStatus =:status WHERE id =:didiId AND villageId = :villageId")
    fun updateVOEndorsementStatus(villageId: Int,didiId:Int,status:Int)

    @Query("SELECT * FROM $DIDI_TABLE where villageId = :villageId AND patSurveyStatus = 2 ORDER BY createdDate DESC")
    fun patCompletedDidis(villageId: Int): List<DidiEntity>
}