package com.patsurvey.nudge.activities.ui.bpc.progress_screens

import android.app.DownloadManager
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonSyntaxException
import com.patsurvey.nudge.MyApplication
import com.patsurvey.nudge.RetryHelper
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.BpcSummaryEntity
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.NumericAnswerEntity
import com.patsurvey.nudge.database.PoorDidiEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.BpcSummaryDao
import com.patsurvey.nudge.database.dao.CasteListDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.LanguageListDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.PoorDidiListDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.TrainingVideoDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.download.AndroidDownloader
import com.patsurvey.nudge.download.FileType
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.request.AddWorkFlowRequest
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.model.response.DidiApiResponse
import com.patsurvey.nudge.model.response.WorkFlowResponse
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.AbleBodiedFlag
import com.patsurvey.nudge.utils.ApiResponseFailException
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.BPC_USER_TYPE
import com.patsurvey.nudge.utils.COMPLETED_STRING
import com.patsurvey.nudge.utils.DIDI_REJECTED
import com.patsurvey.nudge.utils.DOUBLE_ZERO
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.LAST_UPDATE_TIME
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PREF_BPC_DIDI_LIST_SYNCED_FOR_VILLAGE_
import com.patsurvey.nudge.utils.PREF_BPC_PAT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_NAME
import com.patsurvey.nudge.utils.PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_
import com.patsurvey.nudge.utils.PREF_PAT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_PROGRAM_NAME
import com.patsurvey.nudge.utils.PREF_SOCIAL_MAPPING_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_TRANSECT_WALK_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_VO_ENDORSEMENT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_WEALTH_RANKING_COMPLETION_DATE_
import com.patsurvey.nudge.utils.QUESTION_FLAG_WEIGHT
import com.patsurvey.nudge.utils.QuestionType
import com.patsurvey.nudge.utils.SHGFlag
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.StepType
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.USER_BPC
import com.patsurvey.nudge.utils.WealthRank
import com.patsurvey.nudge.utils.findCompleteValue
import com.patsurvey.nudge.utils.formatRatio
import com.patsurvey.nudge.utils.getAuthImagePath
import com.patsurvey.nudge.utils.intToString
import com.patsurvey.nudge.utils.stringToDouble
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BPCProgressScreenRepository @Inject constructor(
    val prefRepo: PrefRepo,
    val apiService: ApiService,
    val villageListDao: VillageListDao,
    val stepsListDao: StepsListDao,
    val tolaDao: TolaDao,
    val casteListDao: CasteListDao,
    val languageListDao: LanguageListDao,
    val questionListDao: QuestionListDao,
    val trainingVideoDao: TrainingVideoDao,
    val numericAnswerDao: NumericAnswerDao,
    val answerDao: AnswerDao,
    val bpcSummaryDao: BpcSummaryDao,
    val poorDidiListDao: PoorDidiListDao,
    val androidDownloader: AndroidDownloader
) : BaseRepository() {

    private val TAG = BPCProgressScreenRepository::class.java.simpleName
    fun getBpcSummaryForVillage(villageId: Int): BpcSummaryEntity? {
        return bpcSummaryDao.getBpcSummaryForVillage(villageId = villageId)
    }

    fun getAllVillages(): List<VillageEntity> {
        return villageListDao.getAllVillages(prefRepo.getAppLanguageId() ?: 2)
    }

    fun getAllStepsForVillage(villageId: Int): List<StepListEntity> {
        return stepsListDao.getAllStepsForVillage(villageId)
    }

    suspend fun addWorkFlow(addWorkFlowRequest: List<AddWorkFlowRequest>): ApiResponseModel<List<WorkFlowResponse>> {
        NudgeLogger.d(TAG, "addWorkFlow Request=>${Gson().toJson(addWorkFlowRequest)}")
        return apiInterface.addWorkFlow(addWorkFlowRequest)
    }

    fun updateWorkflowId(stepId: Int, workflowId: Int, villageId: Int, status: String) {
        stepsListDao.updateWorkflowId(stepId, workflowId, villageId, status)
    }

    fun getAllDidisForVillage(): List<DidiEntity> {
        return didiDao.getAllDidisForVillage(prefRepo.getSelectedVillage().id)
    }

    fun getAllInclusiveQues(didiId: Int): List<SectionAnswerEntity> {
        return answerDao.getAllInclusiveQues(didiId = didiId)
    }

    fun getTotalWeightWithoutNumQues(didiId: Int): Double {
        return answerDao.getTotalWeightWithoutNumQues(didiId)
    }

    fun getQuestion(questionId: Int): QuestionEntity {
        return questionListDao.getQuestion(questionId)
    }

    fun updateVOEndorsementDidiStatus(didiId: Int, status: Int) {
        didiDao.updateVOEndorsementDidiStatus(
            prefRepo.getSelectedVillage().id,
            didiId,
            status
        )
    }

    fun updateDidiScore(score: Double, comment: String, isDidiAccepted: Boolean, didiId: Int) {
        didiDao.updateDidiScore(
            score = score,
            comment = comment,
            didiId = didiId,
            isDidiAccepted = isDidiAccepted
        )
    }

    fun updateModifiedDateServerId(didiId: Int) {
        didiDao.updateModifiedDateServerId(System.currentTimeMillis(), didiId)
    }

    fun getSelectedVillage(): VillageEntity = prefRepo.getSelectedVillage()
    fun getBpcSummaryDataForSelectedVillage(): LiveData<BpcSummaryEntity> {
        return bpcSummaryDao.getBpcSummaryForVillageLiveData(prefRepo.getSelectedVillage().id)
    }

    suspend fun fetchBpcSummaryDataForVillageFromNetwork(forceRefresh: Boolean, village: VillageEntity) {
        try {
            NudgeLogger.d(
                TAG, "fetchBpcSummaryDataForVillageFromNetwork " +
                        "village.id = ${village.id}"
            )
            val bpcSummaryResponse =
                apiService.getBpcSummary(villageId = village.id)
            NudgeLogger.d(
                TAG, "fetchBpcSummaryDataForVillageFromNetwork " +
                        "bpcSummaryResponse status = ${bpcSummaryResponse.status}, message = ${bpcSummaryResponse.message}, data = ${bpcSummaryResponse.data.toString()}"
            )
            if (bpcSummaryResponse.status.equals(SUCCESS, true)) {
                bpcSummaryResponse.data?.let {
                    val bpcSummary = BpcSummaryEntity(
                        cohortCount = it.cohortCount,
                        mobilisedCount = it.mobilisedCount,
                        poorDidiCount = it.poorDidiCount,
                        sentVoEndorsementCount = it.sentVoEndorsementCount,
                        voEndorsedCount = it.voEndorsedCount,
                        villageId = village.id
                    )
                    NudgeLogger.d(
                        TAG, "fetchBpcSummaryDataForVillageFromNetwork " +
                                "bpcSummaryDao.insert(bpcSummary) before"
                    )

//                    bpcSummaryDao.insert(bpcSummary)
                    bpcSummaryDao.updateBpcSummaryData(forceRefresh, village.id, bpcSummary)
                    NudgeLogger.d(
                        TAG, "fetchBpcSummaryDataForVillageFromNetwork " +
                                "bpcSummaryDao.insert(bpcSummary) after"
                    )
                }
            } else {
                NudgeLogger.d(
                    TAG, "fetchBpcSummaryDataForVillageFromNetwork " +
                            "bpcSummaryDao.insert(BpcSummaryEntity(0, 0, 0, 0, 0, 0, villageId = village.id))"
                )
                bpcSummaryDao.insert(
                    BpcSummaryEntity.getEmptySummaryForVillage(village.id)
                )

                val ex = ApiResponseFailException(bpcSummaryResponse.message)
                if (!RetryHelper.retryApiList.contains(ApiType.BPC_SUMMARY_API)) RetryHelper.retryApiList.add(
                    ApiType.BPC_SUMMARY_API
                )
                RetryHelper.stepListApiVillageId.add(village.id)
                onCatchError(ex, ApiType.BPC_SUMMARY_API)
            }
        } catch (ex: Exception) {
            bpcSummaryDao.insert(
                BpcSummaryEntity.getEmptySummaryForVillage(village.id)
            )
            NudgeLogger.e(
                TAG,
                "fetchBpcSummaryDataForVillageFromNetwork catch" +
                        "bpcSummaryDao.insert(BpcSummaryEntity(0, 0, 0, 0, 0, 0, villageId = village.id))",
                ex
            )
            if (ex !is JsonSyntaxException) {
                if (!RetryHelper.retryApiList.contains(ApiType.BPC_SUMMARY_API)) RetryHelper.retryApiList.add(
                    ApiType.BPC_SUMMARY_API
                )
                RetryHelper.stepListApiVillageId.add(village.id)
            }
            onCatchError(ex, ApiType.BPC_SUMMARY_API)
        }
    }

    fun isSummaryAlreadyExistsForVillage(villageId: Int): Int {
        return bpcSummaryDao.isSummaryAlreadyExistsForVillage(villageId)
    }

    fun fetchBpcDataForVillage(
        forceRefresh: Boolean = false,
        village: VillageEntity,
        networkCallbackListener: NetworkCallbackListener
    ) {
        repoJob = MyApplication.appScopeLaunch(Dispatchers.IO) {
            val awaitDiff = CoroutineScope(Dispatchers.IO + exceptionHandler).async {
                try {
                    val villageList =
                        villageListDao.getAllVillages(prefRepo.getAppLanguageId() ?: 2)
                    val villageIdList: ArrayList<Int> = arrayListOf()

                    val localAnswerList = answerDao.getAllAnswer()
                    if (localAnswerList.isNotEmpty()) {
                        answerDao.deleteAnswerTable()
                    }
                    val localNumAnswerList = numericAnswerDao.getAllNumericAnswers()
                    if (localNumAnswerList.isNotEmpty()) {
                        numericAnswerDao.deleteNumericTable()
                    }
                    try {
                        NudgeLogger.d(
                            "VillageSelectionScreen",
                            "fetchDataForBpc getStepsList request -> village.id = ${village.id}"
                        )
                        val response = apiService.getStepsList(village.id)
                        NudgeLogger.d(
                            "VillageSelectionScreen", "fetchDataForBpc getStepsList " +
                                    "response status = ${response.status}, message = ${response.message}, data = ${response.data.toString()}"
                        )
                        if (response.status.equals(SUCCESS, true)) {
                            response.data?.let {
                                if (it.stepList.isNotEmpty()) {
                                    it.stepList.forEach { steps ->
                                        steps.villageId = village.id
                                        steps.isComplete =
                                            findCompleteValue(steps.status).ordinal


                                        if (steps.id == 40) {
                                            prefRepo.savePref(
                                                PREF_TRANSECT_WALK_COMPLETION_DATE_ + village.id,
                                                steps.localModifiedDate
                                                    ?: System.currentTimeMillis()
                                            )
                                        }

                                        if (steps.id == 41) {
                                            prefRepo.savePref(
                                                PREF_SOCIAL_MAPPING_COMPLETION_DATE_ + village.id,
                                                steps.localModifiedDate
                                                    ?: System.currentTimeMillis()
                                            )
                                        }

                                        if (steps.id == 46) {
                                            prefRepo.savePref(
                                                PREF_WEALTH_RANKING_COMPLETION_DATE_ + village.id,
                                                steps.localModifiedDate
                                                    ?: System.currentTimeMillis()
                                            )
                                        }

                                        if (steps.id == 43) {
                                            prefRepo.savePref(
                                                PREF_PAT_COMPLETION_DATE_ + village.id,
                                                steps.localModifiedDate
                                                    ?: System.currentTimeMillis()
                                            )
                                        }
                                        if (steps.id == 44) {
                                            prefRepo.savePref(
                                                PREF_VO_ENDORSEMENT_COMPLETION_DATE_ + village.id,
                                                steps.localModifiedDate
                                                    ?: System.currentTimeMillis()
                                            )
                                        }

                                        if (steps.id == 45) {
                                            prefRepo.savePref(
                                                PREF_BPC_PAT_COMPLETION_DATE_ + village.id,
                                                steps.localModifiedDate
                                                    ?: System.currentTimeMillis()
                                            )
                                        }
//                                            if(steps.id == 44){
//                                                prefRepo.savePref(
//                                                    PREF_WEALTH_RANKING_COMPLETION_DATE, steps.localModifiedDate?: BLANK_STRING)
//                                            }
                                    }
                                    NudgeLogger.d(
                                        "VillageSelectionScreen",
                                        "fetchDataForBpc getStepsList " +
                                                "stepsListDao.insertAll(it.stepList) before"
                                    )
//                                    stepsListDao.insertAll(it.stepList)
                                    stepsListDao.updateStepListForVillage(forceRefresh, village.id, it.stepList)

                                    NudgeLogger.d(
                                        "VillageSelectionScreen",
                                        "fetchDataForBpc getStepsList " +
                                                "stepsListDao.insertAll(it.stepList) after"
                                    )
                                    val bpcStepId =
                                        it.stepList.sortedBy { stepEntity -> stepEntity.orderNumber }
                                            .last().id
                                    if (it.stepList[it.stepList.map { it.id }
                                            .indexOf(bpcStepId)].status != StepStatus.COMPLETED.name)
                                        stepsListDao.markStepAsCompleteOrInProgress(
                                            bpcStepId,
                                            StepStatus.INPROGRESS.ordinal,
                                            village.id
                                        )
                                }
                                prefRepo.savePref(
                                    PREF_PROGRAM_NAME, it.programName
                                )
                            }
                        } else {
                            val ex = ApiResponseFailException(response.message)
                            /* if (!RetryHelper.retryApiList.contains(ApiType.STEP_LIST_API)) RetryHelper.retryApiList.add(
                                 ApiType.STEP_LIST_API
                             )
                             RetryHelper.stepListApiVillageId.add(village.id)*/
                            onCatchError(ex, ApiType.STEP_LIST_API)
                        }
                    } catch (ex: Exception) {
                        /*if (ex !is JsonSyntaxException) {
                            if (!RetryHelper.retryApiList.contains(ApiType.STEP_LIST_API)) RetryHelper.retryApiList.add(
                                ApiType.STEP_LIST_API
                            )
                            RetryHelper.stepListApiVillageId.add(village.id)
                        }*/
                        onCatchError(ex, ApiType.STEP_LIST_API)
                        networkCallbackListener.onFailed()
                    }

                    try {
                        NudgeLogger.d(
                            "VillageSelectionScreen", "fetchDataForBpc getCohortFromNetwork " +
                                    "request village.id = ${village.id}"
                        )
                        val cohortResponse =
                            apiService.getCohortFromNetwork(villageId = village.id)
                        NudgeLogger.d(
                            "VillageSelectionScreen", "fetchDataForBpc getCohortFromNetwork " +
                                    "cohortResponse status = ${cohortResponse.status}, message = ${cohortResponse.message}, data = ${cohortResponse.data.toString()}"
                        )
                        if (cohortResponse.status.equals(SUCCESS, true)) {
                            cohortResponse.data?.let {
                                NudgeLogger.d(
                                    "VillageSelectionScreen",
                                    "fetchDataForBpc getCohortFromNetwork " +
                                            "tolaDao.insertAll(it) before"
                                )
//                                tolaDao.insertAll(it)
                                tolaDao.updateTolaData(forceRefresh, village.id, it)
                                NudgeLogger.d(
                                    "VillageSelectionScreen",
                                    "fetchDataForBpc getCohortFromNetwork " +
                                            "tolaDao.insertAll(it) after"
                                )
                            }
                        } else {
                            val ex = ApiResponseFailException(cohortResponse.message)
                            /*if (!RetryHelper.retryApiList.contains(ApiType.TOLA_LIST_API)) RetryHelper.retryApiList.add(
                                ApiType.TOLA_LIST_API
                            )
                            RetryHelper.stepListApiVillageId.add(village.id)*/
                            onCatchError(ex, ApiType.TOLA_LIST_API)
                            networkCallbackListener.onFailed()
                        }
                    } catch (ex: Exception) {
                        /*if (ex !is JsonSyntaxException) {
                            if (!RetryHelper.retryApiList.contains(ApiType.TOLA_LIST_API)) RetryHelper.retryApiList.add(
                                ApiType.TOLA_LIST_API
                            )
                            RetryHelper.stepListApiVillageId.add(village.id)
                        }*/
                        onCatchError(ex, ApiType.TOLA_LIST_API)
                        networkCallbackListener.onFailed()
                    }

                    try {
                        NudgeLogger.d(
                            "VillageSelectionScreen",
                            "fetchDataForBpc getDidiForBpcFromNetwork " +
                                    "request village.id = ${village.id}"
                        )
                        val didiResponse =
                            apiService.getDidiForBpcFromNetwork(villageId = village.id)
                        NudgeLogger.d(
                            "VillageSelectionScreen",
                            "fetchDataForBpc getDidiForBpcFromNetwork " +
                                    "didiResponse status = ${didiResponse.status}, message = ${didiResponse.message}, data = ${didiResponse.data.toString()}"
                        )
                        if (didiResponse.status.equals(SUCCESS, true)) {
                            didiResponse.data?.let { didiList ->
                                if (didiList.isNotEmpty()) {
                                    try {
                                        didiList.forEach { didi ->
                                            var tolaName = BLANK_STRING
                                            var casteName = BLANK_STRING
                                            val singleTola =
                                                tolaDao.fetchSingleTola(didi.cohortId)
                                            val singleCaste =
                                                casteListDao.getCaste(
                                                    didi.castId,
                                                    prefRepo?.getAppLanguageId() ?: 2
                                                )
                                            singleTola?.let {
                                                tolaName = it.name
                                            }
                                            singleCaste?.let {
                                                casteName = it.casteName
                                            }
//                                                    if (singleTola != null) {
                                            val wealthRanking =
                                                if (didi.beneficiaryProcessStatus?.map { it.name }
                                                        ?.contains(StepType.WEALTH_RANKING.name) == true) didi.beneficiaryProcessStatus?.get(
                                                    didi.beneficiaryProcessStatus!!.map { process -> process.name }
                                                        .indexOf(StepType.WEALTH_RANKING.name))?.status
                                                else WealthRank.NOT_RANKED.rank
                                            val patSurveyAcceptedRejected =
                                                if (didi.beneficiaryProcessStatus?.map { it.name }
                                                        ?.contains(StepType.PAT_SURVEY.name) == true) didi.beneficiaryProcessStatus?.get(
                                                    didi.beneficiaryProcessStatus!!.map { process -> process.name }
                                                        .indexOf(StepType.PAT_SURVEY.name))?.status
                                                else DIDI_REJECTED
                                            val voEndorsementStatus =
                                                if (didi.beneficiaryProcessStatus?.map { it.name }
                                                        ?.contains(StepType.VO_ENDROSEMENT.name) == true) didi.beneficiaryProcessStatus!![didi.beneficiaryProcessStatus!!.map { process -> process.name }
                                                    .indexOf(StepType.VO_ENDROSEMENT.name)]?.let {
                                                    DidiEndorsementStatus.toInt(
                                                        it.status
                                                    )
                                                }
                                                else DidiEndorsementStatus.NOT_STARTED.ordinal

                                            /*didiDao.insertDidi(
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
                                                    villageId = village.id,
                                                    cohortName = tolaName,
                                                    needsToPost = false,
                                                    wealth_ranking = wealthRanking
                                                        ?: BLANK_STRING,
                                                    forVoEndorsement = if (patSurveyAcceptedRejected.equals(
                                                            COMPLETED_STRING, true
                                                        )
                                                    ) 1 else 0,
                                                    voEndorsementStatus = voEndorsementStatus,
                                                    needsToPostRanking = false,
                                                    createdDate = didi.createdDate,
                                                    modifiedDate = didi.modifiedDate,
                                                    beneficiaryProcessStatus = didi.beneficiaryProcessStatus,
                                                    shgFlag = SHGFlag.fromSting(
                                                        intToString(didi.shgFlag)
                                                            ?: SHGFlag.NOT_MARKED.name
                                                    ).value,
                                                    transactionId = "",
                                                    localCreatedDate = didi.localCreatedDate,
                                                    localModifiedDate = didi.localModifiedDate,
                                                    score = didi.bpcScore ?: 0.0,
                                                    comment = didi.bpcComment ?: BLANK_STRING,
                                                    crpScore = didi.crpScore,
                                                    crpComment = didi.crpComment,
                                                    bpcScore = didi.bpcScore ?: 0.0,
                                                    bpcComment = didi.bpcComment
                                                        ?: BLANK_STRING,
                                                    crpUploadedImage = didi.crpUploadedImage,
                                                    needsToPostImage = false,
                                                    rankingEdit = didi.rankingEdit,
                                                    patEdit = didi.patEdit,
                                                    voEndorsementEdit = didi.voEndorsementEdit,
                                                    ableBodiedFlag = AbleBodiedFlag.fromSting(
                                                        intToString(didi.ableBodiedFlag)
                                                            ?: AbleBodiedFlag.NOT_MARKED.name
                                                    ).value
                                                )
                                            )*/
                                            val didiEntity = DidiEntity(
                                                id = didi.id,
                                                serverId = didi.id,
                                                name = didi.name,
                                                address = didi.address,
                                                guardianName = didi.guardianName,
                                                relationship = didi.relationship,
                                                castId = didi.castId,
                                                castName = casteName,
                                                cohortId = didi.cohortId,
                                                villageId = village.id,
                                                cohortName = tolaName,
                                                needsToPost = false,
                                                wealth_ranking = wealthRanking
                                                    ?: BLANK_STRING,
                                                forVoEndorsement = if (patSurveyAcceptedRejected.equals(
                                                        COMPLETED_STRING, true
                                                    )
                                                ) 1 else 0,
                                                voEndorsementStatus = voEndorsementStatus,
                                                needsToPostRanking = false,
                                                createdDate = didi.createdDate,
                                                modifiedDate = didi.modifiedDate,
                                                beneficiaryProcessStatus = didi.beneficiaryProcessStatus,
                                                shgFlag = SHGFlag.fromSting(
                                                    intToString(didi.shgFlag)
                                                        ?: SHGFlag.NOT_MARKED.name
                                                ).value,
                                                transactionId = "",
                                                localCreatedDate = didi.localCreatedDate,
                                                localModifiedDate = didi.localModifiedDate,
                                                score = didi.bpcScore ?: 0.0,
                                                comment = didi.bpcComment ?: BLANK_STRING,
                                                crpScore = didi.crpScore,
                                                crpComment = didi.crpComment,
                                                bpcScore = didi.bpcScore ?: 0.0,
                                                bpcComment = didi.bpcComment
                                                    ?: BLANK_STRING,
                                                crpUploadedImage = didi.crpUploadedImage,
                                                needsToPostImage = false,
                                                rankingEdit = didi.rankingEdit,
                                                patEdit = didi.patEdit,
                                                voEndorsementEdit = didi.voEndorsementEdit,
                                                ableBodiedFlag = AbleBodiedFlag.fromSting(
                                                    intToString(didi.ableBodiedFlag)
                                                        ?: AbleBodiedFlag.NOT_MARKED.name
                                                ).value
                                            )
                                            didiDao.updateDidiAfterRefresh(forceRefresh, didi.id, didiEntity)
//                                                    }
                                            if (!didi.crpUploadedImage.isNullOrEmpty()) {
                                                downloadAuthorizedImageItem(
                                                    didi.id,
                                                    didi.crpUploadedImage ?: BLANK_STRING,
                                                    prefRepo = prefRepo
                                                )
                                            }
                                        }
                                    } catch (ex: Exception) {
                                        onError(
                                            tag = "VillageSelectionViewModel",
                                            "Error : ${didiResponse.message}"
                                        )
                                        networkCallbackListener.onFailed()
                                    }
                                }
                            }
                        } else {
                            val ex = ApiResponseFailException(didiResponse.message)
                            /*if (!RetryHelper.retryApiList.contains(ApiType.BPC_DIDI_LIST_API)) RetryHelper.retryApiList.add(
                                ApiType.BPC_DIDI_LIST_API
                            )
                            RetryHelper.stepListApiVillageId.add(village.id)*/
                            onCatchError(ex, ApiType.BPC_DIDI_LIST_API)
                            networkCallbackListener.onFailed()
                        }
                    } catch (ex: Exception) {
                        /*if (ex !is JsonSyntaxException) {
                            if (!RetryHelper.retryApiList.contains(ApiType.BPC_DIDI_LIST_API)) RetryHelper.retryApiList.add(
                                ApiType.BPC_DIDI_LIST_API
                            )
                            RetryHelper.stepListApiVillageId.add(village.id)
                        }*/
                        onCatchError(ex, ApiType.BPC_DIDI_LIST_API)
                        networkCallbackListener.onFailed()
                    }

                    try {
                        NudgeLogger.d(
                            "VillageSelectionScreen",
                            "fetchDataForBpc getDidisWithRankingFromNetwork " +
                                    "request -> villageId = village.id, \"Category\", StepResultTypeRequest(\n" +
                                    "                                    StepType.WEALTH_RANKING.name, ResultType.POOR.name"
                        )
                        val poorDidiList = apiService.getDidisFromNetwork(village.id)/*apiService.getDidisWithRankingFromNetwork(
                                villageId = village.id, "Category", StepResultTypeRequest(
                                    StepType.WEALTH_RANKING.name, ResultType.POOR.name
                                )
                            )*/
                        NudgeLogger.d(
                            "VillageSelectionScreen",
                            "fetchDataForBpc getDidisWithRankingFromNetwork " +
                                    "poorDidiList status = ${poorDidiList.status}, message = ${poorDidiList.message}, data = ${poorDidiList.data.toString()}"
                        )
                        if (poorDidiList.status.equals(SUCCESS, true)) {
                            poorDidiList.data?.let { didiRank ->
                                if (didiRank.didiList.isNotEmpty()) {
                                    didiRank.didiList.forEach { poorDidis ->
                                        poorDidis?.let { didi ->
                                            var tolaName = BLANK_STRING
                                            var casteName = BLANK_STRING
                                            val singleTola =
                                                tolaDao.fetchSingleTola(didi.cohortId)
                                            val singleCaste =
                                                casteListDao.getCaste(
                                                    didi.castId,
                                                    prefRepo?.getAppLanguageId() ?: 2
                                                )
                                            singleTola?.let {
                                                tolaName = it.name
                                            }
                                            singleCaste?.let {
                                                casteName = it.casteName
                                            }
//                                                    if (singleTola != null) {
                                            val wealthRanking =
                                                if (didi.beneficiaryProcessStatus.map { it.name }
                                                        .contains(StepType.WEALTH_RANKING.name)) didi.beneficiaryProcessStatus[didi.beneficiaryProcessStatus.map { process -> process.name }
                                                    .indexOf(StepType.WEALTH_RANKING.name)].status
                                                else WealthRank.NOT_RANKED.rank
                                            val patSurveyAcceptedRejected =
                                                if (didi.beneficiaryProcessStatus.map { it.name }
                                                        .contains(StepType.PAT_SURVEY.name)) didi.beneficiaryProcessStatus[didi.beneficiaryProcessStatus.map { process -> process.name }
                                                    .indexOf(StepType.PAT_SURVEY.name)].status
                                                else DIDI_REJECTED
                                            val voEndorsementStatus =
                                                if (didi.beneficiaryProcessStatus.map { it.name }
                                                        .contains(StepType.VO_ENDROSEMENT.name)) DidiEndorsementStatus.toInt(
                                                    didi.beneficiaryProcessStatus[didi.beneficiaryProcessStatus.map { process -> process.name }
                                                        .indexOf(StepType.VO_ENDROSEMENT.name)].status)
                                                else DidiEndorsementStatus.NOT_STARTED.ordinal

                                            NudgeLogger.d(
                                                "VillageSelectionScreen",
                                                "fetchDataForBpc getDidisWithRankingFromNetwork " +
                                                        "poorDidiListDao.insertPoorDidi() didiId = ${didi.id} before"
                                            )
                                            /*poorDidiListDao.insertPoorDidi(
                                                PoorDidiEntity(
                                                    id = didi.id,
                                                    serverId = didi.id,
                                                    name = didi.name,
                                                    address = didi.address,
                                                    guardianName = didi.guardianName,
                                                    relationship = didi.relationship,
                                                    castId = didi.castId,
                                                    castName = casteName,
                                                    cohortId = didi.cohortId,
                                                    villageId = village.id,
                                                    cohortName = tolaName,
                                                    needsToPost = false,
                                                    wealth_ranking = wealthRanking,
                                                    forVoEndorsement = if (patSurveyAcceptedRejected.equals(
                                                            COMPLETED_STRING, true
                                                        )
                                                    ) 1 else 0,
                                                    voEndorsementStatus = voEndorsementStatus,
                                                    needsToPostRanking = false,
                                                    createdDate = didi.createdDate,
                                                    modifiedDate = didi.modifiedDate,
                                                    beneficiaryProcessStatus = didi.beneficiaryProcessStatus,
                                                    shgFlag = SHGFlag.fromSting(
                                                        didi.shgFlag ?: SHGFlag.NOT_MARKED.name
                                                    ).value,
                                                    transactionId = "",
                                                    localCreatedDate = didi.localCreatedDate,
                                                    localModifiedDate = didi.localModifiedDate,
                                                    score = didi.score,
                                                    crpScore = didi.crpScore,
                                                    crpComment = didi.crpComment,
                                                    comment = didi.comment,
                                                    crpUploadedImage = didi.crpUploadedImage,
                                                    needsToPostImage = false,
                                                    rankingEdit = didi.rankingEdit,
                                                    patEdit = didi.patEdit
                                                )
                                            )*/

                                            val poorDidi = PoorDidiEntity(
                                                id = didi.id,
                                                serverId = didi.id,
                                                name = didi.name,
                                                address = didi.address,
                                                guardianName = didi.guardianName,
                                                relationship = didi.relationship,
                                                castId = didi.castId,
                                                castName = casteName,
                                                cohortId = didi.cohortId,
                                                villageId = village.id,
                                                cohortName = tolaName,
                                                needsToPost = false,
                                                wealth_ranking = wealthRanking,
                                                forVoEndorsement = if (patSurveyAcceptedRejected.equals(
                                                        COMPLETED_STRING, true
                                                    )
                                                ) 1 else 0,
                                                voEndorsementStatus = voEndorsementStatus,
                                                needsToPostRanking = false,
                                                createdDate = didi.createdDate,
                                                modifiedDate = didi.modifiedDate,
                                                beneficiaryProcessStatus = didi.beneficiaryProcessStatus,
                                                shgFlag = SHGFlag.fromSting(
                                                    didi.shgFlag ?: SHGFlag.NOT_MARKED.name
                                                ).value,
                                                transactionId = "",
                                                localCreatedDate = didi.localCreatedDate,
                                                localModifiedDate = didi.localModifiedDate,
                                                score = didi.score,
                                                crpScore = didi.crpScore,
                                                crpComment = didi.crpComment,
                                                comment = didi.comment,
                                                crpUploadedImage = didi.crpUploadedImage,
                                                needsToPostImage = false,
                                                rankingEdit = didi.rankingEdit,
                                                patEdit = didi.patEdit
                                            )
                                            poorDidiListDao.updatePoorDidiAfterRefresh(forceRefresh, didi.id, poorDidi)
                                            NudgeLogger.d(
                                                "VillageSelectionScreen",
                                                "fetchDataForBpc getDidisWithRankingFromNetwork " +
                                                        "poorDidiListDao.insertPoorDidi() didiId = ${didi.id} after"
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            val ex = ApiResponseFailException(
                                poorDidiList.message ?: "Poor Didi Ranking list error"
                            )
                            /* if (!RetryHelper.retryApiList.contains(ApiType.BPC_POOR_DIDI_LIST_API)) RetryHelper.retryApiList.add(
                                 ApiType.BPC_POOR_DIDI_LIST_API
                             )
                             RetryHelper.stepListApiVillageId.add(village.id)*/
                            onCatchError(ex, ApiType.BPC_POOR_DIDI_LIST_API)
                            networkCallbackListener.onFailed()
                        }
                    } catch (ex: Exception) {
                        /*if (ex !is JsonSyntaxException) {
                            if (!RetryHelper.retryApiList.contains(ApiType.BPC_POOR_DIDI_LIST_API)) RetryHelper.retryApiList.add(
                                ApiType.BPC_POOR_DIDI_LIST_API
                            )
                            RetryHelper.stepListApiVillageId.add(village.id)
                        }*/
                        onCatchError(ex, ApiType.BPC_POOR_DIDI_LIST_API)
                        networkCallbackListener.onFailed()
                    }

                    try {
                        NudgeLogger.d(
                            "VillageSelectionScreen",
                            "fetchDataForBpc fetchPATSurveyToServer " +
                                    "request -> ${listOf(village.id)}"
                        )
                        val answerApiResponse = apiService.fetchPATSurveyToServer(
                            listOf(village.id)
                        )
                        NudgeLogger.d(
                            "VillageSelectionScreen",
                            "fetchDataForBpc fetchPATSurveyToServer " +
                                    "response -> status: ${answerApiResponse.status}"
                        )
                        if (answerApiResponse.status.equals(SUCCESS, true)) {
                            answerApiResponse.data?.let {
                                val answerList: ArrayList<SectionAnswerEntity> =
                                    arrayListOf()
                                val numAnswerList: ArrayList<NumericAnswerEntity> =
                                    arrayListOf()
                                val didiIdList = mutableListOf<Int>()
                                it.forEach { item ->
                                    if (item.userType.equals(USER_BPC, true)) {
                                        didiIdList.add(item.beneficiaryId!!)
                                        didiDao.updatePATProgressStatus(
                                            patSurveyStatus = item.patSurveyStatus
                                                ?: 0,
                                            section1Status = item.section1Status
                                                ?: 0,
                                            section2Status = item.section2Status
                                                ?: 0,
                                            didiId = item.beneficiaryId ?: 0,
                                            shgFlag = item.shgFlag ?: -1,
                                            patExclusionStatus = item.patExclusionStatus ?: 0
                                        )
                                        if (item?.answers?.isNotEmpty() == true) {
                                            item?.answers?.forEach { answersItem ->
                                                val quesDetails =
                                                    questionListDao.getQuestionForLanguage(
                                                        answersItem?.questionId ?: 0,
                                                        prefRepo.getAppLanguageId() ?: 2
                                                    )
                                                if (answersItem?.questionType?.equals(
                                                        QuestionType.Numeric_Field.name
                                                    ) == true
                                                ) {

                                                    if ((prefRepo.getPref(
                                                            PREF_KEY_TYPE_NAME, ""
                                                        ) ?: "").equals(
                                                            BPC_USER_TYPE, true
                                                        )
                                                    ) {
                                                        answerList.add(
                                                            SectionAnswerEntity(
                                                                id = 0,
                                                                optionId = 0,
                                                                didiId = item.beneficiaryId
                                                                    ?: 0,
                                                                questionId = answersItem?.questionId
                                                                    ?: 0,
                                                                villageId = item.villageId
                                                                    ?: 0,
                                                                actionType = answersItem?.section
                                                                    ?: TYPE_EXCLUSION,
                                                                weight = if (answersItem?.options?.isNotEmpty() == true) (answersItem?.options?.get(
                                                                    0
                                                                )?.weight) else 0,
                                                                summary = answersItem?.summary,
                                                                optionValue = if (answersItem?.options?.isNotEmpty() == true) (answersItem?.options?.get(
                                                                    0
                                                                )?.optionValue) else 0,
                                                                totalAssetAmount = if (quesDetails?.questionFlag.equals(
                                                                        QUESTION_FLAG_WEIGHT
                                                                    )
                                                                ) answersItem?.totalWeight?.toDouble() else stringToDouble(
                                                                    formatRatio(
                                                                        answersItem?.ratio
                                                                            ?: DOUBLE_ZERO
                                                                    )
                                                                ),
                                                                needsToPost = false,
                                                                answerValue = (if (quesDetails?.questionFlag.equals(
                                                                        QUESTION_FLAG_WEIGHT
                                                                    )
                                                                ) answersItem?.totalWeight?.toDouble() else stringToDouble(
                                                                    formatRatio(
                                                                        answersItem?.ratio
                                                                            ?: DOUBLE_ZERO
                                                                    )
                                                                )).toString(),
                                                                type = answersItem?.questionType
                                                                    ?: QuestionType.RadioButton.name,
                                                                assetAmount = answersItem?.assetAmount
                                                                    ?: "0",
                                                                questionFlag = quesDetails?.questionFlag
                                                                    ?: BLANK_STRING
                                                            )
                                                        )

                                                        if (answersItem?.options?.isNotEmpty() == true) {

                                                            answersItem?.options?.forEach { optionItem ->
                                                                numAnswerList.add(
                                                                    NumericAnswerEntity(
                                                                        id = 0,
                                                                        optionId = optionItem?.optionId
                                                                            ?: 0,
                                                                        questionId = answersItem?.questionId
                                                                            ?: 0,
                                                                        weight = optionItem?.weight
                                                                            ?: 0,
                                                                        didiId = item.beneficiaryId
                                                                            ?: 0,
                                                                        count = optionItem?.count
                                                                            ?: 0,
                                                                        optionValue = optionItem?.optionValue
                                                                            ?: 0
                                                                    )
                                                                )
                                                            }

                                                        }
                                                    }
                                                } else {
                                                    answerList.add(
                                                        SectionAnswerEntity(
                                                            id = 0,
                                                            optionId = answersItem?.options?.get(
                                                                0
                                                            )?.optionId ?: 0,
                                                            didiId = item.beneficiaryId
                                                                ?: 0,
                                                            questionId = answersItem?.questionId
                                                                ?: 0,
                                                            villageId = item.villageId
                                                                ?: 0,
                                                            actionType = answersItem?.section
                                                                ?: TYPE_EXCLUSION,
                                                            weight = if (answersItem?.options?.isNotEmpty() == true) (answersItem?.options?.get(
                                                                0
                                                            )?.weight) else 0,
                                                            summary = answersItem?.summary,
                                                            optionValue = if (answersItem?.options?.isNotEmpty() == true) (answersItem?.options?.get(
                                                                0
                                                            )?.optionValue) else 0,
                                                            totalAssetAmount = if (quesDetails?.questionFlag.equals(
                                                                    QUESTION_FLAG_WEIGHT
                                                                )
                                                            ) answersItem?.totalWeight?.toDouble() else stringToDouble(
                                                                formatRatio(
                                                                    answersItem?.ratio
                                                                        ?: DOUBLE_ZERO
                                                                )
                                                            ),
                                                            needsToPost = false,
                                                            answerValue = if (answersItem?.options?.isNotEmpty() == true) (answersItem?.options?.get(
                                                                0
                                                            )?.display
                                                                ?: BLANK_STRING) else BLANK_STRING,
                                                            type = answersItem?.questionType
                                                                ?: QuestionType.RadioButton.name
                                                        )
                                                    )
                                                }

                                            }
                                        }
                                    }
                                }
                                if (answerList.isNotEmpty()) {
//                                    answerDao.insertAll(answerList)
                                    answerDao.updateAnswersAfterRefresh(forceRefresh, village.id, answerList)
                                }
                                if (numAnswerList.isNotEmpty()) {
//                                    numericAnswerDao.insertAll(numAnswerList)
                                    numericAnswerDao.updateNumericAnswersAfterRefresh(forceRefresh, didiIdList, numAnswerList)
                                }
                            }
                        } else {
                            val ex =
                                ApiResponseFailException(answerApiResponse.message)
                            /* if (!RetryHelper.retryApiList.contains(ApiType.PAT_BPC_SURVEY_SUMMARY)) RetryHelper.retryApiList.add(
                                 ApiType.PAT_BPC_SURVEY_SUMMARY
                             )

                             if (!RetryHelper.stepListApiVillageId.contains(village.id)) RetryHelper.stepListApiVillageId.add(
                                 village.id
                             )
*/
                            onCatchError(ex, ApiType.PAT_BPC_SURVEY_SUMMARY)
                            networkCallbackListener.onFailed()
                        }
                    } catch (ex: Exception) {
                        /*if (ex !is JsonSyntaxException) {
                            if (!RetryHelper.retryApiList.contains(ApiType.PAT_BPC_SURVEY_SUMMARY)) RetryHelper.retryApiList.add(
                                ApiType.PAT_BPC_SURVEY_SUMMARY
                            )

                            if (!RetryHelper.stepListApiVillageId.contains(village.id)) RetryHelper.stepListApiVillageId.add(
                                village.id
                            )
                        }*/
                        onCatchError(ex, ApiType.PAT_BPC_SURVEY_SUMMARY)
                        networkCallbackListener.onFailed()
                    }

                    prefRepo.savePref(PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_ + village.id, true)
                    prefRepo.savePref(PREF_BPC_DIDI_LIST_SYNCED_FOR_VILLAGE_ + village.id, true)

                } catch (ex: Exception) {
                    NudgeLogger.e(
                        "VillageSelectionViewModel",
                        "fetchDataForBpc -> onCatchError",
                        ex
                    )
                    onCatchError(ex, ApiType.FETCH_ALL_DATA)
                    networkCallbackListener.onFailed()
                } finally {
                    prefRepo.savePref(LAST_UPDATE_TIME, System.currentTimeMillis())
                    withContext(Dispatchers.Main) {
                        delay(250)
                    }
                }
            }.await()
            delay(250)
            NudgeLogger.d(
                "VillageSelectionScreen",
                "fetchDataForBpc after await -> viewModel.showLoader.value = false"
            )
            networkCallbackListener.onSuccess()
        }
    }


    private fun downloadAuthorizedImageItem(id: Int, image: String, prefRepo: PrefRepo) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val imageFile = getAuthImagePath(androidDownloader.mContext, image)
                if (!imageFile.exists()) {
                    val localDownloader = androidDownloader
                    val downloadManager = androidDownloader.mContext.getSystemService(
                        DownloadManager::class.java
                    )
                    localDownloader?.currentDownloadingId?.value = id
                    val downloadId = localDownloader?.downloadAuthorizedImageFile(
                        image,
                        FileType.IMAGE,
                        prefRepo
                    )
                    if (downloadId != null) {
                        localDownloader.checkDownloadStatus(downloadId,
                            id,
                            downloadManager,
                            onDownloadComplete = {
                                didiDao.updateImageLocalPath(id, imageFile.absolutePath)
                                didiDao.updateNeedsToPostImage(id, false)
                            }, onDownloadFailed = {
                                NudgeLogger.d(
                                    "VillageSelectorViewModel",
                                    "downloadAuthorizedImageItem -> onDownloadFailed"
                                )
                            })
                    }
                } else {
                    didiDao.updateImageLocalPath(id, imageFile.absolutePath)
                    didiDao.updateNeedsToPostImage(id, false)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                NudgeLogger.e(
                    "VillageSelectorViewModel",
                    "downloadAuthorizedImageItem -> downloadItem exception",
                    ex
                )
            }
        }
    }

    fun updateVillageDataLoadStatus(villageId: Int, isDataLoadTriedOnce: Boolean) {
        villageListDao.updateVillageDataLoadStatus(villageId, isDataLoadTriedOnce)
    }

    fun isDataLoadTried(villageId: Int): Boolean {
        return villageListDao.getVillage(villageId).isDataLoadTriedOnce
    }

    fun getStepListForVillageLive(): LiveData<List<StepListEntity>> {
        return stepsListDao.getAllStepsForVillageLive(prefRepo.getSelectedVillage().id)
    }

}