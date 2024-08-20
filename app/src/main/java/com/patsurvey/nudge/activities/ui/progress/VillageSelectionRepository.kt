package com.patsurvey.nudge.activities.ui.progress

import android.app.DownloadManager
import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.nudge.core.DEFAULT_LANGUAGE_ID
import com.nudge.core.json
import com.patsurvey.nudge.MyApplication
import com.patsurvey.nudge.RetryHelper
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.activities.settings.TransactionIdRequest
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.BpcSummaryEntity
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.LanguageEntity
import com.patsurvey.nudge.database.NumericAnswerEntity
import com.patsurvey.nudge.database.PoorDidiEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.TrainingVideoEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.BpcSummaryDao
import com.patsurvey.nudge.database.dao.CasteListDao
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
import com.patsurvey.nudge.model.dataModel.UserAndVillageDetailsModel
import com.patsurvey.nudge.model.dataModel.UserDetailsModel
import com.patsurvey.nudge.model.request.AddCohortRequest
import com.patsurvey.nudge.model.request.AddDidiRequest
import com.patsurvey.nudge.model.request.AddWorkFlowRequest
import com.patsurvey.nudge.model.request.AnswerDetailDTOListItem
import com.patsurvey.nudge.model.request.DeleteTolaRequest
import com.patsurvey.nudge.model.request.EditCohortRequest
import com.patsurvey.nudge.model.request.EditDidiRequest
import com.patsurvey.nudge.model.request.EditDidiWealthRankingRequest
import com.patsurvey.nudge.model.request.EditWorkFlowRequest
import com.patsurvey.nudge.model.request.GetQuestionListRequest
import com.patsurvey.nudge.model.request.PATSummarySaveRequest
import com.patsurvey.nudge.model.request.SaveMatchSummaryRequest
import com.patsurvey.nudge.model.request.StepResultTypeRequest
import com.patsurvey.nudge.model.response.OptionsItem
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.ACCEPTED
import com.patsurvey.nudge.utils.AbleBodiedFlag
import com.patsurvey.nudge.utils.ApiResponseFailException
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.BPC_SURVEY_CONSTANT
import com.patsurvey.nudge.utils.BPC_USER_TYPE
import com.patsurvey.nudge.utils.BPC_VERIFICATION_STEP_ORDER
import com.patsurvey.nudge.utils.COMPLETED_STRING
import com.patsurvey.nudge.utils.DIDI_NOT_AVAILABLE
import com.patsurvey.nudge.utils.DIDI_REJECTED
import com.patsurvey.nudge.utils.DOUBLE_ZERO
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.DownloadStatus
import com.patsurvey.nudge.utils.ExclusionType
import com.patsurvey.nudge.utils.FAIL
import com.patsurvey.nudge.utils.FLAG_RATIO
import com.patsurvey.nudge.utils.FLAG_WEIGHT
import com.patsurvey.nudge.utils.FORM_C
import com.patsurvey.nudge.utils.FORM_D
import com.patsurvey.nudge.utils.HEADING_QUESTION_TYPE
import com.patsurvey.nudge.utils.LAST_SYNC_TIME
import com.patsurvey.nudge.utils.LAST_UPDATE_TIME
import com.patsurvey.nudge.utils.LOW_SCORE
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PAT_SURVEY
import com.patsurvey.nudge.utils.PAT_SURVEY_CONSTANT
import com.patsurvey.nudge.utils.PREF_BPC_DIDI_LIST_SYNCED_FOR_VILLAGE_
import com.patsurvey.nudge.utils.PREF_BPC_PAT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_KEY_EMAIL
import com.patsurvey.nudge.utils.PREF_KEY_IDENTITY_NUMBER
import com.patsurvey.nudge.utils.PREF_KEY_NAME
import com.patsurvey.nudge.utils.PREF_KEY_PROFILE_IMAGE
import com.patsurvey.nudge.utils.PREF_KEY_ROLE_NAME
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_NAME
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_STATE_ID
import com.patsurvey.nudge.utils.PREF_KEY_USER_NAME
import com.patsurvey.nudge.utils.PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_
import com.patsurvey.nudge.utils.PREF_NEED_TO_POST_FORM_C_AND_D_
import com.patsurvey.nudge.utils.PREF_PAT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_PROGRAM_NAME
import com.patsurvey.nudge.utils.PREF_SOCIAL_MAPPING_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_TRANSECT_WALK_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_VO_ENDORSEMENT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_WEALTH_RANKING_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.QUESTION_FLAG_RATIO
import com.patsurvey.nudge.utils.QUESTION_FLAG_WEIGHT
import com.patsurvey.nudge.utils.QuestionType
import com.patsurvey.nudge.utils.RESPONSE_CODE_CONFLICT
import com.patsurvey.nudge.utils.RESPONSE_CODE_UNAUTHORIZED
import com.patsurvey.nudge.utils.ResultType
import com.patsurvey.nudge.utils.SHGFlag
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.StepType
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.TolaStatus
import com.patsurvey.nudge.utils.USER_BPC
import com.patsurvey.nudge.utils.USER_CRP
import com.patsurvey.nudge.utils.VERIFIED_STRING
import com.patsurvey.nudge.utils.VO_ENDORSEMENT_STEP_ORDER
import com.patsurvey.nudge.utils.WealthRank
import com.patsurvey.nudge.utils.calculateScore
import com.patsurvey.nudge.utils.compressImage
import com.patsurvey.nudge.utils.findImageLocationFromPath
import com.patsurvey.nudge.utils.formatRatio
import com.patsurvey.nudge.utils.getAuthImagePath
import com.patsurvey.nudge.utils.getEmitLanguageList
import com.patsurvey.nudge.utils.getFileNameFromURL
import com.patsurvey.nudge.utils.getFormPathKey
import com.patsurvey.nudge.utils.getFormSubPath
import com.patsurvey.nudge.utils.getImagePath
import com.patsurvey.nudge.utils.getVideoPath
import com.patsurvey.nudge.utils.intToString
import com.patsurvey.nudge.utils.longToString
import com.patsurvey.nudge.utils.stringToDouble
import com.patsurvey.nudge.utils.toWeightageRatio
import com.patsurvey.nudge.utils.updateLastSyncTime
import com.patsurvey.nudge.utils.videoList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File
import java.util.Collections
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

class VillageSelectionRepository @Inject constructor(
    val prefRepo: PrefRepo,
    val apiService: ApiService,
    val villageListDao: VillageListDao,
    val stepsListDao: StepsListDao,
    val tolaDao: TolaDao,
    val casteListDao: CasteListDao,
    val languageListDao: LanguageListDao,
    val questionDao: QuestionListDao,
    val trainingVideoDao: TrainingVideoDao,
    val numericAnswerDao: NumericAnswerDao,
    val answerDao: AnswerDao,
    val bpcSummaryDao: BpcSummaryDao,
    val poorDidiListDao: PoorDidiListDao,
    val androidDownloader: AndroidDownloader
): BaseRepository() {

    private var isPendingForBpc = 0
    private var isPendingForCrp = 0
    private val pendingTimerTime:Long = 10000

    fun refreshBpcData(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener) {
        repoJob = MyApplication.appScopeLaunch (Dispatchers.IO + exceptionHandler) {
            val awaitDeff = CoroutineScope(Dispatchers.IO).async {
                try {
                    //Fetch PAT Question
//                    fetchQuestions(prefRepo)

                    val villageList =
                        villageListDao.getAllVillages(prefRepo.getAppLanguageId() ?: 2)
                    val villageIdList: ArrayList<Int> = arrayListOf()
                    villageList.forEach { village ->
                        villageIdList.add(village.id)

                        //Fetch Step List Data
                        try {
                            NudgeLogger.d("VillageSelectionRepository", "refreshBpcData getStepsList request -> village.id = ${village.id}")
                            val response = apiService.getStepsList(village.id)
                            NudgeLogger.d("VillageSelectionRepository", "refreshBpcData getStepsList " +
                                    "response status = ${response.status}, message = ${response.message}, data = ${response.data.toString()}")
                            if (response.status.equals(SUCCESS, true)) {
                                response.data?.let { it ->
                                    if (it.stepList.isNotEmpty()) {
                                        it.stepList.forEach { steps ->
                                            steps.villageId = village.id
                                            /*steps.isComplete =
                                                findCompleteValue(steps.status).ordinal*/
                                            if(steps.id == 40){
                                                prefRepo.savePref(
                                                    PREF_TRANSECT_WALK_COMPLETION_DATE_ +village.id, steps.localModifiedDate?: System.currentTimeMillis())
                                            }

                                            if(steps.id == 41){
                                                prefRepo.savePref(
                                                    PREF_SOCIAL_MAPPING_COMPLETION_DATE_ +village.id, steps.localModifiedDate?: System.currentTimeMillis())
                                            }

                                            if(steps.id == 46){
                                                prefRepo.savePref(
                                                    PREF_WEALTH_RANKING_COMPLETION_DATE_ +village.id, steps.localModifiedDate?: System.currentTimeMillis())
                                            }

                                            if(steps.id == 43){
                                                prefRepo.savePref(
                                                    PREF_PAT_COMPLETION_DATE_ +village.id, steps.localModifiedDate?: System.currentTimeMillis())
                                            }
                                            if(steps.id == 44){
                                                prefRepo.savePref(
                                                    PREF_VO_ENDORSEMENT_COMPLETION_DATE_ +village.id, steps.localModifiedDate?: System.currentTimeMillis())
                                            }

                                            if (steps.id == 45) {
                                                prefRepo.savePref(
                                                    PREF_BPC_PAT_COMPLETION_DATE_ + village.id,
                                                    steps.localModifiedDate
                                                        ?: System.currentTimeMillis()
                                                )
                                            }
                                        }
                                        val localStepListForVillage = stepsListDao.getAllStepsForVillage(village.id)
                                        NudgeLogger.d("VillageSelectionRepository", "refreshBpcData getStepsList " +
                                                "stepsListDao.insertAll(it.stepList) before")

                                        val updatedStepList = mutableListOf<StepListEntity>()
                                        localStepListForVillage.forEach { step ->
                                            updatedStepList.add(step.getUpdatedStep(it.stepList[it.stepList.map { it.id }.indexOf(step.id)]))
                                        }
                                        if (localStepListForVillage.size != it.stepList.size) {
                                            if (localStepListForVillage.size < it.stepList.size) {
                                                val tempStepList = mutableListOf<StepListEntity>()
                                                tempStepList.addAll(it.stepList)
                                                tempStepList.sortedBy { it.orderNumber }
                                                localStepListForVillage.forEach { localStep ->
                                                    if (it.stepList.map { remoteStep -> remoteStep.id }.contains(localStep.id)) {
                                                        tempStepList.remove(it.stepList.sortedBy { it.orderNumber }[it.stepList.map { it.id }.indexOf(localStep.id)])
                                                    }
                                                }
                                                updatedStepList.addAll(tempStepList)
                                            } else {
                                                val tempStepList = mutableListOf<StepListEntity>()
                                                tempStepList.addAll(localStepListForVillage)
                                                tempStepList.sortedBy { it.orderNumber }
                                                it.stepList.forEach { remoteStep ->
                                                    if (localStepListForVillage.map { localStep -> remoteStep.id }.contains(remoteStep.id)) {
                                                        tempStepList.remove(localStepListForVillage.sortedBy { it.orderNumber }[localStepListForVillage.map { it.id }.indexOf(remoteStep.id)])
                                                    }
                                                }
                                                updatedStepList.addAll(tempStepList)
                                            }
                                        }
                                        if (updatedStepList.isNotEmpty()) {
                                            stepsListDao.deleteAllStepsForVillage(village.id)
                                            delay(100)
                                            stepsListDao.insertAll(updatedStepList)
                                        }

                                        NudgeLogger.d("VillageSelectionRepository", "refreshBpcData getStepsList " +
                                                "stepsListDao.insertAll(it.stepList) after")
                                    }
                                    prefRepo.savePref(
                                        PREF_PROGRAM_NAME, it.programName
                                    )
                                }
                            } else {
                                val ex = ApiResponseFailException(response.message)
                                if (!RetryHelper.retryApiList.contains(ApiType.STEP_LIST_API)) RetryHelper.retryApiList.add(
                                    ApiType.STEP_LIST_API
                                )
                                RetryHelper.stepListApiVillageId.add(village.id)
                                onCatchError(ex, ApiType.STEP_LIST_API)
                            }
                        }
                        catch (ex: Exception) {
                            if (ex !is JsonSyntaxException) {
                                if (!RetryHelper.retryApiList.contains(ApiType.STEP_LIST_API)) RetryHelper.retryApiList.add(
                                    ApiType.STEP_LIST_API
                                )
                                RetryHelper.stepListApiVillageId.add(village.id)
                            }
                            onCatchError(ex, ApiType.STEP_LIST_API)
                        }

                        //Fetch BPC Summary Data
                        try {
                            NudgeLogger.d("VillageSelectionRepository", "refreshBpcData getBpcSummary " +
                                    "village.id = ${village.id}")
                            val bpcSummaryResponse =
                                apiService.getBpcSummary(villageId = village.id)
                            NudgeLogger.d("VillageSelectionRepository", "refreshBpcData getStepsList " +
                                    "bpcSummaryResponse status = ${bpcSummaryResponse.status}, message = ${bpcSummaryResponse.message}, data = ${bpcSummaryResponse.data.toString()}")
                            if (bpcSummaryResponse.status.equals(SUCCESS, true)) {
                                bpcSummaryDao.deleteForVillage(villageId = village.id)
                                bpcSummaryResponse.data?.let {
                                    val bpcSummary = BpcSummaryEntity(
                                        cohortCount = it.cohortCount,
                                        mobilisedCount = it.mobilisedCount,
                                        poorDidiCount = it.poorDidiCount,
                                        sentVoEndorsementCount = it.sentVoEndorsementCount,
                                        voEndorsedCount = it.voEndorsedCount,
                                        villageId = village.id
                                    )
                                    NudgeLogger.d("VillageSelectionRepository", "refreshBpcData getStepsList " +
                                            "bpcSummaryDao.insert(bpcSummary) before")
                                    bpcSummaryDao.insert(bpcSummary)
                                    NudgeLogger.d("VillageSelectionRepository", "refreshBpcData getStepsList " +
                                            "bpcSummaryDao.insert(bpcSummary) after")
                                }
                            } else {
                                NudgeLogger.d("VillageSelectionRepository", "refreshBpcData getStepsList " +
                                        "bpcSummaryDao.insert(BpcSummaryEntity(0, 0, 0, 0, 0, 0, villageId = village.id))")
                                val existingSummaryDataForVillage = bpcSummaryDao.getBpcSummaryForVillage(villageId = village.id)
                                if (existingSummaryDataForVillage != null && !existingSummaryDataForVillage.isSummaryEmpty())
                                    bpcSummaryDao.insert(BpcSummaryEntity.getEmptySummaryForVillage(village.id))

                                val ex = ApiResponseFailException(bpcSummaryResponse.message)
                                if (!RetryHelper.retryApiList.contains(ApiType.BPC_SUMMARY_API)) RetryHelper.retryApiList.add(
                                    ApiType.BPC_SUMMARY_API
                                )
                                RetryHelper.stepListApiVillageId.add(village.id)
                                onCatchError(ex, ApiType.BPC_SUMMARY_API)
                            }
                        }
                        catch (ex: Exception) {
                            val existingSummaryDataForVillage = bpcSummaryDao.getBpcSummaryForVillage(villageId = village.id)
                            if (existingSummaryDataForVillage != null && !existingSummaryDataForVillage.isSummaryEmpty())
                                bpcSummaryDao.insert(BpcSummaryEntity.getEmptySummaryForVillage(village.id))

                            if (ex !is JsonSyntaxException) {
                                if (!RetryHelper.retryApiList.contains(ApiType.BPC_SUMMARY_API)) RetryHelper.retryApiList.add(
                                    ApiType.BPC_SUMMARY_API
                                )
                                RetryHelper.stepListApiVillageId.add(village.id)
                            }
                            onCatchError(ex, ApiType.BPC_SUMMARY_API)
                        }

                        //Fetch Cohort/Tola Data
                        FetchTolaForBpc(village.id)
                    }
                    //Sync and fetch Didi and other data
                    syncAndFetchDidiForBpc(prefRepo, object : NetworkCallbackListener {
                        override fun onSuccess() {
                            villageList.forEach { village ->
                                fetchDidiForBpc(village.id, prefRepo)
                                fetchPoorDidisForBpc(village.id, prefRepo)
                            }
                        }

                        override fun onFailed() {
                            networkCallbackListener.onFailed()
                        }

                    })
                } catch (ex: Exception) {
                    NudgeLogger.e(
                        "VillageSelectionRepository",
                        "refreshBpcData -> onCatchError",
                        ex
                    )
                    onCatchError(ex, ApiType.FETCH_ALL_DATA)
                } finally {
                    prefRepo.savePref(LAST_UPDATE_TIME, System.currentTimeMillis())
                    withContext(Dispatchers.Main) {
                        delay(250)
//                        NudgeLogger.d("VillageSelectionRepository", "fetchDataForBpc finally -> viewModel.showLoader.value = false")
//                        showLoader.value = false
                    }
                }
            }.await()
            delay(250)
            NudgeLogger.d(
                "VillageSelectionRepository",
                "refreshBpcData after await -> viewModel.showLoader.value = false"
            )
            withContext(Dispatchers.Main) {
                networkCallbackListener.onSuccess()
            }
        }
    }

    //Check Pending Didi For verification and mark BPC Verification Step as In Progress.
    private fun checkPendingDidiForVerification(villageId: Int, prefRepo: PrefRepo) {
        val pendingVerificationDidiCount = didiDao.fetchPendingVerificationDidiCount(villageId)
        if (pendingVerificationDidiCount > 0) {
            stepsListDao.markStepAsInProgress(
                BPC_VERIFICATION_STEP_ORDER,
                StepStatus.INPROGRESS.ordinal,
                villageId
            )
            stepsListDao.updateNeedToPostByOrderNumber(
                orderNumber = BPC_VERIFICATION_STEP_ORDER,
                villageId,
                true
            )
            val voEndorsementStep = stepsListDao.getStepByOrder(VO_ENDORSEMENT_STEP_ORDER, villageId)
            villageListDao.updateStepAndStatusId(villageId, voEndorsementStep.id, StepStatus.COMPLETED.ordinal)
            prefRepo.savePref(PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_ + villageId, false)
        }
    }

    fun refreshCrpData(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener) {
        repoJob = MyApplication.appScopeLaunch (Dispatchers.IO + exceptionHandler) {
            val awaitDeff = CoroutineScope(Dispatchers.IO).async {
                try {
                    //Fetch PAT Question
//                    fetchQuestions(prefRepo)

                    val villageList =
                        villageListDao.getAllVillages(prefRepo.getAppLanguageId() ?: 2)
                    val villageIdList: ArrayList<Int> = arrayListOf()
                    villageList.forEach { village ->
                        villageIdList.add(village.id)

                        //Fetch Step List Data
                        try {
                            NudgeLogger.d("VillageSelectionRepository", "refreshBpcData getStepsList request -> village.id = ${village.id}")
                            val response = apiService.getStepsList(village.id)
                            NudgeLogger.d("VillageSelectionRepository", "refreshBpcData getStepsList " +
                                    "response status = ${response.status}, message = ${response.message}, data = ${response.data.toString()}")
                            if (response.status.equals(SUCCESS, true)) {
                                response.data?.let { it ->
                                    if (it.stepList.isNotEmpty()) {
                                        it.stepList.forEach { steps ->
                                            steps.villageId = village.id
                                            /*steps.isComplete =
                                                findCompleteValue(steps.status).ordinal*/
                                            if(steps.id == 40){
                                                prefRepo.savePref(
                                                    PREF_TRANSECT_WALK_COMPLETION_DATE_ +village.id, steps.localModifiedDate?: System.currentTimeMillis())
                                            }

                                            if(steps.id == 41){
                                                prefRepo.savePref(
                                                    PREF_SOCIAL_MAPPING_COMPLETION_DATE_ +village.id, steps.localModifiedDate?: System.currentTimeMillis())
                                            }

                                            if(steps.id == 46){
                                                prefRepo.savePref(
                                                    PREF_WEALTH_RANKING_COMPLETION_DATE_ +village.id, steps.localModifiedDate?: System.currentTimeMillis())
                                            }

                                            if(steps.id == 43){
                                                prefRepo.savePref(
                                                    PREF_PAT_COMPLETION_DATE_ +village.id, steps.localModifiedDate?: System.currentTimeMillis())
                                            }
                                            if(steps.id == 44){
                                                prefRepo.savePref(
                                                    PREF_VO_ENDORSEMENT_COMPLETION_DATE_ +village.id, steps.localModifiedDate?: System.currentTimeMillis())
                                            }

                                            if (steps.id == 45) {
                                                prefRepo.savePref(
                                                    PREF_BPC_PAT_COMPLETION_DATE_ + village.id,
                                                    steps.localModifiedDate
                                                        ?: System.currentTimeMillis()
                                                )
                                            }
                                        }
                                        val localStepListForVillage = stepsListDao.getAllStepsForVillage(village.id)
                                        NudgeLogger.d("VillageSelectionRepository", "refreshBpcData getStepsList " +
                                                "stepsListDao.insertAll(it.stepList) before")

                                        val updatedStepList = mutableListOf<StepListEntity>()
                                        localStepListForVillage.forEach { step ->
                                            updatedStepList.add(step.getUpdatedStep(it.stepList[it.stepList.map { it.id }.indexOf(step.id)]))
                                        }
                                        if (localStepListForVillage.size != it.stepList.size) {
                                            if (localStepListForVillage.size < it.stepList.size) {
                                                val tempStepList = mutableListOf<StepListEntity>()
                                                tempStepList.addAll(it.stepList)
                                                tempStepList.sortedBy { it.orderNumber }
                                                localStepListForVillage.forEach { localStep ->
                                                    if (it.stepList.map { remoteStep -> remoteStep.id }.contains(localStep.id)) {
                                                        tempStepList.remove(it.stepList.sortedBy { it.orderNumber }[it.stepList.map { it.id }.indexOf(localStep.id)])
                                                    }
                                                }
                                                updatedStepList.addAll(tempStepList)
                                            } else {
                                                val tempStepList = mutableListOf<StepListEntity>()
                                                tempStepList.addAll(localStepListForVillage)
                                                tempStepList.sortedBy { it.orderNumber }
                                                it.stepList.forEach { remoteStep ->
                                                    if (localStepListForVillage.map { localStep -> remoteStep.id }.contains(remoteStep.id)) {
                                                        tempStepList.remove(localStepListForVillage.sortedBy { it.orderNumber }[localStepListForVillage.map { it.id }.indexOf(remoteStep.id)])
                                                    }
                                                }
                                                updatedStepList.addAll(tempStepList)
                                            }
                                        }
                                        if (updatedStepList.isNotEmpty()) {
                                            stepsListDao.deleteAllStepsForVillage(village.id)
                                            delay(100)
                                            stepsListDao.insertAll(updatedStepList)
                                        }

                                        NudgeLogger.d("VillageSelectionRepository", "refreshBpcData getStepsList " +
                                                "stepsListDao.insertAll(it.stepList) after")
                                    }
                                    prefRepo.savePref(
                                        PREF_PROGRAM_NAME, it.programName
                                    )
                                }
                            } else {
                                val ex = ApiResponseFailException(response.message)
                                if (!RetryHelper.retryApiList.contains(ApiType.STEP_LIST_API)) RetryHelper.retryApiList.add(
                                    ApiType.STEP_LIST_API
                                )
                                RetryHelper.stepListApiVillageId.add(village.id)
                                onCatchError(ex, ApiType.STEP_LIST_API)
                            }
                        }
                        catch (ex: Exception) {
                            if (ex !is JsonSyntaxException) {
                                if (!RetryHelper.retryApiList.contains(ApiType.STEP_LIST_API)) RetryHelper.retryApiList.add(
                                    ApiType.STEP_LIST_API
                                )
                                RetryHelper.stepListApiVillageId.add(village.id)
                            }
                            onCatchError(ex, ApiType.STEP_LIST_API)
                        }
                    }
//                    //Fetch Cohort/Tola Data
//                    syncCrpData(prefRepo = prefRepo, object : NetworkCallbackListener {
//                        override fun onSuccess() {
//                            //fetch all data
//                            villageList.forEach { village ->
//                                fetchDidiForCrp(prefRepo, village.id)
//                            }
//                        }
//
//                        override fun onFailed() {
//                            networkCallbackListener.onFailed()
//                        }
//                    })
                } catch (ex: Exception) {
                    NudgeLogger.e(
                        "VillageSelectionRepository",
                        "refreshCrpData -> onCatchError",
                        ex
                    )
                    onCatchError(ex, ApiType.FETCH_ALL_DATA)
                } finally {
                    prefRepo.savePref(LAST_UPDATE_TIME, System.currentTimeMillis())
                    withContext(Dispatchers.Main) {
                        delay(250)
//                        NudgeLogger.d("VillageSelectionRepository", "fetchDataForBpc finally -> viewModel.showLoader.value = false")
//                        showLoader.value = false
                    }
                }
            }.await()
            delay(250)
            NudgeLogger.d(
                "VillageSelectionRepository",
                "refreshBpcData after await -> viewModel.showLoader.value = false"
            )
            withContext(Dispatchers.Main) {
                networkCallbackListener.onSuccess()
            }
        }
    }

    private fun fetchDidiForCrp(prefRepo: PrefRepo, villageId: Int) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            //Fetch Didi Details
            try {
                val oldDidiList = didiDao.getAllDidisForVillage(villageId)
                val didiResponse =
                    apiService.getDidisFromNetwork(villageId = villageId)
                if (didiResponse.status.equals(SUCCESS, true)) {
                    didiResponse.data?.let {
                        if (it.didiList.isNotEmpty()) {
                            try {
                                didiDao.deleteDidiForVillage(villageId)
                                delay(100)
                                it.didiList.forEach { didi ->
                                    var tolaName = BLANK_STRING
                                    var casteName = BLANK_STRING
                                    val singleTola =
                                        tolaDao.fetchSingleTola(didi.cohortId)
                                    val singleCaste =
                                        casteListDao.getCaste(didi.castId, prefRepo?.getAppLanguageId()?:2)
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

                                    var remoteDidiEntity = DidiEntity(
                                        id = didi.id,
                                        serverId = didi.id,
                                        name = didi.name,
                                        address = didi.address,
                                        guardianName = didi.guardianName,
                                        relationship = didi.relationship,
                                        castId = didi.castId,
                                        castName = casteName,
                                        cohortId = didi.cohortId,
                                        villageId = villageId,
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
                                        score = didi.crpScore,
                                        crpScore = didi.crpScore,
                                        crpComment = didi.crpComment,
                                        comment = didi.comment,
                                        crpUploadedImage = didi.crpUploadedImage,
                                        needsToPostImage = false,
                                        rankingEdit = didi.rankingEdit,
                                        patEdit = didi.patEdit,
                                        voEndorsementEdit = didi.voEndorsementEdit,
                                        ableBodiedFlag = AbleBodiedFlag.fromSting(
                                            didi.ableBodiedFlag ?: AbleBodiedFlag.NOT_MARKED.name
                                        ).value
                                    )
                                    val oldDidiEntity = oldDidiList.filter {
                                        it.name == remoteDidiEntity.name
                                                && it.guardianName == remoteDidiEntity.guardianName
                                                && it.address == remoteDidiEntity.address
                                                && it.cohortName == remoteDidiEntity.cohortName
                                                && it.villageId == remoteDidiEntity.villageId
                                    }.firstOrNull()
                                    oldDidiEntity.let { oldDidi ->
                                        if (oldDidi?.serverId == 0) {
                                            remoteDidiEntity = oldDidi.copy(
                                                serverId = remoteDidiEntity.serverId
                                            )
                                        }
                                    }
                                    didiDao.insertDidi(
                                        remoteDidiEntity
                                    )
//                                                    }
                                    if(!didi.crpUploadedImage.isNullOrEmpty()){
                                        downloadAuthorizedImageItem(didi.id,didi.crpUploadedImage?: BLANK_STRING, prefRepo = prefRepo )
                                    }
                                }
                            } catch (ex: Exception) {
                                onError(
                                    tag = "VillageSelectionViewModel",
                                    "Error : ${didiResponse.message}"
                                )

                            }
                        }
                    }
                } else {
                    val ex = ApiResponseFailException(didiResponse.message)
                    if (!RetryHelper.retryApiList.contains(ApiType.DIDI_LIST_API)) RetryHelper.retryApiList.add(
                        ApiType.DIDI_LIST_API
                    )
                    RetryHelper.stepListApiVillageId.add(villageId)
                    onCatchError(ex, ApiType.DIDI_LIST_API)
                }
            } catch (ex: Exception) {
                if (ex !is JsonSyntaxException) {
                    if (!RetryHelper.retryApiList.contains(ApiType.DIDI_LIST_API)) RetryHelper.retryApiList.add(
                        ApiType.DIDI_LIST_API
                    )
                    RetryHelper.stepListApiVillageId.add(villageId)
                }
                onCatchError(ex, ApiType.DIDI_LIST_API)
            }

            //Fetch Wealth Ranking
            try {
                val didiRankingResponse = apiService.getDidisWithRankingFromNetwork(
                    villageId = villageId, "Category", StepResultTypeRequest(
                        StepType.WEALTH_RANKING.name, ResultType.ALL.name
                    )
                )
                if (didiRankingResponse.status.equals(SUCCESS, true)) {
                    didiRankingResponse.data?.let { didiRank ->
                        if (didiRank.beneficiaryList?.richDidi?.isNotEmpty() == true) {
                            didiRank.beneficiaryList?.richDidi?.forEach { richDidi ->
                                richDidi?.id?.let { didiId ->
                                    didiDao.updateDidiRank(
                                        didiId, WealthRank.RICH.rank
                                    )
                                }
                            }
                        }
                        if (didiRank.beneficiaryList?.mediumDidi?.isNotEmpty() == true) {
                            didiRank.beneficiaryList?.mediumDidi?.forEach { mediumDidi ->
                                mediumDidi?.id?.let { didiId ->
                                    didiDao.updateDidiRank(
                                        didiId, WealthRank.MEDIUM.rank
                                    )
                                }
                            }
                        }
                        if (didiRank.beneficiaryList?.poorDidi?.isNotEmpty() == true) {
                            didiRank.beneficiaryList?.poorDidi?.forEach { poorDidi ->
                                poorDidi?.id?.let { didiId ->
                                    didiDao.updateDidiRank(
                                        didiId, WealthRank.POOR.rank
                                    )
                                }
                            }
                        }
                    }
                } else {
                    val ex = ApiResponseFailException(
                        didiRankingResponse.message ?: "Didi Ranking Api Failed"
                    )
                    if (!RetryHelper.retryApiList.contains(ApiType.DIDI_RANKING_API)) RetryHelper.retryApiList.add(
                        ApiType.DIDI_RANKING_API
                    )
                    RetryHelper.stepListApiVillageId.add(villageId)
                    onCatchError(ex, ApiType.DIDI_RANKING_API)
                }
            } catch (ex: Exception) {
                if (ex !is JsonSyntaxException) {
                    if (!RetryHelper.retryApiList.contains(ApiType.DIDI_RANKING_API)) RetryHelper.retryApiList.add(
                        ApiType.DIDI_RANKING_API
                    )
                    RetryHelper.stepListApiVillageId.add(villageId)
                }
                onCatchError(ex, ApiType.DIDI_RANKING_API)
            }

            //Fetch Pat Survey Answers
            try {
                val answerApiResponse =
                    apiService.fetchPATSurveyToServer(
                        listOf(villageId)
                    )
                if (answerApiResponse.status.equals(SUCCESS, true)) {
                    answerApiResponse.data?.let {
                        val answerList: ArrayList<SectionAnswerEntity> =
                            arrayListOf()
                        val numAnswerList: ArrayList<NumericAnswerEntity> =
                            arrayListOf()
                        val didiIdList = mutableListOf<Int>()
                        it.forEach { item ->
                            if (item.userType.equals(USER_CRP, true)){
                                try {
                                    item.beneficiaryId?.let { it1 -> didiIdList.add(it1) }
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
                                } catch (ex: Exception) {
                                    ex.printStackTrace()
                                    Log.e(
                                        "TAG",
                                        "fetchVillageList: Eroor ${ex.message}"
                                    )
                                }

                                if (item?.answers?.isNotEmpty() == true) {
                                    item?.answers?.forEach { answersItem ->
                                        val quesDetails =
                                            questionDao.getQuestionForLanguage(
                                                answersItem?.questionId
                                                    ?: 0,
                                                prefRepo.getAppLanguageId()
                                                    ?: 2
                                            )
                                        if (answersItem?.questionType?.equals(
                                                QuestionType.Numeric_Field.name
                                            ) == true
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
                                                    totalAssetAmount = if(quesDetails?.questionFlag.equals(
                                                            QUESTION_FLAG_WEIGHT)) answersItem?.totalWeight?.toDouble() else stringToDouble(formatRatio(answersItem?.ratio?:DOUBLE_ZERO)) ,
                                                    needsToPost = false,
                                                    answerValue = (if(quesDetails?.questionFlag.equals(
                                                            QUESTION_FLAG_WEIGHT)) answersItem?.totalWeight?.toDouble() else stringToDouble(formatRatio(answersItem?.ratio?:DOUBLE_ZERO))).toString(),
                                                    type = answersItem?.questionType
                                                        ?: QuestionType.RadioButton.name,
                                                    assetAmount = answersItem?.assetAmount
                                                        ?: "0",
                                                    questionFlag = quesDetails?.questionFlag
                                                        ?: BLANK_STRING
                                                )
                                            )

                                            if (answersItem.options?.isNotEmpty() == true) {

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
                                                            optionValue = optionItem?.optionValue ?: 0
                                                        )
                                                    )
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
                                                    totalAssetAmount = if(quesDetails?.questionFlag.equals(
                                                            QUESTION_FLAG_WEIGHT)) answersItem?.totalWeight?.toDouble() else stringToDouble(formatRatio(answersItem?.ratio?:DOUBLE_ZERO)),
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
                            answerDao.deleteAllAnswersForVillage(villageId)
                            delay(100)
                            answerDao.insertAll(answerList)
                        }
                        if (numAnswerList.isNotEmpty()) {
                            numericAnswerDao.deleteAllNumericAnswersForDidis(didiIdList)
                            delay(100)
                            numericAnswerDao.insertAll(numAnswerList)
                        }
                    }
                } else {
                    val ex =
                        ApiResponseFailException(answerApiResponse.message)
                    RetryHelper.retryApiList.add(ApiType.PAT_CRP_SURVEY_SUMMARY)
                    onCatchError(ex, ApiType.PAT_CRP_SURVEY_SUMMARY)
                }
            } catch (ex: Exception) {
                if (ex !is JsonSyntaxException) {
                    RetryHelper.retryApiList.add(ApiType.PAT_CRP_SURVEY_SUMMARY)
                }
                onCatchError(ex, ApiType.PAT_CRP_SURVEY_SUMMARY)
            }
        }
    }

    private fun syncCrpData(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener) {
        addTolasToNetworkForCrp(prefRepo = prefRepo, networkCallbackListener)
    }

    private fun addTolasToNetworkForCrp(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaList = tolaDao.fetchTolaNeedToPost(true,"",0)
            val jsonTola = JsonArray()
            if (tolaList.isNotEmpty()) {
                for (tola in tolaList) {
                    jsonTola.add(AddCohortRequest.getRequestObjectForTola(tola).toJson())
                }
                Log.e("tola need to post","$tolaList.size")
                val response = apiService.addCohort(jsonTola)
                NudgeLogger.d("VillageSelectionRepository","addCohort Request=> ${Gson().toJson(jsonTola)}")
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.let {
                        if((response.data[0].transactionId.isNullOrEmpty())) {
                            response.data.forEach { tolaDataFromNetwork ->
                                tolaList.forEach { tola ->
                                    if (TextUtils.equals(tolaDataFromNetwork.name, tola.name)) {
                                        tola.serverId = tolaDataFromNetwork.id
                                        tola.createdDate = tolaDataFromNetwork.createdDate
                                        tola.modifiedDate = tolaDataFromNetwork.modifiedDate
                                    }
                                    tolaDao.updateTolaDetailAfterSync(
                                        id = tola.id,
                                        serverId = tola.serverId,
                                        needsToPost = false,
                                        transactionId = "",
                                        createdDate = tola.createdDate?:0L,
                                        modifiedDate = tola.modifiedDate?:0L
                                    )
                                    Log.e("tola after update", "$tolaList.size")
                                }
                            }
                            checkTolaAddStatusForCrp(prefRepo, networkCallbackListener)
                        } else {
                            for (i in 0 until response.data.size){
                                tolaList[i].transactionId = response.data[i].transactionId
                            }
                            updateLocalTransactionIdToLocalTolaForCrp(prefRepo, tolaList,networkCallbackListener)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
                }

                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }
            } else {
                checkTolaAddStatusForCrp(prefRepo, networkCallbackListener)
            }
        }

    }

    private fun updateLocalTransactionIdToLocalTolaForCrp(prefRepo: PrefRepo, tolaList: List<TolaEntity>, networkCallbackListener: NetworkCallbackListener) {
        tolaList.forEach{tola->
            tola.transactionId?.let { tolaDao.updateTolaTransactionId(tola.id, it) }
        }
        isPendingForCrp = 1
        checkStatusForCrp(prefRepo, networkCallbackListener)
    }

    fun checkStatusForCrp(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener) {

                        checkTolaAddStatusForCrp(prefRepo, networkCallbackListener)

                        checkTolaDeleteStatusForCrp(prefRepo, networkCallbackListener)

                        checkTolaUpdateStatusForCrp(prefRepo, networkCallbackListener)
                        checkAddDidiStatusForCrp(prefRepo, networkCallbackListener)
                        checkDeleteDidiStatusForCrp(prefRepo, networkCallbackListener)
                        checkUpdateDidiStatusForCrp(prefRepo, networkCallbackListener)


        checkDidiWealthStatusForCrp(prefRepo = prefRepo, networkCallbackListener)


        checkDidiPatStatus(prefRepo, networkCallbackListener)

                        checkVOStatus(prefRepo, networkCallbackListener)


    }


    private fun checkTolaAddStatusForCrp(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaList = tolaDao.fetchPendingTola(true,"")
            if(tolaList.isNotEmpty()) {
                val ids: ArrayList<String> = arrayListOf()
                tolaList.forEach { tola ->
                    tola.transactionId?.let { ids.add(it) }
                }
                val response = apiService.getPendingStatus(TransactionIdRequest("",ids))
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.forEach { transactionIdResponse ->
                        tolaList.forEach { tola ->
                            if (transactionIdResponse.transactionId == tola.transactionId) {
                                tola.serverId = transactionIdResponse.referenceId
                            }
                        }
                    }
                    updateTolaNeedTOPostListForCrp(prefRepo, tolaList,networkCallbackListener)
                } else
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }

                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }

            } else {
                deleteTolaToNetworkForCrp(prefRepo, networkCallbackListener)
            }
        }
    }

    private fun deleteTolaToNetworkForCrp(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener) {
        Log.e("delete tola","called")
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaList = tolaDao.fetchAllTolaNeedToDelete(TolaStatus.TOLA_DELETED.ordinal)
            val jsonTola = JsonArray()
            if (tolaList.isNotEmpty()) {
                for (tola in tolaList) {
                    val localDidiListForTola = didiDao.getDidisForTola(if (tola.serverId == 0) tola.id else tola.serverId)
                    if (localDidiListForTola.isEmpty()) {
                        jsonTola.add(
                            DeleteTolaRequest(
                                tola.serverId,
                                localModifiedDate = System.currentTimeMillis(),
                                tola.name,
                                tola.villageId,
                                tola.localUniqueId ?: ""
                            ).json()
                        )
                    }
                }
                NudgeLogger.d("SyncHelper","deleteTolaToNetwork -> tola need to post :${tolaList.size}")
                NudgeLogger.d("SyncHelper","deleteTolaToNetwork -> jsonTola :${jsonTola}")
                val response = apiService.deleteCohort(jsonTola)
                NudgeLogger.d("VillageSelectionRepository","deleteCohort Request=>${Gson().toJson(jsonTola)}")
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.let {
                        if((response.data[0]?.transactionId.isNullOrEmpty())) {
                            tolaList.forEach { tola ->
                                tolaDao.deleteTola(tola.id)
                            }
                            checkTolaDeleteStatusForCrp(prefRepo, networkCallbackListener)
                        } else {
                            for (i in 0 until response.data.size){
                                tolaList[i].transactionId = response.data[i]?.transactionId
                                tolaList[i].transactionId?.let { it1 ->
                                    tolaDao.updateTolaTransactionId(tolaList[i].id,
                                        it1
                                    )
                                }
                            }
                            isPendingForCrp = 2
                            checkStatusForCrp(prefRepo, networkCallbackListener)
                        }
                    }
                }
                else {
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
                }

                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }

            } else {
                checkTolaDeleteStatusForCrp(prefRepo, networkCallbackListener)
            }
        }
    }

    private fun checkTolaDeleteStatusForCrp(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaList = tolaDao.fetchAllPendingTolaNeedToDelete(TolaStatus.TOLA_DELETED.ordinal,"")
            if(tolaList.isNotEmpty()) {
                val ids: ArrayList<String> = arrayListOf()
                tolaList.forEach { tola ->
                    tola.transactionId?.let { ids.add(it) }
                }
                val response = apiService.getPendingStatus(TransactionIdRequest("",ids))
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.forEach { transactionIdResponse ->
                        tolaList.forEach { tola ->
                            if (transactionIdResponse.transactionId == tola.transactionId) {
                                tolaDao.deleteTola(tola.id)
                            }
                        }
                    }
                    updateTolasToNetworkForCrp(prefRepo, networkCallbackListener)
                } else
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
            } else {
                updateTolasToNetworkForCrp(prefRepo, networkCallbackListener)
            }
        }
    }

    private fun updateTolasToNetworkForCrp(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaList = tolaDao.fetchAllTolaNeedToUpdate(true,"",0)
            val jsonTola = JsonArray()
            if (tolaList.isNotEmpty()) {
                for (tola in tolaList) {
                    jsonTola.add(EditCohortRequest.getRequestObjectForTola(tola).toJson())
                }
                Log.e("tola need to post","$tolaList.size")
                val response = apiService.editCohort(jsonTola)
                NudgeLogger.d("VillageSelectionRepository","editCohort Request=> ${Gson().toJson(jsonTola)}")
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.let {
                        if((response.data[0].transactionId.isNullOrEmpty())) {
                            tolaList.forEach { tola ->
                                tolaDao.updateNeedToPost(tola.id,false)
                            }
                            checkTolaUpdateStatusForCrp(prefRepo, networkCallbackListener)
                        } else {
                            for (i in 0 until response.data.size){
                                tolaList[i].transactionId = response.data[i].transactionId
                                tolaList[i].transactionId?.let { it1 ->
                                    tolaDao.updateTolaTransactionId(tolaList[i].id,
                                        it1
                                    )
                                }
                            }
                            isPendingForCrp = 3
                            checkStatusForCrp(prefRepo, networkCallbackListener)
                        }
                    }
                }
                else {
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
                }

                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }
            } else {
                checkTolaUpdateStatusForCrp(prefRepo, networkCallbackListener)
            }
        }

    }

    private fun checkTolaUpdateStatusForCrp(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaList = tolaDao.fetchAllPendingTolaNeedToUpdate(true,"")
            if(tolaList.isNotEmpty()) {
                val ids: ArrayList<String> = arrayListOf()
                tolaList.forEach { tola ->
                    tola.transactionId?.let { ids.add(it) }
                }
                val response = apiService.getPendingStatus(TransactionIdRequest("",ids))
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.forEach { transactionIdResponse ->
                        tolaList.forEach { tola ->
                            if (transactionIdResponse.transactionId == tola.transactionId) {
                                tolaDao.updateNeedToPost(tola.id,false)
                                tolaDao.updateTolaTransactionId(tola.id,"")
                            }
                        }
                    }
                    addDidisToNetworkForCrp(prefRepo, networkCallbackListener)
                } else
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }
            } else {
                addDidisToNetworkForCrp(prefRepo, networkCallbackListener)
            }
        }
    }

    private fun addDidisToNetworkForCrp(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener) {
        callCrpWorkFlowAPIForStep(prefRepo, 1)
        Log.e("add didi","called")
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = didiDao.fetchAllDidiNeedToAdd(true,"",0, DidiStatus.DIDI_ACTIVE.ordinal)
            for(didi in didiList){
                val tola = tolaDao.fetchSingleTolaFromServerId(didi.cohortId)
                if (tola != null) {
                    didi.cohortId = tola.serverId
                }
            }
            val jsonDidi = JsonArray()
            if (didiList.isNotEmpty()) {
                for (didi in didiList) {
                    jsonDidi.add(AddDidiRequest.getRequestObjectForDidi(didi).toJson())
                }
                val response = apiService.addDidis(jsonDidi)
                NudgeLogger.d("VillageSelectionRepository","addDidis Request=>${Gson().toJson(jsonDidi)}")
                if (response.status.equals(SUCCESS, true)) {
                    if(response.data?.get(0)?.transactionId.isNullOrEmpty()) {
                        response.data?.let {
                            response.data.forEach { didiFromNetwork ->
                                didiList.forEach { didi ->
                                    if (TextUtils.equals(didiFromNetwork.name, didi.name)) {
                                        didi.serverId = didiFromNetwork.id
                                        didi.createdDate = didiFromNetwork.createdDate
                                        didi.modifiedDate = didiFromNetwork.modifiedDate
                                    }
                                    didiDao.updateDidiDetailAfterSync(id = didi.id, serverId = didi.serverId, needsToPost = false, transactionId = "", createdDate = didi.createdDate?:0, modifiedDate = didi.modifiedDate?:0)
                                }
                            }
                        }
                        checkAddDidiStatusForCrp(prefRepo, networkCallbackListener)
                    } else {
                        for (i in 0..(response.data?.size?.minus(1) ?: 0)){
                            didiList[i].transactionId = response.data?.get(i)?.transactionId
                            didiList[i].transactionId?.let {
                                didiDao.updateDidiTransactionId(didiList[i].id,
                                    it
                                )
                            }
                        }
                        isPendingForCrp = 4
                        checkStatusForCrp(prefRepo, networkCallbackListener)
                    }
                } else {
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
                }
                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }
            } else {
                checkAddDidiStatusForCrp(prefRepo, networkCallbackListener)
            }
        }
    }

    private fun checkAddDidiStatusForCrp(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = didiDao.fetchPendingDidi(true,"")
            if(didiList.isNotEmpty()) {
                val ids: ArrayList<String> = arrayListOf()
                didiList.forEach { tola ->
                    tola.transactionId?.let { ids.add(it) }
                }
                val response = apiService.getPendingStatus(TransactionIdRequest("",ids))
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.forEach { transactionIdResponse ->
                        didiList.forEach { didi ->
                            if (transactionIdResponse.transactionId == didi.transactionId) {
                                didi.serverId = transactionIdResponse.referenceId
                            }
                            didiDao.updateDidiDetailAfterSync(id = didi.id, serverId = didi.serverId, needsToPost = false, transactionId = "", createdDate = didi.createdDate?:0, modifiedDate = didi.modifiedDate?:0)
                        }
                    }
                    deleteDidisToNetworkForCrp(prefRepo, networkCallbackListener)
                } else
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
            } else {
                deleteDidisToNetworkForCrp(prefRepo, networkCallbackListener)
            }
        }

    }

    private fun deleteDidisToNetworkForCrp(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = didiDao.getDidisToBeDeleted(
                activeStatus = DidiStatus.DIID_DELETED.ordinal,
                needsToPostDeleteStatus = true,
                transactionId = ""
            )
            val jsonDidi = JsonArray()
            if (didiList.isNotEmpty()) {
                for (didi in didiList) {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("id", didi.serverId)
                    jsonDidi.add(jsonObject)
                }
                Log.e("tola need to post","$didiList.size")
                val response = apiService.deleteDidi(jsonDidi)
                NudgeLogger.d("VillageSelectionRepository","deleteDidi Request=> ${jsonDidi.json()}")
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.let {
                        if((response.data[0].transactionId.isNullOrEmpty())) {
                            didiList.forEach { tola ->
                                didiDao.deleteDidi(tola.id)
                            }
                            checkDeleteDidiStatusForCrp(prefRepo, networkCallbackListener)
                        } else {
                            for (i in 0 until response.data.size){
                                didiList[i].transactionId = response.data[i].transactionId
                                didiList[i].transactionId?.let { it1 ->
                                    didiDao.updateDidiTransactionId(didiList[i].id,
                                        it1
                                    )
                                }
                            }
                            isPendingForCrp = 5
                            checkStatusForCrp(prefRepo, networkCallbackListener)
                        }
                    }
                }
                else {
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
                }
                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }
            } else {
                checkDeleteDidiStatusForCrp(prefRepo, networkCallbackListener)
            }
        }

    }

    private fun checkDeleteDidiStatusForCrp(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = didiDao.fetchAllPendingDidiNeedToDelete(
                DidiStatus.DIID_DELETED.ordinal,
                ""
            )
            if(didiList.isNotEmpty()) {
                val ids: ArrayList<String> = arrayListOf()
                didiList.forEach { didi ->
                    didi.transactionId?.let { ids.add(it) }
                }
                val response = apiService.getPendingStatus(TransactionIdRequest("",ids))
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.forEach { transactionIdResponse ->
                        didiList.forEach { didi ->
                            if (transactionIdResponse.transactionId == didi.transactionId) {
                                didiDao.deleteDidi(didi.id)
                            }
                        }
                    }
                    updateDidiToNetworkForCrp(prefRepo, networkCallbackListener)
                } else
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }

                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }
            } else {
                updateDidiToNetworkForCrp(prefRepo, networkCallbackListener)
            }
        }

    }

    private fun updateDidiToNetworkForCrp(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = didiDao.fetchAllDidiNeedToUpdate(true, "")
            if (didiList.isNotEmpty()) {
                val didiRequestList = arrayListOf<EditDidiRequest>()
                didiList.forEach { didi->
                    didiRequestList.add(
                        EditDidiRequest(
                            didi.serverId,
                            didi.name,
                            didi.address,
                            didi.guardianName,
                            didi.castId,
                            didi.cohortId,
                            didi.villageId,
                            didi.cohortName
                        )
                    )
                }
                NudgeLogger.d("VillageSelectionRepository","updateDidiToNetworkForCrp updateDidis Request=> ${didiRequestList.json()}")
                val response = apiService.updateDidis(didiRequestList)
                if (response.status.equals(SUCCESS, true)) {
                    if(response.data?.get(0)?.transactionId.isNullOrEmpty()) {
                        response.data?.let {
                            response.data.forEach { _ ->
                                didiList.forEach { didi ->
                                    didiDao.updateNeedToPost(didi.id,false)
                                }
                            }
                        }
                        updateDidisNeedTOPostListForCrp(prefRepo, didiList,networkCallbackListener)

                    } else {
                        for (i in 0..(response.data?.size?.minus(1) ?: 0)){
                            didiList[i].transactionId = response.data?.get(i)?.transactionId
                            didiList[i].transactionId?.let {
                                didiDao.updateDidiTransactionId(didiList[i].id,
                                    it
                                )
                            }
                        }
                        isPendingForCrp = 6
                        checkStatusForCrp(prefRepo, networkCallbackListener)
                    }
                } else {
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
                }
                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }
            } else {
                checkUpdateDidiStatusForCrp(prefRepo, networkCallbackListener)
            }
        }

    }

    private fun updateDidisNeedTOPostListForCrp(prefRepo: PrefRepo, oldDidiList: List<DidiEntity>, networkCallbackListener: NetworkCallbackListener) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            oldDidiList.forEach(){ didiEntity ->
                didiEntity.needsToPost = false
                didiEntity.transactionId = ""
                didiDao.updateDidiDetailAfterSync(id = didiEntity.id, serverId = didiEntity.serverId, needsToPost = false, transactionId = "", createdDate = didiEntity.createdDate?:0, modifiedDate = didiEntity.modifiedDate?:0)
            }
            checkUpdateDidiStatusForCrp(prefRepo, networkCallbackListener)
        }
    }

    private fun checkUpdateDidiStatusForCrp(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = didiDao.fetchAllPendingDidiNeedToUpdate(true, "")
            if(didiList.isNotEmpty()) {
                val ids: ArrayList<String> = arrayListOf()
                didiList.forEach { tola ->
                    tola.transactionId?.let { ids.add(it) }
                }
                val response = apiService.getPendingStatus(TransactionIdRequest("",ids))
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.forEach { transactionIdResponse ->
                        didiList.forEach { didi ->
                            if (transactionIdResponse.transactionId == didi.transactionId) {
                                didi.transactionId = ""
                                didiDao.updateNeedToPost(didi.id,false)
                                didiDao.updateDidiTransactionId(didi.id,"")
                            }
                        }
                    }
                    updateWealthRankingToNetwork(prefRepo, networkCallbackListener)
                } else{
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
                }
                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }
            } else {
                updateWealthRankingToNetwork(prefRepo, networkCallbackListener)
            }
        }

    }

    private fun updateWealthRankingToNetwork(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener) {
        Log.e("update wealth ranking","called")
        callCrpWorkFlowAPIForStep(prefRepo, 2)
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                withContext(Dispatchers.IO){
                    val needToPostDidiList = didiDao.getAllNeedToPostDidiRanking(true)
                    if (needToPostDidiList.isNotEmpty()) {
                        val didiWealthRequestList = arrayListOf<EditDidiWealthRankingRequest>()
                        val didiStepRequestList = arrayListOf<EditDidiWealthRankingRequest>()
                        needToPostDidiList.forEach { didi ->
                            didiWealthRequestList.add(
                                EditDidiWealthRankingRequest(
                                    didi.serverId,
                                    StepType.WEALTH_RANKING.name,
                                    didi.wealth_ranking,
                                    rankingEdit = didi.rankingEdit,
                                    localModifiedDate = System.currentTimeMillis(),
                                    name = didi.name,
                                    address = didi.address,
                                    guardianName = didi.guardianName,
                                    villageId = didi.villageId,
                                    deviceId = didi.localUniqueId
                                )
                            )
                            didiStepRequestList.add(
                                EditDidiWealthRankingRequest(
                                    didi.serverId,
                                    StepType.SOCIAL_MAPPING.name,
                                    StepStatus.COMPLETED.name,
                                    rankingEdit = didi.rankingEdit,
                                    localModifiedDate = System.currentTimeMillis(),
                                    name = didi.name,
                                    address = didi.address,
                                    guardianName = didi.guardianName,
                                    villageId = didi.villageId,
                                    deviceId = didi.localUniqueId
                                )
                            )
                        }
                        didiWealthRequestList.addAll(didiStepRequestList)
                        val updateWealthRankResponse = apiService.updateDidiRanking(didiWealthRequestList)
                        NudgeLogger.d("VillageSelectionRepository","updateWealthRankingToNetwork Request=> ${Gson().toJson(didiStepRequestList)}")
                        if(updateWealthRankResponse.status.equals(SUCCESS,true)){
                            val didiListResponse = updateWealthRankResponse.data
                            if(!didiListResponse?.get(0)?.transactionId.isNullOrEmpty()){
                                val size = needToPostDidiList.indices
                                for(i in size) {
                                    val serverResponseDidi = updateWealthRankResponse.data?.get(i)
                                    val localDidi = needToPostDidiList[i]
                                    serverResponseDidi?.transactionId?.let {
                                        didiDao.updateDidiTransactionId(localDidi.id,
                                            it
                                        )
                                    }
                                }
                                isPendingForCrp = 7
                                checkStatusForCrp(prefRepo, networkCallbackListener)

                            } else {
                                needToPostDidiList.forEach { didi ->
                                    didiDao.updateDidiNeedToPostWealthRank(didi.id,false)
                                }
                                checkDidiWealthStatusForCrp(prefRepo, networkCallbackListener)
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                networkCallbackListener.onFailed()
                            }
                        }
                        if(!updateWealthRankResponse.lastSyncTime.isNullOrEmpty()){
                            updateLastSyncTime(prefRepo,updateWealthRankResponse.lastSyncTime)
                        }
                    } else {
                        checkDidiWealthStatusForCrp(prefRepo, networkCallbackListener)
                    }

                }
            } catch (ex: Exception) {
                networkCallbackListener.onFailed()
                onCatchError(ex, ApiType.DIDI_EDIT_API)
            }
        }
    }

    private fun checkDidiWealthStatusForCrp(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = didiDao.fetchPendingWealthStatusDidi(true,"")
            if(didiList.isNotEmpty()) {
                val ids: ArrayList<String> = arrayListOf()
                didiList.forEach { didi ->
                    didi.transactionId?.let { ids.add(it) }
                }
                val response = apiService.getPendingStatus(TransactionIdRequest("",ids))
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.forEach { transactionIdResponse ->
                        didiList.forEach { didi ->
                            if (transactionIdResponse.transactionId == didi.transactionId) {
                                didiDao.updateDidiNeedToPostWealthRank(didi.id,false)
                                didiDao.updateDidiTransactionId(didi.id,"")
                            }
                        }
                    }
                    savePATSummeryToServer(prefRepo, networkCallbackListener)
                } else {
                    withContext(Dispatchers.Main) {
                        networkCallbackListener.onFailed()
                    }
                }
                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }
            } else {
                savePATSummeryToServer(prefRepo, networkCallbackListener)
            }
        }

    }

    private fun savePATSummeryToServer(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener){
        callCrpWorkFlowAPIForStep(prefRepo, 3)
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val didiIDList= answerDao.fetchPATSurveyDidiList()
                uploadDidiImagesToServer(prefRepo, MyApplication.applicationContext())
                if(didiIDList.isNotEmpty()){
                    var optionList: List<OptionsItem>
                    val answeredDidiList: java.util.ArrayList<PATSummarySaveRequest> = arrayListOf()
                    var surveyId =0
                    var scoreDidiList: java.util.ArrayList<EditDidiWealthRankingRequest> = arrayListOf()
                    val userType=if((prefRepo.getPref(PREF_KEY_TYPE_NAME, "") ?: "").equals(BPC_USER_TYPE, true)) USER_BPC else USER_CRP
                    didiIDList.forEachIndexed { index, didi ->
                        NudgeLogger.d("SyncHelper", "savePATSummeryToServer Save: ${didi.id} :: ${didi.patSurveyStatus}")
                        calculateDidiScore(didiId = didi.id, prefRepo = prefRepo)
                        delay(100)
                        didi.score = didiDao.getDidiScoreFromDb(didi.id)
                        val didiEntity = didiDao.getDidi(didi.id)
                        val qList: java.util.ArrayList<AnswerDetailDTOListItem> = arrayListOf()
                        val needToPostQuestionsList = answerDao.getAllNeedToPostQuesForDidi(didi.id)
                        if (needToPostQuestionsList.isNotEmpty()) {
                            needToPostQuestionsList.forEach {
                                surveyId = questionDao.getQuestion(it.questionId).surveyId ?: 0
                                if (!it.type.equals(QuestionType.Numeric_Field.name, true)) {
                                    optionList = listOf(
                                        OptionsItem(
                                            optionId = it.optionId,
                                            optionValue = it.optionValue,
                                            count = 0,
                                            summary = it.summary,
                                            display = it.answerValue,
                                            weight = it.weight,
                                            isSelected = false
                                        )
                                    )
                                } else {
                                    val numOptionList =
                                        numericAnswerDao.getSingleQueOptions(it.questionId, it.didiId)
                                    val tList: java.util.ArrayList<OptionsItem> = arrayListOf()
                                    if (numOptionList.isNotEmpty()) {
                                        numOptionList.forEach { numOption ->
                                            tList.add(
                                                OptionsItem(
                                                    optionId = numOption.optionId,
                                                    optionValue = numOption.optionValue,
                                                    count = numOption.count,
                                                    summary = it.summary,
                                                    display = it.answerValue,
                                                    weight = numOption.weight,
                                                    isSelected = false
                                                )
                                            )
                                        }
                                        optionList = tList
                                    }else{
                                        tList.add(
                                            OptionsItem(
                                                optionId = it.optionId,
                                                optionValue = 0,
                                                count = 0,
                                                summary = it.summary,
                                                display = it.answerValue,
                                                weight = it.weight,
                                                isSelected = false
                                            )
                                        )

                                        optionList = tList
                                    }

                                }
                                try {
                                    qList.add(
                                        AnswerDetailDTOListItem(
                                            questionId = it.questionId,
                                            section = it.actionType,
                                            options = optionList,
                                            assetAmount = it.assetAmount
                                        )
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                        val passingMark = questionDao.getPassingScore()
                        var comment = BLANK_STRING
                        comment =
                            if (didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) {
                                PatSurveyStatus.NOT_AVAILABLE.name
                            } else if (didi.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal) {
                                BLANK_STRING
                            } else {
                                if ((didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal && didi.section2Status == PatSurveyStatus.NOT_STARTED.ordinal)
                                    || (didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal && didi.patExclusionStatus != ExclusionType.NO_EXCLUSION.ordinal)) {
                                    TYPE_EXCLUSION
                                } else {
                                    if (didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal && didi.section2Status == PatSurveyStatus.COMPLETED.ordinal && didi.score < passingMark) {
                                        LOW_SCORE
                                    } else {
                                        BLANK_STRING
                                    }
                                }
                            }
                        scoreDidiList.add(
                            EditDidiWealthRankingRequest(
                                id = didi.serverId,
                                score = didi.score,
                                comment = comment,
                                type = if (prefRepo.isUserBPC()) BPC_SURVEY_CONSTANT else PAT_SURVEY,
                                result = if (didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) {
                                    DIDI_NOT_AVAILABLE
                                } else if (didi.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal) {
                                    PatSurveyStatus.INPROGRESS.name
                                } else {
                                    if (didi.forVoEndorsement == 0 || didi.patExclusionStatus != ExclusionType.NO_EXCLUSION.ordinal) DIDI_REJECTED else {
                                        if (prefRepo.isUserBPC())
                                            VERIFIED_STRING
                                        else
                                            COMPLETED_STRING
                                    }
                                },
                                rankingEdit = didi.patEdit,
                                shgFlag = SHGFlag.fromInt(didi.shgFlag).name,
                                ableBodiedFlag = AbleBodiedFlag.fromInt(didi.ableBodiedFlag).name,
                                address = didiEntity.address,
                                guardianName = didiEntity.guardianName,
                                villageId = didi.villageId,
                                deviceId = didiEntity.localUniqueId
                            )
                        )
                        val stateId = villageListDao.getVillage(didi.villageId).stateId
                        answeredDidiList.add(
                            PATSummarySaveRequest(
                                villageId = didi.villageId,
                                surveyId = surveyId,
                                cohortName = didiEntity.cohortName,
                                beneficiaryAddress = didiEntity.address,
                                guardianName = didiEntity.guardianName,
                                beneficiaryId = didi.serverId,
                                languageId = prefRepo.getAppLanguageId() ?: 2,
                                stateId = stateId,
                                totalScore = didi.score,
                                userType = userType,
                                beneficiaryName = didi.name,
                                answerDetailDTOList = qList,
                                patSurveyStatus = didi.patSurveyStatus,
                                section2Status = didi.section2Status,
                                section1Status = didi.section1Status,
                                shgFlag = didi.shgFlag,
                                patExclusionStatus = didi.patExclusionStatus  ?: 0
                            )
                        )
                    }
                    if (answeredDidiList.isNotEmpty()) {
                        withContext(Dispatchers.IO) {
                            val saveAPIResponse = apiService.savePATSurveyToServer(answeredDidiList)
                            NudgeLogger.d("VillageSelectionRepository","savePATSurveyToServer Request=>${answeredDidiList.json()}")
                            if (saveAPIResponse.status.equals(SUCCESS, true)) {
                                if (saveAPIResponse.data?.get(0)?.transactionId.isNullOrEmpty()) {
                                    didiIDList.forEach { didiItem ->
                                        didiDao.updateNeedToPostPAT(
                                            false,
                                            didiItem.id
                                        )
                                    }

                                    checkDidiPatStatus(prefRepo, networkCallbackListener)
                                } else {
                                    for (i in didiIDList.indices) {
                                        saveAPIResponse.data?.get(i)?.let {
                                            didiDao.updateDidiTransactionId(
                                                didiIDList[i].id,
                                                it.transactionId
                                            )
                                        }
                                        didiDao.updateDidiNeedToPostPat(didiIDList[i].id, true)
                                    }
                                    isPendingForCrp = 8
                                    checkStatusForCrp(prefRepo, networkCallbackListener)
                                }
                                savePatScoreToServer(scoreDidiList)
                            } else {
                                withContext(Dispatchers.Main) {
                                    networkCallbackListener.onFailed()
                                }
                            }
                            if (!saveAPIResponse.lastSyncTime.isNullOrEmpty()) {
                                updateLastSyncTime(prefRepo, saveAPIResponse.lastSyncTime)
                            }
                        }
                    } else {
                        checkDidiPatStatus(prefRepo, networkCallbackListener)
                    }
                } else {
                    checkDidiPatStatus(prefRepo, networkCallbackListener)
                }
            } catch (ex: Exception) {
                networkCallbackListener.onFailed()
                onCatchError(ex, ApiType.CRP_PAT_SAVE_ANSWER_SUMMARY)
                ex.printStackTrace()
            }
        }
    }

    private fun uploadDidiImagesToServer(prefRepo: PrefRepo, context : Context){
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = didiDao.fetchAllDidiNeedsToPostImageWithLimit(true)
            NudgeLogger.d("Synchelper", "uploadDidiImage DidiList: ${didiList} :: Size: ${didiList.size}")
            if(didiList.isNotEmpty()){
                val imageFilePart = ArrayList<MultipartBody.Part>()
                val requestDidiId = ArrayList<RequestBody>()
                val requestUserType = ArrayList<RequestBody>()
                val requestLocation = ArrayList<RequestBody>()
                try {
                    for(didi in didiList) {
                        if(imageFilePart.size == 5) {
                            break
                        }
                        val path = findImageLocationFromPath(didi.localPath)
                        NudgeLogger.d("Synchelper", "uploadDidiImage: $didi.id :: $path[1]")
                        val uri = path[0]
                        NudgeLogger.d(
                            "Synchelper",
                            "uploadDidiImage Prev: ${uri}"
                        )
                        val compressedImageFile =
                            compressImage(uri.toString(), context, getFileNameFromURL(uri))
                        val requestFile = RequestBody.create(
                            "multipart/form-data".toMediaTypeOrNull(),
                            File(compressedImageFile)
                        )
                        imageFilePart.add(MultipartBody.Part.createFormData(
                            "files",
                            File(compressedImageFile).name,
                            requestFile
                        ))
                        requestDidiId.add(RequestBody.create(
                            "multipart/form-data".toMediaTypeOrNull(),
                            didi.serverId.toString()
                        ))
                        requestUserType.add(RequestBody.create(
                            "multipart/form-data".toMediaTypeOrNull(),
                            if (prefRepo.isUserBPC()) USER_BPC else USER_CRP
                        ))
                        requestLocation.add(RequestBody.create("multipart/form-data".toMediaTypeOrNull(), path.get(1)))
                        NudgeLogger.d(
                            "Synchelper",
                            "uploadDidiImage Details: ${requestDidiId[requestDidiId.size-1].contentType().toString()}"
                        )
                    }
                    val imageUploadResponse = apiService.uploadDidiBulkImage(
                        imageFilePart,
                        requestDidiId,
                        requestUserType,
                        requestLocation
                    )
                    NudgeLogger.d(
                        "Synchelper",
                        "uploadDidiImage imageUploadRequest: ${imageUploadResponse.data ?: ""}"
                    )
                    if (imageUploadResponse.status == SUCCESS) {
                        for(i in didiList.indices) {
                            if(i == 5)
                                break
                            didiDao.updateNeedsToPostImage(didiList[i].id, false)
                        }
//                        if(didiList.size>5) {
                        uploadDidiImagesToServer(prefRepo, context)
//                        }
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }

    private fun savePatScoreToServer(scoreDidiList: java.util.ArrayList<EditDidiWealthRankingRequest>) {
        if(scoreDidiList.isNotEmpty()) {
            repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                NudgeLogger.d("VillageSelectionRepository","savePatScoreToServer Request=>${scoreDidiList.json()}")
                apiService.updateDidiScore(scoreDidiList)
            }
        }
    }

    fun checkDidiPatStatus(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = didiDao.fetchPendingPatStatusDidi(true,"")
            if(didiList.isNotEmpty()) {
                val ids: ArrayList<String> = arrayListOf()
                didiList.forEach { didi ->
                    didi.transactionId?.let { ids.add(it) }
                }
                val response = apiService.getPendingStatusForPat(TransactionIdRequest("PAT",ids))
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.forEach { transactionIdResponse ->
                        didiList.forEach { didi ->
                            if (transactionIdResponse.transactionId == didi.transactionId) {
                                didiDao.updateDidiNeedToPostPat(didi.id,false)
                                didiDao.updateDidiTransactionId(didi.id,"")
                            }
                        }
                    }
                    updateVoStatusToNetwork(prefRepo, networkCallbackListener)
                } else
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
            } else {
                updateVoStatusToNetwork(prefRepo, networkCallbackListener)
            }
        }

    }

    private fun updateVoStatusToNetwork(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener) {
        callCrpWorkFlowAPIForStep(prefRepo, 4)
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                withContext(Dispatchers.IO){
                    val needToPostDidiList = didiDao.fetchAllVONeedToPostStatusDidi(
                        needsToPostVo = true,
                        transactionId = ""
                    )
                    if(needToPostDidiList.isNotEmpty()){
                        val didiRequestList = arrayListOf<EditDidiWealthRankingRequest>()
                        needToPostDidiList.forEach { didi->
                            didi.voEndorsementStatus.let {
                                if (it == DidiEndorsementStatus.ENDORSED.ordinal) {
                                    didiRequestList.add(EditDidiWealthRankingRequest(didi.serverId,StepType.VO_ENDROSEMENT.name, ACCEPTED,
                                        localModifiedDate = System.currentTimeMillis(),
                                        rankingEdit = didi.voEndorsementEdit,
                                        address = didi.address,
                                        guardianName = didi.guardianName,
                                        villageId = didi.villageId,
                                        deviceId = didi.localUniqueId
                                    )
                                    )
                                } else if (it == DidiEndorsementStatus.REJECTED.ordinal) {
                                    didiRequestList.add(EditDidiWealthRankingRequest(didi.serverId,StepType.VO_ENDROSEMENT.name, DidiEndorsementStatus.REJECTED.name,
                                        localModifiedDate = System.currentTimeMillis(),
                                        rankingEdit = didi.voEndorsementEdit,
                                        address = didi.address,
                                        guardianName = didi.guardianName,
                                        villageId = didi.villageId,
                                        deviceId = didi.localUniqueId
                                    )
                                    )
                                }
                            }
                        }
                        val updateWealthRankResponse=apiService.updateDidiRanking(didiRequestList)
                        NudgeLogger.d("VillageSelectionRepository","updateVoStatusToNetwork Request=> ${Gson().toJson(didiRequestList)}")
                        if(updateWealthRankResponse.status.equals(SUCCESS,true)){
                            val didiListResponse = updateWealthRankResponse.data
                            if (didiListResponse?.get(0)?.transactionId != null) {
                                for (i in didiListResponse.indices) {
                                    val didiResponse = didiListResponse[i]
                                    val didi = needToPostDidiList[i]
                                    didiResponse.transactionId?.let {
                                        didiDao.updateDidiTransactionId(didi.id,
                                            it
                                        )
                                    }
                                }
                                isPendingForCrp = 9
                                checkStatusForCrp(prefRepo, networkCallbackListener)
                            } else {
                                if (didiListResponse != null) {
                                    for (i in didiRequestList.indices) {
                                        val didi = didiRequestList[i]
                                        didiDao.updateNeedToPostVOWithServerId(false, didi.id)
                                        didiDao.updateDidiTransactionIdWithServerId(didi.id, "")
                                        //commenting for now since it was having some issues.
                                        /*didiDao.updateNeedToPostVO(false, didi.id)
                                        didiDao.updateDidiTransactionId(didi.id, "")*/
                                    }
                                }
                                checkVOStatus(prefRepo, networkCallbackListener)
                            }
                        } else {
                            withContext(Dispatchers.Main){
                                networkCallbackListener.onFailed()
                            }
                        }
                        if(!updateWealthRankResponse.lastSyncTime.isNullOrEmpty()){
                            updateLastSyncTime(prefRepo,updateWealthRankResponse.lastSyncTime)
                        }
                    } else {
                        checkVOStatus(prefRepo, networkCallbackListener)
                    }
                }
            } catch (ex: Exception) {
                networkCallbackListener.onFailed()
                onCatchError(ex, ApiType.DIDI_EDIT_API)
            }
        }
    }

    private fun checkVOStatus(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = didiDao.fetchPendingVOStatusStatusDidi(true,"")
            if(didiList.isNotEmpty()) {
                val ids: ArrayList<String> = arrayListOf()
                didiList.forEach { didi ->
                    didi.transactionId?.let { ids.add(it) }
                }
                val response = apiService.getPendingStatus(TransactionIdRequest("",ids))
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.forEach { transactionIdResponse ->
                        didiList.forEach { didi ->
                            if (transactionIdResponse.transactionId == didi.transactionId) {
                                didiDao.updateNeedToPostVO(false,didi.id)
                                didiDao.updateDidiTransactionId(didi.id,"")
                            }
                        }
                    }
                    uploadFormsCAndD(prefRepo, MyApplication.applicationContext())
                    callCrpWorkFlowAPIForStep(prefRepo, 5)
                    delay(1500)
                    networkCallbackListener.onSuccess()
                } else {
                    networkCallbackListener.onFailed()
                }
                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }
            } else {
                uploadFormsCAndD(prefRepo, MyApplication.applicationContext())
                callCrpWorkFlowAPIForStep(prefRepo, 5)
                delay(1500)
                networkCallbackListener.onSuccess()
            }
        }

    }

    private fun uploadFormsCAndD(prefRepo: PrefRepo, context: Context) {
        repoJob = MyApplication.appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            val languageId = prefRepo.getAppLanguageId() ?: 2
            val villageList = villageListDao.getAllVillages(languageId)
            for (village in villageList) {
                if (prefRepo.getPref(
                        PREF_NEED_TO_POST_FORM_C_AND_D_ + prefRepo.getSelectedVillage().id,
                        false
                    )
                ) {
                    uploadFormCAndD(prefRepo, village.id, context)
                }
            }
        }
    }

    private fun uploadFormCAndD(prefRepo: PrefRepo, villageId: Int, context: Context) {
        repoJob = MyApplication.appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            val formList = arrayListOf<MultipartBody.Part>()
            val villageId = villageListDao.getVillage(villageId).id
            try {
                val formCImageList = (mutableMapOf<String, String>())
                for (i in 0..4) {
                    formCImageList[getFormSubPath(FORM_C, i)] =
                        prefRepo.getPref(getFormPathKey(getFormSubPath(FORM_C, i), villageId), "").toString()
                }
                val formDImageList = (mutableMapOf<String, String>())
                for (i in 0..4) {
                    formDImageList[getFormSubPath(FORM_D, i)] =
                        prefRepo.getPref(getFormPathKey(getFormSubPath(FORM_D, i), villageId), "").toString()
                }
                if (formCImageList.isNotEmpty()) {
                    formCImageList.onEachIndexed { index, it ->
                        if (it.value.isNotEmpty()) {
//                        val pageKey = getFormPathKey(File(it.value).nameWithoutExtension)
                            val compressedFormC =
                                compressImage(it.value, context, getFileNameFromURL(it.value))
                            val requestFormC = RequestBody.create(
                                "multipart/form-data".toMediaTypeOrNull(),
                                File(compressedFormC)
                            )
                            val formCFilePart = MultipartBody.Part.createFormData(
                                "formC",
                                File(compressedFormC).name,
                                requestFormC
                            )
//                              prefRepo.savePref(pageKey,File(compressedFormC).absolutePath)
                            formList.add(formCFilePart)
                        }

                    }
                }
                if (formDImageList.isNotEmpty()) {
                    formDImageList.onEachIndexed { index, it ->
                        if (it.value.isNotEmpty()) {
//                        val pageKey = getFormPathKey(File(it.value).nameWithoutExtension)
                            val compressedFormD =
                                compressImage(it.value, context, getFileNameFromURL(it.value))
                            val requestFormD = RequestBody.create(
                                "multipart/form-data".toMediaTypeOrNull(),
                                File(compressedFormD)
                            )
                            val formDFilePart = MultipartBody.Part.createFormData(
                                "formD",
                                File(compressedFormD).name,
                                requestFormD
                            )
//                                prefRepo.savePref(pageKey,File(compressedFormD).absolutePath)
                            formList.add(formDFilePart)
                        }

                    }
                }

                val requestVillageId =
                    RequestBody.create(
                        "multipart/form-data".toMediaTypeOrNull(),
                        prefRepo.getSelectedVillage().id.toString()
                    )
                val requestUserType =
                    RequestBody.create(
                        "multipart/form-data".toMediaTypeOrNull(),
                        if (prefRepo.isUserBPC()) USER_BPC else USER_CRP
                    )
                val response = apiService.uploadDocument(formList, requestVillageId, requestUserType)
                if(response.status == SUCCESS){
                    prefRepo.savePref(
                        PREF_NEED_TO_POST_FORM_C_AND_D_ + prefRepo.getSelectedVillage().id,false)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                onCatchError(ex, ApiType.DOCUMENT_UPLOAD_API)
            }
        }

    }

    private fun callCrpWorkFlowAPIForStep(prefRepo: PrefRepo, step: Int) {
        val stepList = stepsListDao.getAllStepsByOrder(step,true).sortedBy { it.orderNumber }
        NudgeLogger.e("SyncHelper","callWorkFlowAPIForStep called -> $stepList -> $step")
        callCrpWorkFlowAPI(prefRepo, stepList)
    }

    private fun callCrpWorkFlowAPI(prefRepo: PrefRepo, stepList: List<StepListEntity>) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                NudgeLogger.e("SyncHelper","callWorkFlowAPI called")
                val addWorkFlowRequest = mutableListOf<AddWorkFlowRequest>()
                val editWorkFlowRequest = mutableListOf<EditWorkFlowRequest>()
                val needToEditStep = mutableListOf<StepListEntity>()
                val needToAddStep = mutableListOf<StepListEntity>()
                for(step in stepList){
                    if (step.workFlowId > 0) {
                        editWorkFlowRequest.add((EditWorkFlowRequest(
                            step.workFlowId,
                            StepStatus.getStepFromOrdinal(step.isComplete),
                            villageId = step.villageId,
                            programsProcessId = step.id
                        )))
                        needToEditStep.add(step)
                    } else {
                        needToAddStep.add(step)
                        addWorkFlowRequest.add((AddWorkFlowRequest(
                            StepStatus.INPROGRESS.name, step.villageId,
                            step.programId, step.id
                        )))
                    }
                }
                if (addWorkFlowRequest.size > 0) {

                    NudgeLogger.e("SyncHelper", "callWorkFlowAPI addWorkFlowRequest: $addWorkFlowRequest \n\n")

                    val addWorkFlowResponse = apiService.addWorkFlow(Collections.unmodifiableList(addWorkFlowRequest))
                    NudgeLogger.d("VillageSelectionRepository","addWorkFlow Request=> ${Gson().toJson(Collections.unmodifiableList(addWorkFlowRequest))}")
                    NudgeLogger.e("SyncHelper","callWorkFlowAPI response: status: ${addWorkFlowResponse.status}, message: ${addWorkFlowResponse.message}, data: ${addWorkFlowResponse.data} \n\n")

                    if (addWorkFlowResponse.status.equals(SUCCESS, true)) {
                        addWorkFlowResponse.data?.let {
                            if (addWorkFlowResponse.data[0].transactionId.isNullOrEmpty()) {
                                for (i in addWorkFlowResponse.data.indices) {
                                    val step = needToAddStep[i]
                                    stepsListDao.updateOnlyWorkFlowId(
                                        it[i].id,
                                        step.villageId,
                                        step.id
                                    )
                                    step.workFlowId = it[0].id
                                    NudgeLogger.e(
                                        "SyncHelper",
                                        "callWorkFlowAPI stepsListDao.updateOnlyWorkFlowId before stepId: $step.stepId, it[0].id: ${it[0].id}, villageId: $step.villageId"
                                    )
                                }
                                NudgeLogger.e(
                                    "SyncHelper",
                                    "callWorkFlowAPI stepsListDao.updateOnlyWorkFlowId after"
                                )
                                delay(100)
                                needToAddStep.addAll(needToEditStep)
                                updateStepsToServer(prefRepo, needToAddStep)
                            }
                        }
                    }

                } else if(needToEditStep.size>0){
                    updateStepsToServer(prefRepo, needToEditStep)
                }

            }catch (ex:Exception){
                onCatchError(ex, ApiType.WORK_FLOW_API)
//                onError(tag = "ProgressScreenViewModel", "Error : ${ex.localizedMessage}")
            }
        }
    }

    private fun updateStepsToServer(prefRepo: PrefRepo, needToEdiStep: MutableList<StepListEntity>) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val requestForStepUpdation = mutableListOf<EditWorkFlowRequest>()
            for (step in needToEdiStep) {
                var stepCompletionDate = BLANK_STRING
                if(step.isComplete == StepStatus.COMPLETED.ordinal){
                    if(step.id == 40){
                        stepCompletionDate = longToString(prefRepo.getPref(
                            PREF_TRANSECT_WALK_COMPLETION_DATE_+prefRepo.getSelectedVillage().id,System.currentTimeMillis()))
                    }

                    if(step.id == 41){
                        stepCompletionDate = longToString(prefRepo.getPref(
                            PREF_SOCIAL_MAPPING_COMPLETION_DATE_+prefRepo.getSelectedVillage().id,System.currentTimeMillis()))
                    }

                    if(step.id == 46){
                        stepCompletionDate = longToString(prefRepo.getPref(
                            PREF_WEALTH_RANKING_COMPLETION_DATE_+prefRepo.getSelectedVillage().id,System.currentTimeMillis()))
                    }

                    if(step.id == 43){
                        stepCompletionDate = longToString(prefRepo.getPref(
                            PREF_PAT_COMPLETION_DATE_+prefRepo.getSelectedVillage().id,System.currentTimeMillis()))
                    }
                    if(step.id == 44){
                        stepCompletionDate = longToString(prefRepo.getPref(
                            PREF_VO_ENDORSEMENT_COMPLETION_DATE_+prefRepo.getSelectedVillage().id,System.currentTimeMillis()))
                    }
                }

                requestForStepUpdation.add(
                    EditWorkFlowRequest(
                        step.workFlowId,
                        StepStatus.getStepFromOrdinal(step.isComplete),
                        stepCompletionDate,
                        villageId = step.villageId,
                        programsProcessId = step.id
                    )
                )
            }

            val responseForStepUpdation =
                apiService.editWorkFlow(requestForStepUpdation)
            NudgeLogger.d("VillageSelectionRepository","updateStepsToServer editWorkFlow Request=> ${Gson().toJson(requestForStepUpdation)}")

            NudgeLogger.e(
                "SyncHelper",
                "callWorkFlowAPI response: status: ${responseForStepUpdation.status}, message: ${responseForStepUpdation.message}, data: ${responseForStepUpdation.data} \n\n"
            )


            if (responseForStepUpdation.status.equals(SUCCESS, true)) {
                responseForStepUpdation.data?.let {

                    for(i in responseForStepUpdation.data.indices) {
                        val step = needToEdiStep[i]
                        stepsListDao.updateWorkflowId(
                            step.stepId,
                            step.workFlowId,
                            step.villageId,
                            step.status
                        )

                        NudgeLogger.e(
                            "SyncHelper",
                            "callWorkFlowAPI stepsListDao.updateWorkflowId after "
                        )
                        NudgeLogger.e(
                            "SyncHelper",
                            "callWorkFlowAPI stepsListDao.updateNeedToPost before stepId: $step.stepId"
                        )
                        stepsListDao.updateNeedToPost(step.id, step.villageId, false)
                        NudgeLogger.e(
                            "SyncHelper",
                            "callWorkFlowAPI stepsListDao.updateNeedToPost after stepId: $step.stepId"
                        )

                    }
                }
            }
            if (!responseForStepUpdation.lastSyncTime.isNullOrEmpty()) {
                updateLastSyncTime(
                    prefRepo,
                    responseForStepUpdation.lastSyncTime
                )
            }
        }
    }

    private fun updateTolaNeedTOPostListForCrp(prefRepo: PrefRepo, tolaList: List<TolaEntity>, networkCallbackListener: NetworkCallbackListener) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            Log.e("tola updated","$tolaList.size")
            for(tola in tolaList) {
                tolaDao.updateTolaDetailAfterSync(
                    id = tola.id,
                    serverId = tola.serverId,
                    needsToPost = false,
                    transactionId = "",
                    createdDate = tola.createdDate?:0L,
                    modifiedDate = tola.modifiedDate?:0L
                )
            }
            deleteTolaToNetworkForCrp(prefRepo = prefRepo, networkCallbackListener)
        }
    }

    private fun fetchQuestions(prefRepo: PrefRepo, isRefresh: Boolean) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val localLanguageList = languageListDao.getAllLanguages()
            localLanguageList?.let {
                val stateId = villageListDao.getStateId()
                localLanguageList.forEach { languageEntity ->
                    try {
                        // Fetch QuestionList from Server
                        val localLanguageQuesList =
                            questionDao.getAllQuestionsForLanguage(languageEntity.id)
                        if (localLanguageQuesList.isEmpty() || isRefresh) {
                            NudgeLogger.d("TAG", "fetchQuestions: QuestionList")
                            val quesListResponse = apiService.fetchQuestionListFromServer(
                                GetQuestionListRequest(
                                    languageId = languageEntity.id,
                                    stateId = stateId,
                                    surveyName = if (prefRepo.isUserBPC()) BPC_SURVEY_CONSTANT else PAT_SURVEY_CONSTANT
                                )
                            )
                            if (quesListResponse.status.equals(SUCCESS, true)) {

                                quesListResponse.data?.let { questionList ->
                                    if (isRefresh) {
                                        questionDao.deleteQuestionTableForLanguage(languageId = languageEntity.id)
                                    }
                                    questionList.listOfQuestionSectionList?.forEach { list ->
                                        list?.questionList?.forEach { question ->
                                            question?.sectionOrderNumber = list.orderNumber
                                            question?.actionType = list.actionType
                                            question?.languageId = languageEntity.id
                                            question?.surveyId = questionList.surveyId
                                            question?.thresholdScore =
                                                questionList.thresholdScore
                                            question?.surveyPassingMark =
                                                questionList.surveyPassingMark
                                            NudgeLogger.d("TAG", "fetchQuestionsList: ${question?.options.toString()}")
                                            if(question?.questionFlag.equals(QUESTION_FLAG_WEIGHT) || question?.questionFlag.equals(
                                                    QUESTION_FLAG_RATIO
                                                )) {
                                                val heading = question?.options?.filter {
                                                    it.optionType.equals(
                                                        HEADING_QUESTION_TYPE, true
                                                    )
                                                }?.get(0)?.display
                                                question?.headingProductAssetValue = heading
                                            }
                                        }
                                        list?.questionList?.let {
                                            questionDao.insertAll(it as List<QuestionEntity>)
                                        }
                                    }
                                }
                            } else {
                                val ex = ApiResponseFailException(quesListResponse.message)
                                if (!RetryHelper.retryApiList.contains(if (prefRepo.isUserBPC()) ApiType.PAT_BPC_QUESTION_API else ApiType.PAT_CRP_QUESTION_API)) RetryHelper.retryApiList.add(
                                    if (prefRepo.isUserBPC()) ApiType.PAT_BPC_QUESTION_API else ApiType.PAT_CRP_QUESTION_API
                                )
                                RetryHelper.crpPatQuestionApiLanguageId.add(languageEntity.id)
                                onCatchError(
                                    ex,
                                    if (prefRepo.isUserBPC()) ApiType.PAT_BPC_QUESTION_API else ApiType.PAT_CRP_QUESTION_API
                                )
                            }

                        }
                    } catch (ex: Exception) {
                        if (ex !is JsonSyntaxException) {
                            if (!RetryHelper.retryApiList.contains(if (prefRepo.isUserBPC()) ApiType.PAT_BPC_QUESTION_API else ApiType.PAT_CRP_QUESTION_API)) RetryHelper.retryApiList.add(
                                if (prefRepo.isUserBPC()) ApiType.PAT_BPC_QUESTION_API else ApiType.PAT_CRP_QUESTION_API
                            )
                            RetryHelper.crpPatQuestionApiLanguageId.add(languageEntity.id)
                        }
                        onCatchError(
                            ex,
                            if (prefRepo.isUserBPC()) ApiType.PAT_BPC_QUESTION_API else ApiType.PAT_CRP_QUESTION_API
                        )
                    } finally {
                        /*withContext(Dispatchers.Main) {
                            delay(250)
                            showLoader.value = false
                        }*/
                    }
                }
            }
        }
    }

    private fun fetchPatSurveyFromServerForBpc(villageId: Int, prefRepo: PrefRepo) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

        }
    }

    private suspend fun FetchTolaForBpc(villageId: Int) {
        try {
            NudgeLogger.d("VillageSelectionRepository", "refreshBpcData getCohortFromNetwork " +
                    "request village.id = ${villageId}")
            val cohortResponse =
                apiService.getCohortFromNetwork(villageId = villageId)
            NudgeLogger.d("VillageSelectionRepository", "refreshBpcData getCohortFromNetwork " +
                    "cohortResponse status = ${cohortResponse.status}, message = ${cohortResponse.message}, data = ${cohortResponse.data.toString()}")
            if (cohortResponse.status.equals(SUCCESS, true)) {
                cohortResponse.data?.let { remoteTolaList ->
                    NudgeLogger.d("VillageSelectionRepository", "refreshBpcData getCohortFromNetwork " +
                            "tolaDao.insertAll(it) before")
                    if (remoteTolaList.isNotEmpty()) {
                        for (tola in remoteTolaList) {
                            tola.serverId = tola.id
                        }
                    }

                    tolaDao.deleteTolaForVillage(villageId)
                    delay(100)
                    tolaDao.insertAll(remoteTolaList)
                    NudgeLogger.d("VillageSelectionRepository", "refreshBpcData getCohortFromNetwork " +
                            "tolaDao.insertAll(it) after")
                }
            } else {
                val ex = ApiResponseFailException(cohortResponse.message)
                if (!RetryHelper.retryApiList.contains(ApiType.TOLA_LIST_API)) RetryHelper.retryApiList.add(
                    ApiType.TOLA_LIST_API
                )
                RetryHelper.stepListApiVillageId.add(villageId)
                onCatchError(ex, ApiType.TOLA_LIST_API)
            }
        }
        catch (ex: Exception) {
            if (ex !is JsonSyntaxException) {
                if (!RetryHelper.retryApiList.contains(ApiType.TOLA_LIST_API)) RetryHelper.retryApiList.add(
                    ApiType.TOLA_LIST_API
                )
                RetryHelper.stepListApiVillageId.add(villageId)
            }
            onCatchError(ex, ApiType.TOLA_LIST_API)
        }
    }

    private fun syncAndFetchDidiForBpc(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener) {
        savePATSummeryToServerForBpc(prefRepo = prefRepo, networkCallbackListener =  networkCallbackListener)
    }

    private fun savePATSummeryToServerForBpc(networkCallbackListener: NetworkCallbackListener, prefRepo: PrefRepo){
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
//                val villageList = villageListDao.getAllVillages(prefRepo.getAppLanguageId()?:2)
//                for(village in villageList) {

                    var optionList: List<OptionsItem>
                    val answeredDidiList: java.util.ArrayList<PATSummarySaveRequest> = arrayListOf()
                    val scoreDidiList: java.util.ArrayList<EditDidiWealthRankingRequest> = arrayListOf()
                    var surveyId =0

                    val didiIDList= answerDao.fetchPATSurveyDidiList()
                    if(didiIDList.isNotEmpty()){
                        didiIDList.forEach { didi->
                            val didiEntity = didiDao.getDidi(didi.id)
                            NudgeLogger.d("VillageSelectionRepository", "savePATSummeryToServer Save: ${didi.id} :: ${didi.patSurveyStatus}")
                            val qList: java.util.ArrayList<AnswerDetailDTOListItem> = arrayListOf()
                            calculateDidiScore(didiId = didi.id, prefRepo)
                            delay(100)
                            didi.score = didiDao.getDidiScoreFromDb(didi.id)
                            val needToPostQuestionsList=answerDao.getAllNeedToPostQuesForDidi(didi.id)
                            if(needToPostQuestionsList.isNotEmpty()){
                                needToPostQuestionsList.forEach {
                                    surveyId= questionDao.getQuestion(it.questionId).surveyId?:0
                                    if(!it.type.equals(QuestionType.Numeric_Field.name,true)){
                                        optionList= listOf(
                                            OptionsItem(optionId = it.optionId,
                                                optionValue = it.optionValue,
                                                count = 0,
                                                summary = it.summary,
                                                display = it.answerValue,
                                                weight = it.weight,
                                                isSelected = false
                                            )
                                        )
                                    } else {
                                        val numOptionList = numericAnswerDao.getSingleQueOptions(
                                            it.questionId,
                                            it.didiId
                                        )
                                        val tList: java.util.ArrayList<OptionsItem> = arrayListOf()
                                        if (numOptionList.isNotEmpty()) {
                                            numOptionList.forEach { numOption ->
                                                tList.add(
                                                    OptionsItem(
                                                        optionId = numOption.optionId,
                                                        optionValue = numOption.optionValue,
                                                        count = numOption.count,
                                                        summary = it.summary,
                                                        display = it.answerValue,
                                                        weight = numOption.weight,
                                                        isSelected = false
                                                    )
                                                )
                                            }
                                            optionList = tList
                                        }else{
                                            tList.add(
                                                OptionsItem(
                                                    optionId = it.optionId,
                                                    optionValue = 0,
                                                    count = 0,
                                                    summary = it.summary,
                                                    display = it.answerValue,
                                                    weight = it.weight,
                                                    isSelected = false
                                                )
                                            )

                                            optionList = tList
                                        }
                                    }
                                    try {
                                        qList.add(
                                            AnswerDetailDTOListItem(
                                                questionId = it.questionId,
                                                section = it.actionType,
                                                options = optionList,
                                                assetAmount = it.assetAmount
                                            )
                                        )
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                            val passingMark = questionDao.getPassingScore()
                            var comment = BLANK_STRING
                            comment =
                                if (didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) {
                                    PatSurveyStatus.NOT_AVAILABLE.name
                                } else if (didi.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal) {
                                    BLANK_STRING
                                } else {
                                    if ((didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal && didi.section2Status == PatSurveyStatus.NOT_STARTED.ordinal)
                                        || (didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal && didi.patExclusionStatus != ExclusionType.NO_EXCLUSION.ordinal)) {
                                        TYPE_EXCLUSION
                                    } else {
                                        if (didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal && didi.section2Status == PatSurveyStatus.COMPLETED.ordinal && didi.score < passingMark) {
                                            LOW_SCORE
                                        } else {
                                            BLANK_STRING
                                        }
                                    }
                                }
                            scoreDidiList.add(
                                EditDidiWealthRankingRequest(
                                    id = didi.serverId,
                                    score = didi.score,
                                    comment = comment,
                                    type = if (prefRepo.isUserBPC()) BPC_SURVEY_CONSTANT else PAT_SURVEY,
                                    result = if (didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) {
                                        DIDI_NOT_AVAILABLE
                                    } else if (didi.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal) {
                                        PatSurveyStatus.INPROGRESS.name
                                    } else {
                                        if (didi.forVoEndorsement == 0 || didi.patExclusionStatus != ExclusionType.NO_EXCLUSION.ordinal) DIDI_REJECTED else {
                                            if (prefRepo.isUserBPC())
                                                VERIFIED_STRING
                                            else
                                                COMPLETED_STRING
                                        }
                                    },
                                    rankingEdit = false,
                                    shgFlag = SHGFlag.fromInt(didi.shgFlag).name,
                                    ableBodiedFlag = AbleBodiedFlag.fromInt(didi.ableBodiedFlag).name,
                                    name = didi.name,
                                    address = didiEntity.address,
                                    guardianName = didiEntity.guardianName,
                                    villageId = didi.villageId,
                                    deviceId = didiEntity.localUniqueId
                                )
                            )
                            val patSummarySaveRequest = PATSummarySaveRequest(
                                villageId = didi.villageId,
                                surveyId = surveyId,
                                cohortName = didiEntity.cohortName,
                                beneficiaryAddress = didiEntity.address,
                                guardianName = didiEntity.guardianName,
                                beneficiaryId = if (didi.serverId == 0) didi.id else didi.serverId,
                                languageId = prefRepo.getAppLanguageId() ?: 2,
                                stateId = prefRepo.getSelectedVillage().stateId,
                                totalScore = didi.score,
                                userType = if (prefRepo.isUserBPC()) USER_BPC else USER_CRP,
                                beneficiaryName = didi.name,
                                answerDetailDTOList = qList,
                                patSurveyStatus = didi.patSurveyStatus,
                                section2Status = didi.section2Status,
                                section1Status = didi.section1Status,
                                shgFlag = didi.shgFlag,
                                patExclusionStatus = didi.patExclusionStatus ?: 0
                            )
                            NudgeLogger.d("VillageSelectionRepository", "savePATSummeryToServer patSummarySaveRequest: $patSummarySaveRequest")
                            answeredDidiList.add(
                                patSummarySaveRequest
                            )
                        }
                        if(answeredDidiList.isNotEmpty()){
                            withContext(Dispatchers.IO){
                                NudgeLogger.d("VillageSelectionRepository", "savePATSummeryToServer answeredDidiList: ${answeredDidiList.json()}")
                                val saveAPIResponse= apiService.savePATSurveyToServer(answeredDidiList)

                                if(saveAPIResponse.status.equals(SUCCESS,true)){
                                    if(saveAPIResponse.data?.get(0)?.transactionId.isNullOrEmpty()) {
                                        didiIDList.forEach { didiItem ->
                                            didiDao.updateNeedToPostPAT(
                                                false,
                                                didiItem.id,
                                                didiItem.villageId
                                            )
                                        }
                                        NudgeLogger.d("VillageSelectionRepository", "savePATSummeryToServer -> saveAPIResponse.data?.get(0)?.transactionId.isNullOrEmpty()")
                                        updateBpcPatStatusToNetwork(networkCallbackListener, prefRepo)
                                    } else {
                                        for(i in didiIDList.indices) {
                                            saveAPIResponse.data?.get(i)?.let {
                                                didiDao.updateDidiTransactionId(
                                                    didiIDList[i].id,
                                                    it.transactionId
                                                )
                                            }
                                        }
                                        NudgeLogger.d("VillageSelectionRepository", "savePATSummeryToServer -> !saveAPIResponse.data?.get(0)?.transactionId.isNullOrEmpty()")

                                        isPendingForBpc = 1
                                        startSyncTimerForBpc(prefRepo, networkCallbackListener)
                                    }
                                    // checkDidiPatStatus()
                                } else {

                                    withContext(Dispatchers.Main) {
                                        networkCallbackListener.onFailed()
                                    }
                                }
                                if(!saveAPIResponse.lastSyncTime.isNullOrEmpty()){
                                    updateLastSyncTime(prefRepo,saveAPIResponse.lastSyncTime)
                                }
                                NudgeLogger.d("VillageSelectionRepository", "savePATSummeryToServer syncAndFetchDidiForBpc scoreDidiList: ${scoreDidiList.json()}")
                                apiService.updateDidiScore(scoreDidiList)
                            }
                        }
                    }
//                }
                checkPendingPatStatusForBpc(prefRepo, networkCallbackListener)
            } catch (ex: Exception) {
                ex.printStackTrace()
                onCatchError(ex, ApiType.BPC_PAT_SAVE_ANSWER_SUMMARY)
            }
        }

    }

    private fun calculateDidiScore(didiId: Int, prefRepo: PrefRepo) {
        NudgeLogger.d("VillageSelectionRepository", "calculateDidiScore didiId: ${didiId}")
        var passingMark = 0
        var isDidiAccepted = false
        var comment = LOW_SCORE
        val _inclusiveQueList = answerDao.getAllInclusiveQues(didiId = didiId)
        if (_inclusiveQueList.isNotEmpty()) {
            var totalWightWithoutNumQue = answerDao.getTotalWeightWithoutNumQues(didiId)
            NudgeLogger.d(
                "VillageSelectionRepository",
                "calculateDidiScore: $totalWightWithoutNumQue"
            )
            val numQueList =
                _inclusiveQueList.filter { it.type == QuestionType.Numeric_Field.name }
            if (numQueList.isNotEmpty()) {
                numQueList.forEach { answer ->
                    val numQue = questionDao.getQuestion(answer.questionId)
                    passingMark = numQue.surveyPassingMark ?: 0
                    if (numQue.questionFlag?.equals(FLAG_WEIGHT, true) == true) {
                        val weightList = toWeightageRatio(numQue.json.toString())
                        if (weightList.isNotEmpty()) {
                            val newScore = calculateScore(
                                weightList,
                                answer.totalAssetAmount?.toDouble() ?: 0.0,
                                false
                            )
                            totalWightWithoutNumQue += newScore
                            NudgeLogger.d(
                                "VillageSelectionRepository",
                                "calculateDidiScore: totalWightWithoutNumQue += newScore -> $totalWightWithoutNumQue"
                            )
                        }
                    } else if (numQue.questionFlag?.equals(FLAG_RATIO, true) == true) {
                        val ratioList = toWeightageRatio(numQue.json.toString())
                        val newScore = calculateScore(
                            ratioList,
                            answer.totalAssetAmount?.toDouble() ?: 0.0,
                            true
                        )
                        totalWightWithoutNumQue += newScore
                        NudgeLogger.d(
                            "VillageSelectionRepository",
                            "calculateDidiScore: for Flag FLAG_RATIO totalWightWithoutNumQue += newScore -> $totalWightWithoutNumQue"
                        )
                    }
                }
            }
            // TotalScore
            if (!prefRepo.isUserBPC()) {
                if (totalWightWithoutNumQue >= passingMark) {
                    isDidiAccepted = true
                    comment = BLANK_STRING
                    didiDao.updateVOEndorsementDidiStatus(
                        prefRepo.getSelectedVillage().id,
                        didiId,
                        1
                    )
                } else {
                    isDidiAccepted = false
                    didiDao.updateVOEndorsementDidiStatus(
                        prefRepo.getSelectedVillage().id,
                        didiId,
                        0
                    )
                }
            }
            NudgeLogger.d("VillageSelectionRepository", "calculateDidiScore totalWightWithoutNumQue: $totalWightWithoutNumQue")
            didiDao.updateDidiScore(
                score = totalWightWithoutNumQue,
                comment = comment,
                didiId = didiId,
                isDidiAccepted = isDidiAccepted
            )
        } else {
            didiDao.updateDidiScore(
                score = 0.0,
                comment = TYPE_EXCLUSION,
                didiId = didiId,
                isDidiAccepted = false
            )
        }
    }

    private suspend fun updateBpcPatStatusToNetwork(networkCallbackListener: NetworkCallbackListener, prefRepo: PrefRepo) {
            val needToPostPatDidi =
                didiDao.getAllNeedToPostBPCProcessDidi(true)
            val passingScore = questionDao.getPassingScore()
            if (!needToPostPatDidi.isNullOrEmpty()) {
                val didiRequestList : java.util.ArrayList<EditDidiWealthRankingRequest> = arrayListOf()
                needToPostPatDidi.forEach { didi ->
                    var comment= BLANK_STRING
                    if(didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal ||  didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal)
                        comment= BLANK_STRING
                    else {
                        comment =if((didi.score ?: 0.0) < passingScore) LOW_SCORE else {
                            if(didi.patSurveyStatus==PatSurveyStatus.COMPLETED.ordinal && didi.section2Status==PatSurveyStatus.NOT_STARTED.ordinal){
                                TYPE_EXCLUSION
                            }else BLANK_STRING}
                    }
                    didiRequestList.add(
                        EditDidiWealthRankingRequest(
                            id = didi.serverId,
                            score = didi.score,
                            comment =comment,
                            type = BPC_SURVEY_CONSTANT,
                            address = didi.address,
                            guardianName = didi.guardianName,
                            villageId = didi.villageId,
                            deviceId = didi.localUniqueId,
                            result = if(didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal ||  didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) DIDI_NOT_AVAILABLE
                            else {
                                if (didi.forVoEndorsement == 0) DIDI_REJECTED else {
                                    if (prefRepo.isUserBPC())
                                        VERIFIED_STRING
                                    else
                                        COMPLETED_STRING
                                }
                            }
                        )
                    )
                    try {
                        val updatedPatResponse = apiService.updateDidiRanking(didiRequestList)
                        NudgeLogger.d("VillageSelectionRepository","updateBpcPatStatusToNetwork Request=> ${Gson().toJson(didiRequestList)}")
                        if (updatedPatResponse.status.equals(SUCCESS, true)) {
                            if (updatedPatResponse.data?.isNotEmpty() == true) {
                                if (updatedPatResponse.data?.get(0)?.transactionId.isNullOrEmpty()) {
                                    didiDao.updateNeedsToPostBPCProcessStatus(
                                        needsToPostBPCProcessStatus = false,
                                        didiId = didi.id
                                    )
                                    callWorkFlowAPIForBpc(networkCallbackListener, prefRepo)
                                } else {
                                    for (i in needToPostPatDidi.indices) {
                                        updatedPatResponse.data?.get(i)?.let {
                                            it.transactionId?.let { it1 ->
                                                didiDao.updateDidiTransactionId(
                                                    needToPostPatDidi[i].id,
                                                    it1
                                                )
                                            }
                                        }
                                    }
                                    isPendingForBpc = 2
                                    startSyncTimerForBpc(prefRepo = prefRepo, networkCallbackListener)
                                }
                            } else {
                                didiDao.updateNeedsToPostBPCProcessStatus(
                                    needsToPostBPCProcessStatus = false,
                                    didiId = didi.id
                                )
                                callWorkFlowAPIForBpc(networkCallbackListener, prefRepo)
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                networkCallbackListener.onFailed()
                            }
                        }
                        if(!updatedPatResponse.lastSyncTime.isNullOrEmpty()){
                            updateLastSyncTime(prefRepo,updatedPatResponse.lastSyncTime)
                        }
                    } catch (ex: Exception) {
                        onCatchError(ex, ApiType.DIDI_EDIT_API)
                    }
                }
            } else {
                callWorkFlowAPIForBpc(networkCallbackListener, prefRepo)
            }

    }

    private fun checkPendingPatStatusForBpc(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiIDList= didiDao.fetchPendingPatStatusDidi(true,"")
            if(didiIDList.isNotEmpty()) {
                val ids: java.util.ArrayList<String> = arrayListOf()
                didiIDList.forEach { didi ->
                    didi.transactionId?.let { ids.add(it) }
                }
                NudgeLogger.d("VillageSelectionRepository", "checkPendingPatStatus -> TransactionIdRequest = $ids")
                val response = apiService.getPendingStatus(TransactionIdRequest("",ids))
                if (response.status.equals(SUCCESS, true)) {
                    NudgeLogger.d("VillageSelectionRepository", "checkPendingPatStatus -> SUCCESS")
                    response.data?.forEach { transactionIdResponse ->
                        didiIDList.forEach { didiEntity ->
                            if (transactionIdResponse.transactionId == didiEntity.transactionId) {
                                didiDao.updateNeedToPostPAT(false,didiEntity.serverId)
                            }
                        }
                    }
                    updateBpcPatStatusToNetwork(prefRepo = prefRepo, networkCallbackListener = networkCallbackListener)
                } else {
                    NudgeLogger.d("VillageSelectionRepository", "checkPendingPatStatus -> FAIL")
                    withContext(Dispatchers.Main) {
                        networkCallbackListener.onFailed()
                    }
                }
                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }

            } else {
                NudgeLogger.d("VillageSelectionRepository", "checkPendingPatStatus -> didiIDList is empty")
                updateBpcPatStatusToNetwork(prefRepo = prefRepo, networkCallbackListener = networkCallbackListener)
            }
        }
    }

    private fun checkUpdateBpcPatStatus(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiIDList= didiDao.getAllPendingNeedToPostBPCProcessDidi(true,"")
            if(didiIDList.isNotEmpty()) {
                val ids: java.util.ArrayList<String> = arrayListOf()
                didiIDList.forEach { didi ->
                    didi.transactionId?.let { ids.add(it) }
                }
                val response = apiService.getPendingStatus(TransactionIdRequest("",ids))
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.forEach { transactionIdResponse ->
                        didiIDList.forEach { didiEntity ->
                            if (transactionIdResponse.transactionId == didiEntity.transactionId) {
                                didiDao.updateNeedToPostPAT(false,didiEntity.serverId)
                            }
                        }
                    }
                    updateBpcPatStatusToNetwork(prefRepo = prefRepo, networkCallbackListener = networkCallbackListener)
                } else {
                    withContext(Dispatchers.Main) {
                        networkCallbackListener.onFailed()
                    }
                }
                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }

            } else {
                updateBpcPatStatusToNetwork(prefRepo = prefRepo, networkCallbackListener = networkCallbackListener)
            }
        }
    }

    fun callWorkFlowAPIForBpc( networkCallbackListener: NetworkCallbackListener, prefRepo: PrefRepo) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val villageList = villageListDao.getAllVillages(prefRepo.getAppLanguageId()?:2)
                val addWorkFlowRequest = mutableListOf<AddWorkFlowRequest>()
                val editWorkFlowRequest = mutableListOf<EditWorkFlowRequest>()
                val needToEditStep = mutableListOf<StepListEntity>()
                val needToAddStep = mutableListOf<StepListEntity>()
                for(village in villageList) {
                    val villageId = village.id
                    val stepList =
                        stepsListDao.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }
                    val bpcStep = stepList.last()
                    if (bpcStep.workFlowId > 0) {
                        editWorkFlowRequest.add((EditWorkFlowRequest(
                            bpcStep.workFlowId,
                            StepStatus.getStepFromOrdinal(bpcStep.isComplete),
                            villageId = bpcStep.villageId,
                            programsProcessId = bpcStep.id
                        )))
                        needToEditStep.add(bpcStep)
                    } else {
                        needToAddStep.add(bpcStep)
                        addWorkFlowRequest.add((AddWorkFlowRequest(
                            StepStatus.INPROGRESS.name, bpcStep.villageId,
                            bpcStep.programId, bpcStep.id
                        )))
                    }
                }
                if (addWorkFlowRequest.size > 0) {

                    NudgeLogger.e("SyncHelper", "callWorkFlowAPI addWorkFlowRequest: $addWorkFlowRequest \n\n")

                    val addWorkFlowResponse = apiService.addWorkFlow(Collections.unmodifiableList(addWorkFlowRequest))
                    NudgeLogger.d("VillageSelectionRepository","addWorkFlow Request=> ${Gson().toJson(Collections.unmodifiableList(addWorkFlowRequest))}")
                    NudgeLogger.e("SyncHelper","callWorkFlowAPI response: status: ${addWorkFlowResponse.status}, message: ${addWorkFlowResponse.message}, data: ${addWorkFlowResponse.data} \n\n")

                    if (addWorkFlowResponse.status.equals(SUCCESS, true)) {
                        addWorkFlowResponse.data?.let {
                            if (addWorkFlowResponse.data[0].transactionId.isNullOrEmpty()) {
                                for (i in addWorkFlowResponse.data.indices) {
                                    val step = needToAddStep[i]
                                    stepsListDao.updateOnlyWorkFlowId(
                                        it[i].id,
                                        step.villageId,
                                        step.id
                                    )
                                    step.workFlowId = it[0].id
                                    NudgeLogger.e(
                                        "SyncHelper",
                                        "callWorkFlowAPI stepsListDao.updateOnlyWorkFlowId before stepId: $step.stepId, it[0].id: ${it[0].id}, villageId: $step.villageId"
                                    )
                                }
                                NudgeLogger.e(
                                    "SyncHelper",
                                    "callWorkFlowAPI stepsListDao.updateOnlyWorkFlowId after"
                                )
                                delay(100)
                                needToAddStep.addAll(needToEditStep)
                                updateBpcStepsToServer(needToAddStep, networkCallbackListener, prefRepo)
                            }
                        }
                    } else {
                        networkCallbackListener.onFailed()
                    }

                } else if(needToEditStep.size>0){
                    updateBpcStepsToServer(needToEditStep, networkCallbackListener, prefRepo)
                }

            }catch (ex:Exception){
                withContext(Dispatchers.Main) {
                    networkCallbackListener.onFailed()
                }
                onCatchError(ex, ApiType.WORK_FLOW_API)
            }
        }
    }

    private fun updateBpcStepsToServer(needToEditStep: MutableList<StepListEntity>, networkCallbackListener: NetworkCallbackListener, prefRepo: PrefRepo) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            if (needToEditStep.isNotEmpty()) {
                val requestForStepUpdation = mutableListOf<EditWorkFlowRequest>()
                for (step in needToEditStep) {
                    var stepCompletionDate = BLANK_STRING
                    stepCompletionDate =longToString(prefRepo.getPref(
                        PREF_BPC_PAT_COMPLETION_DATE_+step.villageId, System.currentTimeMillis()))
                    requestForStepUpdation.add(
                        EditWorkFlowRequest(
                            step.workFlowId,
                            StepStatus.getStepFromOrdinal(step.isComplete),
                            stepCompletionDate,
                            villageId = step.villageId,
                            programsProcessId = step.id
                        )
                    )
                }
                val responseForStepUpdation =
                    apiService.editWorkFlow(requestForStepUpdation)
                NudgeLogger.d("VillageSelectionRepository","updateBpcStepsToServer editWorkFlow Request=> ${Gson().toJson(requestForStepUpdation)}")
                NudgeLogger.e(
                    "SyncHelper",
                    "callWorkFlowAPI response: status: ${responseForStepUpdation.status}, message: ${responseForStepUpdation.message}, data: ${responseForStepUpdation.data} \n\n"
                )


                if (responseForStepUpdation.status.equals(SUCCESS, true)) {
                    responseForStepUpdation.data?.let {

                        for(i in responseForStepUpdation.data.indices) {
                            val step = needToEditStep[i]
                            stepsListDao.updateWorkflowId(
                                step.stepId,
                                step.workFlowId,
                                step.villageId,
                                step.status
                            )

                            NudgeLogger.e(
                                "SyncHelper",
                                "callWorkFlowAPI stepsListDao.updateWorkflowId after "
                            )
                            NudgeLogger.e(
                                "SyncHelper",
                                "callWorkFlowAPI stepsListDao.updateNeedToPost before stepId: $step.stepId"
                            )
                            stepsListDao.updateNeedToPost(step.id, step.villageId, false)
                            NudgeLogger.e(
                                "SyncHelper",
                                "callWorkFlowAPI stepsListDao.updateNeedToPost after stepId: $step.stepId"
                            )

                        }
                    }
                    sendBpcMatchScore(networkCallbackListener, prefRepo)
                } else {
                    networkCallbackListener.onFailed()
                }
                if (!responseForStepUpdation.lastSyncTime.isNullOrEmpty()) {
                    updateLastSyncTime(
                        prefRepo,
                        responseForStepUpdation.lastSyncTime
                    )
                }
            } else {
                sendBpcMatchScore(networkCallbackListener, prefRepo)
            }
        }
    }

    private fun isBPCScoreSaved(prefRepo: PrefRepo) : Boolean{
        val villageList = villageListDao.getAllVillages(prefRepo.getAppLanguageId()?:0)
        for(village in villageList) {
            val isBpcScoreSaved = prefRepo.getPref(
                PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_ + village.id,
                false

            )
            if(isBpcScoreSaved)
                return isBpcScoreSaved
            NudgeLogger.d("SettingViewModel", "isBPCScoreSaved -> $isBpcScoreSaved")
        }
        return false
    }

    private fun sendBpcMatchScore(networkCallbackListener: NetworkCallbackListener, prefRepo: PrefRepo) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            if (!isBPCScoreSaved(prefRepo = prefRepo)) {
                val villageList = villageListDao.getAllVillages(prefRepo.getAppLanguageId() ?: 2)
                for (village in villageList) {
                    val didiList = didiDao.getAllDidisForVillage(prefRepo.getSelectedVillage().id)
                    try {
                        val villageId = prefRepo.getSelectedVillage().id
                        val passingScore = questionDao.getPassingScore()
                        val bpcStep =
                            stepsListDao.getAllStepsForVillage(villageId)
                                .sortedBy { it.orderNumber }
                                .last()
                        val matchPercentage = calculateMatchPercentage(
                            didiList.filter { it.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal },
                            passingScore
                        )
                        val saveMatchSummaryRequest = SaveMatchSummaryRequest(
                            programId = bpcStep.programId,
                            score = matchPercentage,
                            villageId = villageId,
                            didiNotAvailableCountBPC = didiList.filter {
                                it.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal
                                        || it.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal
                            }.size
                        )
                        val requestList = arrayListOf(saveMatchSummaryRequest)
                        NudgeLogger.d("VillageSelectionRepository","sendBpcMatchScore saveMatchSummary Request=> ${requestList.json()}")
                        val saveMatchSummaryResponse = apiService.saveMatchSummary(requestList)
                        if (saveMatchSummaryResponse.status.equals(SUCCESS, true)) {
                            withContext(Dispatchers.Main) {
                                networkCallbackListener.onSuccess()
                                prefRepo.savePref(
                                    PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_ + prefRepo.getSelectedVillage().id,
                                    true
                                )
                            }
                        } else {
                            prefRepo.savePref(
                                PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_ + prefRepo.getSelectedVillage().id,
                                false
                            )
                            withContext(Dispatchers.Main) {
                                networkCallbackListener.onFailed()
                            }
                        }
                        if (!saveMatchSummaryResponse.lastSyncTime.isNullOrEmpty()) {
                            updateLastSyncTime(prefRepo, saveMatchSummaryResponse.lastSyncTime)
                        }
                    } catch (ex: Exception) {
                        prefRepo.savePref(
                            PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_ + prefRepo.getSelectedVillage().id,
                            false
                        )
                        onCatchError(ex, ApiType.BPC_SAVE_MATCH_PERCENTAGE_API)
                        withContext(Dispatchers.Main) {
                            networkCallbackListener.onFailed()
                        }
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    networkCallbackListener.onSuccess()
                }
            }
        }
    }

    fun calculateMatchPercentage(didiList: List<DidiEntity>, questionPassingScore: Int): Int {
        val matchedCount = didiList.filter {
            (it.score ?: 0.0) >= questionPassingScore.toDouble()
                    && (it.crpScore ?: 0.0) >= questionPassingScore.toDouble() }.size

        return if (didiList.isNotEmpty() && matchedCount != 0) ((matchedCount.toFloat()/didiList.size.toFloat()) * 100).toInt() else 0

    }

    private fun startSyncTimerForBpc(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener){
        val timer = Timer()
        timer.schedule(object : TimerTask(){
            override fun run() {
                when (isPendingForBpc) {
                    1 -> {
                        checkPendingPatStatusForBpc(prefRepo, networkCallbackListener)
                    }
                    2 -> {
                        checkUpdateBpcPatStatus(prefRepo, networkCallbackListener)
                    }
                }
            }
        },pendingTimerTime)
    }

    private fun fetchDidiForBpc(villageId: Int, prefRepo: PrefRepo) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                NudgeLogger.d("VillageSelectionRepository", "fetchDataForBpc getDidiForBpcFromNetwork " +
                        "request village.id = ${villageId}")
                val didiResponse =
                    apiService.getDidiForBpcFromNetwork(villageId = villageId)
                NudgeLogger.d("VillageSelectionRepository", "fetchDataForBpc getDidiForBpcFromNetwork " +
                        "didiResponse status = ${didiResponse.status}, message = ${didiResponse.message}, data = ${didiResponse.data.toString()}")
                if (didiResponse.status.equals(SUCCESS, true)) {
                    didiResponse.data?.let { didiList ->
                        if (didiList.isNotEmpty()) {
                            try {
                                didiDao.deleteDidiForVillage(villageId)
                                delay(100)
                                didiList.forEach { didi ->
                                    var tolaName = BLANK_STRING
                                    var casteName = BLANK_STRING
                                    val singleTola =
                                        tolaDao.fetchSingleTola(didi.cohortId)
                                    val singleCaste =
                                        casteListDao.getCaste(didi.castId, prefRepo?.getAppLanguageId()?:2)
                                    singleTola?.let {
                                        tolaName = it.name
                                    }
                                    singleCaste?.let {
                                        casteName = it.casteName
                                    }

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
                                                it.status)
                                        }
                                        else DidiEndorsementStatus.NOT_STARTED.ordinal

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
                                            villageId = villageId,
                                            cohortName = tolaName,
                                            needsToPost = false,
                                            wealth_ranking = wealthRanking?: BLANK_STRING,
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
                                                intToString(didi.shgFlag) ?: SHGFlag.NOT_MARKED.name).value,
                                            transactionId = "",
                                            localCreatedDate = didi.localCreatedDate,
                                            localModifiedDate = didi.localModifiedDate,
                                            score = didi.bpcScore ?: 0.0,
                                            comment =  didi.bpcComment ?: BLANK_STRING,
                                            crpScore = didi.crpScore,
                                            crpComment = didi.crpComment,
                                            bpcScore = didi.bpcScore ?: 0.0,
                                            bpcComment = didi.bpcComment ?: BLANK_STRING,
                                            crpUploadedImage = didi.crpUploadedImage,
                                            needsToPostImage = false,
                                            rankingEdit = didi.rankingEdit,
                                            patEdit = didi.patEdit,
                                            voEndorsementEdit = didi.voEndorsementEdit,
                                            ableBodiedFlag = AbleBodiedFlag.fromSting(intToString(didi.ableBodiedFlag) ?: AbleBodiedFlag.NOT_MARKED.name).value
                                        )
                                    )
                                    if(!didi.crpUploadedImage.isNullOrEmpty()){
                                        downloadAuthorizedImageItem(didi.id,didi.crpUploadedImage?: BLANK_STRING, prefRepo = prefRepo )
                                    }
                                }
                            } catch (ex: Exception) {
                                onError(
                                    tag = "VillageSelectionRepository",
                                    "Error : ${didiResponse.message}"
                                )

                            }
                        }
                    }
                } else {
                    val ex = ApiResponseFailException(didiResponse.message)
                    if (!RetryHelper.retryApiList.contains(ApiType.BPC_DIDI_LIST_API)) RetryHelper.retryApiList.add(
                        ApiType.BPC_DIDI_LIST_API
                    )
                    RetryHelper.stepListApiVillageId.add(villageId)
                    onCatchError(ex, ApiType.BPC_DIDI_LIST_API)
                }
            } catch (ex: Exception) {
                if (ex !is JsonSyntaxException) {
                    if (!RetryHelper.retryApiList.contains(ApiType.BPC_DIDI_LIST_API)) RetryHelper.retryApiList.add(
                        ApiType.BPC_DIDI_LIST_API
                    )
                    RetryHelper.stepListApiVillageId.add(villageId)
                }
                onCatchError(ex, ApiType.BPC_DIDI_LIST_API)
            }
            try {
                NudgeLogger.d("VillageSelectionRepository", "fetchDataForBpc fetchPATSurveyToServer " +
                        "request -> ${listOf(villageId)}")
                val answerApiResponse = apiService.fetchPATSurveyToServer(
                    listOf(villageId)
                )
                NudgeLogger.d("VillageSelectionRepository", "fetchDataForBpc fetchPATSurveyToServer " +
                        "response -> status: ${answerApiResponse.status}")
                if (answerApiResponse.status.equals(SUCCESS, true)) {
                    answerApiResponse.data?.let {
                        val answerList: ArrayList<SectionAnswerEntity> =
                            arrayListOf()
                        val numAnswerList: ArrayList<NumericAnswerEntity> =
                            arrayListOf()
                        val didiIdList = mutableListOf<Int>()
                        it.forEach { item ->
                            if (item.userType.equals(USER_BPC, true)) {
                                item.beneficiaryId?.let { it1 -> didiIdList.add(it1) }
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
                                            questionDao.getQuestionForLanguage(
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
                            answerDao.deleteAllAnswersForVillage(villageId)
                            delay(100)
                            answerDao.insertAll(answerList)
                        }
                        if (numAnswerList.isNotEmpty()) {
                            numericAnswerDao.deleteAllNumericAnswersForDidis(didiIdList)
                            delay(100)
                            numericAnswerDao.insertAll(numAnswerList)
                        }
                    }
                } else {
                    val ex = ApiResponseFailException(answerApiResponse.message)
                    if (!RetryHelper.retryApiList.contains(ApiType.PAT_BPC_SURVEY_SUMMARY)) RetryHelper.retryApiList.add(
                        ApiType.PAT_BPC_SURVEY_SUMMARY
                    )

                    if (!RetryHelper.stepListApiVillageId.contains(villageId)) RetryHelper.stepListApiVillageId.add(
                        villageId
                    )
                    onCatchError(ex, ApiType.PAT_BPC_SURVEY_SUMMARY)
                }

                prefRepo.savePref(PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_ + villageId, true)
                prefRepo.savePref(PREF_BPC_DIDI_LIST_SYNCED_FOR_VILLAGE_ + villageId, true)
            } catch (ex: Exception) {
                if (ex !is JsonSyntaxException) {
                    if (!RetryHelper.retryApiList.contains(ApiType.PAT_BPC_SURVEY_SUMMARY)) RetryHelper.retryApiList.add(
                        ApiType.PAT_BPC_SURVEY_SUMMARY
                    )

                    if (!RetryHelper.stepListApiVillageId.contains(villageId)) RetryHelper.stepListApiVillageId.add(
                        villageId
                    )
                }
                onCatchError(ex, ApiType.PAT_BPC_SURVEY_SUMMARY)
            }
            delay(100)
            checkPendingDidiForVerification(villageId, prefRepo)
        }
    }

    private fun fetchPoorDidisForBpc(villageId: Int, prefRepo: PrefRepo) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                NudgeLogger.d("VillageSelectionRepository", "fetchDataForBpc getDidisWithRankingFromNetwork " +
                        "request -> villageId = village.id, \"Category\", StepResultTypeRequest(\n" +
                        "                                    StepType.WEALTH_RANKING.name, ResultType.POOR.name")
                val poorDidiList = apiService.getDidisFromNetwork(villageId)/*apiService.getDidisWithRankingFromNetwork(
                                villageId = village.id, "Category", StepResultTypeRequest(
                                    StepType.WEALTH_RANKING.name, ResultType.POOR.name
                                )
                            )*/
                NudgeLogger.d("VillageSelectionRepository", "fetchDataForBpc getDidisWithRankingFromNetwork " +
                        "poorDidiList status = ${poorDidiList.status}, message = ${poorDidiList.message}, data = ${poorDidiList.data.toString()}")
                if (poorDidiList.status.equals(SUCCESS, true)) {
                    poorDidiList.data?.let { didiRank ->
                        if (didiRank.didiList.isNotEmpty()) {
                            poorDidiListDao.deleteAllDidisForVillage(villageId)
                            delay(100)
                            didiRank.didiList.forEach { poorDidis ->
                                poorDidis?.let { didi ->
                                    var tolaName = BLANK_STRING
                                    var casteName = BLANK_STRING
                                    val singleTola =
                                        tolaDao.fetchSingleTola(didi.cohortId)
                                    val singleCaste =
                                        casteListDao.getCaste(didi.castId, prefRepo?.getAppLanguageId()?:2)
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

                                    NudgeLogger.d("VillageSelectionRepository", "fetchDataForBpc getDidisWithRankingFromNetwork " +
                                            "poorDidiListDao.insertPoorDidi() didiId = ${didi.id} before")
                                    poorDidiListDao.insertPoorDidi(
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
                                            villageId = villageId,
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
                                            shgFlag = SHGFlag.fromSting(didi.shgFlag ?: SHGFlag.NOT_MARKED.name).value,
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
                                    )
                                    NudgeLogger.d("VillageSelectionRepository", "fetchDataForBpc getDidisWithRankingFromNetwork " +
                                            "poorDidiListDao.insertPoorDidi() didiId = ${didi.id} after")
                                }
                            }
                        }
                    }
                } else {
                    val ex = ApiResponseFailException(poorDidiList.message ?: "Poor Didi Ranking list error")
                    if (!RetryHelper.retryApiList.contains(ApiType.BPC_POOR_DIDI_LIST_API)) RetryHelper.retryApiList.add(
                        ApiType.BPC_POOR_DIDI_LIST_API
                    )
                    RetryHelper.stepListApiVillageId.add(villageId)
                    onCatchError(ex, ApiType.BPC_POOR_DIDI_LIST_API)
                }
            } catch (ex: Exception) {
                if (ex !is JsonSyntaxException) {
                    if (!RetryHelper.retryApiList.contains(ApiType.BPC_POOR_DIDI_LIST_API)) RetryHelper.retryApiList.add(
                        ApiType.BPC_POOR_DIDI_LIST_API
                    )
                    RetryHelper.stepListApiVillageId.add(villageId)
                }
                onCatchError(ex, ApiType.BPC_POOR_DIDI_LIST_API)
            }
        }
    }

    private fun downloadAuthorizedImageItem(id:Int, image: String, prefRepo: PrefRepo) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val imageFile = getAuthImagePath(androidDownloader.mContext, image)
                if (!imageFile.exists()) {
                    val localDownloader = androidDownloader
                    val downloadManager = androidDownloader.mContext.getSystemService(DownloadManager::class.java)
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
                                didiDao.updateImageLocalPath(id,imageFile.absolutePath)
                                didiDao.updateNeedsToPostImage(id, false)
                            }, onDownloadFailed = {
                                NudgeLogger.d("VillageSelectorViewModel", "downloadAuthorizedImageItem -> onDownloadFailed")
                            })
                    }
                } else {
                    didiDao.updateImageLocalPath(id,imageFile.absolutePath)
                    didiDao.updateNeedsToPostImage(id, false)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                NudgeLogger.e("VillageSelectorViewModel", "downloadAuthorizedImageItem -> downloadItem exception", ex)
            }
        }
    }



    private fun createMultiLanguageVillageRequest(localLanguageList: List<LanguageEntity>): String {
        var requestString: StringBuilder = StringBuilder()
        var request: String = "2"
        if (localLanguageList.isNotEmpty()) {
            localLanguageList.forEach {
                requestString.append("${it.id}-")
            }
        } else request = "2"
        if(requestString.contains("-")){
            request= requestString.substring(0,requestString.length-1)
        }
        return request
    }

    fun downloadImageItem(context: Context, image: String) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                if (!getImagePath(context, image).exists()) {
                    val localDownloader = (context as MainActivity).downloader
                    val downloadManager = context.getSystemService(DownloadManager::class.java)
                    val downloadId = localDownloader?.downloadImageFile(image, FileType.IMAGE)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.e("VideoListViewModel", "downloadItem exception", ex)
            }
        }

    }

    private fun downloadAuthorizedImageItem(id:Int, downloader: AndroidDownloader, image: String, prefRepo: PrefRepo) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val imageFile = getAuthImagePath(downloader.mContext, image)
                if (!imageFile.exists()) {
                    val localDownloader = downloader
                    val downloadManager = downloader.mContext.getSystemService(DownloadManager::class.java)
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
                                didiDao.updateImageLocalPath(id,imageFile.absolutePath)
                                didiDao.updateNeedsToPostImage(id, false)
                            }, onDownloadFailed = {
                                NudgeLogger.d("VillageSelectorViewModel", "downloadAuthorizedImageItem -> onDownloadFailed")
                            })
                    }
                } else {
                    didiDao.updateImageLocalPath(id,imageFile.absolutePath)
                    didiDao.updateNeedsToPostImage(id, false)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                NudgeLogger.e("VillageSelectorViewModel", "downloadAuthorizedImageItem -> downloadItem exception", ex)
            }
        }
    }

    fun saveVideosToDb(context: Context) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val trainingVideos = trainingVideoDao.getVideoList()
            if (trainingVideos.isEmpty()) {
                videoList.forEach {
                    val trainingVideoEntity = TrainingVideoEntity(
                        id = it.id,
                        title = it.title,
                        description = it.description,
                        url = it.url,
                        thumbUrl = it.thumbUrl,
                        isDownload = if (getVideoPath(
                                context, it.id, fileType = FileType.VIDEO
                            ).exists()
                        ) DownloadStatus.DOWNLOADED.value else DownloadStatus.UNAVAILABLE.value
                    )
                    trainingVideoDao.insert(trainingVideoEntity)
                }
            } else {
                trainingVideos.forEach {
                    val videoIsDownloaded = if (getVideoPath(
                            context, it.id, fileType = FileType.VIDEO
                        ).exists()
                    ) DownloadStatus.DOWNLOADED.value else DownloadStatus.UNAVAILABLE.value
                    if (it.isDownload != videoIsDownloaded) {
                        val trainingVideoEntity = TrainingVideoEntity(
                            id = it.id,
                            title = it.title,
                            description = it.description,
                            url = it.url,
                            thumbUrl = it.thumbUrl,
                            isDownload = videoIsDownloaded
                        )
                        trainingVideoDao.insert(trainingVideoEntity)
                    }
                }
            }
        }
    }

    fun refreshStepListData(
        taskCompleted: (success: Boolean) -> Unit
    ) {

        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val villageList = villageListDao.getAllVillages(prefRepo.getAppLanguageId() ?: 2)
            villageList.forEach { village ->
                try {

                    //Fetch Step List Data try {
                    NudgeLogger.d(
                        "VillageSelectionRepository",
                        "refreshBpcData getStepsList request -> village.id = ${village.id}"
                    )

                    if (stepsListDao.getAllStepsForVillage(village.id).isEmpty()) {
                    val response = apiService.getStepsList(village.id)
                    NudgeLogger.d(
                        "VillageSelectionRepository", "refreshBpcData getStepsList " +
                                "response status = ${response.status}, message = ${response.message}, data = ${response.data.toString()}"
                    )
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.let { it ->
                            if (it.stepList.isNotEmpty()) {
                                it.stepList.forEach { steps ->
                                    steps.villageId = village.id
                                    /*steps.isComplete =
                                    findCompleteValue(steps.status).ordinal*/
                                    if (steps.id == 40) {
                                        prefRepo.savePref(
                                            PREF_TRANSECT_WALK_COMPLETION_DATE_ + village.id,
                                            steps.localModifiedDate ?: System.currentTimeMillis()
                                        )
                                    }

                                    if (steps.id == 41) {
                                        prefRepo.savePref(
                                            PREF_SOCIAL_MAPPING_COMPLETION_DATE_ + village.id,
                                            steps.localModifiedDate ?: System.currentTimeMillis()
                                        )
                                    }

                                    if (steps.id == 46) {
                                        prefRepo.savePref(
                                            PREF_WEALTH_RANKING_COMPLETION_DATE_ + village.id,
                                            steps.localModifiedDate ?: System.currentTimeMillis()
                                        )
                                    }

                                    if (steps.id == 43) {
                                        prefRepo.savePref(
                                            PREF_PAT_COMPLETION_DATE_ + village.id,
                                            steps.localModifiedDate ?: System.currentTimeMillis()
                                        )
                                    }
                                    if (steps.id == 44) {
                                        prefRepo.savePref(
                                            PREF_VO_ENDORSEMENT_COMPLETION_DATE_ + village.id,
                                            steps.localModifiedDate ?: System.currentTimeMillis()
                                        )
                                    }

                                    if (steps.id == 45) {
                                        prefRepo.savePref(
                                            PREF_BPC_PAT_COMPLETION_DATE_ + village.id,
                                            steps.localModifiedDate
                                                ?: System.currentTimeMillis()
                                        )
                                    }
                                }
                                val localStepListForVillage =
                                    stepsListDao.getAllStepsForVillage(village.id)
                                NudgeLogger.d(
                                    "VillageSelectionRepository", "refreshBpcData getStepsList " +
                                            "stepsListDao.insertAll(it.stepList) before"
                                )

                                val updatedStepList = mutableListOf<StepListEntity>()
                                localStepListForVillage.forEach { step ->
                                    updatedStepList.add(step.getUpdatedStep(it.stepList[it.stepList.map { it.id }
                                        .indexOf(step.id)]))
                                }
                                if (localStepListForVillage.size != it.stepList.size) {
                                    if (localStepListForVillage.size < it.stepList.size) {
                                        val tempStepList = mutableListOf<StepListEntity>()
                                        tempStepList.addAll(it.stepList)
                                        tempStepList.sortedBy { it.orderNumber }
                                        localStepListForVillage.forEach { localStep ->
                                            if (it.stepList.map { remoteStep -> remoteStep.id }
                                                    .contains(localStep.id)) {
                                                tempStepList.remove(it.stepList.sortedBy { it.orderNumber }[it.stepList.map { it.id }
                                                    .indexOf(localStep.id)])
                                            }
                                        }
                                        updatedStepList.addAll(tempStepList)
                                    } else {
                                        val tempStepList = mutableListOf<StepListEntity>()
                                        tempStepList.addAll(localStepListForVillage)
                                        tempStepList.sortedBy { it.orderNumber }
                                        it.stepList.forEach { remoteStep ->
                                            if (localStepListForVillage.map { localStep -> remoteStep.id }
                                                    .contains(remoteStep.id)) {
                                                tempStepList.remove(localStepListForVillage.sortedBy { it.orderNumber }[localStepListForVillage.map { it.id }
                                                    .indexOf(remoteStep.id)])
                                            }
                                        }
                                        updatedStepList.addAll(tempStepList)
                                    }
                                }
                                if (updatedStepList.isNotEmpty()) {
                                    stepsListDao.deleteAllStepsForVillage(village.id)
                                    delay(100)
                                    stepsListDao.insertAll(updatedStepList)
                                }

                                NudgeLogger.d(
                                    "VillageSelectionRepository", "refreshBpcData getStepsList " +
                                            "stepsListDao.insertAll(it.stepList) after"
                                )
                            }
                            prefRepo.savePref(
                                PREF_PROGRAM_NAME, it.programName
                            )
                        }
                    } else {
                        val ex = ApiResponseFailException(response.message)
                        if (!RetryHelper.retryApiList.contains(ApiType.STEP_LIST_API)) RetryHelper.retryApiList.add(
                            ApiType.STEP_LIST_API
                        )
                        RetryHelper.stepListApiVillageId.add(village.id)
                        onCatchError(ex, ApiType.STEP_LIST_API)
                    }
                    }
                } catch (ex: Exception) {
                    if (ex !is JsonSyntaxException) {
                        if (!RetryHelper.retryApiList.contains(ApiType.STEP_LIST_API)) RetryHelper.retryApiList.add(
                            ApiType.STEP_LIST_API
                        )
                        RetryHelper.stepListApiVillageId.add(village.id)
                    }
                    onCatchError(ex, ApiType.STEP_LIST_API)
                    withContext(Dispatchers.Main) {
                        taskCompleted(false)
                    }
                }

            }
            withContext(Dispatchers.Main) {
                taskCompleted(true)
            }
        }
    }

    fun fetchUserAndVillageDetails(forceRefresh: Boolean = false, apiSuccess: (success: UserAndVillageDetailsModel) -> Unit) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                var userAndVillageDetailsModel: UserAndVillageDetailsModel? = null
                val localVillageList = villageListDao.getAllVillages(prefRepo.getAppLanguageId()?:2)
                val localLanguageList = languageListDao.getAllLanguages()
                val villageReq= createMultiLanguageVillageRequest(localLanguageList)
                if (!forceRefresh && !localVillageList.isNullOrEmpty()) {
                if (!localVillageList.isNullOrEmpty()) {
                    val stateId = localVillageList[0].stateId
                    userAndVillageDetailsModel = UserAndVillageDetailsModel(true, localVillageList, stateId = stateId)
//                    _villagList.value = localVillageList
//                    _filterVillageList.value = villageList.value
                    withContext(Dispatchers.Main) {
                        apiSuccess(userAndVillageDetailsModel!!)
                    }
                }
                } else {
                    NudgeLogger.d("VillageSelectionRepository", "fetchUserAndVillageDetails -> villageReq: $villageReq")
                    val response = apiService.userAndVillageListAPI(villageReq)
                    NudgeLogger.d("VillageSelectionRepository", "fetchUserAndVillageDetails -> response: ${response.json()}")
                    withContext(Dispatchers.IO) {
                        if (response.status.equals(SUCCESS, true)) {
                            response.data?.let {
                                saveUserDetailsInPref(UserDetailsModel(it.username ?: "", it.name ?: "", it.email ?: "", it.identityNumber  ?: "", it.profileImage ?: "", it.roleName ?: "", it.typeName ?: ""))
                                villageListDao.insertOnlyNewData(
                                    it.villageList ?: listOf(),
                                    prefRepo.isUserBPC()
                                )
                                val stateId = if (it.villageList?.isNotEmpty() == true) it.villageList?.get(0)?.stateId?:1 else -1
                                val localVillageList = villageListDao.getAllVillages(prefRepo.getAppLanguageId()?:2)
                                val defaultLanguageVillageList = villageListDao.getAllVillages(DEFAULT_LANGUAGE_ID)
                                prefRepo.savePref(PREF_KEY_TYPE_STATE_ID,  it.villageList?.get(0)?.stateId?:4)
                                userAndVillageDetailsModel = if (localVillageList.isNotEmpty()) {
                                    UserAndVillageDetailsModel(true, localVillageList, stateId = stateId)
                                } else {
                                    UserAndVillageDetailsModel(true, getEmitLanguageList(defaultLanguageVillageList, localVillageList, prefRepo.getAppLanguageId() ?: 2), stateId)
                                }
                                withContext(Dispatchers.Main) {
                                    apiSuccess(userAndVillageDetailsModel!!)
                                }
                            }

                            if (response.data == null) {
                                withContext(Dispatchers.Main) {
                                apiSuccess(UserAndVillageDetailsModel.getFailedResponseModel())
                            }
                            }


                            if(!response.lastSyncTime.isNullOrEmpty()){
                                updateLastSyncTime(prefRepo,response.lastSyncTime)
                            }

                            Log.d("TAG", "fetchUserDetails: ${prefRepo.getPref(LAST_SYNC_TIME,0L)}")

                        } else if (response.status.equals(FAIL, true)) {
                            withContext(Dispatchers.Main) {
                                NudgeLogger.d("VillageSelectionScreen", "fetchUserDetails response.status.equals(FAIL, true) -> viewModel.showLoader.value = false")
                            }
                            withContext(Dispatchers.Main) {
                            apiSuccess(UserAndVillageDetailsModel.getFailedResponseModel())
                            }
                            NudgeLogger.d("VillageSelectionViewModel", "fetchUserDetails -> response.status: ${response.status}, message: ${response.message}")
                        } else {
                            NudgeLogger.d("VillageSelectionViewModel", "fetchUserDetails -> Error: ${response.message}")
                            onError(tag = "VillageSelectionViewModel", "Error : ${response.message}")
                            withContext(Dispatchers.Main) {
                                NudgeLogger.d("VillageSelectionScreen", "fetchUserDetails else 1 -> viewModel.showLoader.value = false")
                            }
                        }
                    }
                }
                NudgeLogger.d("VillageSelectionViewModel", "UserDetails => " + "\n" +
                        "MOBILE NUMBER: ${prefRepo.getMobileNumber()}\n" +
                        "PREF_KEY_USER_NAME: ${prefRepo.getPref(PREF_KEY_USER_NAME, "")}\n" +
                        "PREF_KEY_NAME: ${prefRepo.getPref(PREF_KEY_NAME, "")}\n" +
                        "PREF_KEY_EMAIL: ${prefRepo.getPref(PREF_KEY_EMAIL, "")}\n" +
                        "PREF_KEY_IDENTITY_NUMBER: ${prefRepo.getPref(PREF_KEY_IDENTITY_NUMBER, "")}\n" +
                        "PREF_KEY_PROFILE_IMAGE: ${prefRepo.getPref(PREF_KEY_PROFILE_IMAGE, "")}\n" +
                        "PREF_KEY_ROLE_NAME: ${prefRepo.getPref(PREF_KEY_ROLE_NAME, "")}\n" +
                        "PREF_KEY_TYPE_NAME: ${prefRepo.getPref(PREF_KEY_TYPE_NAME, "")}")
            } catch (ex: Exception) {
                NudgeLogger.e("VillageSelectionViewModel", "fetchUserDetails -> catch called", ex)
                withContext(Dispatchers.Main){
                    NudgeLogger.d("VillageSelectionScreen", "fetchUserDetails catch (ex: Exception) -> viewModel.showLoader.value = false")
                    apiSuccess(UserAndVillageDetailsModel.getFailedResponseModel())
                }
                onCatchError(ex, ApiType.VILLAGE_LIST_API)
                if (ex is HttpException) {
                    if (ex.response()?.code() == RESPONSE_CODE_UNAUTHORIZED || ex.response()
                            ?.code() == RESPONSE_CODE_CONFLICT
                    ) {
                        RetryHelper.retryApiList.add(ApiType.VILLAGE_LIST_API)
                        withContext(Dispatchers.Main) {
                            RetryHelper.tokenExpired.value = true
                        }
                    }
                }
            }
        }
    }

    private fun preserveOldRecord(oldVillageList: List<VillageEntity>) {
        if (oldVillageList.isNotEmpty()) {
            oldVillageList.forEach { oldVillageItem ->
                villageListDao.updateVillageDataLoadStatus(oldVillageItem.id, oldVillageItem.isDataLoadTriedOnce)
            }
        }
    }

    private fun saveUserDetailsInPref(userDetailsModel: UserDetailsModel) {
        prefRepo.savePref(PREF_KEY_USER_NAME, userDetailsModel.username ?: "")
        prefRepo.savePref(PREF_KEY_NAME, userDetailsModel.name ?: "")
        prefRepo.savePref(PREF_KEY_EMAIL, userDetailsModel.email ?: "")
        prefRepo.savePref(PREF_KEY_IDENTITY_NUMBER, userDetailsModel.identityNumber ?: "")
        prefRepo.savePref(PREF_KEY_PROFILE_IMAGE, userDetailsModel.profileImage ?: "")
        prefRepo.savePref(PREF_KEY_ROLE_NAME, userDetailsModel.roleName ?: "")
        prefRepo.savePref(PREF_KEY_TYPE_NAME, userDetailsModel.typeName ?: "")

        if (userDetailsModel.typeName.equals(BPC_USER_TYPE, true)) {
            prefRepo.setIsUserBPC(true)
        } else {
            prefRepo.setIsUserBPC(false)
        }

    }

    fun saveSelectedVillage(villageEntity: VillageEntity) {
        prefRepo.saveSelectedVillage(villageEntity)
    }

    fun isUserBPC() = prefRepo.isUserBPC()
    fun saveSettingOpenFrom(fromPage: Int) {
        prefRepo.saveSettingOpenFrom(fromPage)
    }

    fun fetchPatQuestionsFromNetwork(isRefresh: Boolean) {
        fetchQuestions(prefRepo, isRefresh)
    }

}