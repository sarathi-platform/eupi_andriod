package com.patsurvey.nudge.activities.ui.progress


import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.google.gson.JsonSyntaxException
import com.patsurvey.nudge.RetryHelper
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.NumericAnswerEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.TrainingVideoEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.CasteListDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.LanguageListDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.TrainingVideoDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.download.FileType
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.request.GetQuestionListRequest
import com.patsurvey.nudge.model.request.StepResultTypeRequest
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.ApiResponseFailException
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.COMPLETED_STRING
import com.patsurvey.nudge.utils.DIDI_REJECTED
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.DownloadStatus
import com.patsurvey.nudge.utils.FAIL
import com.patsurvey.nudge.utils.LAST_UPDATE_TIME
import com.patsurvey.nudge.utils.PAT_SURVEY_CONSTANT
import com.patsurvey.nudge.utils.PREF_KEY_EMAIL
import com.patsurvey.nudge.utils.PREF_KEY_IDENTITY_NUMBER
import com.patsurvey.nudge.utils.PREF_KEY_NAME
import com.patsurvey.nudge.utils.PREF_KEY_PROFILE_IMAGE
import com.patsurvey.nudge.utils.PREF_KEY_USER_NAME
import com.patsurvey.nudge.utils.PREF_PROGRAM_NAME
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.QuestionType
import com.patsurvey.nudge.utils.RESPONSE_CODE_CONFLICT
import com.patsurvey.nudge.utils.RESPONSE_CODE_UNAUTHORIZED
import com.patsurvey.nudge.utils.ResultType
import com.patsurvey.nudge.utils.SHGFlag
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.StepType
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.WealthRank
import com.patsurvey.nudge.utils.findCompleteValue
import com.patsurvey.nudge.utils.videoList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    val answerDao: AnswerDao
) : BaseViewModel() {
    private val _villagList = MutableStateFlow(listOf<VillageEntity>())
    val villageList: StateFlow<List<VillageEntity>> get() = _villagList

    val villageSelected = mutableStateOf(-1)
    val stateId = mutableStateOf(1)
    val showLoader = mutableStateOf(false)

    val shouldRetry = mutableStateOf(false)

    fun isLoggedIn() = (prefRepo.getAccessToken()?.isNotEmpty() == true)

    init {
//        showLoader.value = true
        fetchUserDetails {
            if (prefRepo.getPref(LAST_UPDATE_TIME, 0L) != 0L) {
                if ((System.currentTimeMillis() - prefRepo.getPref(
                        LAST_UPDATE_TIME,
                        0L
                    )) > TimeUnit.DAYS.toMillis(5)
                )
                    fetchVillageList()
//                else
//                    showLoader.value = false
            } else {
                fetchVillageList()
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
                                context,
                                it.id,
                                fileType = FileType.VIDEO
                            ).exists()
                        ) DownloadStatus.DOWNLOADED.value else DownloadStatus.UNAVAILABLE.value
                    )
                    trainingVideoDao.insert(trainingVideoEntity)
                }
            } else {
                trainingVideos.forEach {
                    val videoIsDownloaded = if (getVideoPath(
                            context,
                            it.id,
                            fileType = FileType.VIDEO
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

    private fun fetchVillageList() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                showLoader.value = true
                withContext(Dispatchers.IO) {
                    val villageList = villageListDao.getAllVillages()
                    val localStepsList = stepsListDao.getAllSteps()
                    val localTolaList = tolaDao.getAllTolas()
                    val localLanguageList = languageListDao.getAllLanguages()
                    val villageIdList: ArrayList<Int> = arrayListOf()
                    if (localStepsList.isNotEmpty()) {
                        stepsListDao.deleteAllStepsFromDB()
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
                                            }
                                            stepsListDao.insertAll(it.stepList)
                                        }
                                        prefRepo.savePref(
                                            PREF_PROGRAM_NAME,
                                            it.programName
                                        )
                                        showLoader.value = false

                                    }
                                } else {
                                    val ex = ApiResponseFailException(response.message)
                                    if (!RetryHelper.retryApiList.contains(ApiType.STEP_LIST_API))
                                        RetryHelper.retryApiList.add(ApiType.STEP_LIST_API)
                                    RetryHelper.stepListApiVillageId.add(village.id)
                                    onCatchError(ex, ApiType.STEP_LIST_API)
                                }
                            } catch (ex: Exception) {
                                if (ex !is JsonSyntaxException) {
                                    if (!RetryHelper.retryApiList.contains(ApiType.STEP_LIST_API))
                                        RetryHelper.retryApiList.add(ApiType.STEP_LIST_API)
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
                                    if (!RetryHelper.retryApiList.contains(ApiType.TOLA_LIST_API))
                                        RetryHelper.retryApiList.add(ApiType.TOLA_LIST_API)
                                    RetryHelper.stepListApiVillageId.add(village.id)
                                    onCatchError(ex, ApiType.TOLA_LIST_API)
                                }
                            } catch (ex: Exception) {
                                if (ex !is JsonSyntaxException) {
                                    if (!RetryHelper.retryApiList.contains(ApiType.TOLA_LIST_API))
                                        RetryHelper.retryApiList.add(ApiType.TOLA_LIST_API)
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
                                                        casteListDao.getCaste(didi.castId)
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
                                                        val patSurveyAcceptedRejected =
                                                            if (didi.beneficiaryProcessStatus.map { it.name }
                                                                    .contains(StepType.PAT_SURVEY.name))
                                                                didi.beneficiaryProcessStatus[didi.beneficiaryProcessStatus.map { process -> process.name }
                                                                    .indexOf(StepType.PAT_SURVEY.name)].status
                                                            else
                                                                DIDI_REJECTED
                                                    val voEndorsementStatus =
                                                        if (didi.beneficiaryProcessStatus.map { it.name }
                                                                .contains(StepType.VO_ENDORSEMENT.name))
                                                            DidiEndorsementStatus.toInt(didi.beneficiaryProcessStatus[didi.beneficiaryProcessStatus.map { process -> process.name }
                                                                .indexOf(StepType.VO_ENDORSEMENT.name)].status)
                                                        else
                                                            DidiEndorsementStatus.NOT_STARTED.ordinal

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
                                                            forVoEndorsement = if(patSurveyAcceptedRejected.equals(
                                                                    COMPLETED_STRING,true)) 1 else 0,
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
                                                            comment = didi.comment,

                                                        )
                                                    )
                                                }
                                            }
                                        } catch (ex: Exception) {
                                            onError(
                                                tag = "VillageSelectionViewModel",
                                                "Error : ${didiResponse.message}"
                                            )
                                            showLoader.value = false
                                        }
                                    }
                                    withContext(Dispatchers.Main) {
                                        showLoader.value = false}
                                    }
                                } else {
                                    val ex = ApiResponseFailException(didiResponse.message)
                                    if (!RetryHelper.retryApiList.contains(ApiType.DIDI_LIST_API))
                                        RetryHelper.retryApiList.add(ApiType.DIDI_LIST_API)
                                    RetryHelper.stepListApiVillageId.add(village.id)
                                    onCatchError(ex, ApiType.DIDI_LIST_API)
                                }
                            } catch (ex: Exception) {
                                if (ex !is JsonSyntaxException)
                                    if (!RetryHelper.retryApiList.contains(ApiType.DIDI_LIST_API))
                                        RetryHelper.retryApiList.add(ApiType.DIDI_LIST_API)
                                RetryHelper.stepListApiVillageId.add(village.id)
                                onCatchError(ex, ApiType.DIDI_LIST_API)
                            }
                            try {
                                val didiRankingResponse = apiService.getDidisWithRankingFromNetwork(
                                    villageId = village.id, "Category",
                                    StepResultTypeRequest(
                                        StepType.WEALTH_RANKING.name,
                                        ResultType.ALL.name
                                    )
                                )
                                if (didiRankingResponse.status.equals(SUCCESS, true)) {
                                    didiRankingResponse.data?.let { didiRank ->
                                        if (didiRank.beneficiaryList?.richDidi?.isNotEmpty() == true) {
                                            didiRank.beneficiaryList?.richDidi?.forEach { richDidi ->
                                                richDidi?.id?.let { didiId ->
                                                    didiDao.updateDidiRank(
                                                        didiId,
                                                        WealthRank.RICH.rank
                                                    )
                                                }
                                            }
                                        }
                                        if (didiRank.beneficiaryList?.mediumDidi?.isNotEmpty() == true) {
                                            didiRank.beneficiaryList?.mediumDidi?.forEach { mediumDidi ->
                                                mediumDidi?.id?.let { didiId ->
                                                    didiDao.updateDidiRank(
                                                        didiId,
                                                        WealthRank.MEDIUM.rank
                                                    )
                                                }
                                            }
                                        }
                                        if (didiRank.beneficiaryList?.poorDidi?.isNotEmpty() == true) {
                                            didiRank.beneficiaryList?.poorDidi?.forEach { poorDidi ->
                                                poorDidi?.id?.let { didiId ->
                                                    didiDao.updateDidiRank(
                                                        didiId,
                                                        WealthRank.POOR.rank
                                                    )
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    val ex = ApiResponseFailException(
                                        didiRankingResponse.message ?: "Didi Ranking Api Failed"
                                    )
                                    if (!RetryHelper.retryApiList.contains(ApiType.DIDI_RANKING_API))
                                        RetryHelper.retryApiList.add(ApiType.DIDI_RANKING_API)
                                    RetryHelper.stepListApiVillageId.add(village.id)
                                    onCatchError(ex, ApiType.DIDI_RANKING_API)
                                }
                            } catch (ex: Exception) {
                                if (ex !is JsonSyntaxException) {
                                    if (!RetryHelper.retryApiList.contains(ApiType.DIDI_RANKING_API))
                                        RetryHelper.retryApiList.add(ApiType.DIDI_RANKING_API)
                                    RetryHelper.stepListApiVillageId.add(village.id)
                                }
                                onCatchError(ex, ApiType.DIDI_RANKING_API)
                            }
                        }
                    }

                    localLanguageList?.let {
                        launch {
                            localLanguageList.forEach { languageEntity ->
                                try {
                                    val quesListResponse = apiService.fetchQuestionListFromServer(
                                        GetQuestionListRequest(
                                            languageEntity.id,
                                            stateId.value,
                                            PAT_SURVEY_CONSTANT
                                        )
                                    )
//                                    to explicitly throw exception
//                                    throw ApiResponseFailException("Api Failed for testing")

                                    if (quesListResponse.status.equals(SUCCESS, true)) {
                                        quesListResponse.data?.let { questionList ->
                                            if (questionList.listOfQuestionSectionList?.isNotEmpty() == true) {
                                                questionList.listOfQuestionSectionList?.forEach { list ->
                                                    list?.questionList?.forEach { question ->
                                                        question?.sectionOrderNumber =
                                                            list.orderNumber
                                                        question?.actionType = list.actionType
                                                        question?.languageId = languageEntity.id
                                                        question?.surveyId = questionList.surveyId
                                                        question?.thresholdScore =
                                                            questionList.thresholdScore
                                                        question?.surveyPassingMark =
                                                            questionList.surveyPassingMark
                                                    }
                                                    list?.questionList?.let {
                                                        questionListDao.insertAll(it as List<QuestionEntity>)
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        val ex = ApiResponseFailException(quesListResponse.message)
                                        if (!RetryHelper.retryApiList.contains(ApiType.PAT_CRP_QUESTION_API))
                                            RetryHelper.retryApiList.add(ApiType.PAT_CRP_QUESTION_API)
                                        RetryHelper.crpPatQuestionApiLanguageId.add(languageEntity.id)
                                        onCatchError(ex, ApiType.PAT_CRP_QUESTION_API)
                                    }
                                } catch (ex: Exception) {
                                    if (ex !is JsonSyntaxException) {
                                        if (!RetryHelper.retryApiList.contains(ApiType.PAT_CRP_QUESTION_API))
                                            RetryHelper.retryApiList.add(ApiType.PAT_CRP_QUESTION_API)
                                        RetryHelper.crpPatQuestionApiLanguageId.add(languageEntity.id)
                                    }
                                    onCatchError(ex, ApiType.PAT_CRP_QUESTION_API)
                                }

                                // Fetch QuestionList from Server

                            }
                        }
                    }
                    answerDao.deleteAnswerTable()
                    numericAnswerDao.deleteNumericTable()
                    villageIdList?.let {
                        launch {
                            try {
                                val answerApiResponse = apiService.fetchPATSurveyToServer(it)
                                if (answerApiResponse.status.equals(SUCCESS, true)) {
                                    answerApiResponse.data?.let {
                                        val answerList: ArrayList<SectionAnswerEntity> =
                                            arrayListOf()
                                        val numAnswerList: ArrayList<NumericAnswerEntity> =
                                            arrayListOf()
                                        it.forEach { item ->
                                            didiDao.updatePATProgressStatus(
                                                patSurveyStatus = item.patSurveyStatus ?: 0,
                                                section1Status = item.section1Status ?: 0,
                                                section2Status = item.section2Status ?: 0,
                                                didiId = item.beneficiaryId ?: 0
                                            )
                                            if (item?.answers?.isNotEmpty() == true) {
                                                item?.answers?.forEach { answersItem ->
                                                    if (answersItem?.questionType?.equals(
                                                            QuestionType.Numeric_Field.name
                                                        ) == true
                                                    ) {
                                                        answerList.add(
                                                            SectionAnswerEntity(
                                                                id = 0,
                                                                optionId = 0,
                                                                didiId = item.beneficiaryId ?: 0,
                                                                questionId = answersItem?.questionId
                                                                    ?: 0,
                                                                villageId = item.villageId ?: 0,
                                                                actionType = answersItem?.section
                                                                    ?: TYPE_EXCLUSION,
                                                                weight = 0,
                                                                summary = answersItem?.summary,
                                                                optionValue = answersItem?.options?.get(
                                                                    0
                                                                )?.optionValue,
                                                                totalAssetAmount = answersItem?.totalWeight,
                                                                needsToPost = false,
                                                                answerValue = answersItem?.options?.get(
                                                                    0
                                                                )?.summary
                                                                    ?: BLANK_STRING,
                                                                type = answersItem?.questionType
                                                                    ?: QuestionType.RadioButton.name
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
                                                                            ?: 0
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
                                                                )?.optionId
                                                                    ?: 0,
                                                                didiId = item.beneficiaryId ?: 0,
                                                                questionId = answersItem?.questionId
                                                                    ?: 0,
                                                                villageId = item.villageId ?: 0,
                                                                actionType = answersItem?.section
                                                                    ?: TYPE_EXCLUSION,
                                                                weight = 0,
                                                                summary = answersItem?.summary,
                                                                optionValue = answersItem?.options?.get(
                                                                    0
                                                                )?.optionValue,
                                                                totalAssetAmount = answersItem?.totalWeight,
                                                                needsToPost = false,
                                                                answerValue = answersItem?.options?.get(
                                                                    0
                                                                )?.display
                                                                    ?: BLANK_STRING,
                                                                type = answersItem?.questionType
                                                                    ?: QuestionType.RadioButton.name
                                                            )
                                                        )
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
                                    val ex = ApiResponseFailException(answerApiResponse.message)
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
                }
            }
            catch (ex: Exception) {
                onCatchError(ex)
                showLoader.value = false
            } finally {
                prefRepo.savePref(LAST_UPDATE_TIME, System.currentTimeMillis())
                startRetryIfAny()
            }
        }
    }


    fun updateSelectedVillage() {
        prefRepo.saveSelectedVillage(villageList.value[villageSelected.value])
    }

    private fun fetchUserDetails(apiSuccess: () -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val response = apiService.userAndVillageListAPI(prefRepo.getAppLanguageId() ?: 2)
                withContext(Dispatchers.IO) {
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.let {
                            prefRepo.savePref(PREF_KEY_USER_NAME, it.username)
                            prefRepo.savePref(PREF_KEY_NAME, it.name)
                            prefRepo.savePref(PREF_KEY_EMAIL, it.email)
                            prefRepo.savePref(PREF_KEY_IDENTITY_NUMBER, it.identityNumber)
                            prefRepo.savePref(PREF_KEY_PROFILE_IMAGE, it.profileImage)
                            villageListDao.insertAll(it.villageList)
                            _villagList.emit(villageListDao.getAllVillages())
                            apiSuccess()
                        }

                        if (response.data == null)
                            showLoader.value = false
                    } else if (response.status.equals(FAIL, true)) {
                        withContext(Dispatchers.Main) {
                            showLoader.value = false
                        }
                    } else {
                        onError(tag = "VillageSelectionViewModel", "Error : ${response.message}")
                        withContext(Dispatchers.Main) {
                            showLoader.value = false
                        }
                    }
                }
            } catch (ex: Exception) {
                Log.d("VillageSelectionViewModel", "fetchUserDetails: catch called")
                onCatchError(ex, ApiType.VILLAGE_LIST_API)
                if (ex is HttpException) {
                    if (ex.response()?.code() == RESPONSE_CODE_UNAUTHORIZED || ex.response()?.code() == RESPONSE_CODE_CONFLICT) {
                        withContext(Dispatchers.Main) {
                            RetryHelper.tokenExpired.value = true
                        }
                    }
                }
            }
        }
    }

    fun saveVillageListAfterTokenRefresh(villageList: List<VillageEntity>) {
        _villagList.value = villageList
    }

    override fun onServerError(error: ErrorModel?) {
//        showLoader.value = false
//        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
//            _villagList.value = villageListDao.getAllVillages()
//        }
        job = CoroutineScope(Dispatchers.Main).launch {
            networkErrorMessage.value = error?.message.toString()
        }

    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        job = CoroutineScope(Dispatchers.Main).launch {
            networkErrorMessage.value = errorModel?.message.toString()
        }
    }

    fun startRetryIfAny() {
        Log.d("startRetryIfAny: ", "shouldRetyCalled")
        RetryHelper.retryApiList.forEach { apiType ->
            RetryHelper.retryApi(apiType)
        }
    }

}