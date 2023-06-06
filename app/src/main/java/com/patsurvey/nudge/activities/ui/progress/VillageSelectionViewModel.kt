package com.patsurvey.nudge.activities.ui.progress


import android.content.Context
import android.os.Environment
import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.*
import com.patsurvey.nudge.database.dao.*
import com.patsurvey.nudge.download.FileType
import com.patsurvey.nudge.model.request.GetQuestionListRequest
import com.patsurvey.nudge.model.request.StepResultTypeRequest
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.network.model.ErrorModel
import com.patsurvey.nudge.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
            } else
                fetchVillageList()
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
                        isDownload = if (getVideoPath(context, it.id, fileType = FileType.VIDEO).exists()) DownloadStatus.DOWNLOADED.value else DownloadStatus.UNAVAILABLE.value
                    )
                    trainingVideoDao.insert(trainingVideoEntity)
                }
            } else {
                trainingVideos.forEach {
                    val videoIsDownloaded = if (getVideoPath(context, it.id, fileType = FileType.VIDEO).exists()) DownloadStatus.DOWNLOADED.value else DownloadStatus.UNAVAILABLE.value
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
                withContext(Dispatchers.IO) {
                    val villageList = villageListDao.getAllVillages()
                    val localStepsList = stepsListDao.getAllSteps()
                    val localTolaList = tolaDao.getAllTolas()
                    val localLanguageList = languageListDao.getAllLanguages()
                    val villageIdList:ArrayList<Int> = arrayListOf()
                    if (localStepsList.isNotEmpty()) {
                        stepsListDao.deleteAllStepsFromDB()
                    }
                    villageList.forEach { village ->
                        villageIdList.add(village.id)
                        launch {
                             stateId.value=village.stateId
                            val response = apiService.getStepsList(village.id)
                            val cohortResponse =
                                apiService.getCohortFromNetwork(villageId = village.id)
                            val didiResponse =
                                apiService.getDidisFromNetwork(villageId = village.id)
                            val didiRankingResponse = apiService.getDidisWithRankingFromNetwork(
                                villageId = village.id, "Category",
                                StepResultTypeRequest(
                                    StepType.WEALTH_RANKING.name,
                                    ResultType.ALL.name
                                )
                            )
                            if (response.status.equals(SUCCESS, true)) {
                                response.data?.let {

                                    it.stepList.forEach { steps ->
                                        steps.villageId = village.id
                                        steps.isComplete = findCompleteValue(steps.status).ordinal
                                    }
                                    stepsListDao.insertAll(it.stepList)
                                    prefRepo.savePref(
                                        PREF_PROGRAM_NAME,
                                        it.programName
                                    )
                                    showLoader.value = false

                                }


                            }

                            if (cohortResponse.status.equals(SUCCESS, true)) {
                                cohortResponse.data?.let {
                                    tolaDao.insertAll(it)
                                }
                            }
                            if (didiResponse.status.equals(SUCCESS, true)) {
                                didiResponse.data?.let {
                                    try {

                                        it.didiList.forEach { didi ->
                                            var tolaName = BLANK_STRING
                                            var casteName = BLANK_STRING
                                            val singleTola = tolaDao.fetchSingleTola(didi.cohortId)
                                            val singleCaste = casteListDao.getCaste(didi.castId)
                                            singleTola?.let {
                                                tolaName = it.name
                                            }
                                            singleCaste?.let {
                                                casteName = it.casteName
                                            }
                                            if (singleTola != null) {
                                                val wealthRanking = if (didi.beneficiaryProcessStatus.map { it.name }.contains(StepType.WEALTH_RANKING.name))
                                                    didi.beneficiaryProcessStatus[didi.beneficiaryProcessStatus.map { process -> process.name }.indexOf(StepType.WEALTH_RANKING.name)].status
                                                else
                                                    WealthRank.NOT_RANKED.rank
                                                val patSurveyStatus = if (didi.beneficiaryProcessStatus.map { it.name }.contains(StepType.PAT_SURVEY.name))
                                                    PatSurveyStatus.toInt(didi.beneficiaryProcessStatus[didi.beneficiaryProcessStatus.map { process -> process.name }.indexOf(StepType.PAT_SURVEY.name)].status)
                                                else
                                                    PatSurveyStatus.NOT_STARTED.ordinal
                                                val voEndorsementStatus = if (didi.beneficiaryProcessStatus.map { it.name }.contains(StepType.VO_ENDORSEMENT.name))
                                                    DidiEndorsementStatus.toInt(didi.beneficiaryProcessStatus[didi.beneficiaryProcessStatus.map { process -> process.name }.indexOf(StepType.PAT_SURVEY.name)].status)
                                                else
                                                    DidiEndorsementStatus.NOT_STARTED.ordinal
                                                didiDao.insertDidi(
                                                    DidiEntity(
                                                        id = didi.id,
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
                                                        patSurveyStatus = patSurveyStatus,
                                                        voEndorsementStatus = voEndorsementStatus,
                                                        needsToPostRanking = false,
                                                        createdDate = didi.createdDate,
                                                        modifiedDate = didi.modifiedDate,
                                                        beneficiaryProcessStatus = didi.beneficiaryProcessStatus,
                                                        shgFlag = SHGFlag.NOT_MARKED.value
                                                    )
                                                )
                                            }
                                        }
                                    } catch (ex: Exception) {
                                        onError(
                                            tag = "VillageSelectionViewModel",
                                            "Error : ${response.message}"
                                        )
                                        showLoader.value = false
                                    }
                                }
                                withContext(Dispatchers.Main) {
                                    showLoader.value = false
                                }
                            }
                            if (didiRankingResponse.status.equals(SUCCESS, true)) {
                                didiRankingResponse.data?.let { didiRank ->
                                    didiRank.beneficiaryList?.richDidi?.forEach { richDidi ->
                                        richDidi?.id?.let { didiId ->
                                            didiDao.updateDidiRank(didiId, WealthRank.RICH.rank)
                                        }
                                    }
                                    didiRank.beneficiaryList?.mediumDidi?.forEach { mediumDidi ->
                                        mediumDidi?.id?.let { didiId ->
                                            didiDao.updateDidiRank(didiId, WealthRank.MEDIUM.rank)
                                        }
                                    }
                                    didiRank.beneficiaryList?.poorDidi?.forEach { poorDidi ->
                                        poorDidi?.id?.let { didiId ->
                                            didiDao.updateDidiRank(didiId, WealthRank.POOR.rank)
                                        }

                                    }

                                }
                            } else {
                                onError(
                                    tag = "VillageSelectionViewModel",
                                    "Error : ${response.message}"
                                )
                                showLoader.value = false
                            }

                        }
                    }
                    localLanguageList?.let {
                        localLanguageList.forEach { languageEntity ->
                            val quesListResponse = apiService.fetchQuestionListFromServer(
                                GetQuestionListRequest(languageEntity.id, stateId.value, PAT_SURVEY_CONSTANT)
                            )
                            // Fetch QuestionList from Server
                            if (quesListResponse.status.equals(SUCCESS, true)) {
                                quesListResponse.data?.let { questionList ->
                                    questionList.listOfQuestionSectionList?.forEach { list ->
                                        list?.questionList?.forEach { question ->
                                            question?.sectionOrderNumber = list.orderNumber
                                            question?.actionType = list.actionType
                                            question?.languageId = languageEntity.id
                                            question?.surveyId = questionList.surveyId
                                            question?.thresholdScore = questionList.thresholdScore
                                            question?.surveyPassingMark = questionList.surveyPassingMark
                                        }
                                        list?.questionList?.let {
                                            questionListDao.insertAll(it as List<QuestionEntity>)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    answerDao.deleteAnswerTable()
                    numericAnswerDao.deleteNumericTable()
                    villageIdList?.let {
                        val answerApiResponse = apiService.fetchPATSurveyToServer(it)
                        if(answerApiResponse.status.equals(SUCCESS,true)){
                            answerApiResponse.data?.let {
                                val answerList:ArrayList<SectionAnswerEntity> = arrayListOf()
                                val numAnswerList:ArrayList<NumericAnswerEntity> = arrayListOf()
                                it.forEach {item->
                                    didiDao.updatePATProgressStatus(
                                        patSurveyStatus = item.patSurveyStatus?:0,
                                        section1Status = item.section1Status?:0,
                                        section2Status = item.section2Status?:0,
                                        didiId = item.beneficiaryId?:0)
                                   if(item?.answers?.isNotEmpty() == true){
                                       item?.answers?.forEach { answersItem ->
                                           if(answersItem?.questionType?.equals(QuestionType.Numeric_Field.name) == true){
                                               answerList.add(SectionAnswerEntity(
                                                   id = 0,
                                                   optionId = 0,
                                                   didiId = item.beneficiaryId?:0,
                                                   questionId =answersItem?.questionId?:0 ,
                                                   villageId = item.villageId?:0,
                                                   actionType = answersItem?.section?: TYPE_EXCLUSION,
                                                   weight = 0,
                                                   summary = answersItem?.summary,
                                                   optionValue = answersItem?.options?.get(0)?.optionValue,
                                                   totalAssetAmount = answersItem?.totalWeight,
                                                   needsToPost = false,
                                                   answerValue =  answersItem?.options?.get(0)?.summary?: BLANK_STRING,
                                                   type = answersItem?.questionType?:QuestionType.RadioButton.name))

                                               if(answersItem?.options?.isNotEmpty()==true){

                                                   answersItem?.options?.forEach { optionItem->
                                                       numAnswerList.add(NumericAnswerEntity(
                                                           id=0,
                                                           optionId = optionItem?.optionId?:0,
                                                           questionId = answersItem?.questionId?:0,
                                                           weight = optionItem?.weight?:0,
                                                           didiId = item.beneficiaryId?:0,
                                                           count = optionItem?.count?:0
                                                       ))
                                                   }

                                               }
                                           }else{
                                               answerList.add(SectionAnswerEntity(
                                                   id = 0,
                                                   optionId = answersItem?.options?.get(0)?.optionId?:0,
                                                   didiId = item.beneficiaryId?:0,
                                                   questionId =answersItem?.questionId?:0 ,
                                                   villageId = item.villageId?:0,
                                                   actionType = answersItem?.section?: TYPE_EXCLUSION,
                                                   weight = 0,
                                                   summary = answersItem?.summary,
                                                   optionValue = answersItem?.options?.get(0)?.optionValue,
                                                   totalAssetAmount = answersItem?.totalWeight,
                                                   needsToPost = false,
                                                   answerValue =  answersItem?.options?.get(0)?.summary?: BLANK_STRING,
                                                   type = answersItem?.questionType?:QuestionType.RadioButton.name))
                                           }

                                       }
                                   }

                               }
                                if(answerList.isNotEmpty()){
                                    answerDao.insertAll(answerList)
                                }
                                if(numAnswerList.isNotEmpty()){
                                    numericAnswerDao.insertAll(numAnswerList)
                                }
                            }
                        }
                    }
                }

            } catch (ex: Exception) {
                onCatchError(ex)
                showLoader.value = false
            } finally {
                prefRepo.savePref(LAST_UPDATE_TIME, System.currentTimeMillis())
                showLoader.value = false
            }
        }
    }


    fun updateSelectedVillage() {
        prefRepo.saveSelectedVillage(villageList.value[villageSelected.value])
    }

    private fun fetchUserDetails(apiSuccess: () -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val response = apiService.userAndVillageListAPI(prefRepo.getAppLanguageId() ?: 1)
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
                onCatchError(ex)
                withContext(Dispatchers.Main) {
                    showLoader.value = false
                }
            }
        }
    }

    override fun onServerError(error: ErrorModel?) {
//        showLoader.value = false
//        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
//            _villagList.value = villageListDao.getAllVillages()
//        }
        networkErrorMessage.value= error?.message.toString()
    }

}