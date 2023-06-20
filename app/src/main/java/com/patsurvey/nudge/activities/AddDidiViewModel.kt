package com.patsurvey.nudge.activities

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.compose.runtime.*
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.patsurvey.nudge.CheckDBStatus
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.CasteEntity
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.LastTolaSelectedEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.dao.*
import com.patsurvey.nudge.intefaces.LocalDbListener
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.request.AddDidiRequest
import com.patsurvey.nudge.model.request.EditWorkFlowRequest
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddDidiViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val casteListDao: CasteListDao,
    val tolaDao: TolaDao,
    val didiDao: DidiDao,
    val stepsListDao: StepsListDao,
    val villageListDao: VillageListDao,
    val lastSelectedTolaDao: LastSelectedTolaDao,
    val apiService: ApiService
) : BaseViewModel() {
    val houseNumber = mutableStateOf(BLANK_STRING)
    val didiName = mutableStateOf(BLANK_STRING)
    val dadaName = mutableStateOf(BLANK_STRING)
    val isDidiValid = mutableStateOf(true)
    val selectedCast = mutableStateOf(Pair(-1, ""))
    val selectedTola = mutableStateOf(Pair(-1, ""))
    private val _casteList = MutableStateFlow(listOf<CasteEntity>())
    val casteList: StateFlow<List<CasteEntity>> get() = _casteList

    private val _didiList = MutableStateFlow(listOf<DidiEntity>())
    val didiList: StateFlow<List<DidiEntity>> get() = _didiList

    private val _tolaList = MutableStateFlow(listOf<TolaEntity>())
    val tolaList: StateFlow<List<TolaEntity>> get() = _tolaList

    var tolaMapList by mutableStateOf(mapOf<String, List<DidiEntity>>())
        private set

    var filterTolaMapList by mutableStateOf(mapOf<String, List<DidiEntity>>())
        private set

    var filterDidiList by mutableStateOf(listOf<DidiEntity>())
        private set

    var villageId: Int = -1
    var stepId: Int = -1

    val isSocialMappingComplete = mutableStateOf(false)
    val isVoEndorsementComplete = mutableStateOf(false)
    val isPATSurveyComplete = mutableStateOf(false)
    val showLoader = mutableStateOf(false)
    val pendingDidiCount = mutableStateOf(0)
    val isTolaSynced = mutableStateOf(0)

    private var _markedNotAvailable = MutableStateFlow(mutableListOf<Int>())


    init {
        villageId = prefRepo.getSelectedVillage().id
        if (didiList.value.isNotEmpty()) {
            _didiList.value = emptyList()
        }
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO) {
                val localDidList = didiDao.getAllDidisForVillage(villageId)
                val updatedList = mutableListOf<DidiEntity>()
                localDidList.forEach {
                    if (it.cohortId != 0 && it.cohortName == BLANK_STRING) {
                        val tola = tolaDao.fetchSingleTola(it.cohortId)
                        if (tola != null) {
                            it.cohortName = tola.name
                            updatedList.add(it)
                        } else {
                            didiDao.deleteDidisForTola(it.cohortId)
                        }
                    } else {
                        updatedList.add(it)
                    }
                }
                _didiList.value = updatedList
                _casteList.emit(
                    casteListDao.getAllCasteForLanguage(
                        prefRepo.getAppLanguageId() ?: 2
                    )
                )
                _tolaList.emit(tolaDao.getAllTolasForVillage(villageId))
                if (lastSelectedTolaDao.getTolaCountForVillage(villageId = villageId) > 0) {
                    val selectedDBTola = lastSelectedTolaDao.getTolaForVillage(villageId)
                    withContext(Dispatchers.Main) {
                        selectedTola.value = Pair(selectedDBTola.tolaId, selectedDBTola.tolaName)
                    }

                }
                pendingDidiCount.value =
                    didiDao.getAllPendingPATDidisCount(prefRepo.getSelectedVillage().id)
                filterDidiList = didiList.value
                filterDidiList.filter { it.wealth_ranking == WealthRank.POOR.rank }.forEach {
                    getDidiAvailabilityStatus(it.id)
                }


            }
        }

        validateDidiDetails()
        getSocialMappingStepId()
        CheckDBStatus(this@AddDidiViewModel).isFirstStepNeedToBeSync(tolaDao){
                isTolaSynced.value = it
             }

    }

    fun saveLastSelectedTolaForVillage(tolaId: Int, tolaName: String) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO) {
                if (lastSelectedTolaDao.getTolaCountForVillage(villageId = villageId) > 0) {
                    lastSelectedTolaDao.updateSelectedTola(tolaId, tolaName, villageId)
                } else {
                    lastSelectedTolaDao.insertSelectedTola(
                        LastTolaSelectedEntity(
                            tolaId,
                            tolaName,
                            villageId
                        )
                    )
                }
            }
        }
    }

    fun fetchDidisFrommDB() {
        showLoader.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO) {
                val localDidList = didiDao.getAllDidisForVillage(villageId)
                val updatedList = mutableListOf<DidiEntity>()
                localDidList.forEach {
                    if (it.cohortId != 0 && it.cohortName == BLANK_STRING) {
                        val tola = tolaDao.fetchSingleTola(it.cohortId)
                        if (tola != null) {
                            it.cohortName = tola.name
                            updatedList.add(it)
                        }
                    } else {
                        updatedList.add(it)
                    }
                }
                _didiList.value = updatedList
                filterDidiList = didiList.value
                pendingDidiCount.value =
                    didiDao.getAllPendingPATDidisCount(prefRepo.getSelectedVillage().id)
                showLoader.value = false
            }
        }
    }

    fun validateDidiDetails() {
        isDidiValid.value =
            !(houseNumber.value.isEmpty() || didiName.value.isEmpty() || dadaName.value.isEmpty()
                    || selectedCast.value.first == -1 || selectedTola.value.first == -1)
    }

    fun saveDidiIntoDatabase(
        localDbListener: LocalDbListener,
        networkCallbackListener: NetworkCallbackListener
    ) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val ifDidiExist = didiDao.getDidiExist(
                didiName.value, houseNumber.value,
                dadaName.value, selectedTola.value.first, villageId
            )
            val selectedTolaFromDb = tolaDao.fetchSingleTola(selectedTola.value.first)
            if (ifDidiExist == 0) {
                var newId = didiDao.getAllDidis().size
               val lastDidi= didiDao.fetchLastDidiDetails()
                if(lastDidi !=null){
                    newId = lastDidi.id
                }

                didiDao.insertDidi(
                    DidiEntity(
                        newId + 1,
                        name = didiName.value,
                        guardianName = dadaName.value,
                        address = houseNumber.value,
                        castId = selectedCast.value.first,
                        castName = selectedCast.value.second,
                        cohortId = selectedTolaFromDb?.id ?: selectedTola.value.first,
                        cohortName = selectedTolaFromDb?.name ?: selectedTola.value.second,
                        relationship = HUSBAND_STRING,
                        villageId = prefRepo.getSelectedVillage().id,
                        localCreatedDate = System.currentTimeMillis(),
                        localModifiedDate = System.currentTimeMillis(),
                        shgFlag = SHGFlag.NOT_MARKED.value,
                        transactionId = "",
                        needsToPostRanking = false
                    )
                )

                _didiList.value = didiDao.getAllDidisForVillage(villageId)
                filterDidiList = didiDao.getAllDidisForVillage(villageId)
                setSocialMappingINProgress(stepId, villageId, object : NetworkCallbackListener {
                    override fun onSuccess() {

                    }

                    override fun onFailed() {
                        networkCallbackListener.onFailed()
                    }
                })
                withContext(Dispatchers.Main) {
                    prefRepo.savePref(DIDI_COUNT, didiList.value.size)
                    isSocialMappingComplete.value = false
                    isPATSurveyComplete.value = false
                    localDbListener.onInsertionSuccess()
                }
            } else {
                withContext(Dispatchers.Main) {
                    localDbListener.onInsertionFailed()
                }
            }
        }
    }

    fun updateDidiIntoDatabase(didiId: Int, networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _didiList.value
            didiList.value
            filterDidiList
            val updatedDidi = DidiEntity(
                id = didiId,
                name = didiName.value,
                guardianName = dadaName.value,
                address = houseNumber.value,
                castId = selectedCast.value.first,
                castName = selectedCast.value.second,
                cohortId = selectedTola.value.first,
                cohortName = selectedTola.value.second,
                relationship = HUSBAND_STRING,
                villageId = tolaList.value[getSelectedTolaIndex(selectedTola.value.first)].villageId,
                createdDate = _didiList.value.get(_didiList.value.map { it.id }
                    .indexOf(didiId)).createdDate ?:0,
                modifiedDate = _didiList.value.get(_didiList.value.map { it.id }
                    .indexOf(didiId)).modifiedDate ?:0,
                localCreatedDate = _didiList.value.get(_didiList.value.map { it.id }
                    .indexOf(didiId)).localCreatedDate?:0,
                localModifiedDate = System.currentTimeMillis(),
                shgFlag = _didiList.value.get(_didiList.value.map { it.id }
                    .indexOf(didiId)).shgFlag,
                beneficiaryProcessStatus = _didiList.value.get(_didiList.value.map { it.id }
                    .indexOf(didiId)).beneficiaryProcessStatus,
                patSurveyStatus = _didiList.value.get(_didiList.value.map { it.id }
                    .indexOf(didiId)).patSurveyStatus,
                section1Status = _didiList.value.get(_didiList.value.map { it.id }
                    .indexOf(didiId)).section1Status,
                section2Status = _didiList.value.get(_didiList.value.map { it.id }
                    .indexOf(didiId)).section2Status,
                transactionId = "",
                serverId = _didiList.value.get(_didiList.value.map { it.id }
                    .indexOf(didiId)).serverId,
                needsToPostRanking = _didiList.value.get(_didiList.value.map { it.id }
                    .indexOf(didiId)).needsToPostRanking
            )
            updatedDidi.guardianName
            didiDao.insertDidi(updatedDidi)

            _didiList.value = didiDao.getAllDidisForVillage(villageId)
            filterDidiList = didiDao.getAllDidisForVillage(villageId)

            setSocialMappingINProgress(stepId, villageId, object : NetworkCallbackListener {
                override fun onSuccess() {

                }

                override fun onFailed() {
                    networkCallbackListener.onFailed()
                }

            })
            withContext(Dispatchers.Main) {
                prefRepo.savePref(DIDI_COUNT, didiList.value.size)
                isSocialMappingComplete.value = false
                isPATSurveyComplete.value = false

            }

        }
    }

    private fun getSelectedTolaIndex(selectedTolaId: Int): Int {
        return tolaList.value.map { it.id }.indexOf(selectedTolaId)
    }

    fun filterList() {
        val map = mutableMapOf<String, MutableList<DidiEntity>>()
        didiList.value.forEachIndexed { _, didiDetailsModel ->
            if (map.containsKey(didiDetailsModel.cohortName)) {
                map[didiDetailsModel.cohortName]?.add(didiDetailsModel)
            } else {
                map[didiDetailsModel.cohortName] = mutableListOf(didiDetailsModel)
            }
        }
        tolaMapList = map
        filterTolaMapList = map

    }

    @SuppressLint("SuspiciousIndentation")
    fun performQuery(query: String, isTolaFilterSelected: Boolean) {
        if (!isTolaFilterSelected) {
            filterDidiList = if (query.isNotEmpty()) {
                val filteredList = ArrayList<DidiEntity>()
                didiList.value.forEach { didi ->
                    if (didi.name.lowercase().contains(query.lowercase())) {
                        filteredList.add(didi)
                    }
                }
                filteredList
            } else {
                didiList.value
            }
        } else {
            if (query.isNotEmpty()) {
                val fList = mutableMapOf<String, MutableList<DidiEntity>>()
                tolaMapList.keys.forEach { key ->
                    val newDidiList = ArrayList<DidiEntity>()
                    tolaMapList[key]?.forEach { didi ->
                        if (didi.name.lowercase().contains(query.lowercase())) {
                            newDidiList.add(didi)
                        }
                    }
                    if (newDidiList.isNotEmpty())
                        fList[key] = newDidiList
                }
                filterTolaMapList = fList
            } else {
                filterTolaMapList = tolaMapList
            }

        }
    }

    fun resetAllFields() {
        houseNumber.value = BLANK_STRING
        didiName.value = BLANK_STRING
        dadaName.value = BLANK_STRING
        selectedCast.value = Pair(-1, BLANK_STRING)

    }

    fun markSocialMappingComplete(villageId: Int, stepId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val mStepId = if (stepId == -1) {
                stepId
            } else
                stepId
            stepsListDao.markStepAsCompleteOrInProgress(
                mStepId,
                StepStatus.COMPLETED.ordinal,
                villageId
            )
            val existingList = villageListDao.getVillage(villageId).steps_completed
            val updatedCompletedStepsList = mutableListOf<Int>()
            if (!existingList.isNullOrEmpty()) {
                existingList.forEach {
                    updatedCompletedStepsList.add(it)
                }
            }
            updatedCompletedStepsList.add(stepId)
            villageListDao.updateLastCompleteStep(villageId, updatedCompletedStepsList)
            val stepDetails = stepsListDao.getStepForVillage(villageId, stepId)
            if (stepDetails.orderNumber < stepsListDao.getAllSteps().size) {
                stepsListDao.markStepAsInProgress(
                    (stepDetails.orderNumber + 1),
                    StepStatus.INPROGRESS.ordinal,
                    villageId
                )
                prefRepo.savePref("$VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_${villageId}", false)
                for (i in 1..5) {
                    prefRepo.savePref(getFormPathKey(getFormSubPath(FORM_C, i)), "")
                    prefRepo.savePref(getFormPathKey(getFormSubPath(FORM_D, i)), "")
                }
            }
        }
    }

    fun getSocialMappingStepId() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepList = stepsListDao.getAllSteps()
            withContext(Dispatchers.Main) {
                stepId = stepList[stepList.map { it.orderNumber }.sorted().indexOf(2)].id
            }
        }
    }


    fun isSocialMappingComplete(stepId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val isComplete =
                stepsListDao.isStepComplete(stepId, villageId) == StepStatus.COMPLETED.ordinal
            withContext(Dispatchers.Main) {
                isSocialMappingComplete.value = isComplete
                isPATSurveyComplete.value = isComplete
            }

        }
    }

    fun isVoEndorsementCompleteForVillage(villageId: Int) {
        val isComplete =
            prefRepo.getPref("$VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_${villageId}", false)
        isVoEndorsementComplete.value = isComplete
    }


    fun addDidisToNetwork(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val jsonDidi = JsonArray()
            val filteredDidiList = didiList.value.filter { it.needsToPost }
            if (filteredDidiList.isNotEmpty()) {
                for (didi in filteredDidiList) {
                    jsonDidi.add(AddDidiRequest.getRequestObjectForDidi(didi).toJson())
                }
                val response = apiService.addDidis(jsonDidi)
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.let {
                        response.data.forEach { didiFromNetwork ->
                            didiList.value.forEach { didi ->
                                didiDao.updateDidiServerId(villageId = prefRepo.getSelectedVillage().id,
                                    modifiedDate = didiFromNetwork.modifiedDate,
                                    createdDate = didiFromNetwork.createdDate,
                                    cohortId = didiFromNetwork.cohortId,
                                    castId = didiFromNetwork.castId,
                                    serverId = didiFromNetwork.id,
                                    guardianName = didiFromNetwork.guardianName,
                                    name = didiFromNetwork.name)
                                if (TextUtils.equals(didiFromNetwork.name, didi.name)) {
                                    didi.serverId = didiFromNetwork.id
                                    didi.createdDate = didiFromNetwork.createdDate
                                    didi.modifiedDate = didiFromNetwork.modifiedDate
                                }
                            }
                        }
                        networkCallbackListener.onSuccess()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        networkCallbackListener.onFailed()
                    }
                }
            }
        }
    }

    fun updateDidisNeedTOPostList(villageId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            updateTolaListWithIds(didiList, villageId)
        }
    }

    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        TODO("Not yet implemented")
    }

    private fun updateTolaListWithIds(newDidiList: StateFlow<List<DidiEntity>>, villageId: Int) {
        val oldDidiList = didiDao.getAllDidisForVillage(villageId)
        didiDao.deleteDidiForVillage(villageId)
        val didis = mutableListOf<DidiEntity>()
        newDidiList.value.forEach {
            didis.add(
                DidiEntity(
                    id = it.id,
                    name = it.name,
                    address = it.address,
                    guardianName = it.guardianName,
                    relationship = it.relationship,
                    castId = it.castId,
                    castName = it.castName,
                    cohortId = it.cohortId,
                    cohortName = it.cohortName,
                    wealth_ranking = it.beneficiaryProcessStatus?.find { it.name == StepType.WEALTH_RANKING.name }?.status
                        ?: WealthRank.NOT_RANKED.rank,
                    villageId = this.villageId,
                    needsToPost = false,
                    createdDate = it.createdDate,
                    needsToPostPAT = oldDidiList[oldDidiList.map { it.id }
                        .indexOf(it.id)].needsToPostPAT,
                    needsToPostRanking = oldDidiList[oldDidiList.map { it.id }
                        .indexOf(it.id)].needsToPostRanking,
                    modifiedDate = System.currentTimeMillis(),
                    beneficiaryProcessStatus = it.beneficiaryProcessStatus,
                    patSurveyStatus = oldDidiList[oldDidiList.map { it.id }
                        .indexOf(it.id)].patSurveyStatus,
                    section1Status = oldDidiList[oldDidiList.map { it.id }
                        .indexOf(it.id)].section1Status,
                    section2Status = oldDidiList[oldDidiList.map { it.id }
                        .indexOf(it.id)].section2Status,
                    shgFlag = oldDidiList[oldDidiList.map { it.id }.indexOf(it.id)].shgFlag,
                    transactionId = ""
                )
            )
        }
        didiDao.insertAll(didis)
    }

    fun callWorkFlowAPI(
        villageId: Int,
        stepId: Int,
        networkCallbackListener: NetworkCallbackListener
    ) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val dbResponse = stepsListDao.getStepForVillage(villageId, stepId)
                val stepList = stepsListDao.getAllStepsForVillage(villageId)
                if (dbResponse.workFlowId > 0) {
                    val response = apiService.editWorkFlow(
                        listOf(
                            EditWorkFlowRequest(dbResponse.workFlowId, StepStatus.COMPLETED.name)
                        )
                    )
                    withContext(Dispatchers.IO) {
                        if (response.status.equals(SUCCESS, true)) {
                            response.data?.let {
                                stepsListDao.updateWorkflowId(
                                    stepId,
                                    dbResponse.workFlowId,
                                    villageId,
                                    it[0].status
                                )
                            }
                        } else {
                            networkCallbackListener.onFailed()
                            onError(tag = "ProgressScreenViewModel", "Error : ${response.message}")
                        }
                    }
                }
                launch {
                    try {
                        stepList.forEach { step ->
                            if (step.id != stepId && step.orderNumber > dbResponse.orderNumber && step.workFlowId > 0) {
                                val inProgressStepResponse = apiService.editWorkFlow(
                                    listOf(
                                        EditWorkFlowRequest(
                                            step.workFlowId,
                                            StepStatus.INPROGRESS.name
                                        )
                                    )
                                )
                                if (inProgressStepResponse.status.equals(SUCCESS, true)) {
                                    inProgressStepResponse.data?.let {
                                        stepsListDao.updateWorkflowId(
                                            step.id,
                                            step.workFlowId,
                                            villageId,
                                            it[0].status
                                        )
                                    }
                                }
                            }
                        }
                    } catch (ex: Exception) {
                        onCatchError(ex, ApiType.WORK_FLOW_API)
                    }
                }
            } catch (ex: Exception) {
                networkCallbackListener.onFailed()
                onError(tag = "ProgressScreenViewModel", "Error : ${ex.localizedMessage}")
            }
        }
    }

    fun setSocialMappingINProgress(
        stepId: Int,
        villageId: Int,
        networkCallbackListener: NetworkCallbackListener
    ) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val step = stepsListDao.getStepForVillage(villageId, stepId)
            stepsListDao.markStepAsCompleteOrInProgress(
                stepId,
                StepStatus.INPROGRESS.ordinal,
                villageId
            )
            val completeStepList = stepsListDao.getAllCompleteStepsForVillage(villageId)
            completeStepList?.let {
                it.forEach { newStep ->
                    if (newStep.orderNumber > step.orderNumber) {
                        if (filterDidiList.isEmpty()) {
                            stepsListDao.markStepAsCompleteOrInProgress(
                                newStep.stepId,
                                StepStatus.NOT_STARTED.ordinal,
                                villageId = villageId
                            )
                        }
                        else {
                            stepsListDao.markStepAsCompleteOrInProgress(
                                newStep.id,
                                StepStatus.INPROGRESS.ordinal,
                                villageId
                            )
                        }
                    }
                }
            }
            completeStepList?.let {
                val apiRequest = mutableListOf<EditWorkFlowRequest>()
                it.forEach { newStep ->
                    if (newStep.orderNumber > step.orderNumber) {
                        if (newStep.workFlowId > 0) {
                            apiRequest.add(
                                EditWorkFlowRequest(
                                    newStep.workFlowId,
                                    StepStatus.INPROGRESS.name
                                )
                            )
                        }
                    }
                }
                if (apiRequest.isNotEmpty()) {
                    launch {
                        val response = apiService.editWorkFlow(apiRequest)
                        if (response.status.equals(SUCCESS)) {
                            response.data?.let { response ->
                                response.forEach { it ->
                                    stepsListDao.updateWorkflowId(
                                        stepId,
                                        it.id,
                                        villageId,
                                        it.status
                                    )
                                }
                            }
                        } else {
                            networkCallbackListener.onFailed()
                        }
                    }
                }
            }
        }
    }

    fun setDidiAsUnavailable(didiId: Int) {
        _markedNotAvailable.value = _markedNotAvailable.value.also {
            it.add(didiId)
        }
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiPatProgress = didiDao.getDidi(didiId).patSurveyStatus
            if (didiPatProgress == PatSurveyStatus.INPROGRESS.ordinal || didiPatProgress == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) {
                _didiList.value[_didiList.value.map { it.id }.indexOf(didiId)].patSurveyStatus =
                    PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal
                didiDao.updateQuesSectionStatus(
                    didiId,
                    PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal
                )
            } else {
                _didiList.value[_didiList.value.map { it.id }.indexOf(didiId)].patSurveyStatus =
                    PatSurveyStatus.NOT_AVAILABLE.ordinal
                didiDao.updateQuesSectionStatus(didiId, PatSurveyStatus.NOT_AVAILABLE.ordinal)
            }
            didiDao.updateModifiedDate(System.currentTimeMillis(),didiId)
            didiDao.updateNeedToPostPAT(true, didiId, prefRepo.getSelectedVillage().id)
            pendingDidiCount.value =
                didiDao.getAllPendingPATDidisCount(prefRepo.getSelectedVillage().id)
        }
    }

    fun updateDidiPatStatus(didiId: Int, patSurveyStatus: PatSurveyStatus) {
        _didiList.value[_didiList.value.map { it.id }.indexOf(didiId)].patSurveyStatus =
            patSurveyStatus.ordinal
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            didiDao.updateQuesSectionStatus(didiId, patSurveyStatus.ordinal)
        }
    }

    fun getDidiAvailabilityStatus(didiId: Int) {
        val prefValue = prefRepo.getPref("${PREF_DIDI_UNAVAILABLE}_$didiId", false)
        if (prefValue) {
            _markedNotAvailable.value = _markedNotAvailable.value.also {
                it.add(didiId)
            }
        }
    }

    fun checkIfTolaIsNotDeleted() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tola = tolaDao.fetchSingleTola(selectedTola.value.first)
            if (tola == null) {
                selectedTola.value = Pair(-1, "")
            }
        }
    }


    fun checkIfTolaIsUpdated() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
        val selectedTolaId = selectedTola.value.first
            val tola = tolaDao.fetchSingleTola(selectedTolaId)
            if (tola != null && tola.name != selectedTola.value.second) {
                selectedTola.value = Pair(tola.id ?: -1, tola.name ?: "")
            } else {
                selectedTola.value = Pair(-1, "")
            }
        }
    }

    fun getPatStepStatus(stepId: Int, callBack: (isComplete: Boolean) -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepStatus = stepsListDao.isStepComplete(stepId, villageId)
            withContext(Dispatchers.Main) {
                if (stepStatus == StepStatus.COMPLETED.ordinal) {
                    delay(100)
                    callBack(true)
                } else {
                    delay(100)
                    callBack(false)
                }
            }
        }
    }

    fun deleteDidiOffline(didi: DidiEntity, networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            didiDao.deleteDidiOffline(
                id = didi.id,
                activeStatus = DidiStatus.DIID_DELETED.ordinal,
                needsToPostDeleteStatus = true
            )
            _didiList.value = didiDao.getAllDidisForVillage(villageId)
            filterDidiList = didiDao.getAllDidisForVillage(villageId)

            setSocialMappingINProgress(stepId, villageId, object : NetworkCallbackListener {
                override fun onSuccess() {
                    networkCallbackListener.onSuccess()
                }

                override fun onFailed() {
                    networkCallbackListener.onFailed()
                }

            })
            withContext(Dispatchers.Main) {
                prefRepo.savePref(DIDI_COUNT, didiList.value.size)
                isSocialMappingComplete.value = false
                isPATSurveyComplete.value = false
            }
        }
    }

    fun deleteDidiFromNetwork(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didisToBeDeleted =
                didiDao.getDidisToBeDeleted(villageId = villageId, needsToPostDeleteStatus = true)
            if (didisToBeDeleted.isNotEmpty()) {
                val jsonArray = JsonArray()
                didisToBeDeleted.forEach { didi ->
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("id", didi.id)
                    jsonObject.addProperty("localModifiedDate", System.currentTimeMillis())
                    jsonArray.add(jsonObject)
                    val deleteDidiApiResponse = apiService.deleteDidi(jsonArray)
                    if (deleteDidiApiResponse.status.equals(SUCCESS)) {
                        didiDao.updateDeletedDidiNeedToPostStatus(didi.id, needsToPostDeleteStatus = false)
                        networkCallbackListener.onSuccess()
                    } else {
                        networkCallbackListener.onSuccess()
                    }
                }
            }
        }
    }
    fun fetchDidiDetails(didiId: Int){
            job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                val didi=didiDao.getDidi(didiId)

               didiName.value=didi.name
               dadaName.value=didi.guardianName
               houseNumber.value=didi.address
               selectedTola.value= Pair(didi.cohortId,didi.cohortName)
               selectedCast.value= Pair(didi.castId,didi.castName)
            }
    }

    fun getFormPathKey(subPath: String): String {
        //val subPath formPictureScreenViewModel.pageItemClicked.value
        //"${PREF_FORM_PATH}_${formPictureScreenViewModel.prefRepo.getSelectedVillage().name}_${subPath}"
        return "${PREF_FORM_PATH}_${prefRepo.getSelectedVillage().name}_${subPath}"
    }

    fun getFormSubPath(formName: String, pageNumber: Int): String {
        return "${formName}_page_$pageNumber"
    }

}