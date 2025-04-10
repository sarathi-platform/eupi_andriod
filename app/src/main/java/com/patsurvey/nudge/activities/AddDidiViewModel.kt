package com.patsurvey.nudge.activities

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.patsurvey.nudge.CheckDBStatus
import com.patsurvey.nudge.MyApplication.Companion.appScopeLaunch
import com.patsurvey.nudge.activities.settings.TransactionIdRequest
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.database.CasteEntity
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.LastTolaSelectedEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.intefaces.LocalDbListener
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.request.AddDidiRequest
import com.patsurvey.nudge.model.request.EditDidiRequest
import com.patsurvey.nudge.model.request.EditWorkFlowRequest
import com.patsurvey.nudge.utils.ARG_FROM_PAT_SURVEY
import com.patsurvey.nudge.utils.AbleBodiedFlag
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.BPC_VERIFICATION_STEP_ORDER
import com.patsurvey.nudge.utils.DIDI_COUNT
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.FORM_C
import com.patsurvey.nudge.utils.FORM_D
import com.patsurvey.nudge.utils.HUSBAND_STRING
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PREF_DIDI_UNAVAILABLE
import com.patsurvey.nudge.utils.PREF_FORM_PATH
import com.patsurvey.nudge.utils.PREF_SOCIAL_MAPPING_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.QuestionType
import com.patsurvey.nudge.utils.SHGFlag
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.SummaryNavigation
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.TYPE_INCLUSION
import com.patsurvey.nudge.utils.VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_
import com.patsurvey.nudge.utils.WealthRank
import com.patsurvey.nudge.utils.getPatScoreEventName
import com.patsurvey.nudge.utils.getUniqueIdForEntity
import com.patsurvey.nudge.utils.longToString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@HiltViewModel
class AddDidiViewModel @Inject constructor(
    val addDidiRepository: AddDidiRepository
) : BaseViewModel() {
    val houseNumber = mutableStateOf(BLANK_STRING)
    val didiName = mutableStateOf(BLANK_STRING)
    val dadaName = mutableStateOf(BLANK_STRING)
    val isDidiValid = mutableStateOf(true)
    val exclusiveQuesCount = mutableStateOf(0)
    val inclusiveQuesCount = mutableStateOf(0)
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

    private lateinit var castList : List<CasteEntity>

    var villageId: Int = -1
    var stepId: Int = -1

    val isSocialMappingComplete = mutableStateOf(false)
    val isVoEndorsementComplete = mutableStateOf(false)
    val isPATSurveyComplete = mutableStateOf(false)
    val showLoader = mutableStateOf(false)
    val pendingDidiCount = mutableStateOf(0)
    val isTolaSynced = mutableStateOf(0)
    private var isPending = 0

    private var _markedNotAvailable = MutableStateFlow(mutableListOf<Int>())

    val villageEntity = mutableStateOf<VillageEntity?>(null)

    init {
        setVillage(addDidiRepository.getSelectedVillage().id)
        villageId = addDidiRepository.getSelectedVillage().id
        if (didiList.value.isNotEmpty()) {
            _didiList.value = emptyList()
        }
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO) {
                castList = addDidiRepository.getAllCasteForLanguage(
                    addDidiRepository.getAppLanguageId() ?: 2
                )
                getValidDidisFromDB(
                    addDidiRepository.prefRepo.getFromPage().equals(ARG_FROM_PAT_SURVEY, true)
                )
                val languageId = addDidiRepository.getAppLanguageId() ?: 2
                val casteList = addDidiRepository.getAllCasteForLanguage(
                    languageId = languageId
                )
                /*AnalyticsHelper.logEvent(
                    Events.CASTE_LIST_READ,
                    mapOf(
                        EventParams.LANGUAGE_ID to languageId,
                        EventParams.CASTE_LIST to "$casteList",
                        EventParams.FROM_SCREEN to "Add DidiScreen"
                    )
                )*/
                _casteList.emit(
                    casteList
                )
                _tolaList.value = addDidiRepository.getAllTolasForVillage(villageId)
                pendingDidiCount.value =
                    addDidiRepository.getAllPendingPATDidisCount(addDidiRepository.getSelectedVillage().id)
                filterDidiList = didiList.value
                filterDidiList.filter { it.wealth_ranking == WealthRank.POOR.rank }.forEach {
                    getDidiAvailabilityStatus(it.id)
                }


            }
        }

        validateDidiDetails()
        getSocialMappingStepId()
        CheckDBStatus(this@AddDidiViewModel).isFirstStepNeedToBeSync(addDidiRepository.tolaDao) {
            isTolaSynced.value = it
        }

    }

    fun getValidDidisFromDB(isComingFromSocialPage: Boolean = false) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val localDidList =
                if (isComingFromSocialPage) addDidiRepository.getAllDidisForVillageAscending(
                    villageId
                ) else addDidiRepository.getAllDidisForVillage(villageId)
            val updatedList = mutableListOf<DidiEntity>()
            localDidList.forEach {
                if (it.cohortId != 0 && it.cohortName == BLANK_STRING) {
                    val tola = addDidiRepository.fetchSingleTola(it.cohortId)
                    if (tola != null) {
                        it.cohortName = tola.name
                        updatedList.add(it)
                    } else {
                        addDidiRepository.deleteDidisForTola(it.cohortId)
                    }
                } else {
                    updatedList.add(it)
                }
            }
            _didiList.value = updatedList

        }
    }

    fun getCastName(castId : Int) : String{
        var castName = ""
        for(cast in castList){
            if(castId == cast.id)
                castName = cast.casteName
        }
        return castName
    }

    private fun setVillage(villageId: Int) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            var village = addDidiRepository.fetchVillageDetailsForLanguage(
                villageId,
                addDidiRepository.getAppLanguageId() ?: 2
            )
            withContext(Dispatchers.Main) {
                villageEntity.value = village
            }
        }
    }

    fun fetchLastSelectedTola(){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            if (addDidiRepository.getTolaCountForVillage(villageId = villageId) > 0) {
                val selectedDBTola = addDidiRepository.getTolaForVillage(villageId)
                withContext(Dispatchers.Main) {
                    selectedTola.value =
                        Pair(selectedDBTola.tolaId, selectedDBTola.tolaName)
                }

            }
        }
    }

    private fun deleteDidisToNetwork(networkCallbackListener: NetworkCallbackListener) {
        if (!isSyncEnabled(prefRepo = addDidiRepository.prefRepo)) {
            return
        }
        job = appScopeLaunch (Dispatchers.IO + exceptionHandler) {
            NudgeLogger.d("AddDidiViewModel", "deleteDidisToNetwork -> called")
            try {
                val didiList = addDidiRepository.getDidisToBeDeletedForVillage(
                    villageId = villageId,
                    activeStatus = DidiStatus.DIID_DELETED.ordinal,
                    needsToPostDeleteStatus = true,
                    transactionId = "",
                    serverId = 0
                )
                val jsonDidi = JsonArray()
                if (didiList.isNotEmpty()) {
                    for (didi in didiList) {
                        val jsonObject = JsonObject()
                        jsonObject.addProperty("id", didi.serverId)
                        jsonDidi.add(jsonObject)
                    }
                    NudgeLogger.d("AddDidiViewModel", "deleteDidisToNetwork -> didiList: $didiList")
                    val response = addDidiRepository.deleteDidi(jsonDidi)
                    NudgeLogger.d(
                        "AddDidiViewModel",
                        "deleteDidisToNetwork ->  response: status = ${response.status}, message = ${response.message}, data = ${response.data.toString()}"
                    )
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.let {
                            if((response.data[0].transactionId.isNullOrEmpty())) {
                                didiList.forEach { tola ->
                                    addDidiRepository.deleteDidi(tola.id)
                                }
                                checkDeleteDidiStatus(networkCallbackListener)
                            } else {
                                for (i in 0 until response.data.size){
                                    didiList[i].transactionId = response.data[i].transactionId
                                    didiList[i].transactionId?.let { it1 ->
                                        addDidiRepository.updateDidiTransactionId(
                                            didiList[i].id,
                                            it1
                                        )
                                    }
                                }
                                isPending = 2
                                startSyncTimerForDidiStatus(networkCallbackListener)
                            }
                        }
                    } else {
                        NudgeLogger.d("AddDidiViewModel", "deleteDidisToNetwork -> onFailed")
                        networkCallbackListener.onFailed()
                    }
                    if(!response.lastSyncTime.isNullOrEmpty()){
                        addDidiRepository.updateLastSyncTime(response.lastSyncTime)
                    }
                } else {
                    checkDeleteDidiStatus(networkCallbackListener)
                }
            } catch (ex: Exception) {
                networkCallbackListener.onFailed()
                NudgeLogger.d("AddDidiViewModel", "deleteDidisToNetwork -> onFailed")
                onError(tag = "AddDidiViewModel", "deleteDidisToNetwork -> Error : ${ex.localizedMessage}")
                onCatchError(ex, ApiType.DIDI_DELETE_API)
            }
        }
    }

    fun checkDeleteDidiStatus(networkCallbackListener: NetworkCallbackListener) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            NudgeLogger.d("AddDidiViewModel", "checkDeleteDidiStatus -> called")
            try {
                val didiList = addDidiRepository.fetchAllPendingDidiNeedToDelete(
                    DidiStatus.DIID_DELETED.ordinal,
                    "",
                    0
                )
                if(didiList.isNotEmpty()) {
                    val ids: ArrayList<String> = arrayListOf()
                    didiList.forEach { didi ->
                        didi.transactionId?.let { ids.add(it) }
                    }
                    NudgeLogger.d("AddDidiViewModel", "checkDeleteDidiStatus -> didiList: $didiList")
                    val response = addDidiRepository.getPendingStatus(TransactionIdRequest("", ids))
                    NudgeLogger.d("AddDidiViewModel", "checkDeleteDidiStatus ->  response: status = ${response.status}, message = ${response.message}, data = ${response.data.toString()}")
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.forEach { transactionIdResponse ->
                            didiList.forEach { didi ->
                                if (transactionIdResponse.transactionId == didi.transactionId) {
                                    addDidiRepository.deleteDidi(didi.id)
                                }
                            }
                        }
                        updateDidiToNetwork(networkCallbackListener)
                    } else {
                        NudgeLogger.d("AddDidiViewModel", "checkDeleteDidiStatus -> onFailed")
                        networkCallbackListener.onFailed()
                    }

                    if(!response.lastSyncTime.isNullOrEmpty()){
                        addDidiRepository.updateLastSyncTime(response.lastSyncTime)
                    }

                } else {
                    updateDidiToNetwork(networkCallbackListener)
                }
            } catch (ex: Exception) {
                networkCallbackListener.onFailed()
                NudgeLogger.d("AddDidiViewModel", "checkDeleteDidiStatus -> onFailed")
                onError(tag = "AddDidiViewModel", "checkDeleteDidiStatus -> Error : ${ex.localizedMessage}")
                onCatchError(ex, ApiType.STATUS_CALL_BACK_API)
            }
        }
    }

    fun updateDidiToNetwork(networkCallbackListener: NetworkCallbackListener){
        if (!isSyncEnabled(prefRepo = addDidiRepository.prefRepo)) {
            return
        }
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            NudgeLogger.d("AddDidiViewModel", "updateDidiToNetwork -> called")
            try {
                val didiList = addDidiRepository.fetchAllDidiNeedToUpdate(true, "", 0)
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
                    NudgeLogger.d("AddDidiViewModel", "updateDidiToNetwork -> didiList: $didiList")
                    val response = addDidiRepository.updateDidis(didiRequestList)
                    NudgeLogger.d("AddDidiViewModel", "updateDidiToNetwork ->  response: status = ${response.status}, message = ${response.message}, data = ${response.data.toString()}")
                    if (response.status.equals(SUCCESS, true)) {
                        if(response.data?.get(0)?.transactionId.isNullOrEmpty()) {
                            response.data?.let {
                                response.data.forEach { _ ->
                                    didiList.forEach { didi ->
                                        addDidiRepository.updateNeedToPost(didi.id, false)
                                    }
                                }
                            }
                            didiList.forEach { didiEntity ->
                                didiEntity.needsToPost = false
                                didiEntity.transactionId = ""
                                addDidiRepository.updateDidiDetailAfterSync(
                                    id = didiEntity.id,
                                    serverId = didiEntity.serverId,
                                    needsToPost = false,
                                    transactionId = "",
                                    createdDate = didiEntity.createdDate ?: 0,
                                    modifiedDate = didiEntity.modifiedDate ?: 0
                                )
                            }
                            checkUpdateDidiStatus(networkCallbackListener)
                        } else {
                            for (i in 0..(response.data?.size?.minus(1) ?: 0)) {
                                didiList[i].transactionId = response.data?.get(i)?.transactionId
                                didiList[i].transactionId?.let {
                                    addDidiRepository.updateDidiTransactionId(
                                        didiList[i].id,
                                        it
                                    )
                                }
                            }
                            isPending = 3
                            startSyncTimerForDidiStatus(networkCallbackListener)
                        }
                    } else {
                        NudgeLogger.d("AddDidiViewModel", "updateDidiToNetwork ->onFailed")
                        networkCallbackListener.onFailed()
                    }
                    if (!response.lastSyncTime.isNullOrEmpty()) {
                        addDidiRepository.updateLastSyncTime(response.lastSyncTime)
                    }
                } else {
                    checkUpdateDidiStatus(networkCallbackListener)
                }
            } catch (ex: Exception) {
                networkCallbackListener.onFailed()
                NudgeLogger.d("AddDidiViewModel", "updateDidiToNetwork -> onFailed")
                onError(
                    tag = "AddDidiViewModel",
                    "updateDidiToNetwork -> Error : ${ex.localizedMessage}"
                )
                onCatchError(ex, ApiType.DIDI_EDIT_API)
            }
        }
    }

    private fun checkUpdateDidiStatus(networkCallbackListener: NetworkCallbackListener) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            NudgeLogger.d("AddDidiViewModel", "checkUpdateDidiStatus -> called")
            try {
                val didiList = addDidiRepository.fetchAllPendingDidiNeedToUpdate(true, "", 0)
                if (didiList.isNotEmpty()) {
                    val ids: ArrayList<String> = arrayListOf()
                    didiList.forEach { tola ->
                        tola.transactionId?.let { ids.add(it) }
                    }
                    NudgeLogger.d(
                        "AddDidiViewModel",
                        "checkUpdateDidiStatus -> didiList: $didiList"
                    )
                    val response = addDidiRepository.getPendingStatus(TransactionIdRequest("", ids))
                    NudgeLogger.d(
                        "AddDidiViewModel",
                        "checkUpdateDidiStatus ->  response: status = ${response.status}, message = ${response.message}, data = ${response.data.toString()}"
                    )
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.forEach { transactionIdResponse ->
                            didiList.forEach { didi ->
                                if (transactionIdResponse.transactionId == didi.transactionId) {
                                    didi.transactionId = ""
                                    addDidiRepository.updateNeedToPost(didi.id, false)
                                    addDidiRepository.updateDidiTransactionId(didi.id, "")
                                }
                            }
                        }
                        NudgeLogger.d("AddDidiViewModel", "checkUpdateDidiStatus -> onFailed")
                        networkCallbackListener.onSuccess()
                    } else {
                        NudgeLogger.d("AddDidiViewModel", "checkUpdateDidiStatus -> onFailed")
                        networkCallbackListener.onFailed()
                    }
                } else {
                    NudgeLogger.d("AddDidiViewModel", "checkUpdateDidiStatus -> onSuccess")
                    networkCallbackListener.onSuccess()
                }
            } catch (ex: Exception) {
                networkCallbackListener.onFailed()
                NudgeLogger.d("AddDidiViewModel", "checkUpdateDidiStatus -> onFailed")
                onError(
                    tag = "AddDidiViewModel",
                    "checkUpdateDidiStatus -> Error : ${ex.localizedMessage}"
                )
                onCatchError(ex, ApiType.STATUS_CALL_BACK_API)
            }
        }
    }

    fun saveLastSelectedTolaForVillage(tolaId: Int, tolaName: String) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO) {
                if (addDidiRepository.getTolaCountForVillage(villageId = villageId) > 0) {
                    addDidiRepository.updateSelectedTola(tolaId, tolaName, villageId)
                } else {
                    addDidiRepository.insertSelectedTola(
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
                val localDidList = if (addDidiRepository.prefRepo.getFromPage().equals(
                        ARG_FROM_PAT_SURVEY, true
                    )
                ) addDidiRepository.getAllDidisForVillageAscending(villageId) else addDidiRepository.getAllDidisForVillage(
                    villageId
                )
                val updatedList = mutableListOf<DidiEntity>()
                localDidList.forEach {
                    if (it.cohortId != 0 && it.cohortName == BLANK_STRING) {
                        val tola = addDidiRepository.fetchSingleTola(it.cohortId)
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
                    addDidiRepository.getAllPendingPATDidisCount(addDidiRepository.getSelectedVillage().id)
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
        isOnline: Boolean,
        localDbListener: LocalDbListener,
        networkCallbackListener: NetworkCallbackListener
    ) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val ifDidiExist = addDidiRepository.getDidiExist(
                didiName.value, houseNumber.value,
                dadaName.value, selectedTola.value.first, villageId
            )
            val selectedTolaFromDb = addDidiRepository.fetchSingleTola(selectedTola.value.first)
            if (ifDidiExist == 0) {
                var newId = addDidiRepository.getAllDidis().size
                val lastDidi = addDidiRepository.fetchLastDidiDetails()
                if (lastDidi != null) {
                    newId = lastDidi.id
                }
                var didiEntity = DidiEntity(
                    newId + 1,
                    name = didiName.value.trim(),
                    guardianName = dadaName.value.trim(),
                    address = houseNumber.value.trim(),
                    castId = selectedCast.value.first,
                    castName = selectedCast.value.second,
                    cohortId = selectedTolaFromDb?.id ?: selectedTola.value.first,
                    cohortName = selectedTolaFromDb?.name ?: selectedTola.value.second,
                    relationship = HUSBAND_STRING,
                    villageId = addDidiRepository.getSelectedVillage().id,
                    localCreatedDate = System.currentTimeMillis(),
                    localModifiedDate = System.currentTimeMillis(),
                    shgFlag = SHGFlag.NOT_MARKED.value,
                    transactionId = "",
                    needsToPostRanking = false,
                    localUniqueId = getUniqueIdForEntity(),
                    ableBodiedFlag = AbleBodiedFlag.NOT_MARKED.value
                )
                addDidiRepository.insertDidi(didiEntity)

                addDidiRepository.saveEvent(
                    didiEntity,
                    EventName.ADD_DIDI,
                    EventType.STATEFUL
                )
                _didiList.value = addDidiRepository.getAllDidisForVillage(villageId)
                filterDidiList = addDidiRepository.getAllDidisForVillage(villageId)
                setSocialMappingINProgress(
                    stepId,
                    villageId,
                    isOnline,
                    object : NetworkCallbackListener {
                        override fun onSuccess() {

                        }

                        override fun onFailed() {
                            networkCallbackListener.onFailed()
                        }
                    })
                withContext(Dispatchers.Main) {
                    addDidiRepository.savePref(DIDI_COUNT, didiList.value.size)
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

    fun updateDidiIntoDatabase(
        didiId: Int, isOnline: Boolean,
        networkCallbackListener: NetworkCallbackListener,
        localDbListener: LocalDbListener
    ) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _didiList.value
            filterDidiList

            val ifDidiExist = addDidiRepository.getDidiExist(
                didiName.value, houseNumber.value,
                dadaName.value, selectedTola.value.first, villageId
            )
            if (ifDidiExist == 0) {
                val updatedDidi = DidiEntity(
                    id = didiId,
                    name = didiName.value.trim(),
                    guardianName = dadaName.value.trim(),
                    address = houseNumber.value.trim(),
                    castId = selectedCast.value.first,
                    castName = selectedCast.value.second,
                    cohortId = selectedTola.value.first,
                    cohortName = selectedTola.value.second,
                    relationship = HUSBAND_STRING,
                    wealth_ranking = _didiList.value.get(_didiList.value.map { it.id }
                        .indexOf(didiId)).wealth_ranking,
                    villageId = tolaList.value[getSelectedTolaIndex(selectedTola.value.first)].villageId,
                    createdDate = _didiList.value.get(_didiList.value.map { it.id }
                        .indexOf(didiId)).createdDate ?: 0,
                    modifiedDate = _didiList.value.get(_didiList.value.map { it.id }
                        .indexOf(didiId)).modifiedDate ?: 0,
                    localCreatedDate = _didiList.value.get(_didiList.value.map { it.id }
                        .indexOf(didiId)).localCreatedDate ?: 0,
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
                        .indexOf(didiId)).needsToPostRanking,
                    needsToPost = true,
                    localUniqueId = _didiList.value.get(_didiList.value.map { it.id }
                        .indexOf(didiId)).localUniqueId,
                    ableBodiedFlag = didiList.value.get(_didiList.value.map { it.id }
                        .indexOf(didiId)).ableBodiedFlag
                )
                val selectedTolaEntity =
                    addDidiRepository.fetchSingleTolaFromServerId(selectedTola.value.first)
                addDidiRepository.insertDidi(updatedDidi)
                addDidiRepository.saveEvent(
                    updatedDidi,
                    EventName.UPDATE_DIDI,
                    EventType.STATEFUL
                )

                _didiList.value = addDidiRepository.getAllDidisForVillage(villageId)
                filterDidiList = addDidiRepository.getAllDidisForVillage(villageId)

                if (isOnline) {
                    updateDidiToNetwork(updatedDidi, networkCallbackListener)
                } else {
                    networkCallbackListener.onSuccess()
                }

//            setSocialMappingINProgress(stepId, villageId, object : NetworkCallbackListener {
//                override fun onSuccess() {
//
//                }
//
//                override fun onFailed() {
//                    networkCallbackListener.onFailed()
//                }
//
//            })
                withContext(Dispatchers.Main) {
                    addDidiRepository.savePref(DIDI_COUNT, didiList.value.size)
//                isSocialMappingComplete.value = false
                    isPATSurveyComplete.value = false
                    localDbListener.onInsertionSuccess()

                }
            }else{
                withContext(Dispatchers.Main){
                    localDbListener.onInsertionFailed()
                }
            }

        }
    }

    private fun getSelectedTolaIndex(selectedTolaId: Int): Int {
        return tolaList.value.map { it.id }.indexOf(selectedTolaId)
    }

    fun filterList() {
        val map = mutableMapOf<String, MutableList<DidiEntity>>()
        didiList.value.forEachIndexed { _, didiDetailsModel ->
            if(getFromPage().equals(ARG_FROM_PAT_SURVEY, true)){
                if (didiDetailsModel.wealth_ranking != BLANK_STRING){
                    if (map.containsKey(didiDetailsModel.cohortName)) {
                        map[didiDetailsModel.cohortName]?.add(didiDetailsModel)
                    } else {
                        map[didiDetailsModel.cohortName] = mutableListOf(didiDetailsModel)
                    }
                }
            }else{
                if (map.containsKey(didiDetailsModel.cohortName)) {
                    map[didiDetailsModel.cohortName]?.add(didiDetailsModel)
                } else {
                    map[didiDetailsModel.cohortName] = mutableListOf(didiDetailsModel)
                }
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
            addDidiRepository.markStepAsCompleteOrInProgress(
                mStepId,
                StepStatus.COMPLETED.ordinal,
                villageId
            )
            addDidiRepository.updateNeedToPost(mStepId, villageId, true)
            val existingList = addDidiRepository.getVillage(villageId).steps_completed
            val updatedCompletedStepsList = mutableListOf<Int>()
            if (!existingList.isNullOrEmpty()) {
                existingList.forEach {
                    updatedCompletedStepsList.add(it)
                }
            }
            updatedCompletedStepsList.add(stepId)
            addDidiRepository.updateLastCompleteStep(villageId, updatedCompletedStepsList)
            val stepDetails = addDidiRepository.getStepForVillage(villageId, stepId)
            if (stepDetails.orderNumber < addDidiRepository.getAllSteps().size) {
                addDidiRepository.markStepAsInProgress(
                    (stepDetails.orderNumber + 1),
                    StepStatus.INPROGRESS.ordinal,
                    villageId
                )
                addDidiRepository.updateNeedToPost(stepDetails.id, villageId, true)
                addDidiRepository.savePref(
                    "$VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_${villageId}",
                    false
                )
                for (i in 1..5) {
                    addDidiRepository.savePref(getFormPathKey(getFormSubPath(FORM_C, i)), "")
                    addDidiRepository.savePref(getFormPathKey(getFormSubPath(FORM_D, i)), "")
                }
            }
        }
    }

    fun getSocialMappingStepId() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepList = addDidiRepository.getAllSteps()
            withContext(Dispatchers.Main) {
                stepId = stepList[stepList.map { it.orderNumber }.sorted().indexOf(2)].id
            }
        }
    }


    fun isSocialMappingComplete(stepId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val isComplete =
                addDidiRepository.isStepComplete(stepId, villageId) == StepStatus.COMPLETED.ordinal
            withContext(Dispatchers.Main) {
                isSocialMappingComplete.value = isComplete
                isPATSurveyComplete.value = isComplete
            }

        }
    }

    fun isVoEndorsementCompleteForVillage(villageId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepList =
                addDidiRepository.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }
            val isComplete = stepList[stepList.map { it.orderNumber }.indexOf(5)].isComplete
            Log.d(
                "DidiItemCard-isVoEndorsementCompleteForVillage: ",
                "step: ${stepList[stepList.map { it.orderNumber }.indexOf(5)].name}"
            )
            Log.d(
                "DidiItemCard-isVoEndorsementCompleteForVillage: ",
                "step isComplete: ${
                    stepList[stepList.map { it.orderNumber }.indexOf(5)].isComplete
                }"
            )
            isVoEndorsementComplete.value = isComplete == StepStatus.COMPLETED.ordinal
            Log.d(
                "DidiItemCard-isVoEndorsementCompleteForVillage: ",
                "isVoEndorsementComplete.valuee: ${isVoEndorsementComplete.value}"
            )
        }
    }


    fun addDidisToNetwork(networkCallbackListener: NetworkCallbackListener) {
        if (!isSyncEnabled(prefRepo = addDidiRepository.prefRepo)) {
            return
        }
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            NudgeLogger.d("AddDidiViewModel", "addDidisToNetwork called")
            try {
                val jsonDidi = JsonArray()
                val filteredDidiList = addDidiRepository.fetchAllDidiNeedToAdd(true, "", 0)
                if (filteredDidiList.isNotEmpty()) {
                    for (didi in filteredDidiList) {
                        val tola = addDidiRepository.fetchSingleTolaFromServerId(didi.cohortId)
                        if (tola != null) {
                            didi.cohortId = tola.serverId
                        }
                        jsonDidi.add(
                            AddDidiRequest.getRequestObjectForDidi(
                                didi,
                                cohortdeviceId = tola?.localUniqueId
                            ).toJson()
                        )
                    }
                    NudgeLogger.d("AddDidiViewModel", "addDidisToNetwork: didiList: $jsonDidi")
                    val response = addDidiRepository.addDidis(jsonDidi)
                    NudgeLogger.d(
                        "AddDidiViewModel",
                        "addDidisToNetwork: response:  response: status = ${response.status}, message = ${response.message}, data = ${response.data.toString()}"
                    )
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.let {
                            if (response.data[0].transactionId.isNullOrEmpty()) {
                                for (i in filteredDidiList.indices) {
                                    val didiFromNetwork = response.data[i]
                                    val didi = filteredDidiList[i]
                                    addDidiRepository.updateDidiDetailAfterSync(
                                        id = didi.id,
                                        modifiedDate = didiFromNetwork.modifiedDate,
                                        createdDate = didiFromNetwork.createdDate,
                                        serverId = didiFromNetwork.id,
                                        needsToPost = false,
                                        transactionId = ""
                                    )
                                }
                                response.data.forEach { didiFromNetwork ->
                                    didiList.value.forEach { didi ->
                                        if (TextUtils.equals(didiFromNetwork.name, didi.name)) {
                                            didi.serverId = didiFromNetwork.id
                                            didi.createdDate = didiFromNetwork.createdDate
                                            didi.modifiedDate = didiFromNetwork.modifiedDate
                                        }
                                    }
                                }
                                checkAddDidiStatus(networkCallbackListener)
                            } else {
                                for(i in filteredDidiList.indices){
                                    response.data[i].transactionId.let { it1 ->
                                        addDidiRepository.updateDidiTransactionId(
                                            filteredDidiList[i].id,
                                            it1
                                        )
                                    }
                                }
                                isPending = 1
                                startSyncTimerForDidiStatus(networkCallbackListener)
                            }
                        }
                    } else {
                        NudgeLogger.d("AddDidiViewModel", "addDidisToNetwork: onFailed")
                        networkCallbackListener.onFailed()
                    }
                    if(!response.lastSyncTime.isNullOrEmpty()){
                        addDidiRepository.updateLastSyncTime(response.lastSyncTime)
                    }
                } else {
                    checkAddDidiStatus(networkCallbackListener)
                }
            } catch (ex: Exception) {
                networkCallbackListener.onFailed()
                NudgeLogger.d("AddDidiViewModel", "addDidisToNetwork -> onFailed")
                onError(
                    tag = "AddDidiViewModel",
                    "addDidisToNetwork -> Error : ${ex.localizedMessage}"
                )
                onCatchError(ex, ApiType.DIDI_EDIT_API)
            }
        }
    }

    private fun startSyncTimerForDidiStatus(networkCallbackListener: NetworkCallbackListener) {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                when (isPending) {
                    1 -> {
                        checkAddDidiStatus(networkCallbackListener)
                    }

                    2 -> {
                        checkDeleteDidiStatus(networkCallbackListener)
                    }

                    3 -> {
                        checkUpdateDidiStatus(networkCallbackListener)
                    }
                }
            }
        }, 10000)
    }

    private fun checkAddDidiStatus(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            NudgeLogger.d("AddDidiViewModel", "checkAddDidiStatus: called")
            try {
                val didiList = addDidiRepository.fetchPendingDidi(true, "")
                if (didiList.isNotEmpty()) {
                    val ids: ArrayList<String> = arrayListOf()
                    didiList.forEach { tola ->
                        tola.transactionId?.let { ids.add(it) }
                    }
                    NudgeLogger.d("TransectWalkScreen", "checkAddDidiStatus -> didiList: $didiList")
                    val response = addDidiRepository.getPendingStatus(TransactionIdRequest("", ids))
                    NudgeLogger.d("TransectWalkScreen", "checkAddDidiStatus -> response: $response")
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.forEach { transactionIdResponse ->
                            didiList.forEach { didi ->
                                if (transactionIdResponse.transactionId == didi.transactionId) {
                                    didi.serverId = transactionIdResponse.referenceId
                                }
                            }
                        }
                        didiList.forEach { didiEntity ->
                            didiEntity.needsToPost = false
                            didiEntity.transactionId = ""
                            addDidiRepository.updateDidiDetailAfterSync(
                                id = didiEntity.id,
                                serverId = didiEntity.serverId,
                                needsToPost = false,
                                transactionId = "",
                                createdDate = didiEntity.createdDate ?: 0,
                                modifiedDate = didiEntity.modifiedDate ?: 0
                            )
                        }
                        deleteDidisToNetwork(networkCallbackListener)
                    } else {
                        NudgeLogger.d("TransectWalkScreen", "checkAddDidiStatus -> onFailed")
                        networkCallbackListener.onFailed()
                    }
                } else {
                    deleteDidisToNetwork(networkCallbackListener)
                }
            } catch (ex: Exception) {
                networkCallbackListener.onFailed()
                NudgeLogger.d("AddDidiViewModel", "checkAddDidiStatus -> onFailed")
                onError(
                    tag = "AddDidiViewModel",
                    "checkAddDidiStatus -> Error : ${ex.localizedMessage}"
                )
                onCatchError(ex, ApiType.STATUS_CALL_BACK_API)
            }
        }
    }

    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        /*TODO("Not yet implemented")*/
    }

    /*private fun updateTolaListWithIds(newDidiList: StateFlow<List<DidiEntity>>, villageId: Int) {
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
    }*/

    fun saveSocialMappingCompletionDate() {
        val currentTime = System.currentTimeMillis()
        addDidiRepository.savePref(
            PREF_SOCIAL_MAPPING_COMPLETION_DATE_ + addDidiRepository.getSelectedVillage().id,
            currentTime
        )
    }
    fun callWorkFlowAPI(
        villageId: Int,
        stepId: Int,
        networkCallbackListener: NetworkCallbackListener
    ) {
        if (!isSyncEnabled(prefRepo = addDidiRepository.prefRepo)) {
            return
        }
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            NudgeLogger.d("AddDidiViewModel", "callWorkFlowAPI -> called")
            try {
                val dbResponse = addDidiRepository.getStepForVillage(villageId, stepId)
                NudgeLogger.d("AddDidiViewModel", "callWorkFlowAPI -> dbResponse = $dbResponse")
                val stepList =
                    addDidiRepository.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }
                NudgeLogger.d("AddDidiViewModel", "callWorkFlowAPI -> stepList = $stepList")

                if (dbResponse.workFlowId > 0) {
                    val primaryWorkFlowRequest = listOf(
                        EditWorkFlowRequest(
                            stepList[stepList.map { it.orderNumber }.indexOf(2)].workFlowId,
                            StepStatus.COMPLETED.name, longToString(
                                addDidiRepository.getPref(
                                    PREF_SOCIAL_MAPPING_COMPLETION_DATE_ + addDidiRepository.getSelectedVillage().id,
                                    System.currentTimeMillis(),

                                    ),

                                ),
                            villageId = villageId,
                            programsProcessId = stepList[stepList.map { it.orderNumber }
                                .indexOf(2)].id
                        )
                    )
                    NudgeLogger.d("AddDidiViewModel", "callWorkFlowAPI -> primaryWorkFlowRequest = $primaryWorkFlowRequest")
                    val response = addDidiRepository.editWorkFlow(
                        primaryWorkFlowRequest
                    )
                    NudgeLogger.d("AddDidiViewModel", "callWorkFlowAPI -> response: status = ${response.status}, message = ${response.message}, data = ${response.data.toString()}")
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.let {
                            addDidiRepository.updateWorkflowId(
                                stepList[stepList.map { it.orderNumber }.indexOf(2)].id,
                                stepList[stepList.map { it.orderNumber }.indexOf(2)].workFlowId,
                                villageId,
                                it[0].status
                            )
                            NudgeLogger.d(
                                "AddDidiViewModel",
                                "callWorkFlowAPI -> stepsListDao.updateNeedToPost before: stepId = ${
                                    stepList[stepList.map { it.orderNumber }
                                        .indexOf(2)].id
                                }, villageId = $villageId, needToPost = false \n")

                            addDidiRepository.updateNeedToPost(stepList[stepList.map { it.orderNumber }
                                .indexOf(2)].id, villageId, false)

                            NudgeLogger.d("AddDidiViewModel", "callWorkFlowAPI -> stepsListDao.updateNeedToPost after \n")
                        }
                        networkCallbackListener.onSuccess()
                    } else {
                        networkCallbackListener.onFailed()
                        onError(tag = "AddDidiViewModel", "Error : ${response.message}")
                    }
                    if (!response.lastSyncTime.isNullOrEmpty()) {
                        addDidiRepository.updateLastSyncTime(response.lastSyncTime)
                    }
                }
                try {
                    NudgeLogger.d("AddDidiViewModel", "callWorkFlowAPI -> second try = called")
                    stepList.forEach { step ->
                        NudgeLogger.d("AddDidiViewModel", "callWorkFlowAPI -> step = $step")
                        NudgeLogger.d("AddDidiViewModel", "callWorkFlowAPI -> " +
                                "step.orderNumber > 2 && step.workFlowId > 0: " +
                                "${step.orderNumber > 2} && ${step.workFlowId > 0}")
                        if (step.orderNumber > 2 &&  step.workFlowId > 0) {
                            val inProgressStepRequest = listOf(
                                EditWorkFlowRequest(
                                    step.workFlowId,
                                    StepStatus.INPROGRESS.name,
                                    villageId = villageId,
                                    programsProcessId = step.id
                                )
                            )

                            NudgeLogger.d("AddDidiViewModel", "callWorkFlowAPI -> inProgressStepRequest = $inProgressStepRequest")

                            val inProgressStepResponse =
                                addDidiRepository.editWorkFlow(inProgressStepRequest)

                            NudgeLogger.d("AddDidiViewModel", "callWorkFlowAPI -> inProgressStepResponse: status = ${inProgressStepResponse.status}, message = ${inProgressStepResponse.message}, data = ${inProgressStepResponse.data.toString()}")

                            if (inProgressStepResponse.status.equals(SUCCESS, true)) {
                                inProgressStepResponse.data?.let {
                                    addDidiRepository.updateWorkflowId(
                                        step.id,
                                        step.workFlowId,
                                        villageId,
                                        it[0].status
                                    )
                                }
                                addDidiRepository.updateNeedToPost(stepId, villageId, false)
                                networkCallbackListener.onSuccess()
                            } else {
                                NudgeLogger.d(
                                    "AddDidiViewModel",
                                    "callWorkFlowAPI -> inProgressStepResponse = FAIL"
                                )
                                networkCallbackListener.onFailed()
                            }

                            if (!inProgressStepResponse.lastSyncTime.isNullOrEmpty()) {
                                addDidiRepository.updateLastSyncTime(inProgressStepResponse.lastSyncTime)
                            }
                        }
                    }
                } catch (ex: Exception) {
                    NudgeLogger.d("AddDidiViewModel", "callWorkFlowAPI -> second try- onFailed()")
                    networkCallbackListener.onFailed()
                    onCatchError(ex, ApiType.WORK_FLOW_API)
                }
            } catch (ex: Exception) {
                NudgeLogger.d("AddDidiViewModel", "callWorkFlowAPI -> onFailed()")
                networkCallbackListener.onFailed()
                onError(
                    tag = "AddDidiViewModel",
                    "callWorkFlowAPI -> Error : ${ex.localizedMessage}"
                )
                onCatchError(ex, ApiType.WORK_FLOW_API)
            }
        }
    }

    fun setSocialMappingINProgress(
        stepId: Int,
        villageId: Int,
        isOnline: Boolean,
        networkCallbackListener: NetworkCallbackListener
    ) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            NudgeLogger.d("AddDidiViewModel", "setSocialMappingINProgress -> called")
            try {
                val stepList =
                    addDidiRepository.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }
                NudgeLogger.d(
                    "AddDidiViewModel",
                    "setSocialMappingINProgress -> stepsList: $stepList \n\n"
                )

                NudgeLogger.d(
                    "AddDidiViewModel",
                    "setSocialMappingINProgress -> stepsListDao.markStepAsCompleteOrInProgress before " +
                            "stepId = ${
                                stepList[stepList.map { it.orderNumber }.indexOf(2)].id
                            },\n" +
                            "isComplete = StepStatus.INPROGRESS.ordinal,\n" +
                            "villageId = $villageId \n"
                )

                val socialMappingStep = stepList[stepList.map { it.orderNumber }.indexOf(2)]
                addDidiRepository.markStepAsCompleteOrInProgress(
                    stepId = socialMappingStep.id,
                    isComplete = StepStatus.INPROGRESS.ordinal,
                    villageId = villageId
                )
                if (socialMappingStep.isComplete == StepStatus.COMPLETED.ordinal) {
                    saveWorkflowEventIntoDb(
                        stepStatus = StepStatus.INPROGRESS,
                        villageId = villageId,
                        stepId = socialMappingStep.id
                    )
                }
                NudgeLogger.d(
                    "AddDidiViewModel",
                    "setSocialMappingINProgress -> stepsListDao.markStepAsCompleteOrInProgress after " +
                            "stepId = ${
                                stepList[stepList.map { it.orderNumber }.indexOf(2)].id
                            },\n" +
                            "isComplete = StepStatus.INPROGRESS.ordinal,\n" +
                            "villageId = $villageId \n"
                )
                addDidiRepository.updateNeedToPost(stepId, villageId, true)
                val completeStepList = addDidiRepository.getAllCompleteStepsForVillage(villageId)
                NudgeLogger.d(
                    "AddDidiViewModel",
                    "setSocialMappingINProgress -> completeStepList: $completeStepList \n\n"
                )
                completeStepList.let {
                    it.forEach { newStep ->
                        if (newStep.orderNumber > stepList[stepList.map { steps -> steps.orderNumber }
                                .indexOf(2)].orderNumber && newStep.orderNumber < BPC_VERIFICATION_STEP_ORDER) {
                            NudgeLogger.d(
                                "AddDidiViewModel",
                                "setSocialMappingINProgress -> newStep.orderNumber > stepList[stepList.map { steps -> steps.orderNumber }.indexOf(2)].orderNumber: true" +
                                        "newStep.orderNumber: ${newStep.orderNumber}"
                            )
                            if (filterDidiList.isEmpty()) {
                                addDidiRepository.markStepAsCompleteOrInProgress(
                                    newStep.id,
                                    StepStatus.NOT_STARTED.ordinal,
                                    villageId = villageId
                                )
                                saveWorkflowEventIntoDb(
                                    stepStatus = StepStatus.NOT_STARTED,
                                    villageId = villageId,
                                    stepId = newStep.id
                                )
                            } else {
                                addDidiRepository.markStepAsCompleteOrInProgress(
                                    newStep.id,
                                    StepStatus.INPROGRESS.ordinal,
                                    villageId
                                )
                                saveWorkflowEventIntoDb(
                                    stepStatus = StepStatus.INPROGRESS,
                                    villageId = villageId,
                                    stepId = newStep.id
                                )
                            }
                            addDidiRepository.updateNeedToPost(newStep.id, villageId, true)
                        } else {
                            NudgeLogger.d(
                                "AddDidiViewModel",
                                "setSocialMappingINProgress -> newStep.orderNumber > stepList[stepList.map { steps -> steps.orderNumber }.indexOf(2)].orderNumber: false, newStep.orderNumber: ${newStep.orderNumber}"
                            )
                        }
                    }
                }
                try {
                    if (isOnline && isSyncEnabled(prefRepo = addDidiRepository.prefRepo)) {
                        val apiRequest = mutableListOf<EditWorkFlowRequest>()
                        apiRequest.add(
                            EditWorkFlowRequest(
                                stepList[stepList.map { it.orderNumber }.indexOf(2)].workFlowId,
                                StepStatus.INPROGRESS.name,
                                villageId = villageId,
                                programsProcessId = stepList[stepList.map { it.orderNumber }
                                    .indexOf(2)].id
                            )
                        )
                        completeStepList.let {
                            it.forEach { newStep ->
                                if (newStep.orderNumber > stepList[stepList.map { it.orderNumber }
                                        .indexOf(2)].orderNumber && newStep.orderNumber < BPC_VERIFICATION_STEP_ORDER) {
                                    if (newStep.workFlowId > 0) {
                                        apiRequest.add(
                                            EditWorkFlowRequest(
                                                newStep.workFlowId,
                                                StepStatus.INPROGRESS.name,
                                                villageId = villageId,
                                                programsProcessId = stepList[stepList.map { it.orderNumber }
                                                    .indexOf(2)].id
                                            )
                                        )
                                    }
                                }
                            }
                            if (apiRequest.isNotEmpty()) {
                                NudgeLogger.d(
                                    "AddDidiViewModel",
                                    "setSocialMappingINProgress -> apiRequest: $apiRequest"
                                )
                                val response = addDidiRepository.editWorkFlow(apiRequest)
                                NudgeLogger.d(
                                    "AddDidiViewModel",
                                    "setSocialMappingINProgress -> response: status = ${response.status}, message = ${response.message}, data = ${response.data.toString()}"
                                )
                                if (response.status.equals(SUCCESS)) {
                                    response.data?.let { response ->
                                        response.forEach {
                                            addDidiRepository.updateWorkflowId(
                                                it.programsProcessId,
                                                it.id,
                                                villageId,
                                                it.status
                                            )
                                            addDidiRepository.updateNeedToPost(
                                                it.programsProcessId,
                                                villageId,
                                                false
                                            )
                                        }
                                        NudgeLogger.d(
                                            "AddDidiViewModel",
                                            "setSocialMappingINProgress -> onSuccess"
                                        )
                                        networkCallbackListener.onSuccess()
                                    }

                                } else {
                                    NudgeLogger.d(
                                        "AddDidiViewModel",
                                        "setSocialMappingINProgress -> onFailed"
                                    )
                                    networkCallbackListener.onFailed()
                                }

                                if (!response.lastSyncTime.isNullOrEmpty()) {
                                    addDidiRepository.updateLastSyncTime(response.lastSyncTime)
                                }
                            }
                        }
                    }
                } catch (ex: Exception) {
                    NudgeLogger.d("AddDidiViewModel", "setSocialMappingINProgress -> onFailed")
                    networkCallbackListener.onFailed()
                    onCatchError(ex, ApiType.WORK_FLOW_API)
                }
            } catch (ex: Exception) {
                networkCallbackListener.onFailed()
                onCatchError(ex, ApiType.WORK_FLOW_API)
            }
        }
    }

    fun setDidiAsUnavailable(didiId: Int) {
        _markedNotAvailable.value = _markedNotAvailable.value.also {
            it.add(didiId)
        }
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiPatProgress = addDidiRepository.getDidi(didiId).patSurveyStatus
            if (didiPatProgress == PatSurveyStatus.INPROGRESS.ordinal || didiPatProgress == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) {
                _didiList.value[_didiList.value.map { it.id }.indexOf(didiId)].patSurveyStatus =
                    PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal
                addDidiRepository.updateQuesSectionStatus(
                    didiId,
                    PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal
                )
            } else {
                _didiList.value[_didiList.value.map { it.id }.indexOf(didiId)].patSurveyStatus =
                    PatSurveyStatus.NOT_AVAILABLE.ordinal
                addDidiRepository.updateQuesSectionStatus(
                    didiId,
                    PatSurveyStatus.NOT_AVAILABLE.ordinal
                )
            }
            val updatedDidiEntity = addDidiRepository.getDidi(didiId)
            addDidiRepository.updateModifiedDate(System.currentTimeMillis(), didiId)
            addDidiRepository.updateNeedToPostPAT(
                true,
                didiId,
                addDidiRepository.getSelectedVillage().id
            )
            //TODO @Anupam check why not available case is not working.
            addDidiRepository.saveEvent(
                eventItem = updatedDidiEntity,
                eventName = EventName.SAVE_PAT_ANSWERS,
                eventType = EventType.STATEFUL
            )
            addDidiRepository.saveEvent(
                eventItem = updatedDidiEntity,
                eventName = getPatScoreEventName(
                    updatedDidiEntity,
                    addDidiRepository.prefRepo.isUserBPC()
                ),
                EventType.STATEFUL
            )
            pendingDidiCount.value =
                addDidiRepository.getAllPendingPATDidisCount(addDidiRepository.getSelectedVillage().id)
        }
    }

    fun updateDidiPatStatus(didiId: Int, patSurveyStatus: PatSurveyStatus) {
        _didiList.value[_didiList.value.map { it.id }.indexOf(didiId)].patSurveyStatus =
            patSurveyStatus.ordinal
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            addDidiRepository.updateQuesSectionStatus(didiId, patSurveyStatus.ordinal)
        }
    }

    fun getDidiAvailabilityStatus(didiId: Int) {
        val prefValue = addDidiRepository.getPref("${PREF_DIDI_UNAVAILABLE}_$didiId", false)
        if (prefValue) {
            _markedNotAvailable.value = _markedNotAvailable.value.also {
                it.add(didiId)
            }
        }
    }

    fun checkIfTolaIsNotDeleted() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tola = addDidiRepository.fetchSingleTola(selectedTola.value.first)
            if (tola == null) {
                selectedTola.value = Pair(-1, "")
            }
        }
    }


    fun checkIfTolaIsUpdated() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val selectedTolaId = selectedTola.value.first
            val tola = addDidiRepository.fetchSingleTola(selectedTolaId)
            if (tola != null && tola.name != selectedTola.value.second) {
                selectedTola.value = Pair(tola.id, tola.name)
            } else if (tola == null) {
                selectedTola.value = Pair(-1, "")
            }
        }
    }

    fun getPatStepStatus(stepId: Int, callBack: (isComplete: Boolean) -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepStatus = addDidiRepository.isStepComplete(stepId, villageId)
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

    fun deleteDidiOffline(
        didi: DidiEntity,
        isOnline: Boolean,
        isFilterSelected: Boolean=false,
        networkCallbackListener: NetworkCallbackListener
    ) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            addDidiRepository.deleteDidiOffline(
                id = didi.id,
                activeStatus = DidiStatus.DIID_DELETED.ordinal,
                needsToPostDeleteStatus = didi.serverId != 0
            )

            addDidiRepository.saveEvent(
                didi,
                EventName.DELETE_DIDI,
                EventType.STATEFUL
            )

            if (didi.serverId == 0)
                addDidiRepository.updateNeedToPost(id = didi.id, needsToPost = false)

            _didiList.value = addDidiRepository.getAllDidisForVillage(villageId)
            filterDidiList = addDidiRepository.getAllDidisForVillage(villageId)
            if(isFilterSelected)
                 filterList()

            if (filterDidiList.isEmpty()) {
                val currentStep = addDidiRepository.getStepForVillage(villageId, stepId)

                val stepList = addDidiRepository.getAllStepsForVillage(villageId)
                addDidiRepository.markStepAsCompleteOrInProgress(
                    stepId,
                    StepStatus.INPROGRESS.ordinal,
                    villageId
                )
                addDidiRepository.updateNeedToPost(stepId, villageId, true)
                stepList.forEach { newStep ->
                    if (newStep.orderNumber > currentStep.orderNumber) {
                        addDidiRepository.markStepAsCompleteOrInProgress(
                            newStep.id,
                            StepStatus.NOT_STARTED.ordinal,
                            villageId
                        )
                        addDidiRepository.updateNeedToPost(newStep.stepId, villageId, true)
                    }
                }
            }
            if (isOnline && isSyncEnabled(prefRepo = addDidiRepository.prefRepo)) {
                deleteDidisToNetwork(networkCallbackListener)
            } else {
                networkCallbackListener.onSuccess()
            }


            /*setSocialMappingINProgress(stepId, villageId, object : NetworkCallbackListener {
                override fun onSuccess() {
                    networkCallbackListener.onSuccess()
                }

                override fun onFailed() {
                    networkCallbackListener.onFailed()
                }

            })*/
            withContext(Dispatchers.Main) {
                addDidiRepository.savePref(DIDI_COUNT, didiList.value.size)
//                isSocialMappingComplete.value = false
                isPATSurveyComplete.value = false
            }
        }
    }

    fun deleteDidiFromNetwork(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val didisToBeDeleted =
                    addDidiRepository.getDidisToBeDeletedForVillage(
                        villageId = villageId,
                        activeStatus = DidiStatus.DIID_DELETED.ordinal,
                        needsToPostDeleteStatus = true,
                        transactionId = "",
                        serverId = 0
                    )
                if (didisToBeDeleted.isNotEmpty()) {
                    val jsonArray = JsonArray()
                    didisToBeDeleted.forEach { didi ->
                        if (didi.serverId != 0) {
                            val jsonObject = JsonObject()
                            jsonObject.addProperty("id", didi.id)
                            jsonObject.addProperty("localModifiedDate", System.currentTimeMillis())
                            jsonArray.add(jsonObject)
                            val deleteDidiApiResponse = addDidiRepository.deleteDidi(jsonArray)
                            if (deleteDidiApiResponse.status.equals(SUCCESS)) {
                                addDidiRepository.updateDeletedDidiNeedToPostStatus(
                                    didi.id,
                                    needsToPostDeleteStatus = false
                                )
                                networkCallbackListener.onSuccess()
                            } else {
                                networkCallbackListener.onSuccess()
                            }
                            if(!deleteDidiApiResponse.lastSyncTime.isNullOrEmpty()){
                                addDidiRepository.updateLastSyncTime(deleteDidiApiResponse.lastSyncTime)
                            }
                        } else {
                            networkCallbackListener.onSuccess()
                        }
                    }
                }
            } catch (ex: Exception) {
                onCatchError(ex, ApiType.DIDI_DELETE_API)
            }
        }
    }


    fun fetchDidiDetails(didiId: Int){
            job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                val didi = addDidiRepository.getDidi(didiId)
               didiName.value=didi.name
               dadaName.value=didi.guardianName
               houseNumber.value=didi.address
               selectedTola.value= Pair(didi.cohortId, didi.cohortName)
               selectedCast.value= Pair(didi.castId, didi.castName)
            }
    }

    fun getFormPathKey(subPath: String): String {
        //val subPath formPictureScreenViewModel.pageItemClicked.value
        //"${PREF_FORM_PATH}_${formPictureScreenViewModel.prefRepo.getSelectedVillage().name}_${subPath}"
        return "${PREF_FORM_PATH}_${addDidiRepository.getSelectedVillage().id}_${subPath}"
    }

    fun getFormSubPath(formName: String, pageNumber: Int): String {
        return "${formName}_page_$pageNumber"
    }

    fun checkIfLastStepIsComplete(
        currentStepId: Int,
        callBack: (isPreviousStepComplete: Boolean) -> Unit
    ) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepList =
                addDidiRepository.getAllStepsForVillage(addDidiRepository.getSelectedVillage().id)
            val currentStepIndex = stepList.map { it.id }.indexOf(currentStepId)

            withContext(Dispatchers.Main) {
                callBack(stepList.sortedBy { it.orderNumber }[currentStepIndex - 1].isComplete == StepStatus.COMPLETED.ordinal)
            }
        }
    }

    fun updateDidiToNetwork(didi: DidiEntity, networkCallbackListener: NetworkCallbackListener) {
        if (!isSyncEnabled(prefRepo = addDidiRepository.prefRepo)) {
            return
        }
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                if (didi.serverId != 0) {
                    val didiRequestList = arrayListOf<EditDidiRequest>()
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
                    val response = addDidiRepository.updateDidis(didiRequestList)
                    NudgeLogger.d(
                        "AddDidiViewModel",
                        "updateDidiToNetwork-> response: status = ${response.status}, message = ${response.message}, data = ${response.data.toString()}"
                    )
                    if (response.status.equals(SUCCESS, true)) {
                        addDidiRepository.updateNeedToPost(didi.id, false)
                        networkCallbackListener.onSuccess()
                        NudgeLogger.d(
                            "AddDidiViewModel",
                            "updateDidiToNetwork(didi: DidiEntity, networkCallbackListener: NetworkCallbackListener) -> onSuccess"
                        )
                    } else {
                        networkCallbackListener.onFailed()
                        NudgeLogger.d(
                            "AddDidiViewModel",
                            "updateDidiToNetwork(didi: DidiEntity, networkCallbackListener: NetworkCallbackListener) -> onFailed"
                        )
                    }
                    if (!response.lastSyncTime.isNullOrEmpty()) {
                        addDidiRepository.updateLastSyncTime(response.lastSyncTime)
                    }

                } else {
                    networkCallbackListener.onSuccess()
                    NudgeLogger.d(
                        "AddDidiViewModel",
                        "updateDidiToNetwork(didi: DidiEntity, networkCallbackListener: NetworkCallbackListener) -> onSuccess"
                    )
                }
            } catch (ex: Exception) {
                networkCallbackListener.onFailed()
                NudgeLogger.d(
                    "AddDidiViewModel",
                    "updateDidiToNetwork(didi: DidiEntity, networkCallbackListener: NetworkCallbackListener) -> onFailed"
                )
                onError(
                    tag = "AddDidiViewModel",
                    "updateDidiToNetwork(didi: DidiEntity, networkCallbackListener: NetworkCallbackListener) -> Error : ${ex.localizedMessage}"
                )
                onCatchError(ex, ApiType.DIDI_EDIT_API)
            }
        }
    }

    fun validateDidiToNavigate(didiId: Int, onNavigateToSummary: (Int) -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val questionExclusionAnswered =
                addDidiRepository.getAnswerForDidi(didiId = didiId, actionType = TYPE_EXCLUSION)
            val questionInclusionAnswered =
                addDidiRepository.getAnswerForDidi(didiId = didiId, actionType = TYPE_INCLUSION)
            val quesList = addDidiRepository.getAllQuestionsForLanguage(
                addDidiRepository.getAppLanguageId() ?: 2
            )
            val yesQuesCount = addDidiRepository.fetchOptionYesCount(
                didiId = didiId,
                QuestionType.RadioButton.name,
                TYPE_EXCLUSION
            )
            exclusiveQuesCount.value = quesList.filter { it.actionType == TYPE_EXCLUSION }.size
            inclusiveQuesCount.value = quesList.filter { it.actionType == TYPE_INCLUSION }.size
            if (questionInclusionAnswered.isNotEmpty()) {
                if (inclusiveQuesCount.value == questionInclusionAnswered.size) {
                    withContext(Dispatchers.Main) {
                        if (yesQuesCount > 0) {
                            onNavigateToSummary(SummaryNavigation.SECTION_1_PAGE.ordinal)
                        } else onNavigateToSummary(SummaryNavigation.SECTION_2_PAGE.ordinal)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onNavigateToSummary(SummaryNavigation.DIDI_CAMERA_PAGE.ordinal)
                    }
                }
            } else {
                if (questionExclusionAnswered.isNotEmpty()) {
                    if (exclusiveQuesCount.value == questionExclusionAnswered.size) {
                        withContext(Dispatchers.Main) {
                            onNavigateToSummary(SummaryNavigation.SECTION_1_PAGE.ordinal)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            onNavigateToSummary(SummaryNavigation.DIDI_CAMERA_PAGE.ordinal)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onNavigateToSummary(SummaryNavigation.DIDI_CAMERA_PAGE.ordinal)
                    }
                }
            }
        }
    }

    fun saveStepId(stepId: Int) {
        addDidiRepository.saveStepId(stepId)
    }

    fun getFromPage(): String {
        return addDidiRepository.getFromPage()
    }

    fun saveSummaryScreenOpenFrom(openFrom: Int) {
        addDidiRepository.saveSummaryScreenOpenFrom(openFrom)
    }

    fun saveQuestionScreenOpenFrom(openFrom: Int) {
        addDidiRepository.saveQuestionScreenOpenFrom(openFrom)
    }

    fun saveWorkflowEventIntoDb(stepStatus: StepStatus, villageId: Int, stepId: Int) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepEntity =
                addDidiRepository.getStepForVillage(villageId = villageId, stepId = stepId)
            val updateWorkflowEvent = addDidiRepository.createWorkflowEvent(
                eventItem = stepEntity,
                stepStatus = stepStatus,
                eventName = EventName.WORKFLOW_STATUS_UPDATE,
                eventType = EventType.STATEFUL,
                prefRepo = addDidiRepository.prefRepo
            )
            updateWorkflowEvent?.let { event ->
                addDidiRepository.saveEventToMultipleSources(event, listOf())
            }
        }
    }

    fun isComingPatScreen(): Boolean {
        return (addDidiRepository.prefRepo.getFromPage().equals(
            ARG_FROM_PAT_SURVEY, true
        ))
    }

}