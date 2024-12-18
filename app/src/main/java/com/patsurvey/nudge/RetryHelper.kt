package com.patsurvey.nudge

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.nudge.core.DEFAULT_LANGUAGE_ID
import com.nudge.core.database.dao.CasteListDao
import com.nudge.core.database.entities.CasteEntity
import com.nudge.core.getDefaultBackUpFileName
import com.nudge.core.getDefaultImageBackUpFileName
import com.nudge.core.preference.CoreSharedPrefs
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.BpcSummaryEntity
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.LanguageEntity
import com.patsurvey.nudge.database.NumericAnswerEntity
import com.patsurvey.nudge.database.PoorDidiEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.BpcSummaryDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.LanguageListDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.PoorDidiListDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.request.GetQuestionListRequest
import com.patsurvey.nudge.model.request.LoginRequest
import com.patsurvey.nudge.model.request.OtpRequest
import com.patsurvey.nudge.model.request.StepResultTypeRequest
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.AbleBodiedFlag
import com.patsurvey.nudge.utils.ApiResponseFailException
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.COMMON_ERROR_MSG
import com.patsurvey.nudge.utils.COMPLETED_STRING
import com.patsurvey.nudge.utils.DIDI_REJECTED
import com.patsurvey.nudge.utils.DOUBLE_ZERO
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.FAIL
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PAT_SURVEY_CONSTANT
import com.patsurvey.nudge.utils.PREF_BPC_PAT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_KEY_EMAIL
import com.patsurvey.nudge.utils.PREF_KEY_IDENTITY_NUMBER
import com.patsurvey.nudge.utils.PREF_KEY_NAME
import com.patsurvey.nudge.utils.PREF_KEY_PROFILE_IMAGE
import com.patsurvey.nudge.utils.PREF_KEY_ROLE_NAME
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_NAME
import com.patsurvey.nudge.utils.PREF_KEY_USER_NAME
import com.patsurvey.nudge.utils.PREF_PAT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_PROGRAM_NAME
import com.patsurvey.nudge.utils.PREF_SOCIAL_MAPPING_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_TRANSECT_WALK_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_VO_ENDORSEMENT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_WEALTH_RANKING_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.QUESTION_FLAG_WEIGHT
import com.patsurvey.nudge.utils.QuestionType
import com.patsurvey.nudge.utils.RESPONSE_CODE_500
import com.patsurvey.nudge.utils.RESPONSE_CODE_BAD_GATEWAY
import com.patsurvey.nudge.utils.RESPONSE_CODE_CONFLICT
import com.patsurvey.nudge.utils.RESPONSE_CODE_DEACTIVATED
import com.patsurvey.nudge.utils.RESPONSE_CODE_NETWORK_ERROR
import com.patsurvey.nudge.utils.RESPONSE_CODE_NOT_FOUND
import com.patsurvey.nudge.utils.RESPONSE_CODE_NO_DATA
import com.patsurvey.nudge.utils.RESPONSE_CODE_SERVICE_TEMPORARY_UNAVAILABLE
import com.patsurvey.nudge.utils.RESPONSE_CODE_TIMEOUT
import com.patsurvey.nudge.utils.RESPONSE_CODE_UNAUTHORIZED
import com.patsurvey.nudge.utils.ResultType
import com.patsurvey.nudge.utils.SHGFlag
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.StepType
import com.patsurvey.nudge.utils.TIMEOUT_ERROR_MSG
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.UNAUTHORISED_MESSAGE
import com.patsurvey.nudge.utils.UNREACHABLE_ERROR_MSG
import com.patsurvey.nudge.utils.WealthRank
import com.patsurvey.nudge.utils.findCompleteValue
import com.patsurvey.nudge.utils.formatRatio
import com.patsurvey.nudge.utils.stringToDouble
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

object RetryHelper {

    private const val MAX_RETRY_COUNT = 2

    var retryApiList = mutableListOf<ApiType>()
    var crpPatQuestionApiLanguageId = mutableSetOf<Int>()
    var stepListApiVillageId = mutableSetOf<Int>()
    var stateId = -1

    var retryCount = mutableMapOf<ApiType, Int>()

    val autoReadOtp = mutableStateOf("")

    private var prefRepo: PrefRepo? = null
    private var apiService: ApiService? = null
    private var tolaDao: TolaDao? = null
    private var stepsListDao: StepsListDao? = null
    private var villageListDao: VillageListDao? = null
    private var didiDao: DidiDao? = null
    private var answerDao: AnswerDao? = null
    private var numericAnswerDao: NumericAnswerDao? = null
    private var questionDao: QuestionListDao? = null
    private var castListDao: CasteListDao? = null
    private var bpcSummaryDao: BpcSummaryDao? = null
    private var poorDidiListDao: PoorDidiListDao? = null
    private var languageDao: LanguageListDao? = null

    val tokenExpired = mutableStateOf(false)

    fun init(
        prefRepo: PrefRepo,
        apiService: ApiService,
        tolaDao: TolaDao,
        stepsListDao: StepsListDao,
        villageListDao: VillageListDao,
        didiDao: DidiDao,
        answerDao: AnswerDao,
        numericAnswerDao: NumericAnswerDao,
        questionDao: QuestionListDao,
        castListDao: CasteListDao,
        bpcSummaryDao: BpcSummaryDao,
        poorDidiListDao: PoorDidiListDao,
        languageListDao: LanguageListDao
    ) {
        setPrefRepo(prefRepo)
        setApiServices(apiService)
        setTolaDao(tolaDao)
        setDidiDao(didiDao)
        setStepListDao(stepsListDao)
        setVillageListDao(villageListDao)
        setAnswerDao(answerDao)
        setNumericAnswerDao(numericAnswerDao)
        setQuestionDao(questionDao)
        setCastListDao(castListDao)
        setBpcSummaryDao(bpcSummaryDao)
        setPoorDidiListDao(poorDidiListDao)
    }

    fun cleanUp() {
        /*var retryApiListToSave = ""
        var villageListToSave = ""
        var crpPatQuestionLangIdToSave = ""
        if (retryApiList.isNotEmpty()) {
            retryApiList.forEach {
                retryApiListToSave = retryApiListToSave + "|" + it.name
            }
            prefRepo?.savePref(PREF_RETRY_API_LIST, retryApiListToSave)
        }
        if (stepListApiVillageId.isNotEmpty()) {
            stepListApiVillageId.forEach {
                villageListToSave = "$villageListToSave|$it"
            }
            prefRepo?.savePref(PREF_VILLAGE_ID_TO_RETRY, villageListToSave)
        }
        if (crpPatQuestionApiLanguageId.isNotEmpty()) {
            crpPatQuestionApiLanguageId.forEach {
                crpPatQuestionLangIdToSave = "$crpPatQuestionLangIdToSave|$it"
            }
            prefRepo?.savePref(PREF_LANGUAGE_ID_TO_RETRY, crpPatQuestionLangIdToSave)
        }*/

        prefRepo = null
        apiService = null
        tolaDao = null
        stepsListDao = null
        villageListDao = null
        didiDao = null
        answerDao = null
        numericAnswerDao = null
        questionDao = null
        castListDao = null
        poorDidiListDao = null
        languageDao = null
    }

    private fun setPrefRepo(mPrefRepo: PrefRepo) {
        prefRepo = mPrefRepo
    }

    private fun setApiServices(mApiService: ApiService) {
        apiService = mApiService
    }

    private fun setTolaDao(mTolaDao: TolaDao) {
        tolaDao = mTolaDao
    }

    private fun setStepListDao(mStepsListDao: StepsListDao) {
        stepsListDao = mStepsListDao
    }

    private fun setVillageListDao(mVillageListDao: VillageListDao) {
        villageListDao = mVillageListDao
    }

    private fun setDidiDao(mDidiDao: DidiDao) {
        didiDao = mDidiDao
    }

    private fun setQuestionDao(mQuestionListDao: QuestionListDao) {
        questionDao = mQuestionListDao
    }

    private fun setAnswerDao(mAnswerDao: AnswerDao) {
        answerDao = mAnswerDao
    }

    private fun setNumericAnswerDao(mNumericAnswerDao: NumericAnswerDao) {
        numericAnswerDao = mNumericAnswerDao
    }

    private fun setCastListDao(mCasteListDao: CasteListDao) {
        castListDao = mCasteListDao
    }

    private fun setBpcSummaryDao(mBpcSummaryDao: BpcSummaryDao) {
        bpcSummaryDao = mBpcSummaryDao
    }

    private fun setLanuageDao(mLanguageListDao: LanguageListDao) {
        languageDao = mLanguageListDao
    }

    private fun setPoorDidiListDao(mPoorDidiListDao: PoorDidiListDao) {
        poorDidiListDao = mPoorDidiListDao
    }

    fun retryApi(apiType: ApiType) {
        Log.d("retryApi: ", "retryApi: ${apiType.name}")
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val initialCount = if (retryCount.containsKey(apiType)) retryCount[apiType] else 0
            if ((initialCount ?: 0) <= MAX_RETRY_COUNT) {
                when (apiType) {
                    ApiType.STEP_LIST_API -> {
                        stepListApiVillageId.forEach { id ->
                            try {
                                val localStepList = stepsListDao?.getAllStepsForVillage(id)
                                if (localStepList?.isNullOrEmpty() == true) {
                                    val response = apiService?.getStepsList(id)
                                    if (response?.status.equals(SUCCESS, true)) {
                                        response?.data?.let {

                                            it.stepList.forEach { steps ->
                                                steps.villageId = id
                                                steps.isComplete =
                                                    findCompleteValue(steps.status).ordinal

                                                if (steps.id == 40) {
                                                    prefRepo?.savePref(
                                                        PREF_TRANSECT_WALK_COMPLETION_DATE_ + id,
                                                        steps.localModifiedDate
                                                            ?: System.currentTimeMillis()
                                                    )
                                                }

                                                if (steps.id == 41) {
                                                    prefRepo?.savePref(
                                                        PREF_SOCIAL_MAPPING_COMPLETION_DATE_ + id,
                                                        steps.localModifiedDate
                                                            ?: System.currentTimeMillis()
                                                    )
                                                }

                                                if (steps.id == 46) {
                                                    prefRepo?.savePref(
                                                        PREF_WEALTH_RANKING_COMPLETION_DATE_ + id,
                                                        steps.localModifiedDate
                                                            ?: System.currentTimeMillis()
                                                    )
                                                }

                                                if (steps.id == 43) {
                                                    prefRepo?.savePref(
                                                        PREF_PAT_COMPLETION_DATE_ + id,
                                                        steps.localModifiedDate
                                                            ?: System.currentTimeMillis()
                                                    )
                                                }
                                                if (steps.id == 44) {
                                                    prefRepo?.savePref(
                                                        PREF_VO_ENDORSEMENT_COMPLETION_DATE_ + id,
                                                        steps.localModifiedDate
                                                            ?: System.currentTimeMillis()
                                                    )
                                                }

                                                if (steps.id == 45) {
                                                    prefRepo?.savePref(
                                                        PREF_BPC_PAT_COMPLETION_DATE_ + id,
                                                        steps.localModifiedDate
                                                            ?: System.currentTimeMillis()
                                                    )
                                                }

                                            }
                                            stepsListDao?.insertAll(it.stepList)
                                            prefRepo?.savePref(
                                                PREF_PROGRAM_NAME,
                                                it.programName
                                            )
                                        }
                                        retryApiList.remove(ApiType.STEP_LIST_API)
                                    } else {
                                        val ex = ApiResponseFailException(response?.message!!)
                                        onCatchError(ex, ApiType.STEP_LIST_API)
                                    }
                                }
                            } catch (ex: Exception) {
                                onCatchError(ex, ApiType.STEP_LIST_API)
                            }
                        }
                    }

                    ApiType.TOLA_LIST_API -> {
                        stepListApiVillageId.forEach { id ->
                            try {
                                val localCohortList = tolaDao?.getAllTolasForVillage(id) ?: listOf()
                                if (localCohortList.isEmpty()) {
                                    val cohortResponse =
                                        apiService?.getCohortFromNetwork(villageId = id)
                                    if (cohortResponse?.status.equals(SUCCESS, true)) {
                                        cohortResponse?.data?.let {
                                            tolaDao?.insertAll(it)
                                        }
                                        retryApiList.remove(ApiType.TOLA_LIST_API)
                                    } else {
                                        val ex = ApiResponseFailException(cohortResponse?.message!!)
                                        onCatchError(ex, ApiType.TOLA_LIST_API)
                                    }
                                }
                            } catch (ex: Exception) {
                                onCatchError(ex, ApiType.TOLA_LIST_API)
                            }
                        }
                    }

                    ApiType.DIDI_LIST_API -> {
                        stepListApiVillageId.forEach { id ->
                            try {
                                val localDidiList = didiDao?.getAllDidisForVillage(id) ?: listOf()
                                if (localDidiList.isEmpty()) {
                                    val didiResponse =
                                        apiService?.getDidisFromNetwork(villageId = id)
                                    if (didiResponse?.status.equals(SUCCESS, true)) {
                                        didiResponse?.data?.let {
                                            it.didiList.forEach { didi ->
                                                var tolaName = BLANK_STRING
                                                var casteName = BLANK_STRING
                                                val singleTola =
                                                    tolaDao?.fetchSingleTola(didi.cohortId)
                                                val singleCaste = castListDao?.getCaste(
                                                    didi.castId,
                                                    prefRepo?.getAppLanguageId() ?: 2
                                                )
                                                singleTola?.let {
                                                    tolaName = it.name
                                                }
                                                singleCaste?.let {
                                                    casteName = it.casteName
                                                }
                                                if (singleTola != null) {
                                                    val wealthRanking =
                                                        if (didi.beneficiaryProcessStatus.map { it.name }
                                                                .contains(StepType.WEALTH_RANKING.name))
                                                            didi.beneficiaryProcessStatus[didi.beneficiaryProcessStatus.map { process -> process.name }
                                                                .indexOf(StepType.WEALTH_RANKING.name)].status
                                                        else
                                                            WealthRank.NOT_RANKED.rank
                                                    val patSurveyStatus =
                                                        if (didi.beneficiaryProcessStatus.map { it.name }
                                                                .contains(StepType.PAT_SURVEY.name))
                                                            PatSurveyStatus.toInt(didi.beneficiaryProcessStatus[didi.beneficiaryProcessStatus.map { process -> process.name }
                                                                .indexOf(StepType.PAT_SURVEY.name)].status)
                                                        else
                                                            PatSurveyStatus.NOT_STARTED.ordinal
                                                    val voEndorsementStatus =
                                                        if (didi.beneficiaryProcessStatus.map { it.name }
                                                                .contains(StepType.VO_ENDROSEMENT.name))
                                                            DidiEndorsementStatus.toInt(didi.beneficiaryProcessStatus[didi.beneficiaryProcessStatus.map { process -> process.name }
                                                                .indexOf(StepType.PAT_SURVEY.name)].status)
                                                        else
                                                            DidiEndorsementStatus.NOT_STARTED.ordinal
                                                    didiDao?.insertDidi(
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
                                                            villageId = id,
                                                            cohortName = tolaName,
                                                            needsToPost = false,
                                                            wealth_ranking = wealthRanking,
                                                            patSurveyStatus = patSurveyStatus,
                                                            voEndorsementStatus = voEndorsementStatus,
                                                            needsToPostRanking = false,
                                                            createdDate = didi.createdDate,
                                                            modifiedDate = didi.modifiedDate,
                                                            beneficiaryProcessStatus = didi.beneficiaryProcessStatus,
                                                            shgFlag = SHGFlag.NOT_MARKED.value,
                                                            transactionId = "",
                                                            ableBodiedFlag = AbleBodiedFlag.NOT_MARKED.value
                                                        )
                                                    )
                                                }
                                            }

                                        }
                                    } else {
                                        val ex = ApiResponseFailException(didiResponse?.message!!)
                                        onCatchError(ex, ApiType.DIDI_LIST_API)
                                    }
                                }
                            } catch (ex: Exception) {
                                onCatchError(ex, ApiType.DIDI_LIST_API)
                            }
                        }
                    }

                    ApiType.DIDI_RANKING_API -> {
                        stepListApiVillageId.forEach { id ->
                            try {
                                val didiRankingResponse =
                                    apiService?.getDidisWithRankingFromNetwork(
                                        villageId = id, "Category",
                                        StepResultTypeRequest(
                                            StepType.WEALTH_RANKING.name,
                                            ResultType.ALL.name
                                        )
                                    )
                                if (didiRankingResponse?.status.equals(SUCCESS, true)) {
                                    didiRankingResponse?.data?.let { didiRank ->
                                        didiRank.beneficiaryList?.richDidi?.forEach { richDidi ->
                                            richDidi?.id?.let { didiId ->
                                                didiDao?.updateDidiRank(
                                                    didiId,
                                                    WealthRank.RICH.rank
                                                )
                                            }
                                        }
                                        didiRank.beneficiaryList?.mediumDidi?.forEach { mediumDidi ->
                                            mediumDidi?.id?.let { didiId ->
                                                didiDao?.updateDidiRank(
                                                    didiId,
                                                    WealthRank.MEDIUM.rank
                                                )
                                            }
                                        }
                                        didiRank.beneficiaryList?.poorDidi?.forEach { poorDidi ->
                                            poorDidi?.id?.let { didiId ->
                                                didiDao?.updateDidiRank(
                                                    didiId,
                                                    WealthRank.POOR.rank
                                                )
                                            }
                                        }
                                    }
                                    retryApiList.remove(ApiType.DIDI_RANKING_API)
                                } else {
                                    val ex = ApiResponseFailException(
                                        didiRankingResponse?.message ?: "Didi Ranking Api Failed"
                                    )
                                    onCatchError(ex, ApiType.DIDI_RANKING_API)
                                }
                            } catch (ex: Exception) {
                                onCatchError(ex, ApiType.DIDI_RANKING_API)
                            }
                        }
                    }

                    ApiType.PAT_CRP_QUESTION_API -> {
                        crpPatQuestionApiLanguageId.forEach { id ->
                            try {
                                val localQuestionList = questionDao?.getAllQuestionsForLanguage(id) ?: listOf()
                                if (localQuestionList.isEmpty()) {
                                    val quesListResponse = apiService?.fetchQuestionListFromServer(
                                        GetQuestionListRequest(
                                            id,
                                            stateId,
                                            PAT_SURVEY_CONSTANT
                                        )
                                    )
                                    if (quesListResponse?.status.equals(SUCCESS, true)) {
                                        quesListResponse?.data?.let { questionList ->
                                            questionList.listOfQuestionSectionList?.forEach { list ->
                                                list?.questionList?.forEach { question ->
                                                    question?.sectionOrderNumber = list.orderNumber
                                                    question?.actionType = list.actionType
                                                    question?.languageId = id
                                                    question?.surveyId = questionList.surveyId
                                                    question?.thresholdScore =
                                                        questionList.thresholdScore
                                                    question?.surveyPassingMark =
                                                        questionList.surveyPassingMark
                                                }
                                                list?.questionList?.let {
                                                    questionDao?.insertAll(it as List<QuestionEntity>)
                                                }
                                            }
                                        }
                                        retryApiList.remove(ApiType.PAT_CRP_QUESTION_API)
                                    } else {
                                        val ex =
                                            ApiResponseFailException(quesListResponse?.message!!)
                                        onCatchError(ex, ApiType.PAT_CRP_QUESTION_API)
                                    }
                                }
                            } catch (ex: Exception) {
                                onCatchError(ex, ApiType.PAT_CRP_QUESTION_API)
                            }
                        }
                    }

                    ApiType.PAT_CRP_SURVEY_SUMMARY -> {
                        try {
                            val villageIds = mutableListOf<Int>()
                            stepListApiVillageId.forEach {
                                villageIds.add(it)
                            }
                            val answerApiResponse =
                                apiService?.fetchPATSurveyToServer(villageIds)
                            if (answerApiResponse?.status.equals(SUCCESS, true)) {
                                answerApiResponse?.data?.let {
                                    val answerList: ArrayList<SectionAnswerEntity> = arrayListOf()
                                    val numAnswerList: ArrayList<NumericAnswerEntity> =
                                        arrayListOf()
                                    it.forEach { item ->
                                        didiDao?.updatePATProgressStatus(
                                            patSurveyStatus = item.patSurveyStatus ?: 0,
                                            section1Status = item.section1Status ?: 0,
                                            section2Status = item.section2Status ?: 0,
                                            didiId = item.beneficiaryId ?: 0,
                                            shgFlag = item.shgFlag ?:-1,
                                            patExclusionStatus = item.patExclusionStatus ?: 0
                                        )
                                        if (item?.answers?.isNotEmpty() == true) {
                                            item?.answers?.forEach { answersItem ->

                                                val quesDetails =
                                                    questionDao?.getQuestionForLanguage(
                                                        answersItem?.questionId
                                                            ?: 0,
                                                        prefRepo?.getAppLanguageId()
                                                            ?: 2
                                                    )
                                                if (answersItem?.questionType?.equals(QuestionType.Numeric_Field.name) == true) {
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
                                                            weight = 0,
                                                            summary = answersItem?.summary,
                                                            optionValue = if (answersItem?.options?.isNotEmpty() == true) (answersItem?.options?.get(
                                                                0
                                                            )?.optionValue) else 0,
                                                            totalAssetAmount = if(quesDetails?.questionFlag.equals(
                                                                    QUESTION_FLAG_WEIGHT)) answersItem?.totalWeight?.toDouble() else stringToDouble(
                                                                formatRatio(answersItem?.ratio?: DOUBLE_ZERO)
                                                            ) ,
                                                            needsToPost = false,
                                                            answerValue = (if(quesDetails?.questionFlag.equals(
                                                                    QUESTION_FLAG_WEIGHT)) answersItem?.totalWeight?.toDouble() else stringToDouble(
                                                                formatRatio(answersItem?.ratio?: DOUBLE_ZERO)
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
                                                                    count = optionItem?.count ?: 0,
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
                                                            weight = 0,
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

                                    if (answerList.isNotEmpty()) {
                                        answerList.forEach { sectionAnswerEntity ->
                                            val localAnswersForDidi = answerDao?.isQuestionAnswered(sectionAnswerEntity.didiId, sectionAnswerEntity.questionId) ?: 0
                                            if (localAnswersForDidi == 0) {
                                                answerDao?.insertAnswer(sectionAnswerEntity)
                                            }

                                        }
//                                        answerDao?.insertAll(answerList)
                                    }
                                    if (numAnswerList.isNotEmpty()) {
                                        numAnswerList.forEach { numericAnswerEntity ->
                                            val localNumericAnswer = numericAnswerDao?.isNumericQuestionAnswered(numericAnswerEntity.questionId, numericAnswerEntity.optionId, numericAnswerEntity.didiId)
                                            if (localNumericAnswer == 0) {
                                                numericAnswerDao?.insertNumericOption(
                                                    numericAnswerEntity
                                                )
                                            }

                                        }
//                                        numericAnswerDao?.insertAll(numAnswerList)
                                    }
                                }
                            } else {
                                val ex = ApiResponseFailException(answerApiResponse?.message!!)
                                onCatchError(ex, ApiType.PAT_CRP_SURVEY_SUMMARY)
                            }
                        } catch (ex: Exception) {
                            onCatchError(ex, ApiType.PAT_CRP_SURVEY_SUMMARY)
                        }
                    }

                    ApiType.BPC_SUMMARY_API -> {
                        stepListApiVillageId?.forEach { villageId ->
                            try {
                                val bpcSummaryResponse =
                                    apiService?.getBpcSummary(villageId = villageId)
                                if (bpcSummaryResponse?.status.equals(SUCCESS, true)) {
                                    bpcSummaryResponse?.data?.let {
                                        val bpcSummary = BpcSummaryEntity(
                                            cohortCount = it.cohortCount,
                                            mobilisedCount = it.mobilisedCount,
                                            poorDidiCount = it.poorDidiCount,
                                            sentVoEndorsementCount = it.sentVoEndorsementCount,
                                            voEndorsedCount = it.voEndorsedCount,
                                            villageId = villageId
                                        )
                                        bpcSummaryDao?.insert(bpcSummary)
                                    }
                                } else {
                                    //TODO remove mock data
                                    bpcSummaryDao?.insert(
                                        BpcSummaryEntity(
                                            0,
                                            12,
                                            14,
                                            24,
                                            77,
                                            19,
                                            villageId = villageId
                                        )
                                    )

                                    val ex = ApiResponseFailException(bpcSummaryResponse?.message!!)
                                    onCatchError(ex, ApiType.BPC_SUMMARY_API)
                                }
                            } catch (ex: Exception) {

                                onCatchError(ex, ApiType.BPC_SUMMARY_API)
                            }
                        }
                    }

                    ApiType.BPC_DIDI_LIST_API -> {
                        stepListApiVillageId.forEach { villageId ->
                            try {
                                val didiResponse =
                                    apiService?.getDidiForBpcFromNetwork(villageId = villageId)
                                if (didiResponse?.status.equals(SUCCESS, true)) {
                                    didiResponse?.data?.let { beneficiaryResponse ->
                                        beneficiaryResponse.forEach {
                                           /* it.selected.forEach { didi ->
                                                var tolaName = BLANK_STRING
                                                var casteName = BLANK_STRING
//                                            val singleTola = tolaDao.fetchSingleTola(didi.cohortId)
                                                val singleCaste = castListDao?.getCaste(didi.castId,prefRepo?.getAppLanguageId()?:2)
//                                            singleTola?.let {
//                                                tolaName = it.name
//                                            }
                                                singleCaste?.let {
                                                    casteName = it.casteName
                                                }
//                                             if (singleTola != null) {
                                                val wealthRanking =
                                                    if (didi.beneficiaryProcessStatus.map { it.name }
                                                            .contains(StepType.WEALTH_RANKING.name))
                                                        didi.beneficiaryProcessStatus[didi.beneficiaryProcessStatus.map { process -> process.name }
                                                            .indexOf(StepType.WEALTH_RANKING.name)].status
                                                    else
                                                        WealthRank.NOT_RANKED.rank
                                                val patSurveyStatus =
                                                    if (didi.beneficiaryProcessStatus.map { it.name }
                                                            .contains(StepType.PAT_SURVEY.name))
                                                        PatSurveyStatus.toInt(didi.beneficiaryProcessStatus[didi.beneficiaryProcessStatus.map { process -> process.name }
                                                            .indexOf(StepType.PAT_SURVEY.name)].status)
                                                    else
                                                        PatSurveyStatus.NOT_STARTED.ordinal
                                                val voEndorsementStatus =
                                                    if (didi.beneficiaryProcessStatus.map { it.name }
                                                            .contains(StepType.VO_ENDROSEMENT.name))
                                                        DidiEndorsementStatus.toInt(didi.beneficiaryProcessStatus[didi.beneficiaryProcessStatus.map { process -> process.name }
                                                            .indexOf(StepType.PAT_SURVEY.name)].status)
                                                    else
                                                        DidiEndorsementStatus.NOT_STARTED.ordinal

                                                //TODO Create new table
                                                bpcSelectedDidiDao?.insertDidi(
                                                    BpcSelectedDidiEntity(
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
                                                        wealth_ranking = *//*wealthRanking*//*WealthRank.POOR.rank,
                                                        patSurveyStatus = PatSurveyStatus.NOT_STARTED.ordinal,
                                                        voEndorsementStatus = *//*voEndorsementStatus*//*DidiEndorsementStatus.ENDORSED.ordinal,
                                                        section1Status = PatSurveyStatus.COMPLETED.ordinal,
                                                        section2Status = PatSurveyStatus.COMPLETED.ordinal,
                                                        createdDate = didi.createdDate,
                                                        modifiedDate = didi.modifiedDate,
                                                        beneficiaryProcessStatus = didi.beneficiaryProcessStatus,
                                                        shgFlag = SHGFlag.NOT_MARKED.value,
                                                        transactionId = "",
                                                        bpcScore = didi.bpcScore,
                                                        bpcComment = didi.bpcComment,
                                                        crpComment = didi.crpComment,
                                                        crpScore = didi.crpScore
                                                    )
                                                )
                                            }
                                            it.not_selected.forEach { didi ->
                                                var `tolaName = BLANK_STRING
                                                var casteName = BLANK_STRING
//                                            val singleTola = tolaDao.fetchSingleTola(didi.cohortId)
                                                val singleCaste = castListDao?.getCaste(didi.castId,
                                                    prefRepo?.getAppLanguageId()?:2)
//                                            singleTola?.let {
//                                                tolaName = it.name
//                                            }
                                                singleCaste?.let {
                                                    casteName = it.casteName
                                                }
//                                             if (singleTola != null) {
                                                val wealthRanking =
                                                    if (didi.beneficiaryProcessStatus.map { it.name }
                                                            .contains(StepType.WEALTH_RANKING.name))
                                                        didi.beneficiaryProcessStatus[didi.beneficiaryProcessStatus.map { process -> process.name }
                                                            .indexOf(StepType.WEALTH_RANKING.name)].status
                                                    else
                                                        WealthRank.NOT_RANKED.rank
                                                val patSurveyStatus =
                                                    if (didi.beneficiaryProcessStatus.map { it.name }
                                                            .contains(StepType.PAT_SURVEY.name))
                                                        PatSurveyStatus.toInt(didi.beneficiaryProcessStatus[didi.beneficiaryProcessStatus.map { process -> process.name }
                                                            .indexOf(StepType.PAT_SURVEY.name)].status)
                                                    else
                                                        PatSurveyStatus.NOT_STARTED.ordinal
                                                val voEndorsementStatus =
                                                    if (didi.beneficiaryProcessStatus.map { it.name }
                                                            .contains(StepType.VO_ENDROSEMENT.name))
                                                        DidiEndorsementStatus.toInt(didi.beneficiaryProcessStatus[didi.beneficiaryProcessStatus.map { process -> process.name }
                                                            .indexOf(StepType.PAT_SURVEY.name)].status)
                                                    else
                                                        DidiEndorsementStatus.NOT_STARTED.ordinal
                                                bpcNonSelectedDidiDao?.insertNonSelectedDidi(
                                                    BpcNonSelectedDidiEntity(
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
                                                        wealth_ranking = *//*wealthRanking*//*WealthRank.POOR.rank,
                                                        patSurveyStatus = PatSurveyStatus.NOT_STARTED.ordinal,
                                                        voEndorsementStatus = *//*voEndorsementStatus*//*DidiEndorsementStatus.ENDORSED.ordinal,
                                                        section1Status = PatSurveyStatus.COMPLETED.ordinal,
                                                        section2Status = PatSurveyStatus.COMPLETED.ordinal,
                                                        createdDate = didi.createdDate,
                                                        modifiedDate = didi.modifiedDate,
                                                        beneficiaryProcessStatus = didi.beneficiaryProcessStatus,
                                                        shgFlag = SHGFlag.NOT_MARKED.value,
                                                        transactionId = "",
                                                        bpcScore = didi.bpcScore,
                                                        bpcComment = didi.bpcComment,
                                                        crpComment = didi.crpComment,
                                                        crpScore = didi.crpScore
                                                    )
                                                )
                                            }*/
                                        }
                                    }
                                } else {
                                    val ex = ApiResponseFailException(didiResponse?.message!!)
                                    onCatchError(ex, ApiType.BPC_DIDI_LIST_API)
                                }
                            } catch (ex: Exception) {
                                onCatchError(ex, ApiType.BPC_DIDI_LIST_API)
                            }
                        }
                    }

                    ApiType.PAT_BPC_QUESTION_API -> {

                    }

                    ApiType.PAT_BPC_SURVEY_SUMMARY -> {

                    }
                    ApiType.CAST_LIST_API -> {
                            try {
                                val casteEntityList = arrayListOf<CasteEntity>()
                                val casteResponse = apiService?.getCasteList()
                                if (casteResponse?.status.equals(SUCCESS, true)) {
                                    castListDao?.deleteCasteTable()
                                    casteResponse?.data?.forEach { casteModel ->
                                        casteEntityList.add(CasteEntity.getCasteEntity(casteModel))
                                    }
                                    castListDao?.insertAll(casteEntityList)
                                } else {
                                    val ex = ApiResponseFailException(casteResponse?.message!!)

                                    onCatchError(ex, ApiType.CAST_LIST_API)
                                }
                            } catch (ex: Exception) {
                                onCatchError(ex, ApiType.CAST_LIST_API)
                            }
                    }
                    ApiType.BPC_POOR_DIDI_LIST_API -> {
                        stepListApiVillageId.forEach { id ->
                            try {
                                val poorDidiList = apiService?.getDidisFromNetwork(id)
                                if (poorDidiList?.status.equals(SUCCESS, true)) {
                                    poorDidiList?.data?.let { didiRank ->
                                        if (didiRank.didiList.isNotEmpty()) {
                                            didiRank.didiList.forEach { poorDidis ->
                                                poorDidis?.let { didi ->
                                                    var tolaName = BLANK_STRING
                                                    var casteName = BLANK_STRING
                                                    val singleTola =
                                                        tolaDao?.fetchSingleTola(didi.cohortId)
                                                    val singleCaste =
                                                        castListDao?.getCaste(didi.castId, prefRepo?.getAppLanguageId()?:2)
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

                                                    poorDidiListDao?.insertPoorDidi(
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
                                                            villageId = id,
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
                                                            shgFlag = SHGFlag.NOT_MARKED.value,
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
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    val ex = ApiResponseFailException(poorDidiList?.message ?: "Poor Didi Ranking list error")
                                    onCatchError(ex, ApiType.BPC_POOR_DIDI_LIST_API)
                                }
                            } catch (ex: Exception) {
                                onCatchError(ex, ApiType.BPC_POOR_DIDI_LIST_API)
                            }
                        }
                    }
                    else -> {
                        //TODO check if retry required for workflow api.
                    }
                }
                retryCount[apiType] = (initialCount ?: 0) + 1
            }
        }
    }

    fun retryVillageListApi(saveVillageList: (success: Boolean, villageList: List<VillageEntity>?) -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val languageList = languageDao?.getAllLanguages()
                val request = createMultiLanguageVillageRequest(
                    languageList ?: listOf(
                        LanguageEntity(
                            id = 2,
                            language = "English",
                            langCode = "en",
                            orderNumber = 1,
                            localName = "English"
                        )
                    )
                )
                val response = apiService?.userAndVillageListAPI(request)
                withContext(Dispatchers.IO) {
                    if (response?.status.equals(SUCCESS, true)) {
                        response?.data?.let {
                            prefRepo?.savePref(PREF_KEY_USER_NAME, it.username ?: "")
                            prefRepo?.savePref(PREF_KEY_NAME, it.name ?: "")
                            prefRepo?.savePref(PREF_KEY_EMAIL, it.email ?: "")
                            prefRepo?.savePref(PREF_KEY_IDENTITY_NUMBER, it.identityNumber ?: "")
                            prefRepo?.savePref(PREF_KEY_PROFILE_IMAGE, it.profileImage ?: "")
                            prefRepo?.savePref(PREF_KEY_ROLE_NAME, it.roleName ?: "")
                            prefRepo?.savePref(PREF_KEY_TYPE_NAME, it.typeName ?: "")
                            if (it.villageList?.isNotEmpty() == true) {
                                villageListDao?.insertOnlyNewData(
                                    it.villageList ?: listOf(),
                                    userBPC = prefRepo?.isUserBPC() ?: false
                                )
                                delay(500)
                                val localVillageList = villageListDao?.getAllVillages(prefRepo?.getAppLanguageId() ?:DEFAULT_LANGUAGE_ID)
                                if (localVillageList.isNullOrEmpty()) {
                                    saveVillageList(true, villageListDao?.getAllVillages(DEFAULT_LANGUAGE_ID))
                                } else{
                                    saveVillageList(true, localVillageList)
                                }
//                                saveVillageList(true, villageListDao?.getAllVillages(prefRepo?.getAppLanguageId()?:2))
                            } else {
                                saveVillageList(false, listOf())
                            }
                        }
                        if (response?.data == null)
                            saveVillageList(false, listOf())
                    } else if (response?.status.equals(FAIL, true)) {
                        withContext(Dispatchers.Main) {
                            saveVillageList(false, listOf())
                        }
                    }
                }
            } catch (ex: Exception) {
                Log.d("RetryHelper", "retryVillageListApi: ex -> ${ex.stackTrace}")
                onCatchError(ex, ApiType.VILLAGE_LIST_API)
                withContext(Dispatchers.Main) {
                    saveVillageList(false, listOf())
                }
            }
        }
    }


    var job: Job? = null

    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, e ->
        when (e) {
            is HttpException -> {
                when (e.response()?.code() ?: 0) {
                    RESPONSE_CODE_UNAUTHORIZED ->
                        onServerError(ErrorModel(e.response()?.code() ?: 0, UNAUTHORISED_MESSAGE))

                    RESPONSE_CODE_NOT_FOUND ->
                        onServerError(
                            ErrorModel(
                                message = UNREACHABLE_ERROR_MSG,
                                statusCode = e.response()?.code() ?: -1
                            )
                        )

                    RESPONSE_CODE_DEACTIVATED,
                    RESPONSE_CODE_500,
                    RESPONSE_CODE_BAD_GATEWAY,
                    RESPONSE_CODE_SERVICE_TEMPORARY_UNAVAILABLE ->
                        onServerError(ErrorModel(statusCode = e.response()?.code() ?: -1))

                    else ->
                        onServerError(
                            ErrorModel(
                                statusCode = e.response()?.code() ?: -1,
                                message = e.message ?: COMMON_ERROR_MSG
                            )
                        )
                }
            }

            is SocketTimeoutException -> {
                onServerError(
                    ErrorModel(
                        statusCode = RESPONSE_CODE_TIMEOUT,
                        message = TIMEOUT_ERROR_MSG
                    )
                )
            }

            is IOException -> {
                onServerError(ErrorModel(statusCode = RESPONSE_CODE_NETWORK_ERROR))
            }

            is JsonSyntaxException -> {
                onServerError(ErrorModel(-1, e.message, statusCode = RESPONSE_CODE_NO_DATA))
            }

            else -> onServerError(ErrorModel(-1, e.message))
        }
    }

    private fun onCatchError(e: Exception, api: ApiType) {
        Log.d("RetryHelper", "onCatchError: ${e.message}")
        when (e) {
            is HttpException -> {
                Log.d("RetryHelper", "onCatchError code: ${e.response()?.code() ?: 0}")
                when (e.response()?.code() ?: 0) {
                    RESPONSE_CODE_UNAUTHORIZED ->
                        onServerError(
                            ErrorModelWithApi(
                                e.response()?.code() ?: 0,
                                apiName = api,
                                UNAUTHORISED_MESSAGE
                            )
                        )

                    RESPONSE_CODE_NOT_FOUND ->
                        onServerError(
                            ErrorModelWithApi(
                                apiName = api, message = UNREACHABLE_ERROR_MSG,
                                statusCode = e.response()?.code() ?: -1
                            )
                        )

                    RESPONSE_CODE_DEACTIVATED,
                    RESPONSE_CODE_500,
                    RESPONSE_CODE_BAD_GATEWAY,
                    RESPONSE_CODE_SERVICE_TEMPORARY_UNAVAILABLE ->
                        onServerError(
                            ErrorModelWithApi(
                                apiName = api,
                                statusCode = e.response()?.code() ?: -1
                            )
                        )

                    else ->
                        onServerError(
                            ErrorModelWithApi(
                                apiName = api, statusCode = e.response()?.code() ?: -1,
                                message = e.message ?: COMMON_ERROR_MSG
                            )
                        )
                }
            }

            is SocketTimeoutException -> {
                onServerError(
                    ErrorModelWithApi(
                        apiName = api,
                        statusCode = RESPONSE_CODE_TIMEOUT,
                        message = TIMEOUT_ERROR_MSG
                    )
                )
            }

            is IOException -> {
                onServerError(
                    ErrorModelWithApi(
                        apiName = api,
                        statusCode = RESPONSE_CODE_NETWORK_ERROR
                    )
                )
            }

            is JsonSyntaxException -> {
                onServerError(
                    ErrorModelWithApi(
                        -1,
                        apiName = api,
                        e.message,
                        statusCode = RESPONSE_CODE_NO_DATA
                    )
                )
            }

            is ApiResponseFailException -> {
                onServerError(ErrorModelWithApi(code = -1, apiName = api, e.message))
            }

            else -> onServerError(ErrorModelWithApi(-1, apiName = api, e.message))
        }
    }

    fun onServerError(error: ErrorModel?) {

    }

    fun onServerError(errorModel: ErrorModelWithApi?) {
        if (errorModel?.code?.equals(RESPONSE_CODE_UNAUTHORIZED) == true || errorModel?.code?.equals(
                RESPONSE_CODE_CONFLICT
            ) == true
        ) {
//            tokenExpired.value = true
        }
    }

    fun updateOtp(
        otpNumber: MutableState<String>,
        onOtpResponse: (success: Boolean, message: String) -> Unit
    ) {
        val otpRequest =
            OtpRequest(mobileNumber = prefRepo?.getMobileNumber() ?: "", otp = if (otpNumber.value == "") autoReadOtp.value else otpNumber.value) //Text this code
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                launch {
                    val response = apiService?.validateOtp(otpRequest)
                    NudgeLogger.d("RetryHelper","validateOtp => ${Gson().toJson(otpRequest)}")
                    if (response?.status.equals(SUCCESS, true)) {
                        response?.data?.let {
                            prefRepo?.saveAccessToken(it.token)
                            CoreSharedPrefs.getInstance(NudgeCore.getAppContext())
                                .setBackupFileName(
                                    getDefaultBackUpFileName(
                                        prefRepo?.getMobileNumber() ?: BLANK_STRING,
                                        prefRepo?.getLoggedInUserType() ?: BLANK_STRING
                                    )
                                )
                            CoreSharedPrefs.getInstance(NudgeCore.getAppContext())
                                .setImageBackupFileName(
                                    getDefaultImageBackUpFileName(
                                        prefRepo?.getMobileNumber() ?: BLANK_STRING,
                                        prefRepo?.getLoggedInUserType() ?: BLANK_STRING
                                    )
                                )
                        }
                        withContext(Dispatchers.Main) {
                            onOtpResponse(true, response?.message ?: "Login Successful")
                        }
                    } else if (response?.status.equals(FAIL, true)) {
                        withContext(Dispatchers.Main) {
                            onOtpResponse(false, response?.message ?: "Something went wrong")
                        }
                    }
                }
            } catch (ex: Exception) {
                onCatchError(ex, ApiType.VALIDATE_OTP_API)
                withContext(Dispatchers.Main) {
                    onOtpResponse(false, "Something went wrong, exception: ${ex.message}")
                }
            }
        }
    }

    fun generateOtp(onLoginResponse: (success: Boolean, message: String, mobileNumber: String) -> Unit) {
        val mobileNumber = prefRepo?.getMobileNumber() ?: ""
        val loginRequest = LoginRequest(mobileNumber = mobileNumber)
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                launch {
                    val response = apiService?.generateOtp(loginRequest)
                    NudgeLogger.d("RetryHelper ","generateOtp=> ${Gson().toJson(loginRequest)}")
                    if (response?.status.equals(SUCCESS, true)) {
                        withContext(Dispatchers.Main) {
                            prefRepo?.saveMobileNumber(mobileNumber)
                            onLoginResponse(true, response?.message ?: "OTP Send", mobileNumber)
                        }
                    } else if (response?.status.equals(FAIL, true)) {
                        withContext(Dispatchers.Main) {
                            onLoginResponse(
                                false,
                                response?.message ?: "Something went wrong",
                                mobileNumber
                            )
                        }
                    }
                }
            } catch (ex: Exception) {
                onCatchError(ex, ApiType.GENERATE_OTP_API)
                withContext(Dispatchers.Main) {
                    onLoginResponse(
                        false,
                        "Something went wrong, exception: ${ex.message}",
                        mobileNumber
                    )
                }
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

}