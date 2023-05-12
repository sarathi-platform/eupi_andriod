package com.patsurvey.nudge.activities

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.compose.runtime.*
import com.google.gson.JsonArray
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.CasteEntity
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.dao.*
import com.patsurvey.nudge.intefaces.LocalDbListener
import com.patsurvey.nudge.model.request.AddDidiRequest
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.DIDI_COUNT
import com.patsurvey.nudge.utils.HUSBAND_STRING
import com.patsurvey.nudge.utils.SUCCESS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    val showLoader = mutableStateOf(false)



    init {
        job=CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO){
                casteListDao.insertCaste(CasteEntity(1,"Hindu"))
                casteListDao.insertCaste(CasteEntity(2,"Muslim"))
                casteListDao.insertCaste(CasteEntity(3,"Sikh"))
                casteListDao.insertCaste(CasteEntity(4,"Christian"))

                _didiList.emit(didiDao.getAllDidisForVillage(villageId))

                _casteList.emit(casteListDao.getAllCaste())
                _tolaList.emit(tolaDao.getAllTolas())
                filterDidiList = didiList.value
            }
        }

        validateDidiDetails()
        getSocialMappingStepId()

        selectedTola.value= prefRepo.getLastSelectedTola() as Pair<Int, String>
        villageId = prefRepo.getSelectedVillage().id
    }

    fun fetchDidisFrommDB(){
        showLoader.value = true
        job=CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO){
                _didiList.emit(didiDao.getAllDidis())
                filterDidiList=didiList.value
                showLoader.value = false
            }
        }
    }
    fun validateDidiDetails(){
        isDidiValid.value = !(houseNumber.value.isEmpty() || didiName.value.isEmpty() || dadaName.value.isEmpty()
                || selectedCast.value.first==-1 || selectedTola.value.first==-1)
    }

    fun saveDidiIntoDatabase(localDbListener: LocalDbListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val ifDidiExist=didiDao.getDidiExist(didiName.value,houseNumber.value,dadaName.value,selectedTola.value.first)
            if(ifDidiExist==0) {
                val newId = didiDao.getAllDidis().size
                didiDao.insertDidi(
                    DidiEntity(
                        newId + 1,
                        name = didiName.value,
                        guardianName = dadaName.value,
                        address = houseNumber.value,
                        castId = selectedCast.value.first,
                        castName = selectedCast.value.second,
                        cohortId = selectedTola.value.first,
                        cohortName = selectedTola.value.second,
                        relationship = HUSBAND_STRING,
                        villageId = tolaList.value[getSelectedTolaIndex(selectedTola.value.first)].villageId
                    )
                )

                _didiList.emit(didiDao.getAllDidisForVillage(villageId))
                filterDidiList = didiDao.getAllDidisForVillage(villageId)
                withContext(Dispatchers.Main) {
                    prefRepo.savePref(DIDI_COUNT, didiList.value.size)
                    isSocialMappingComplete.value = false
                    localDbListener.onInsertionSuccess()
                }
            }else{
                withContext(Dispatchers.Main) {
                    localDbListener.onInsertionFailed()
                }
            }
        }
    }

    fun updateDidiIntoDatabase(didiId:Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            didiDao.updateDidi(
                DidiEntity(
                    didiId,
                    name = didiName.value,
                    guardianName = dadaName.value,
                    address = houseNumber.value,
                    castId = selectedCast.value.first,
                    castName = selectedCast.value.second,
                    cohortId = selectedTola.value.first,
                    cohortName = selectedTola.value.second,
                    relationship = HUSBAND_STRING,
                    villageId = tolaList.value[getSelectedTolaIndex(selectedTola.value.first)].villageId
                )
            )

            _didiList.emit(didiDao.getAllDidisForVillage(villageId))
            filterDidiList = didiDao.getAllDidisForVillage(villageId)
            withContext(Dispatchers.Main) {
                prefRepo.savePref(DIDI_COUNT, didiList.value.size)
                    isSocialMappingComplete.value = false

            }

        }
    }

    fun checkAndUpdateTolaList() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            if (tolaList.value.isEmpty()) {
                _tolaList.emit(tolaDao.getAllTolas())
            } else {
                return@launch
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
        filterTolaMapList=map

    }

   @SuppressLint("SuspiciousIndentation")
   fun performQuery(query: String, isTolaFilterSelected:Boolean){
       if(!isTolaFilterSelected){
       filterDidiList = if(query.isNotEmpty()) {
           val filteredList = ArrayList<DidiEntity>()
           didiList.value.forEach { didi ->
               if (didi.name.lowercase().contains(query.lowercase())) {
                   filteredList.add(didi)
               }
           }
           filteredList
       }else {
           didiList.value
        }
       }else{
           if(query.isNotEmpty()){
               val fList= mutableMapOf<String, MutableList<DidiEntity>>()
               tolaMapList.keys.forEach { key->
                val newDidiList= ArrayList<DidiEntity>()
                   tolaMapList[key]?.forEach { didi->
                       if (didi.name.lowercase().contains(query.lowercase())) {
                          newDidiList.add(didi)
                       }
                   }
                   if(newDidiList.isNotEmpty())
                        fList[key]=newDidiList
               }
               filterTolaMapList=fList
           }else{
               filterTolaMapList=tolaMapList
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
            stepsListDao.markStepAsComplete(mStepId)
            val existingList = villageListDao.getVillage(villageId).steps_completed
            val updatedCompletedStepsList = mutableListOf<Int>()
            if (!existingList.isNullOrEmpty()) {
                existingList.forEach {
                    updatedCompletedStepsList.add(it)
                }
            }
            updatedCompletedStepsList.add(stepId)
            villageListDao.updateLastCompleteStep(villageId, updatedCompletedStepsList)
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
            val isComplete = stepsListDao.isStepComplete(stepId)
            withContext(Dispatchers.Main) {
                isSocialMappingComplete.value = isComplete
            }

        }
    }


    fun addDidisToNetwork() {
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
                        response.data.forEach { it2 ->
                            didiList.value.forEach { didi->
                                if (TextUtils.equals(it2.name, didi.name)) {
                                    didi.id = it2.id
                                }
                            }

                        }
                        updateTolaListWithIds(didiList)
                        didiDao.setNeedToPost(
                            didiList.value.filter { it.needsToPost }.map { it.id },
                            false
                        )
                    }
                }
            }
        }
    }

    private fun updateTolaListWithIds(newDidiList: StateFlow<List<DidiEntity>>) {
        didiDao.deleteDidiTable()
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
                villageId=villageId,
                needsToPost = true)
            )
        }
        didiDao.insertAll(didis)
    }

}