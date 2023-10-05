package com.patsurvey.nudge.activities.ui.progress


import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.google.gson.JsonSyntaxException
import com.patsurvey.nudge.MyApplication
import com.patsurvey.nudge.RetryHelper
import com.patsurvey.nudge.RetryHelper.crpPatQuestionApiLanguageId
import com.patsurvey.nudge.RetryHelper.retryApiList
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.analytics.AnalyticsHelper
import com.patsurvey.nudge.analytics.EventParams
import com.patsurvey.nudge.analytics.Events
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.BpcSummaryEntity
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.LanguageEntity
import com.patsurvey.nudge.database.NumericAnswerEntity
import com.patsurvey.nudge.database.PoorDidiEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.TrainingVideoEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.BpcNonSelectedDidiDao
import com.patsurvey.nudge.database.dao.BpcSelectedDidiDao
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
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.request.GetQuestionListRequest
import com.patsurvey.nudge.model.request.StepResultTypeRequest
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.AbleBodiedFlag
import com.patsurvey.nudge.utils.ApiResponseFailException
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.BPC_SURVEY_CONSTANT
import com.patsurvey.nudge.utils.BPC_USER_TYPE
import com.patsurvey.nudge.utils.COMPLETED_STRING
import com.patsurvey.nudge.utils.CRP_USER_TYPE
import com.patsurvey.nudge.utils.DEFAULT_LANGUAGE_ID
import com.patsurvey.nudge.utils.DIDI_REJECTED
import com.patsurvey.nudge.utils.DOUBLE_ZERO
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.DownloadStatus
import com.patsurvey.nudge.utils.FAIL
import com.patsurvey.nudge.utils.HEADING_QUESTION_TYPE
import com.patsurvey.nudge.utils.LAST_SYNC_TIME
import com.patsurvey.nudge.utils.LAST_UPDATE_TIME
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PAT_SURVEY_CONSTANT
import com.patsurvey.nudge.utils.PREF_BPC_DIDI_LIST_SYNCED_FOR_VILLAGE_
import com.patsurvey.nudge.utils.PREF_BPC_PAT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_KEY_EMAIL
import com.patsurvey.nudge.utils.PREF_KEY_IDENTITY_NUMBER
import com.patsurvey.nudge.utils.PREF_KEY_NAME
import com.patsurvey.nudge.utils.PREF_KEY_PROFILE_IMAGE
import com.patsurvey.nudge.utils.PREF_KEY_ROLE_NAME
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_NAME
import com.patsurvey.nudge.utils.PREF_KEY_USER_NAME
import com.patsurvey.nudge.utils.PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_
import com.patsurvey.nudge.utils.PREF_PAT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_PROGRAM_NAME
import com.patsurvey.nudge.utils.PREF_SOCIAL_MAPPING_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_TRANSECT_WALK_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_VO_ENDORSEMENT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_WEALTH_RANKING_COMPLETION_DATE_
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
import com.patsurvey.nudge.utils.USER_BPC
import com.patsurvey.nudge.utils.USER_CRP
import com.patsurvey.nudge.utils.WealthRank
import com.patsurvey.nudge.utils.findCompleteValue
import com.patsurvey.nudge.utils.formatRatio
import com.patsurvey.nudge.utils.getAuthImagePath
import com.patsurvey.nudge.utils.getImagePath
import com.patsurvey.nudge.utils.intToString
import com.patsurvey.nudge.utils.showCustomToast
import com.patsurvey.nudge.utils.stringToDouble
import com.patsurvey.nudge.utils.updateLastSyncTime
import com.patsurvey.nudge.utils.videoList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class VillageSelectionViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val apiService: ApiService,
    val villageListDao: VillageListDao,
    val stepsListDao: StepsListDao,
    val tolaDao: TolaDao,
    val didiDao: DidiDao,
    val casteListDao: CasteListDao,
    val languageListDao: LanguageListDao,
    val questionListDao: QuestionListDao,
    val trainingVideoDao: TrainingVideoDao,
    val numericAnswerDao: NumericAnswerDao,
    val answerDao: AnswerDao,
    val bpcSummaryDao: BpcSummaryDao,
    val bpcSelectedDidiDao: BpcSelectedDidiDao,
    val bpcNonSelectedDidiDao: BpcNonSelectedDidiDao,
    val poorDidiListDao: PoorDidiListDao,
    val downloader: AndroidDownloader,
    val villageSelectionRepository: VillageSelectionRepository

) : BaseViewModel() {
    private val _villagList = MutableStateFlow(listOf<VillageEntity>())
    val villageList: StateFlow<List<VillageEntity>> get() = _villagList

    val villageSelected = mutableStateOf(0)
    val stateId = mutableStateOf(1)
    val showLoader = mutableStateOf(false)

    val shouldRetry = mutableStateOf(false)
    val multiVillageRequest = mutableStateOf("2")

    val isVoEndorsementComplete = mutableStateOf(mutableMapOf<Int, Boolean>())

    fun isLoggedIn() = (prefRepo.getAccessToken()?.isNotEmpty() == true)

    fun init(context: Context) {
        showLoader.value = true
        fetchUserDetails { success ->
            if (success) {
                fetchQuestions()
                fetchCastList()
                if (prefRepo.getPref(LAST_UPDATE_TIME, 0L) != 0L) {
                    if ((System.currentTimeMillis() - prefRepo.getPref(LAST_UPDATE_TIME, 0L)) > TimeUnit.DAYS.toMillis(30)) {
                        if ((prefRepo.getPref(PREF_KEY_TYPE_NAME, "") ?: "").equals(CRP_USER_TYPE, true)) {
                            fetchVillageList(context)
                        } else {
                            fetchDataForBpc(context)
                        }
                    } else {
                        showLoader.value = false
                    }
                } else {
                    if ((prefRepo.getPref(PREF_KEY_TYPE_NAME, "") ?: "").equals(CRP_USER_TYPE, true)) {
                        fetchVillageList(context)
                    } else {
                        fetchDataForBpc(context)
                    }
                }
            }
        }
    }

    private fun fetchQuestions(){
        showLoader.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val localLanguageList = languageListDao.getAllLanguages()
            localLanguageList?.let {
                localLanguageList.forEach { languageEntity ->
                    try {
                        // Fetch QuestionList from Server
                        val localLanguageQuesList =
                            questionListDao.getAllQuestionsForLanguage(languageEntity.id)
                        if (localLanguageQuesList.isEmpty()) {
                            NudgeLogger.d("TAG", "fetchQuestions: QuestionList")
                            val quesListResponse = apiService.fetchQuestionListFromServer(
                                GetQuestionListRequest(
                                    languageId = languageEntity.id,
                                    stateId = stateId.value,
                                    surveyName = if (prefRepo.isUserBPC()) BPC_SURVEY_CONSTANT else PAT_SURVEY_CONSTANT
                                )
                            )
                            if (quesListResponse.status.equals(SUCCESS, true)) {
                                quesListResponse.data?.let { questionList ->
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
                                                    QUESTION_FLAG_RATIO)) {
                                                val heading = question?.options?.filter {
                                                    it.optionType.equals(
                                                        HEADING_QUESTION_TYPE, true
                                                    )
                                                }?.get(0)?.display
                                                question?.headingProductAssetValue = heading
                                            }
                                        }
                                        list?.questionList?.let {
                                            questionListDao.insertAll(it as List<QuestionEntity>)
                                        }
                                    }
                                }
                            } else {
                                val ex = ApiResponseFailException(quesListResponse.message)
                                if (!retryApiList.contains(if (prefRepo.isUserBPC()) ApiType.PAT_BPC_QUESTION_API else ApiType.PAT_CRP_QUESTION_API)) retryApiList.add(
                                    if (prefRepo.isUserBPC()) ApiType.PAT_BPC_QUESTION_API else ApiType.PAT_CRP_QUESTION_API
                                )
                                crpPatQuestionApiLanguageId.add(languageEntity.id)
                                onCatchError(
                                    ex,
                                    if (prefRepo.isUserBPC()) ApiType.PAT_BPC_QUESTION_API else ApiType.PAT_CRP_QUESTION_API
                                )
                            }

                        }
                    } catch (ex: Exception) {
                        if (ex !is JsonSyntaxException) {
                            if (!retryApiList.contains(if (prefRepo.isUserBPC()) ApiType.PAT_BPC_QUESTION_API else ApiType.PAT_CRP_QUESTION_API)) retryApiList.add(
                                if (prefRepo.isUserBPC()) ApiType.PAT_BPC_QUESTION_API else ApiType.PAT_CRP_QUESTION_API
                            )
                            crpPatQuestionApiLanguageId.add(languageEntity.id)
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

    private fun fetchDataForBpc(context: Context) {
        showLoader.value = true
        job = MyApplication.appScopeLaunch (Dispatchers.IO + exceptionHandler){
            val awaitDeff= CoroutineScope(Dispatchers.IO).async {
                try {
                    val villageList =
                        villageListDao.getAllVillages(prefRepo.getAppLanguageId() ?: 2)
                    val localStepsList = stepsListDao.getAllSteps()
                    val localLanguageList = languageListDao.getAllLanguages()
                    val villageIdList: ArrayList<Int> = arrayListOf()

                    val localAnswerList = answerDao.getAllAnswer()
                    if (localAnswerList.isNotEmpty()) {
                        answerDao.deleteAnswerTable()
                    }
                    val localNumAnswerList = numericAnswerDao.getAllNumericAnswers()
                    if (localNumAnswerList.isNotEmpty()) {
                        numericAnswerDao.deleteNumericTable()
                    }
                    villageList.forEach { village ->
                        villageIdList.add(village.id)

                        stateId.value = village.stateId
                        RetryHelper.stateId = stateId.value
                        try {
                            NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc getStepsList request -> village.id = ${village.id}")
                            val response = apiService.getStepsList(village.id)
                            NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc getStepsList " +
                                    "response status = ${response.status}, message = ${response.message}, data = ${response.data.toString()}")
                            if (response.status.equals(SUCCESS, true)) {
                                response.data?.let {
                                    if (it.stepList.isNotEmpty()) {
                                        it.stepList.forEach { steps ->
                                            steps.villageId = village.id
                                            steps.isComplete =
                                                findCompleteValue(steps.status).ordinal

//                                            if(steps.id == 46){
//                                                prefRepo.savePref(
//                                                    PREF_WEALTH_RANKING_COMPLETION_DATE, steps.localModifiedDate?: BLANK_STRING)
//                                            }
                                            if(steps.id == 40){
                                                prefRepo.savePref(
                                                    PREF_TRANSECT_WALK_COMPLETION_DATE_+village.id, steps.localModifiedDate?: System.currentTimeMillis())
                                            }

                                            if(steps.id == 41){
                                                prefRepo.savePref(
                                                    PREF_SOCIAL_MAPPING_COMPLETION_DATE_+village.id, steps.localModifiedDate?: System.currentTimeMillis())
                                            }

                                            if(steps.id == 46){
                                                prefRepo.savePref(
                                                    PREF_WEALTH_RANKING_COMPLETION_DATE_+village.id, steps.localModifiedDate?: System.currentTimeMillis())
                                            }

                                            if(steps.id == 43){
                                                prefRepo.savePref(
                                                    PREF_PAT_COMPLETION_DATE_+village.id, steps.localModifiedDate?: System.currentTimeMillis())
                                            }
                                            if(steps.id == 44){
                                                prefRepo.savePref(
                                                    PREF_VO_ENDORSEMENT_COMPLETION_DATE_+village.id, steps.localModifiedDate?: System.currentTimeMillis())
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
                                        NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc getStepsList " +
                                                "stepsListDao.insertAll(it.stepList) before")
                                        stepsListDao.insertAll(it.stepList)
                                        NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc getStepsList " +
                                                "stepsListDao.insertAll(it.stepList) after")
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
                                if (!retryApiList.contains(ApiType.STEP_LIST_API)) retryApiList.add(
                                    ApiType.STEP_LIST_API
                                )
                                RetryHelper.stepListApiVillageId.add(village.id)
                                onCatchError(ex, ApiType.STEP_LIST_API)
                            }
                        } catch (ex: Exception) {
                            if (ex !is JsonSyntaxException) {
                                if (!retryApiList.contains(ApiType.STEP_LIST_API)) retryApiList.add(
                                    ApiType.STEP_LIST_API
                                )
                                RetryHelper.stepListApiVillageId.add(village.id)
                            }
                            onCatchError(ex, ApiType.STEP_LIST_API)
                        }
                        try {
                            NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc getBpcSummary " +
                                    "village.id = ${village.id}")
                            val bpcSummaryResponse =
                                apiService.getBpcSummary(villageId = village.id)
                            NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc getStepsList " +
                                    "bpcSummaryResponse status = ${bpcSummaryResponse.status}, message = ${bpcSummaryResponse.message}, data = ${bpcSummaryResponse.data.toString()}")
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
                                    NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc getStepsList " +
                                            "bpcSummaryDao.insert(bpcSummary) before")
                                    bpcSummaryDao.insert(bpcSummary)
                                    NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc getStepsList " +
                                            "bpcSummaryDao.insert(bpcSummary) after")
                                }
                            } else {
                                NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc getStepsList " +
                                        "bpcSummaryDao.insert(BpcSummaryEntity(0, 0, 0, 0, 0, 0, villageId = village.id))")
                                bpcSummaryDao.insert(
                                    BpcSummaryEntity(
                                        0, 0, 0, 0, 0, 0, villageId = village.id
                                    )
                                )

                                val ex = ApiResponseFailException(bpcSummaryResponse.message)
                                if (!retryApiList.contains(ApiType.BPC_SUMMARY_API)) retryApiList.add(
                                    ApiType.BPC_SUMMARY_API
                                )
                                RetryHelper.stepListApiVillageId.add(village.id)
                                onCatchError(ex, ApiType.BPC_SUMMARY_API)
                            }
                        }
                        catch (ex: Exception) {
                            bpcSummaryDao.insert(
                                BpcSummaryEntity(
                                    0, 0, 0, 0, 0, 0, villageId = village.id
                                )
                            )
                            if (ex !is JsonSyntaxException) {
                                if (!retryApiList.contains(ApiType.BPC_SUMMARY_API)) retryApiList.add(
                                    ApiType.BPC_SUMMARY_API
                                )
                                RetryHelper.stepListApiVillageId.add(village.id)
                            }
                            onCatchError(ex, ApiType.BPC_SUMMARY_API)
                        }

                        try {
                            NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc getCohortFromNetwork " +
                                    "request village.id = ${village.id}")
                            val cohortResponse =
                                apiService.getCohortFromNetwork(villageId = village.id)
                            NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc getCohortFromNetwork " +
                                    "cohortResponse status = ${cohortResponse.status}, message = ${cohortResponse.message}, data = ${cohortResponse.data.toString()}")
                            if (cohortResponse.status.equals(SUCCESS, true)) {
                                cohortResponse.data?.let {
                                    NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc getCohortFromNetwork " +
                                            "tolaDao.insertAll(it) before")
                                    tolaDao.insertAll(it)
                                    NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc getCohortFromNetwork " +
                                            "tolaDao.insertAll(it) after")
                                }
                            } else {
                                val ex = ApiResponseFailException(cohortResponse.message)
                                if (!retryApiList.contains(ApiType.TOLA_LIST_API)) retryApiList.add(
                                    ApiType.TOLA_LIST_API
                                )
                                RetryHelper.stepListApiVillageId.add(village.id)
                                onCatchError(ex, ApiType.TOLA_LIST_API)
                            }
                        }
                        catch (ex: Exception) {
                            if (ex !is JsonSyntaxException) {
                                if (!retryApiList.contains(ApiType.TOLA_LIST_API)) retryApiList.add(
                                    ApiType.TOLA_LIST_API
                                )
                                RetryHelper.stepListApiVillageId.add(village.id)
                            }
                            onCatchError(ex, ApiType.TOLA_LIST_API)
                        }

                        try {
                            NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc getDidiForBpcFromNetwork " +
                                    "request village.id = ${village.id}")
                            val didiResponse =
                                apiService.getDidiForBpcFromNetwork(villageId = village.id)
                            NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc getDidiForBpcFromNetwork " +
                                    "didiResponse status = ${didiResponse.status}, message = ${didiResponse.message}, data = ${didiResponse.data.toString()}")
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
                                                    casteListDao.getCaste(didi.castId, prefRepo?.getAppLanguageId()?:2)
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
                                                        villageId = village.id,
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
                                                        shgFlag = SHGFlag.fromSting(intToString(didi.shgFlag)?: SHGFlag.NOT_MARKED.name).value,
                                                        transactionId = "",
                                                        localCreatedDate = didi.localCreatedDate,
                                                        localModifiedDate = didi.localModifiedDate,
                                                        crpScore = didi.crpScore,
                                                        crpComment = didi.crpComment,
                                                        bpcScore = didi.bpcScore ?: 0.0,
                                                        bpcComment = didi.bpcComment ?: BLANK_STRING,
                                                        crpUploadedImage = didi.crpUploadedImage,
                                                        needsToPostImage = false,
                                                        rankingEdit = didi.rankingEdit,
                                                        patEdit = didi.patEdit,
                                                        ableBodiedFlag = AbleBodiedFlag.fromSting(intToString(didi.ableBodiedFlag) ?: AbleBodiedFlag.NOT_MARKED.name).value
                                                    )
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
                                if (!retryApiList.contains(ApiType.BPC_DIDI_LIST_API)) retryApiList.add(
                                    ApiType.BPC_DIDI_LIST_API
                                )
                                RetryHelper.stepListApiVillageId.add(village.id)
                                onCatchError(ex, ApiType.BPC_DIDI_LIST_API)
                            }
                        } catch (ex: Exception) {
                            if (ex !is JsonSyntaxException) {
                                if (!retryApiList.contains(ApiType.BPC_DIDI_LIST_API)) retryApiList.add(
                                    ApiType.BPC_DIDI_LIST_API
                                )
                                RetryHelper.stepListApiVillageId.add(village.id)
                            }
                            onCatchError(ex, ApiType.BPC_DIDI_LIST_API)
                        }

                        try {
                            NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc getDidisWithRankingFromNetwork " +
                                    "request -> villageId = village.id, \"Category\", StepResultTypeRequest(\n" +
                                    "                                    StepType.WEALTH_RANKING.name, ResultType.POOR.name")
                            val poorDidiList = apiService.getDidisFromNetwork(village.id)/*apiService.getDidisWithRankingFromNetwork(
                                villageId = village.id, "Category", StepResultTypeRequest(
                                    StepType.WEALTH_RANKING.name, ResultType.POOR.name
                                )
                            )*/
                            NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc getDidisWithRankingFromNetwork " +
                                    "poorDidiList status = ${poorDidiList.status}, message = ${poorDidiList.message}, data = ${poorDidiList.data.toString()}")
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

                                                NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc getDidisWithRankingFromNetwork " +
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
                                                NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc getDidisWithRankingFromNetwork " +
                                                        "poorDidiListDao.insertPoorDidi() didiId = ${didi.id} after")
                                            }
                                        }
                                    }
                                }
                            } else {
                                val ex = ApiResponseFailException(poorDidiList.message ?: "Poor Didi Ranking list error")
                                if (!retryApiList.contains(ApiType.BPC_POOR_DIDI_LIST_API)) retryApiList.add(
                                    ApiType.BPC_POOR_DIDI_LIST_API
                                )
                                RetryHelper.stepListApiVillageId.add(village.id)
                                onCatchError(ex, ApiType.BPC_POOR_DIDI_LIST_API)
                            }
                        } catch (ex: Exception) {
                            if (ex !is JsonSyntaxException) {
                                if (!retryApiList.contains(ApiType.BPC_POOR_DIDI_LIST_API)) retryApiList.add(
                                    ApiType.BPC_POOR_DIDI_LIST_API
                                )
                                RetryHelper.stepListApiVillageId.add(village.id)
                            }
                            onCatchError(ex, ApiType.BPC_POOR_DIDI_LIST_API)
                        }

                        try {
                            NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc fetchPATSurveyToServer " +
                                    "request -> ${listOf(village.id)}")
                            val answerApiResponse = apiService.fetchPATSurveyToServer(
                                listOf(village.id)
                            )
                            NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc fetchPATSurveyToServer " +
                                    "response -> status: ${answerApiResponse.status}")
                            if (answerApiResponse.status.equals(SUCCESS, true)) {
                                answerApiResponse.data?.let {
                                    val answerList: ArrayList<SectionAnswerEntity> =
                                        arrayListOf()
                                    val numAnswerList: ArrayList<NumericAnswerEntity> =
                                        arrayListOf()
                                    it.forEach { item ->
                                        if (item.userType.equals(USER_BPC, true)) {
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
                                        answerDao.insertAll(answerList)
                                    }
                                    if (numAnswerList.isNotEmpty()) {
                                        numericAnswerDao.insertAll(numAnswerList)
                                    }
                                }
                            } else {
                                val ex =
                                    ApiResponseFailException(answerApiResponse.message)
                                if (!retryApiList.contains(ApiType.PAT_BPC_SURVEY_SUMMARY)) retryApiList.add(
                                    ApiType.PAT_BPC_SURVEY_SUMMARY
                                )

                                if (!RetryHelper.stepListApiVillageId.contains(village.id)) RetryHelper.stepListApiVillageId.add(
                                    village.id
                                )

                                onCatchError(ex, ApiType.PAT_BPC_SURVEY_SUMMARY)
                            }
                        } catch (ex: Exception) {
                            if (ex !is JsonSyntaxException) {
                                if (!retryApiList.contains(ApiType.PAT_BPC_SURVEY_SUMMARY)) retryApiList.add(
                                    ApiType.PAT_BPC_SURVEY_SUMMARY
                                )

                                if (!RetryHelper.stepListApiVillageId.contains(village.id)) RetryHelper.stepListApiVillageId.add(
                                    village.id
                                )
                            }
                            onCatchError(ex, ApiType.PAT_BPC_SURVEY_SUMMARY)
                        }

                        prefRepo.savePref(PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_ + village.id, true)
                        prefRepo.savePref(PREF_BPC_DIDI_LIST_SYNCED_FOR_VILLAGE_ + village.id, true)
                    }

                } catch (ex: Exception) {
                    NudgeLogger.e(
                        "VillageSelectionViewModel",
                        "fetchDataForBpc -> onCatchError",
                        ex
                    )
                    onCatchError(ex, ApiType.FETCH_ALL_DATA)
                } finally {
                    prefRepo.savePref(LAST_UPDATE_TIME, System.currentTimeMillis())
                    startRetryIfAny()
                    withContext(Dispatchers.Main) {
                        delay(250)
//                        NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc finally -> viewModel.showLoader.value = false")
//                        showLoader.value = false
                    }
                }
            }.await()
            delay(250)
            NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc after await -> viewModel.showLoader.value = false")
            showLoader.value = false
        }
    }
    private fun fetchCastList() {
        showLoader.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val languageList = languageListDao.getAllLanguages()
            languageList.forEach { language ->
                val localCasteList = casteListDao.getAllCasteForLanguage(language.id)
                if (localCasteList.isEmpty()) {
                    try {
                        val casteResponse = apiService.getCasteList(language.id)
                        if (casteResponse.status.equals(SUCCESS, true)) {
                            casteResponse.data?.let { casteList ->
                                casteList.forEach { casteEntity ->
                                    casteEntity.languageId = language.id
                                }
                                casteListDao.insertAll(casteList)
                                AnalyticsHelper.logEvent(
                                    Events.CASTE_LIST_WRITE,
                                    mapOf(
                                        EventParams.LANGUAGE_ID to language.id,
                                        EventParams.CASTE_LIST to "$casteList",
                                        EventParams.FROM_SCREEN to "VillageSelectionScreen"
                                    )
                                )
                            }
                        } else {
                            val ex = ApiResponseFailException(casteResponse.message)
                            if (!retryApiList.contains(ApiType.CAST_LIST_API)) {
                                retryApiList.add(ApiType.CAST_LIST_API)
                                crpPatQuestionApiLanguageId.add(language.id)
                            }
                            onCatchError(ex, ApiType.CAST_LIST_API)
                        }
                    } catch (ex: Exception) {
                        if (!retryApiList.contains(ApiType.CAST_LIST_API)) {
                            retryApiList.add(ApiType.CAST_LIST_API)
                            crpPatQuestionApiLanguageId.add(language.id)
                        }
                        onCatchError(ex, ApiType.CAST_LIST_API)
                    } finally {
                        if (retryApiList.contains(ApiType.CAST_LIST_API)) RetryHelper.retryApi(
                            ApiType.CAST_LIST_API
                        )
                    }
                } /*else {
                    withContext(Dispatchers.Main) {
                        delay(250)
                        showLoader.value = false
                    }
                }*/
            }
        }
    }

    fun saveVideosToDb(context: Context) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
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

    private fun getVideoPath(context: Context, videoItemId: Int, fileType: FileType): File {
        return File("${context.getExternalFilesDir(if (fileType == FileType.VIDEO) Environment.DIRECTORY_MOVIES else if (fileType == FileType.IMAGE) Environment.DIRECTORY_DCIM else Environment.DIRECTORY_DOCUMENTS)?.absolutePath}/${videoItemId}.mp4")
    }

    private fun fetchVillageList(context: Context) {
        showLoader.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                withContext(Dispatchers.IO) {
                    val villageList = villageListDao.getAllVillages(prefRepo.getAppLanguageId()?:2)
                    val localStepsList = stepsListDao.getAllSteps()
                    val villageIdList: ArrayList<Int> = arrayListOf()
                    if (localStepsList.isNotEmpty()) {
                        stepsListDao.deleteAllStepsFromDB()
                    }
                    val localAnswerList = answerDao.getAllAnswer()
                    if (localAnswerList.isNotEmpty()) {
                        answerDao.deleteAnswerTable()
                    }
                    val localNumAnswerList = numericAnswerDao.getAllNumericAnswers()
                    if (localNumAnswerList.isNotEmpty()) {
                        numericAnswerDao.deleteNumericTable()
                    }

                    villageList.forEach { village ->
                        villageIdList.add(village.id)
                        launch {
                            stateId.value = village.stateId
                            RetryHelper.stateId = stateId.value
                            try {
                                val response = apiService.getStepsList(village.id)
                                if (response.status.equals(SUCCESS, true)) {
                                    response.data?.let {
                                        if (it.stepList.isNotEmpty()) {
                                            it.stepList.forEach { steps ->
                                                steps.villageId = village.id
                                                steps.isComplete =
                                                    findCompleteValue(steps.status).ordinal
                                                steps.needToPost = false

                                                if(steps.id == 40){
                                                    prefRepo.savePref(
                                                        PREF_TRANSECT_WALK_COMPLETION_DATE_+village.id, steps.localModifiedDate?: System.currentTimeMillis())
                                                }

                                                if(steps.id == 41){
                                                    prefRepo.savePref(
                                                        PREF_SOCIAL_MAPPING_COMPLETION_DATE_+village.id, steps.localModifiedDate?: System.currentTimeMillis())
                                                }

                                                if(steps.id == 46){
                                                    prefRepo.savePref(
                                                        PREF_WEALTH_RANKING_COMPLETION_DATE_+village.id, steps.localModifiedDate?: System.currentTimeMillis())
                                                }

                                                if(steps.id == 43){
                                                    prefRepo.savePref(
                                                        PREF_PAT_COMPLETION_DATE_+village.id, steps.localModifiedDate?: System.currentTimeMillis())
                                                }
                                                if(steps.id == 44){
                                                    prefRepo.savePref(
                                                        PREF_VO_ENDORSEMENT_COMPLETION_DATE_+village.id, steps.localModifiedDate?: System.currentTimeMillis())
                                                }
                                            }
                                            NudgeLogger.d("VillageSelectionViewModel", "it.stepList: ${it.stepList} \n")
                                            stepsListDao.insertAll(it.stepList)
                                            setVoEndorsementCompleteForVillages()
                                        }

                                        prefRepo.savePref(
                                            PREF_PROGRAM_NAME, it.programName
                                        )

                                    }
                                } else {
                                    val ex = ApiResponseFailException(response.message)
                                    if (!retryApiList.contains(ApiType.STEP_LIST_API)) retryApiList.add(
                                        ApiType.STEP_LIST_API
                                    )
                                    RetryHelper.stepListApiVillageId.add(village.id)
                                    onCatchError(ex, ApiType.STEP_LIST_API)
                                }
                            } catch (ex: Exception) {
                                if (ex !is JsonSyntaxException) {
                                    if (!retryApiList.contains(ApiType.STEP_LIST_API)) retryApiList.add(
                                        ApiType.STEP_LIST_API
                                    )
                                    RetryHelper.stepListApiVillageId.add(village.id)
                                }
                                onCatchError(ex, ApiType.STEP_LIST_API)
                            }
                            try {
                                val cohortResponse =
                                    apiService.getCohortFromNetwork(villageId = village.id)
                                if (cohortResponse.status.equals(SUCCESS, true)) {
                                    cohortResponse.data?.let {
                                        if (it.isNotEmpty()) {
                                            for (tola in cohortResponse.data) {
                                                tola.serverId = tola.id
                                            }
                                            tolaDao.insertAll(it)
                                        }
                                    }
                                } else {
                                    val ex = ApiResponseFailException(cohortResponse.message)
                                    if (!retryApiList.contains(ApiType.TOLA_LIST_API)) retryApiList.add(
                                        ApiType.TOLA_LIST_API
                                    )
                                    RetryHelper.stepListApiVillageId.add(village.id)
                                    onCatchError(ex, ApiType.TOLA_LIST_API)
                                }
                            } catch (ex: Exception) {
                                if (ex !is JsonSyntaxException) {
                                    if (!retryApiList.contains(ApiType.TOLA_LIST_API)) retryApiList.add(
                                        ApiType.TOLA_LIST_API
                                    )
                                    RetryHelper.stepListApiVillageId.add(village.id)
                                }
                                onCatchError(ex, ApiType.TOLA_LIST_API)
                            }
                            try {
                                val didiResponse =
                                    apiService.getDidisFromNetwork(villageId = village.id)
                                if (didiResponse.status.equals(SUCCESS, true)) {
                                    didiResponse.data?.let {
                                        if (it.didiList.isNotEmpty()) {
                                            try {
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
                                                            shgFlag = SHGFlag.fromSting(didi.shgFlag ?: SHGFlag.NOT_MARKED.name).value,
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
                                                            ableBodiedFlag = AbleBodiedFlag.fromSting(didi.ableBodiedFlag ?: AbleBodiedFlag.NOT_MARKED.name).value
                                                        )
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

                                        try {
                                            val answerApiResponse =
                                                apiService.fetchPATSurveyToServer(
                                                    listOf(village.id)
                                                )
                                            if (answerApiResponse.status.equals(SUCCESS, true)) {
                                                answerApiResponse.data?.let {
                                                    val answerList: ArrayList<SectionAnswerEntity> =
                                                        arrayListOf()
                                                    val numAnswerList: ArrayList<NumericAnswerEntity> =
                                                        arrayListOf()
                                                    it.forEach { item ->
                                                        if (item.userType.equals(USER_CRP, true)){
                                                        try {

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
                                                                    questionListDao.getQuestionForLanguage(
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
                                                        answerDao.insertAll(answerList)
                                                    }
                                                    if (numAnswerList.isNotEmpty()) {
                                                        numericAnswerDao.insertAll(numAnswerList)
                                                    }
                                                }
                                            } else {
                                                val ex =
                                                    ApiResponseFailException(answerApiResponse.message)
                                                retryApiList.add(ApiType.PAT_CRP_SURVEY_SUMMARY)
                                                onCatchError(ex, ApiType.PAT_CRP_SURVEY_SUMMARY)
                                            }
                                        } catch (ex: Exception) {
                                            if (ex !is JsonSyntaxException) {
                                                retryApiList.add(ApiType.PAT_CRP_SURVEY_SUMMARY)
                                            }
                                            onCatchError(ex, ApiType.PAT_CRP_SURVEY_SUMMARY)
                                        }
                                    }
                                } else {
                                    val ex = ApiResponseFailException(didiResponse.message)
                                    if (!retryApiList.contains(ApiType.DIDI_LIST_API)) retryApiList.add(
                                        ApiType.DIDI_LIST_API
                                    )
                                    RetryHelper.stepListApiVillageId.add(village.id)
                                    onCatchError(ex, ApiType.DIDI_LIST_API)
                                }
                            } catch (ex: Exception) {
                                if (ex !is JsonSyntaxException) {
                                    if (!retryApiList.contains(ApiType.DIDI_LIST_API)) retryApiList.add(
                                        ApiType.DIDI_LIST_API
                                    )
                                    RetryHelper.stepListApiVillageId.add(village.id)
                                }
                                onCatchError(ex, ApiType.DIDI_LIST_API)
                            }
                            try {
                                val didiRankingResponse = apiService.getDidisWithRankingFromNetwork(
                                    villageId = village.id, "Category", StepResultTypeRequest(
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
                                    if (!retryApiList.contains(ApiType.DIDI_RANKING_API)) retryApiList.add(
                                        ApiType.DIDI_RANKING_API
                                    )
                                    RetryHelper.stepListApiVillageId.add(village.id)
                                    onCatchError(ex, ApiType.DIDI_RANKING_API)
                                }
                            } catch (ex: Exception) {
                                if (ex !is JsonSyntaxException) {
                                    if (!retryApiList.contains(ApiType.DIDI_RANKING_API)) retryApiList.add(
                                        ApiType.DIDI_RANKING_API
                                    )
                                    RetryHelper.stepListApiVillageId.add(village.id)
                                }
                                onCatchError(ex, ApiType.DIDI_RANKING_API)
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                NudgeLogger.e("VillageSelectionViewModel", "fetchVillageList -> onCatchError", ex)
                onCatchError(ex, ApiType.FETCH_ALL_DATA)
            } finally {
                prefRepo.savePref(LAST_UPDATE_TIME, System.currentTimeMillis())
                startRetryIfAny()
                withContext(Dispatchers.Main) {
                    delay(250)
                    NudgeLogger.d("VillageSelectionScreen", "fetchVillageList finally -> viewModel.showLoader.value = false")
                    showLoader.value = false
                }
            }
        }
    }


    fun updateSelectedVillage() {
        prefRepo.saveSelectedVillage(villageList.value[villageSelected.value])
    }

    private fun createMultiLanguageVillageRequest(localLanguageList: List<LanguageEntity>):String {
        var requestString:StringBuilder= StringBuilder()
        var request:String= "2"
        if(localLanguageList.isNotEmpty()){
            localLanguageList.forEach {
                requestString.append("${it.id}-")
            }
        }else request = "2"
        if(requestString.contains("-")){
           request= requestString.substring(0,requestString.length-1)
        }
        multiVillageRequest.value=request
        return request
    }

    private fun fetchUserDetails(apiSuccess: (success: Boolean) -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val localVillageList = villageListDao.getAllVillages(prefRepo.getAppLanguageId()?:2)
                val localLanguageList = languageListDao.getAllLanguages()
               val villageReq= createMultiLanguageVillageRequest(localLanguageList)
                if (!localVillageList.isNullOrEmpty()) {
                    _villagList.value = localVillageList
                    setVoEndorsementCompleteForVillages()
                    apiSuccess(true)
                } else {
                    val response = apiService.userAndVillageListAPI(villageReq)
                    withContext(Dispatchers.IO) {
                        if (response.status.equals(SUCCESS, true)) {
                            response.data?.let {
                                prefRepo.savePref(PREF_KEY_USER_NAME, it.username ?: "")
                                prefRepo.savePref(PREF_KEY_NAME, it.name ?: "")
                                prefRepo.savePref(PREF_KEY_EMAIL, it.email ?: "")
                                prefRepo.savePref(PREF_KEY_IDENTITY_NUMBER, it.identityNumber ?: "")
                                prefRepo.savePref(PREF_KEY_PROFILE_IMAGE, it.profileImage ?: "")
                                prefRepo.savePref(PREF_KEY_ROLE_NAME, it.roleName ?: "")
                                prefRepo.savePref(PREF_KEY_TYPE_NAME, it.typeName ?: "")
                                villageListDao.insertAll(it.villageList ?: listOf())
                                stateId.value= it.villageList?.get(0)?.stateId?:1
                                val localVillageList = villageListDao.getAllVillages(prefRepo.getAppLanguageId()?:2)
                                if (localVillageList.isNotEmpty()) {
                                    _villagList.emit(localVillageList)
                                }
                                else{
                                    _villagList.emit(villageListDao.getAllVillages(DEFAULT_LANGUAGE_ID))
                                }
                                if (it.typeName.equals(BPC_USER_TYPE, true)) {
                                    prefRepo.setIsUserBPC(true)
                                } else {
                                    prefRepo.setIsUserBPC(false)
                                }
                                apiSuccess(true)
                            }

                            if (response.data == null) {
                                apiSuccess(false)
                            }

                            if(!response.lastSyncTime.isNullOrEmpty()){
                                updateLastSyncTime(prefRepo,response.lastSyncTime)
                            }

                            Log.d("TAG", "fetchUserDetails: ${prefRepo.getPref(LAST_SYNC_TIME,0L)}")

                        } else if (response.status.equals(FAIL, true)) {
                            withContext(Dispatchers.Main) {
                                NudgeLogger.d("VillageSelectionScreen", "fetchUserDetails response.status.equals(FAIL, true) -> viewModel.showLoader.value = false")
                                showLoader.value = false
                            }
                            apiSuccess(false)
                            NudgeLogger.d("VillageSelectionViewModel", "fetchUserDetails -> response.status: ${response.status}, message: ${response.message}")
                        } else {
                            NudgeLogger.d("VillageSelectionViewModel", "fetchUserDetails -> Error: ${response.message}")
                            onError(tag = "VillageSelectionViewModel", "Error : ${response.message}")
                            withContext(Dispatchers.Main) {
                                NudgeLogger.d("VillageSelectionScreen", "fetchUserDetails else 1 -> viewModel.showLoader.value = false")
                                showLoader.value = false
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
                    showLoader.value = false
                }
                apiSuccess(false)
                onCatchError(ex, ApiType.VILLAGE_LIST_API)
                if (ex is HttpException) {
                    if (ex.response()?.code() == RESPONSE_CODE_UNAUTHORIZED || ex.response()
                            ?.code() == RESPONSE_CODE_CONFLICT
                    ) {
                        retryApiList.add(ApiType.VILLAGE_LIST_API)
                        withContext(Dispatchers.Main) {
                            RetryHelper.tokenExpired.value = true
                        }
                    }
                }
            }
        }
    }

    fun setVoEndorsementCompleteForVillages() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                villageList.value.forEach { village ->
                    val stepList = stepsListDao.getAllStepsForVillage(village.id)
                    isVoEndorsementComplete.value[village.id] = (stepList.sortedBy { it.orderNumber }[4].isComplete == StepStatus.COMPLETED.ordinal)
                }
            } catch (ex: Exception) {
                Log.d("TAG", "setVoEndorsementCompleteForVillages: exception -> $ex")
            }
        }
    }


    fun saveVillageListAfterTokenRefresh(villageList: List<VillageEntity>) {
        _villagList.value = villageList
        RetryHelper.retryApiList.remove(ApiType.VILLAGE_LIST_API)
    }

    override fun onServerError(error: ErrorModel?) {
        NudgeLogger.d("VillageSelectionScreen", "onServerError 1 -> viewModel.showLoader.value = false")
        showLoader.value = false
//        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
//            _villagList.value = villageListDao.getAllVillages()
//        }
        job = CoroutineScope(Dispatchers.Main).launch {
            networkErrorMessage.value = error?.message.toString()
        }
        Log.e("server error", "villege screen")
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        NudgeLogger.d("VillageSelectionScreen", "onServerError 2 -> viewModel.showLoader.value = false")
        showLoader.value = false
        job = CoroutineScope(Dispatchers.Main).launch {
            networkErrorMessage.value = errorModel?.message.toString()
        }
    }

    fun startRetryIfAny() {
        NudgeLogger.d("startRetryIfAny: ", "shouldRetyCalled")
        RetryHelper.retryApiList.forEach { apiType ->
            RetryHelper.retryApi(apiType)
        }
    }

     fun downloadImageItem(context: Context, image: String) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
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

    private fun downloadAuthorizedImageItem(id:Int, image: String, prefRepo: PrefRepo) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
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
    private fun downloadAuthorizedImageItemForNonSelectedDidi(id:Int, image: String, prefRepo: PrefRepo) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
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
                            bpcNonSelectedDidiDao.updateImageLocalPath(id,imageFile.absolutePath)
                        }, onDownloadFailed = {
                            NudgeLogger.d("VillageSelectorViewModel", "downloadAuthorizedImageItemForNonSelectedDidi -> onDownloadFailed")
                        })
                    }
                } else {
                    bpcNonSelectedDidiDao.updateImageLocalPath(id,imageFile.absolutePath)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                NudgeLogger.e("VillageSelectorViewModel", "downloadAuthorizedImageItemForNonSelectedDidi -> downloadItem exception", ex)
            }
        }
    }
    private fun downloadAuthorizedImageItemForSelectedDidi(id:Int, image: String, prefRepo: PrefRepo) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
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
                            bpcSelectedDidiDao.updateImageLocalPath(id,imageFile.absolutePath)
                        }, onDownloadFailed = {
                            NudgeLogger.d("VillageSelectorViewModel", "downloadAuthorizedImageItemForSelectedDidi -> onDownloadFailed")
                        })
                    }
                } else {
                    bpcSelectedDidiDao.updateImageLocalPath(id,imageFile.absolutePath)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                NudgeLogger.e("VillageSelectorViewModel", "downloadAuthorizedImageItemForSelectedDidi -> downloadItem exception", ex)
            }
        }
    }

    fun refreshBpcData(context: Context) {
        showLoader.value = true
        villageSelectionRepository.refreshBpcData(prefRepo = prefRepo, object : NetworkCallbackListener{
            override fun onSuccess() {
                showLoader.value = false
            }

            override fun onFailed() {
                showLoader.value = false
            }
        })
    }

    fun refreshCrpData() {

    }

}