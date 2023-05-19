package com.patsurvey.nudge.activities.ui.progress

import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.*
import com.patsurvey.nudge.model.request.GetQuestionListRequest
import com.patsurvey.nudge.model.request.StepResultTypeRequest
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    val questionListDao: QuestionListDao
) : BaseViewModel() {

    private val _villagList = MutableStateFlow(listOf<VillageEntity>())
    val villageList: StateFlow<List<VillageEntity>> get() = _villagList

    val villageSelected = mutableStateOf(-1)
    val showLoader = mutableStateOf(false)

    fun isLoggedIn() = (prefRepo.getAccessToken()?.isNotEmpty() == true)

    init {
        showLoader.value=true
        fetchUserDetails{
            fetchVillageList()
        }
    }
    private fun fetchVillageList(){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                withContext(Dispatchers.IO) {
                    val villageList=villageListDao.getAllVillages()
                    val localStepsList = stepsListDao.getAllSteps()
                    val localTolaList = tolaDao.getAllTolas()
                    val localLanguageList = languageListDao.getAllLanguages()
                    if(localStepsList.isNotEmpty()){
                        stepsListDao.deleteAllStepsFromDB()
                    }
                    if(localTolaList.isNotEmpty()){
                        tolaDao.deleteTolaTable()
                    }
                    villageList.forEach { village->
                        launch {

                            val response=apiService.getStepsList(village.id)
                            val cohortResponse=apiService.getCohortFromNetwork(villageId = village.id)
                            val didiResponse=apiService.getDidisFromNetwork(villageId = village.id)
                            val didiRankingResponse=apiService.getDidisWithRankingFromNetwork(villageId = village.id,"Category",
                            StepResultTypeRequest(StepType.WEALTH_RANKING.name,ResultType.ALL.name)
                            )
                            if(response.status.equals(SUCCESS,true)){
                                response.data?.let {
                                    
                                    it.stepList.forEach { steps->
                                        steps.villageId=village.id
                                        steps.isComplete= findCompleteValue(steps.status).ordinal
                                    }
                                    stepsListDao.insertAll(it.stepList)
                                    prefRepo.savePref(
                                        PREF_PROGRAM_NAME,
                                        it.programName
                                    )
                                    showLoader.value = false

                                }


                            }

                            if(cohortResponse.status.equals(SUCCESS,true)){
                                cohortResponse.data?.let {
                                    tolaDao.insertAll(it)
                                }
                            }
                            if(didiResponse.status.equals(SUCCESS,true)){
                                didiResponse.data?.let {
                                    try {

                                       it.didiList.forEach { didi->
                                           var tolaName= BLANK_STRING
                                           var casteName= BLANK_STRING
                                           val singleTola=tolaDao.fetchSingleTola(didi.cohortId)
                                           val singleCaste=casteListDao.getCaste(didi.castId)
                                           singleTola?.let {
                                               tolaName=it.name
                                           }
                                           singleCaste?.let {
                                               casteName=it.casteName
                                           }
                                           didiDao.insertDidi(DidiEntity(id = didi.id,
                                               name = didi.name,
                                               address = didi.address,
                                               guardianName = didi.guardianName,
                                               relationship = didi.relationship,
                                               castId = didi.castId,
                                               castName =casteName,
                                               cohortId = didi.cohortId, villageId = village.id,
                                               cohortName = tolaName,
                                               needsToPost = false,
                                            beneficiaryProcessStatus = didi.beneficiaryProcessStatus))
                                       }
                                    }catch (ex:Exception){
                                        onError(tag = "VillageSelectionViewModel", "Error : ${response.message}")
                                        showLoader.value=false
                                    }
                                }
                                withContext(Dispatchers.Main) {
                                    showLoader.value = false
                                }
                            }
                            if(didiRankingResponse.status.equals(SUCCESS,true)){
                                didiRankingResponse.data?.let { didiRank->
                                    didiRank.beneficiaryList?.richDidi?.forEach { richDidi->
                                        richDidi?.id?.let { didiId->
                                            didiDao.updateDidiRank(didiId,WealthRank.RICH.rank)
                                        }
                                    }
                                    didiRank.beneficiaryList?.mediumDidi?.forEach {mediumDidi->
                                        mediumDidi?.id?.let { didiId->
                                            didiDao.updateDidiRank(didiId,WealthRank.MEDIUM.rank)
                                        }
                                    }
                                    didiRank.beneficiaryList?.poorDidi?.forEach { poorDidi->
                                        poorDidi?.id?.let { didiId->
                                            didiDao.updateDidiRank(didiId,WealthRank.POOR.rank)
                                        }

                                    }

                                }
                            }
                            else {
                                onError(tag = "VillageSelectionViewModel", "Error : ${response.message}")
                                showLoader.value=false
                            }

                        }
                    }
                    localLanguageList?.let {
                        localLanguageList.forEach { languageEntity ->
                            val quesListResponse = apiService.fetchQuestionListFromServer(
                                GetQuestionListRequest(languageEntity.id,1, PAT_SURVEY_CONSTANT)
                            )
                            // Fetch QuestionList from Server
                            if(quesListResponse.status.equals(SUCCESS,true)){
                                val localQuestionList=questionListDao.getAllQuestions()
                                if(localQuestionList.isNotEmpty()){
                                    questionListDao.deleteQuestionTable()
                                }
                               quesListResponse.data?.let { questionList ->
                                   questionList.listOfQuestionSectionList?.forEach { list ->
                                        list?.questionList?.forEach { question->
                                            question?.sectionOrderNumber= list.orderNumber
                                            question?.actionType= list.actionType
                                        }
                                       list?.questionList?.let {
                                           questionListDao.insertAll(it as List<QuestionEntity>)
                                       }
                                   }
                               }
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                onError(tag = "VillageSelectionViewModel", "Exception : ${ex.localizedMessage}")
                showLoader.value = false
            }
        }
    }


    fun updateSelectedVillage() {
        prefRepo.saveSelectedVillage(villageList.value[villageSelected.value])
    }

    private fun fetchUserDetails(apiSuccess:()->Unit) {
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
                        showLoader.value = false
                    }
                }
            } catch (ex: Exception) {
                onError(tag = "VillageSelectionViewModel", "Exception : ${ex.localizedMessage}")
                showLoader.value = false
            }
        }
    }
}