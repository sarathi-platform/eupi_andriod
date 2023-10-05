package com.patsurvey.nudge.activities.ui.progress

import com.google.gson.JsonSyntaxException
import com.patsurvey.nudge.MyApplication
import com.patsurvey.nudge.RetryHelper
import com.patsurvey.nudge.activities.settings.TransactionIdRequest
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.BpcSummaryEntity
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.NumericAnswerEntity
import com.patsurvey.nudge.database.PoorDidiEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.StepListEntity
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
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.request.AnswerDetailDTOListItem
import com.patsurvey.nudge.model.request.EditDidiWealthRankingRequest
import com.patsurvey.nudge.model.request.EditWorkFlowRequest
import com.patsurvey.nudge.model.request.GetQuestionListRequest
import com.patsurvey.nudge.model.request.PATSummarySaveRequest
import com.patsurvey.nudge.model.request.SaveMatchSummaryRequest
import com.patsurvey.nudge.model.response.OptionsItem
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.AbleBodiedFlag
import com.patsurvey.nudge.utils.ApiResponseFailException
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.BPC_SURVEY_CONSTANT
import com.patsurvey.nudge.utils.BPC_USER_TYPE
import com.patsurvey.nudge.utils.COMPLETED_STRING
import com.patsurvey.nudge.utils.DIDI_NOT_AVAILABLE
import com.patsurvey.nudge.utils.DIDI_REJECTED
import com.patsurvey.nudge.utils.DOUBLE_ZERO
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.ExclusionType
import com.patsurvey.nudge.utils.FLAG_RATIO
import com.patsurvey.nudge.utils.FLAG_WEIGHT
import com.patsurvey.nudge.utils.HEADING_QUESTION_TYPE
import com.patsurvey.nudge.utils.LAST_UPDATE_TIME
import com.patsurvey.nudge.utils.LOW_SCORE
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PAT_SURVEY
import com.patsurvey.nudge.utils.PAT_SURVEY_CONSTANT
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
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.QUESTION_FLAG_RATIO
import com.patsurvey.nudge.utils.QUESTION_FLAG_WEIGHT
import com.patsurvey.nudge.utils.QuestionType
import com.patsurvey.nudge.utils.SHGFlag
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.StepType
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.USER_BPC
import com.patsurvey.nudge.utils.USER_CRP
import com.patsurvey.nudge.utils.VERIFIED_STRING
import com.patsurvey.nudge.utils.WealthRank
import com.patsurvey.nudge.utils.calculateScore
import com.patsurvey.nudge.utils.formatRatio
import com.patsurvey.nudge.utils.intToString
import com.patsurvey.nudge.utils.stringToDouble
import com.patsurvey.nudge.utils.toWeightageRatio
import com.patsurvey.nudge.utils.updateLastSyncTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

class VillageSelectionRepository @Inject constructor(
    val apiService: ApiService,
    val villageListDao: VillageListDao,
    val stepsListDao: StepsListDao,
    val tolaDao: TolaDao,
    val didiDao: DidiDao,
    val casteListDao: CasteListDao,
    val languageListDao: LanguageListDao,
    val questionDao: QuestionListDao,
    val trainingVideoDao: TrainingVideoDao,
    val numericAnswerDao: NumericAnswerDao,
    val answerDao: AnswerDao,
    val bpcSummaryDao: BpcSummaryDao,
    val bpcSelectedDidiDao: BpcSelectedDidiDao,
    val bpcNonSelectedDidiDao: BpcNonSelectedDidiDao,
    val poorDidiListDao: PoorDidiListDao,
    val androidDownloader: AndroidDownloader
): BaseRepository() {

    private var isPendingForBpc = 0
    private val pendingTimerTime:Long = 10000

    fun refreshBpcData(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener) {
        repoJob = MyApplication.appScopeLaunch (Dispatchers.IO + exceptionHandler) {
            val awaitDeff = CoroutineScope(Dispatchers.IO).async {
                try {
                    val villageList =
                        villageListDao.getAllVillages(prefRepo.getAppLanguageId() ?: 2)
                    val villageIdList: ArrayList<Int> = arrayListOf()
                    villageList.forEach { village ->
                        villageIdList.add(village.id)

                        //Fetch Step List Data
                        try {
                            NudgeLogger.d("VillageSelectionScreen", "refreshBpcData getStepsList request -> village.id = ${village.id}")
                            val response = apiService.getStepsList(village.id)
                            NudgeLogger.d("VillageSelectionScreen", "refreshBpcData getStepsList " +
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
                                        NudgeLogger.d("VillageSelectionScreen", "refreshBpcData getStepsList " +
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

                                        NudgeLogger.d("VillageSelectionScreen", "refreshBpcData getStepsList " +
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
                            NudgeLogger.d("VillageSelectionScreen", "refreshBpcData getBpcSummary " +
                                    "village.id = ${village.id}")
                            val bpcSummaryResponse =
                                apiService.getBpcSummary(villageId = village.id)
                            NudgeLogger.d("VillageSelectionScreen", "refreshBpcData getStepsList " +
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
                                    NudgeLogger.d("VillageSelectionScreen", "refreshBpcData getStepsList " +
                                            "bpcSummaryDao.insert(bpcSummary) before")
                                    bpcSummaryDao.insert(bpcSummary)
                                    NudgeLogger.d("VillageSelectionScreen", "refreshBpcData getStepsList " +
                                            "bpcSummaryDao.insert(bpcSummary) after")
                                }
                            } else {
                                NudgeLogger.d("VillageSelectionScreen", "refreshBpcData getStepsList " +
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
                        syncAndFetchTolaForBpc(village.id)

                        //Sync and fetch Didi and other data
                        syncAndFetchDidiForBpc(prefRepo, village.id, object : NetworkCallbackListener {
                            override fun onSuccess() {
                                fetchDidiForBpc(village.id, prefRepo)
                                fetchPoorDidisForBpc(village.id, prefRepo)
                                fetchPatSurveyFromServer(village.id, prefRepo)
                            }

                            override fun onFailed() {
                                networkCallbackListener.onFailed()
                            }

                        })

                        fetchQuestions(prefRepo)

                    }
                } catch (ex: Exception) {
                    NudgeLogger.e(
                        "VillageSelectionViewModel",
                        "refreshBpcData -> onCatchError",
                        ex
                    )
                    onCatchError(ex, ApiType.FETCH_ALL_DATA)
                } finally {
                    prefRepo.savePref(LAST_UPDATE_TIME, System.currentTimeMillis())
                    withContext(Dispatchers.Main) {
                        delay(250)
//                        NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc finally -> viewModel.showLoader.value = false")
//                        showLoader.value = false
                    }
                }
            }.await()
            delay(250)
            NudgeLogger.d(
                "VillageSelectionScreen",
                "refreshBpcData after await -> viewModel.showLoader.value = false"
            )
            withContext(Dispatchers.Main) {
                networkCallbackListener.onSuccess()
            }
        }
    }

    private fun fetchQuestions(prefRepo: PrefRepo){

        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val localLanguageList = languageListDao.getAllLanguages()
            localLanguageList?.let {
                val stateId = villageListDao.getStateId()
                localLanguageList.forEach { languageEntity ->
                    try {
                        // Fetch QuestionList from Server
                        val localLanguageQuesList =
                            questionDao.getAllQuestionsForLanguage(languageEntity.id)
                        if (localLanguageQuesList.isEmpty()) {
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

    private fun fetchPatSurveyFromServer(villageId: Int, prefRepo: PrefRepo) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc fetchPATSurveyToServer " +
                        "request -> ${listOf(villageId)}")
                val answerApiResponse = apiService.fetchPATSurveyToServer(
                    listOf(villageId)
                )
                NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc fetchPATSurveyToServer " +
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
        }

    }


    private suspend fun syncAndFetchTolaForBpc(villageId: Int) {
        try {
            NudgeLogger.d("VillageSelectionScreen", "refreshBpcData getCohortFromNetwork " +
                    "request village.id = ${villageId}")
            val cohortResponse =
                apiService.getCohortFromNetwork(villageId = villageId)
            NudgeLogger.d("VillageSelectionScreen", "refreshBpcData getCohortFromNetwork " +
                    "cohortResponse status = ${cohortResponse.status}, message = ${cohortResponse.message}, data = ${cohortResponse.data.toString()}")
            if (cohortResponse.status.equals(SUCCESS, true)) {
                cohortResponse.data?.let { remoteTolaList ->
                    NudgeLogger.d("VillageSelectionScreen", "refreshBpcData getCohortFromNetwork " +
                            "tolaDao.insertAll(it) before")
                    if (remoteTolaList.isNotEmpty()) {
                        for (tola in remoteTolaList) {
                            tola.serverId = tola.id
                        }
                    }

                    tolaDao.deleteTolaForVillage(villageId)
                    delay(100)
                    tolaDao.insertAll(remoteTolaList)
                    NudgeLogger.d("VillageSelectionScreen", "refreshBpcData getCohortFromNetwork " +
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


    private fun syncAndFetchDidiForBpc(prefRepo: PrefRepo, villageId: Int, networkCallbackListener: NetworkCallbackListener) {
        savePATSummeryToServer(prefRepo = prefRepo, networkCallbackListener =  networkCallbackListener)
    }

    private fun savePATSummeryToServer(networkCallbackListener: NetworkCallbackListener, prefRepo: PrefRepo){
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val villageList = villageListDao.getAllVillages(prefRepo.getAppLanguageId()?:2)
                for(village in villageList) {

                    var optionList: List<OptionsItem>
                    val answeredDidiList: java.util.ArrayList<PATSummarySaveRequest> = arrayListOf()
                    val scoreDidiList: java.util.ArrayList<EditDidiWealthRankingRequest> = arrayListOf()
                    var surveyId =0

                    val didiIDList= answerDao.fetchPATSurveyDidiList()
                    if(didiIDList.isNotEmpty()){
                        didiIDList.forEach { didi->
                            NudgeLogger.d("SyncBPCDataOnServer", "savePATSummeryToServer Save: ${didi.id} :: ${didi.patSurveyStatus}")
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
                                    id = if (didi.serverId == 0) didi.id else didi.serverId,
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
                                    ableBodiedFlag = AbleBodiedFlag.fromInt(didi.ableBodiedFlag).name
                                )
                            )
                            val patSummarySaveRequest = PATSummarySaveRequest(
                                villageId = village.id,
                                surveyId = surveyId,
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
                            NudgeLogger.d("SyncBPCDataOnServer", "savePATSummeryToServer patSummarySaveRequest: $patSummarySaveRequest")
                            answeredDidiList.add(
                                patSummarySaveRequest
                            )
                        }
                        if(answeredDidiList.isNotEmpty()){
                            withContext(Dispatchers.IO){
                                NudgeLogger.d("SyncBPCDataOnServer", "savePATSummeryToServer answeredDidiList: $answeredDidiList")
                                val saveAPIResponse= apiService.savePATSurveyToServer(answeredDidiList)
                                if(saveAPIResponse.status.equals(SUCCESS,true)){
                                    if(saveAPIResponse.data?.get(0)?.transactionId.isNullOrEmpty()) {
                                        didiIDList.forEach { didiItem ->
                                            didiDao.updateNeedToPostPAT(
                                                false,
                                                didiItem.id,
                                                village.id
                                            )
                                        }
                                        NudgeLogger.d("SyncBPCDataOnServer", "savePATSummeryToServer -> saveAPIResponse.data?.get(0)?.transactionId.isNullOrEmpty()")
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
                                        NudgeLogger.d("SyncBPCDataOnServer", "savePATSummeryToServer -> !saveAPIResponse.data?.get(0)?.transactionId.isNullOrEmpty()")

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
                                NudgeLogger.d("SyncBPCDataOnServer", "savePATSummeryToServer scoreDidiList: $scoreDidiList")
                                apiService.updateDidiScore(scoreDidiList)
                            }
                        }
                    }
                }
                checkPendingPatStatus(prefRepo, networkCallbackListener)
            } catch (ex: Exception) {
                ex.printStackTrace()
                onCatchError(ex, ApiType.BPC_PAT_SAVE_ANSWER_SUMMARY)
            }
        }

    }

    private fun calculateDidiScore(didiId: Int, prefRepo: PrefRepo) {
        NudgeLogger.d("SyncBPCDataOnServer", "calculateDidiScore didiId: ${didiId}")
        var passingMark = 0
        var isDidiAccepted = false
        var comment = LOW_SCORE
        val _inclusiveQueList = answerDao.getAllInclusiveQues(didiId = didiId)
        if (_inclusiveQueList.isNotEmpty()) {
            var totalWightWithoutNumQue = answerDao.getTotalWeightWithoutNumQues(didiId)
            NudgeLogger.d(
                "SyncBPCDataOnServer",
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
                                "SyncBPCDataOnServer",
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
                            "SyncBPCDataOnServer",
                            "calculateDidiScore: for Flag FLAG_RATIO totalWightWithoutNumQue += newScore -> $totalWightWithoutNumQue"
                        )
                    }
                }
            }
            // TotalScore


            /*if (totalWightWithoutNumQue >= passingMark) {
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
            }*/
            NudgeLogger.d("SyncBPCDataOnServer", "calculateDidiScore totalWightWithoutNumQue: $totalWightWithoutNumQue")
            didiDao.updateDidiScore(
                score = totalWightWithoutNumQue,
                comment = comment,
                didiId = didiId,
                isDidiAccepted = isDidiAccepted
            )
            if (prefRepo.isUserBPC()) {
                bpcSelectedDidiDao.updateSelDidiScore(
                    score = totalWightWithoutNumQue,
                    comment = comment,
                    didiId = didiId,
                )
            }
        } else {
            didiDao.updateDidiScore(
                score = 0.0,
                comment = TYPE_EXCLUSION,
                didiId = didiId,
                isDidiAccepted = false
            )
            if (prefRepo.isUserBPC()) {
                bpcSelectedDidiDao.updateSelDidiScore(
                    score = 0.0,
                    comment = TYPE_EXCLUSION,
                    didiId = didiId,
                )
            }
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
                            id = if (didi.serverId == 0) didi.id else didi.serverId,
                            score = didi.score,
                            comment =comment,
                            type = BPC_SURVEY_CONSTANT,
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

    private fun checkPendingPatStatus(prefRepo: PrefRepo, networkCallbackListener: NetworkCallbackListener) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiIDList= didiDao.fetchPendingPatStatusDidi(true,"")
            if(didiIDList.isNotEmpty()) {
                val ids: java.util.ArrayList<String> = arrayListOf()
                didiIDList.forEach { didi ->
                    didi.transactionId?.let { ids.add(it) }
                }
                NudgeLogger.d("SyncBPCDataOnServer", "checkPendingPatStatus -> TransactionIdRequest = $ids")
                val response = apiService.getPendingStatus(TransactionIdRequest("",ids))
                if (response.status.equals(SUCCESS, true)) {
                    NudgeLogger.d("SyncBPCDataOnServer", "checkPendingPatStatus -> SUCCESS")
                    response.data?.forEach { transactionIdResponse ->
                        didiIDList.forEach { didiEntity ->
                            if (transactionIdResponse.transactionId == didiEntity.transactionId) {
                                didiDao.updateNeedToPostPAT(false,didiEntity.serverId)
                            }
                        }
                    }
                    updateBpcPatStatusToNetwork(prefRepo = prefRepo, networkCallbackListener = networkCallbackListener)
                } else {
                    NudgeLogger.d("SyncBPCDataOnServer", "checkPendingPatStatus -> FAIL")
                    withContext(Dispatchers.Main) {
                        networkCallbackListener.onFailed()
                    }
                }
                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }

            } else {
                NudgeLogger.d("SyncBPCDataOnServer", "checkPendingPatStatus -> didiIDList is empty")
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

    private fun callWorkFlowAPIForBpc(networkCallbackListener: NetworkCallbackListener, prefRepo: PrefRepo) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val villageList = villageListDao.getAllVillages(prefRepo.getAppLanguageId()?:2)
                val editWorkFlowRequest = java.util.ArrayList<EditWorkFlowRequest>()
                val stepListRequest = java.util.ArrayList<StepListEntity>()
                for(village in villageList) {
                    val villageId = village.id
                    val stepList =
                        stepsListDao.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }
                    val bpcStep = stepList.last()
                    if(bpcStep.workFlowId>0) {
                        stepListRequest.add(bpcStep)
                        editWorkFlowRequest.add(
                            EditWorkFlowRequest(
                                bpcStep.workFlowId,
                                StepStatus.getStepFromOrdinal(bpcStep.isComplete)
                            )
                        )
                    }
                }
                withContext(Dispatchers.IO){
                    val response = apiService.editWorkFlow(editWorkFlowRequest)
                    if (response.status.equals(SUCCESS, true)) {
                        if(response.data?.get(0)?.transactionId?.isEmpty() == true) {
                            for(i in editWorkFlowRequest.indices) {
                                val request = editWorkFlowRequest[i]
                                val step = stepListRequest[i]
                                stepsListDao.updateWorkflowId(
                                    step.id,
                                    step.workFlowId,
                                    step.villageId,
                                    request.status
                                )
                                stepsListDao.updateNeedToPost(step.id, step.villageId, false)
                            }
                        }
                        sendBpcMatchScore(networkCallbackListener, prefRepo)
                    }else{
                        withContext(Dispatchers.Main) {
                            networkCallbackListener.onFailed()
                        }
//                            settingViewModel.onError("ex", ApiType.BPC_UPDATE_DIDI_LIST_API)
                    }

                    if(!response.lastSyncTime.isNullOrEmpty()){
                        updateLastSyncTime(prefRepo,response.lastSyncTime)
                    }
                }
            }catch (ex:Exception){
                withContext(Dispatchers.Main) {
                    networkCallbackListener.onFailed()
                }
                onCatchError(ex, ApiType.WORK_FLOW_API)
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

    private fun sendBpcMatchScore(
        networkCallbackListener: NetworkCallbackListener,
        prefRepo: PrefRepo
    ) {
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
                        checkPendingPatStatus(prefRepo, networkCallbackListener)
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
                NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc getDidiForBpcFromNetwork " +
                        "request village.id = ${villageId}")
                val didiResponse =
                    apiService.getDidiForBpcFromNetwork(villageId = villageId)
                NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc getDidiForBpcFromNetwork " +
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
                                    //TODO Add Images
                                    /*if(!didi.crpUploadedImage.isNullOrEmpty()){
                                        downloadAuthorizedImageItem(didi.id,didi.crpUploadedImage?: BLANK_STRING, prefRepo = prefRepo )
                                    }*/
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
        }
    }

    private fun fetchPoorDidisForBpc(villageId: Int, prefRepo: PrefRepo) {
        repoJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc getDidisWithRankingFromNetwork " +
                        "request -> villageId = village.id, \"Category\", StepResultTypeRequest(\n" +
                        "                                    StepType.WEALTH_RANKING.name, ResultType.POOR.name")
                val poorDidiList = apiService.getDidisFromNetwork(villageId)/*apiService.getDidisWithRankingFromNetwork(
                                villageId = village.id, "Category", StepResultTypeRequest(
                                    StepType.WEALTH_RANKING.name, ResultType.POOR.name
                                )
                            )*/
                NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc getDidisWithRankingFromNetwork " +
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
                                    NudgeLogger.d("VillageSelectionScreen", "fetchDataForBpc getDidisWithRankingFromNetwork " +
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

    override fun onServerError(error: ErrorModel?) {

    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {

    }


}