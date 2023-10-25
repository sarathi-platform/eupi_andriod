package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository

import android.util.Log
import com.google.gson.JsonSyntaxException
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.COMPLETED_STRING
import com.nrlm.baselinesurvey.SUCCESS
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.dao.DidiDao
import com.nrlm.baselinesurvey.database.entity.DidiEntity
import com.nrlm.baselinesurvey.network.interfaces.ApiService
import com.nrlm.baselinesurvey.utils.BaselineCore
import javax.inject.Inject

class SurveyeeListScreenRepositoryImpl @Inject constructor(
    private val prefRepo: PrefRepo,
    private val apiService: ApiService,
    private val didiDao: DidiDao
): SurveyeeListScreenRepository {

    override suspend fun getSurveyeeList(): List<DidiEntity> {
        try {
            val didiListResponse = apiService.getDidisFromNetwork()
            if (didiListResponse.status.equals(SUCCESS, true)) {
                didiListResponse.data?.let {
                    if (it.didiList.isNotEmpty()) {
                        try {
                            it.didiList.forEach { didi ->
                                var tolaName = BLANK_STRING
                                var casteName = BLANK_STRING
                                /*val singleTola =
                                    tolaDao.fetchSingleTola(didi.cohortId)
                                val singleCaste =
                                    casteListDao.getCaste(didi.castId, prefRepo?.getAppLanguageId()?:2)
                                singleTola?.let {
                                    tolaName = it.name
                                }
                                singleCaste?.let {
                                    casteName = it.casteName
                                }*/
                                val wealthRanking = if (didi.beneficiaryProcessStatus.map { it.name }.contains("WEALTH_RANKING"))
                                    didi.beneficiaryProcessStatus[didi.beneficiaryProcessStatus.map { process -> process.name }.indexOf("WEALTH_RANKING")].status
                                else
                                    "NOT_RANKED"
                                /*val patSurveyAcceptedRejected =
                                    if (didi.beneficiaryProcessStatus.map { it.name }
                                            .contains(StepType.PAT_SURVEY.name)) didi.beneficiaryProcessStatus[didi.beneficiaryProcessStatus.map { process -> process.name }
                                        .indexOf(StepType.PAT_SURVEY.name)].status
                                    else DIDI_REJECTED
                                val voEndorsementStatus =
                                    if (didi.beneficiaryProcessStatus.map { it.name }
                                            .contains(StepType.VO_ENDROSEMENT.name)) DidiEndorsementStatus.toInt(
                                        didi.beneficiaryProcessStatus[didi.beneficiaryProcessStatus.map { process -> process.name }
                                            .indexOf(StepType.VO_ENDROSEMENT.name)].status)
                                    else DidiEndorsementStatus.NOT_STARTED.ordinal*/

                                didiDao.insertDidi(
                                    DidiEntity(
                                        id = didi.id,
                                        serverId = didi.id,
                                        name = didi.name,
                                        address = didi.address,
                                        guardianName = didi.guardianName,
                                        relationship = didi.relationship,
                                        castId = didi.castId,
                                        castName = casteName,
                                        cohortId = didi.cohortId,
                                        villageId = 57012,
                                        cohortName = tolaName,
                                        needsToPost = false,
                                        wealth_ranking = wealthRanking,
                                        forVoEndorsement = 1,
                                        voEndorsementStatus = 2,
                                        needsToPostRanking = false,
                                        createdDate = didi.createdDate,
                                        modifiedDate = didi.modifiedDate,
                                        beneficiaryProcessStatus = didi.beneficiaryProcessStatus,
                                        shgFlag = 1,
                                        transactionId = "",
                                        localCreatedDate = didi.localCreatedDate,
                                        localModifiedDate = didi.localModifiedDate,
                                        activeStatus = 1,
                                        score = didi.crpScore,
                                        crpScore = didi.crpScore,
                                        crpComment = didi.crpComment,
                                        comment = didi.comment,
                                        crpUploadedImage = didi.crpUploadedImage,
                                        needsToPostImage = false,
                                        rankingEdit = didi.rankingEdit,
                                        patEdit = didi.patEdit,
                                        voEndorsementEdit = didi.voEndorsementEdit,
                                        ableBodiedFlag = 1
                                    )
                                )
                                if(!didi.crpUploadedImage.isNullOrEmpty()){
                                    BaselineCore.downloadAuthorizedImageItem(didi.id,didi.crpUploadedImage?: BLANK_STRING, prefRepo = prefRepo, didiDao)
                                }
                            }
                        } catch (ex: Exception) {
                            Log.e(
                                "SurveyeeListScreenRepositoryImpl",
                                "Error : ${didiListResponse.message}"
                            )

                        }
                    }
                }
                return didiDao.getAllDidis()
            } else {
                val localList = didiDao.getAllDidis()
                return localList.ifEmpty { emptyList() }
            }
        } catch (ex: Exception) {
            Log.e(
                "SurveyeeListScreenRepositoryImpl",
                "Error : ${ex.message}",
                ex
            )
            val localList = didiDao.getAllDidis()
            return localList.ifEmpty { emptyList() }
        }
    }




}