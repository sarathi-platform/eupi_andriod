package com.patsurvey.nudge.database.dao

import androidx.room.*
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.converters.BeneficiaryProcessStatusModel
import com.patsurvey.nudge.utils.DIDI_TABLE
import com.patsurvey.nudge.utils.WealthRank

@Dao
interface DidiDao {

    @Query("SELECT * FROM $DIDI_TABLE where activeStatus = 1 ORDER BY id DESC")
    fun getAllDidis(): List<DidiEntity>

    @Query("SELECT * FROM $DIDI_TABLE where villageId = :villageId and activeStatus = 1 ORDER BY localCreatedDate DESC")
    fun getAllDidisForVillage(villageId: Int): List<DidiEntity>

    @Query("Select * FROM $DIDI_TABLE where id = :id and activeStatus = 1")
    fun getDidi(id: Int): DidiEntity

    @Query("Select COUNT(*) FROM $DIDI_TABLE where name = :name AND address=:address AND guardianName=:guardianName AND cohortId=:tolaId AND villageId= :villageId and activeStatus = 1")
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

    @Query("Update $DIDI_TABLE SET serverId = :serverId, needsToPost = :needsToPost, transactionId = :transactionId, createdDate = :createdDate, modifiedDate =:modifiedDate where id = :id")
    fun updateDidiDetailAfterSync(id: Int, serverId: Int, needsToPost: Boolean, transactionId: String, createdDate: Long, modifiedDate: Long)

    @Query("UPDATE $DIDI_TABLE SET needsToPostRanking = :needsToPostRanking WHERE id = :id")
    fun setNeedToPostRanking(id:Int, needsToPostRanking: Boolean)
    @Query("UPDATE $DIDI_TABLE SET needsToPostRanking = :needsToPostRanking WHERE serverId = :id")
    fun setNeedToPostRankingServerId(id:Int, needsToPostRanking: Boolean)

    @Query("UPDATE $DIDI_TABLE SET wealth_ranking = :rank WHERE id = :didiId")
    fun updateDidiRank(didiId: Int, rank: String)
    @Query("UPDATE $DIDI_TABLE SET wealth_ranking = :rank WHERE serverId = :didiId")
    fun updateDidiRankUsingServerId(didiId: Int, rank: String)

    @Query("SELECT COUNT(id) from $DIDI_TABLE where wealth_ranking = :unRankedStatus and villageId = :villageId and activeStatus = 1")
    fun getUnrankedDidiCount(villageId: Int, unRankedStatus: String = WealthRank.NOT_RANKED.rank): Int

    @Query("SELECT * FROM $DIDI_TABLE where wealth_ranking = :rank and villageId = :villageId and activeStatus = 1")
    fun getAllPoorDidisForVillage(villageId: Int, rank: String = WealthRank.POOR.rank): List<DidiEntity>

    @Query("UPDATE $DIDI_TABLE SET localPath = :path WHERE id = :didiId")
    fun saveLocalImagePath(path: String, didiId: Int)

    @Query("SELECT * FROM $DIDI_TABLE where needsToPostRanking = :needsToPostRanking AND villageId = :villageId")
    fun getAllNeedToPostDidiRanking(needsToPostRanking: Boolean,villageId: Int): List<DidiEntity>

    @Query("SELECT * FROM $DIDI_TABLE where needsToPostPAT = :needsToPostPAT AND villageId = :villageId")
    fun getAllNeedToPostPATDidi(needsToPostPAT: Boolean, villageId: Int): List<DidiEntity>

    @Query("SELECT * FROM $DIDI_TABLE where needsToPostBPCProcessStatus = :needsToPostBPCProcessStatus AND villageId = :villageId")
    fun getAllNeedToPostBPCProcessDidi(needsToPostBPCProcessStatus: Boolean, villageId: Int): List<DidiEntity>

    @Query("SELECT * FROM $DIDI_TABLE where needsToPostBPCProcessStatus = :needsToPostBPCProcessStatus AND villageId = :villageId AND transactionId != :transactionId")
    fun getAllPendingNeedToPostBPCProcessDidi(needsToPostBPCProcessStatus: Boolean, villageId: Int,transactionId: String): List<DidiEntity>

    @Query("DELETE FROM $DIDI_TABLE where cohortId =:tolaId")
    fun deleteDidisForTola(tolaId: Int)

    @Query("UPDATE $DIDI_TABLE SET activeStatus = :activeStatus, needsToPostDeleteStatus = :needsToPostDeleteStatus where cohortId = :tolaId")
    fun deleteDidisForTola(tolaId: Int, activeStatus: Int, needsToPostDeleteStatus: Boolean)

    @Query("UPDATE $DIDI_TABLE SET beneficiaryProcessStatus = :status WHERE id = :didiId")
    fun updateBeneficiaryProcessStatus(didiId: Int, status: List<BeneficiaryProcessStatusModel>)

    @Query("UPDATE $DIDI_TABLE SET beneficiaryProcessStatus = :status WHERE serverId = :didiId")
    fun updateBeneficiaryProcessStatusServerId(didiId: Int, status: List<BeneficiaryProcessStatusModel>)


    @Query("UPDATE $DIDI_TABLE SET patSurveyStatus = :patSurveyProgress WHERE id = :didiId")
    fun updateQuesSectionStatus(didiId: Int, patSurveyProgress: Int)
    @Query("select * from $DIDI_TABLE where cohortId = :tolaId and activeStatus = 1")
    fun getDidisForTola(tolaId: Int): List<DidiEntity>

    @Query("UPDATE $DIDI_TABLE SET section1Status = :section1 WHERE id = :didiId")
    fun updatePatSection1Status(didiId: Int, section1: Int)

    @Query("UPDATE $DIDI_TABLE SET section2Status = :section2 WHERE id = :didiId")
    fun updatePatSection2Status(didiId: Int, section2: Int)

    @Query("select * from $DIDI_TABLE where id = :didiId and activeStatus = 1")
    fun fetchDidiDetails(didiId: Int): DidiEntity

    @Query("update $DIDI_TABLE set shgFlag =:shgFlag where id = :didiId")
    fun updateDidiShgStatus(didiId: Int, shgFlag: Int)
    @Query("update $DIDI_TABLE set cohortName = :newName where cohortId = :id")
    fun updateTolaName(id: Int, newName: String)

    @Query("SELECT * from $DIDI_TABLE where needsToPost = :needsToPost and transactionId = :transactionId")
    fun fetchAllDidiNeedToPost( needsToPost: Boolean,transactionId : String?) : List<DidiEntity>

    @Query("DELETE from $DIDI_TABLE where needsToPost = :needsToPost")
    fun deleteDidiNeedToPost( needsToPost: Boolean)

    @Query("SELECT * FROM $DIDI_TABLE where needsToPostRanking = :needsToPostRanking")
    fun getAllNeedToPostDidiRanking(needsToPostRanking: Boolean): List<DidiEntity>

    @Query("select COUNT(*) from $DIDI_TABLE where villageId =:villageId AND patSurveyStatus=0 AND wealth_ranking='POOR' AND activeStatus = 1")
    fun fetchPendingDidiCount(villageId: Int): Int

    @Query("UPDATE $DIDI_TABLE set voEndorsementStatus =:status WHERE id =:didiId AND villageId = :villageId")
    fun updateVOEndorsementStatus(villageId: Int,didiId:Int,status:Int)

    @Query("UPDATE $DIDI_TABLE set forVoEndorsement = 1 WHERE id =:didiId AND villageId = :villageId")
    fun updateVOEndorsementDidiStatus(villageId: Int,didiId:Int)

    @Query("SELECT * FROM $DIDI_TABLE where villageId = :villageId AND patSurveyStatus = 2 AND activeStatus = 1 ORDER BY createdDate DESC")
    fun patCompletedDidis(villageId: Int): List<DidiEntity>

    @Query("UPDATE $DIDI_TABLE set patSurveyStatus = :patSurveyStatus,section1Status=:section1Status,section2Status=:section2Status,needsToPostPAT=0,shgFlag=:shgFlag WHERE id =:didiId")
    fun updatePATProgressStatus(patSurveyStatus: Int,section1Status:Int,section2Status:Int,didiId:Int,shgFlag:Int)

    @Query("UPDATE $DIDI_TABLE set needsToPostPAT =:needsToPostPAT WHERE id=:didiId AND villageId=:villageId")
    fun updateNeedToPostPAT(needsToPostPAT: Boolean,didiId: Int,villageId: Int)

    @Query("UPDATE $DIDI_TABLE set needsToPostPAT =:needsToPostPAT WHERE id=:didiId")
    fun updateNeedToPostPAT(needsToPostPAT: Boolean,didiId: Int)

    @Query("UPDATE $DIDI_TABLE set needsToPostBPCProcessStatus =:needsToPostBPCProcessStatus WHERE id=:didiId")
    fun updateNeedsToPostBPCProcessStatus(needsToPostBPCProcessStatus: Boolean,didiId: Int)

    @Query("UPDATE $DIDI_TABLE set needsToPostVo =:needsToPostVo WHERE id=:didiId AND villageId=:villageId")
    fun updateNeedToPostVO(needsToPostVo: Boolean,didiId: Int,villageId: Int)

    @Query("SELECT COUNT(*) FROM $DIDI_TABLE where villageId = :villageId AND patSurveyStatus< 2 AND wealth_ranking='POOR' AND activeStatus = 1 ORDER BY createdDate DESC")
    fun getAllPendingPATDidisCount(villageId: Int): Int

    @Query("SELECT * from $DIDI_TABLE where needsToPost = :needsToPost and transactionId != :transactionId")
    fun fetchPendingDidi(needsToPost: Boolean,transactionId : String?) : List<DidiEntity>

    @Query("UPDATE $DIDI_TABLE SET transactionId = :transactionId WHERE id = :id")
    fun updateDidiTransactionId(id: Int, transactionId: String)

    @Query("UPDATE $DIDI_TABLE SET transactionId = :transactionId WHERE serverId = :id")
    fun updateDidiTransactionIdWithServerId(id: Int, transactionId: String)

    @Query("UPDATE $DIDI_TABLE SET needsToPostRanking = :needsToPostRanking WHERE id = :didiId")
    fun updateDidiNeedToPostWealthRank(didiId: Int, needsToPostRanking: Boolean)

    @Query("UPDATE $DIDI_TABLE SET needsToPostRanking = :needsToPostRanking WHERE serverId = :didiId")
    fun updateDidiNeedToPostWealthRankServerId(didiId: Int, needsToPostRanking: Boolean)

    @Query("SELECT * from $DIDI_TABLE where needsToPostRanking = :needsToPostRanking and transactionId != :transactionId")
    fun fetchPendingWealthStatusDidi(needsToPostRanking: Boolean,transactionId : String?) : List<DidiEntity>

    @Query("UPDATE $DIDI_TABLE set needsToPostPAT =:needsToPostPAT WHERE id=:didiId")
    fun updateDidiNeedToPostPat(didiId: Int, needsToPostPAT: Boolean)

    @Query("SELECT * from $DIDI_TABLE where needsToPostPAT = :needsToPostPAT and transactionId != :transactionId")
    fun fetchPendingPatStatusDidi(needsToPostPAT: Boolean,transactionId : String?) : List<DidiEntity>

    @Query("UPDATE $DIDI_TABLE SET activeStatus = :activeStatus, needsToPostDeleteStatus = :needsToPostDeleteStatus where id = :id")
    fun deleteDidiOffline(id: Int, activeStatus: Int, needsToPostDeleteStatus: Boolean)

    @Query("DELETE from $DIDI_TABLE where activeStatus = :activeStatus and id = :id")
    fun deleteDidiFromDb(id: Int, activeStatus: Int)

    @Query("SELECT * from $DIDI_TABLE where needsToPostDeleteStatus = :needsToPostDeleteStatus and villageId=:villageId")
    fun getDidisToBeDeleted(villageId: Int, needsToPostDeleteStatus: Boolean): List<DidiEntity>

    @Query("UPDATE $DIDI_TABLE SET needsToPostDeleteStatus = :needsToPostDeleteStatus where id = :id")
    fun updateDeletedDidiNeedToPostStatus(id: Int, needsToPostDeleteStatus: Boolean)

    @Query("UPDATE $DIDI_TABLE SET cohortId = :cohortId WHERE id =:id")
    fun updateTolaIdForDidi(cohortId:Int,id:Int)

    @Query("SELECT COUNT(*) from $DIDI_TABLE where patSurveyStatus>2 AND villageId =:villageId AND activeStatus = 1")
    fun fetchNotAvailableDidis(villageId: Int) : Int

    @Query("UPDATE $DIDI_TABLE set needsToPostVo =:needsToPostVo WHERE id=:didiId ")
    fun updateNeedToPostVO(needsToPostVo: Boolean,didiId: Int)

    @Query("UPDATE $DIDI_TABLE set needsToPostVo =:needsToPostVo WHERE serverId=:didiId ")
    fun updateNeedToPostVOWithServerId(needsToPostVo: Boolean,didiId: Int)

    @Query("SELECT * from $DIDI_TABLE ORDER BY id DESC LIMIT 1")
    fun fetchLastDidiDetails(): DidiEntity

    @Query("SELECT * from $DIDI_TABLE where needsToPostVo = :needsToPostVo and transactionId = :transactionId")
    fun fetchAllVONeedToPostStatusDidi(needsToPostVo: Boolean,transactionId: String?): List<DidiEntity>

    @Query("SELECT * from $DIDI_TABLE where needsToPostVo = :needsToPostVo and transactionId != :transactionId")
    fun fetchPendingVOStatusStatusDidi(needsToPostVo: Boolean,transactionId: String?): List<DidiEntity>

    @Query("DELETE from $DIDI_TABLE")
    fun deleteAllDidi()

    @Query("SELECT * from $DIDI_TABLE where needsToPost = :needsToPost and transactionId = :transactionId and serverId = :serverId")
    fun fetchAllDidiNeedToAdd( needsToPost: Boolean,transactionId : String?,serverId : Int) : List<DidiEntity>

    @Query("SELECT * from $DIDI_TABLE where needsToPost = :needsToPost and transactionId = :transactionId and serverId != :serverId")
    fun fetchAllDidiNeedToUpdate( needsToPost: Boolean,transactionId : String?,serverId : Int) : List<DidiEntity>

    @Query("SELECT * from $DIDI_TABLE where activeStatus = :status")
    fun fetchAllDidiNeedToDelete(status: Int) : List<DidiEntity>

    @Query("SELECT * from $DIDI_TABLE where needsToPost = :needsToPost and transactionId != :transactionId and serverId != :serverId")
    fun fetchAllPendingDidiNeedToUpdate(  needsToPost: Boolean,transactionId : String?,serverId : Int) : List<DidiEntity>

    @Query("SELECT * from $DIDI_TABLE where activeStatus = :status and transactionId != :transactionId and serverId != :serverId")
    fun fetchAllPendingDidiNeedToDelete(status: Int,transactionId: String?,serverId : Int) : List<DidiEntity>

    @Query("DELETE from $DIDI_TABLE where id = :id")
    fun deleteDidi(id : Int)

    @Query("UPDATE $DIDI_TABLE set score =:score, comment=:comment,isDidiAccepted=:isDidiAccepted  WHERE id=:didiId ")
    fun updateDidiScore(score: Double,comment:String,isDidiAccepted:Boolean,didiId: Int)

    @Query("SELECT * from $DIDI_TABLE where forVoEndorsement = 1 AND section2Status=2 AND villageId=:villageId ORDER BY localCreatedDate DESC ")
    fun fetchVOEndorseStatusDidi(villageId: Int): List<DidiEntity>

    @Query("UPDATE $DIDI_TABLE set localModifiedDate =:localModifiedDate WHERE id=:didiId ")
    fun updateModifiedDate(localModifiedDate: Long,didiId: Int)

    @Query("UPDATE $DIDI_TABLE set localModifiedDate =:localModifiedDate WHERE serverId=:didiId ")
    fun updateModifiedDateServerId(localModifiedDate: Long,didiId: Int)

    @Query("SELECT * FROM $DIDI_TABLE where needsToPostVo = :needsToPostVo AND villageId = :villageId")
    fun getAllNeedToPostVoDidi(needsToPostVo: Boolean, villageId: Int): List<DidiEntity>

    @Query("UPDATE $DIDI_TABLE SET isExclusionYesSelected = :isExclusionYesSelected WHERE id = :didiId")
    fun updateExclusionStatus(didiId: Int, isExclusionYesSelected: Int)

    @Query("UPDATE $DIDI_TABLE SET localPath = :localPath WHERE id = :didiId")
    fun updateImageLocalPath(didiId: Int, localPath: String)

    @Query("UPDATE $DIDI_TABLE SET needsToPostImage = :needsToPostImage WHERE id =:id")
    fun updateNeedsToPostImage(id:Int, needsToPostImage: Boolean)

    @Query("SELECT * from $DIDI_TABLE where needsToPostImage = :needsToPostImage")
    fun fetchAllDidiNeedsToPostImage(needsToPostImage: Boolean) : List<DidiEntity>

    @Query("UPDATE $DIDI_TABLE SET rankingEdit = :rankingEdit where villageId = :villageId")
    fun updateRankEditFlag(villageId: Int, rankingEdit: Boolean)

    @Query("UPDATE $DIDI_TABLE SET patEdit = :patEdit where villageId = :villageId")
    fun updatePatEditFlag(villageId: Int, patEdit: Boolean)
}