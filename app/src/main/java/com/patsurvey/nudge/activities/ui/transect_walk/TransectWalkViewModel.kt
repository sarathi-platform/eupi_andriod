package com.patsurvey.nudge.activities.ui.transect_walk

import android.text.TextUtils
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.gson.JsonArray
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.request.AddCohortRequest
import com.patsurvey.nudge.model.request.DeleteTolaRequest
import com.patsurvey.nudge.model.request.EditCohortRequest
import com.patsurvey.nudge.model.response.GetCohortResponseModel
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TransectWalkViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val apiInterface: ApiService,
    val tolaDao: TolaDao,
    val stepsListDao: StepsListDao,
    val villageListDao: VillageListDao
) : BaseViewModel() {

    val tolaList = mutableStateListOf<TolaEntity>()
    val villageEntity = mutableStateOf<VillageEntity?>(null)

    val isTransectWalkComplete = mutableStateOf(false)

    val showLoader = mutableStateOf(false)

    init {
//        fetchTolaList(villageId)

    }

    fun addTola(tola: Tola) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaItem = TolaEntity(
                id = 0,
                name = tola.name,
                type = CohortType.TOLA.type,
                latitude = tola.location.lat ?: 0.0,
                longitude = tola.location.long ?: 0.0,
                villageEntity.value?.id ?: 0
            )
            tolaDao.insert(tolaItem)
            withContext(Dispatchers.Main) {
                tolaList.add(tolaItem)
                prefRepo.savePref(TOLA_COUNT, tolaList.size)
                if (isTransectWalkComplete.value) {
                    isTransectWalkComplete.value = false
                }
            }
        }
    }

    /*fun addEmptyTola() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaItem = TolaEntity.createEmptyTolaForVillageId(villageEntity.value?.id ?: 0)
            tolaDao.insert(tolaItem)
            withContext(Dispatchers.Main) {
                tolaList.add(tolaItem)
                prefRepo.savePref(TOLA_COUNT, tolaList.size)
                if (isTransectWalkComplete.value) {
                    isTransectWalkComplete.value = false
                }
            }
        }
    }*/

    fun addTolasToNetwork() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val jsonTola = JsonArray()
            val filteredTolaList = tolaList.filter { it.needsToPost }
            if (filteredTolaList.isNotEmpty()) {
                for (tola in filteredTolaList) {
                    jsonTola.add(AddCohortRequest.getRequestObjectForTola(tola).toJson())
                }
                Log.d("TransectWalkViewModel", "$jsonTola")

                val response = apiInterface.addCohort(jsonTola)
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.let {
                        response.data.forEach { it2 ->
                            tolaList.forEach { tola ->
                                if (TextUtils.equals(it2.name, tola.name)) {
                                    tola.id = it2.id
                                }
                            }
                        }
                        updateTolaListWithIds(tolaList)
                        tolaDao.setNeedToPost(
                            tolaList.filter { it.needsToPost }.map { it.id },
                            false
                        )
                    }
                }
            }
        }
    }

    private fun updateTolaListWithIds(tolaList: SnapshotStateList<TolaEntity>) {
        tolaDao.deleteTolaTable()
        val tolas = mutableListOf<TolaEntity>()
        tolaList.forEach {
            tolas.add(
                TolaEntity(
                    id = it.id,
                    name = it.name,
                    type = it.type,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    villageId = it.villageId,
                    needsToPost = true,
                    status = it.status
                )
            )
        }
        tolaDao.insertAll(tolas)
    }

    fun removeTola(tolaId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
//                val jsonTola = JsonArray()
//                jsonTola.add(DeleteTolaRequest(tolaId).toJson())
//                val response = apiInterface.deleteCohort(jsonTola)
//                if (response.status.equals(SUCCESS)) {
//                    tolaDao.removeTola(tolaId)
//                } else {
                    tolaDao.deleteTolaOffline(tolaId, TolaStatus.TOLA_DELETED.ordinal)
                    tolaDao.setNeedToPost(listOf(tolaId), true)
//                }
                withContext(Dispatchers.Main) {
                    tolaList.removeAt(tolaList.map { it.id }.indexOf(tolaId))
                    if (isTransectWalkComplete.value)
                        isTransectWalkComplete.value = false
                }
            } catch (ex: Exception) {
                onError("TransectWalkViewModel", "${ex.message}: \n${ex.stackTraceToString()}")
            }
        }
    }

    fun updateTola(id: Int, newName: String, newLocation: LocationCoordinates?) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val updatedTola = TolaEntity(
                id = id,
                name = newName,
                type = CohortType.TOLA.type,
                latitude = newLocation?.lat ?: 0.0,
                longitude = newLocation?.long ?: 0.0,
                villageId = tolaList[getIndexOfTola(id)].villageId,
                needsToPost = true
            )
            tolaDao.insert(updatedTola)
//            val jsonTola = JsonArray()
//            jsonTola.add(EditCohortRequest.getRequestObjectForTola(updatedTola).toJson())
//            val response = apiInterface.editCohort(jsonTola)
//            if (response.status.equals(SUCCESS)) {
//                tolaDao.setNeedToPost(listOf(updatedTola.id), needsToPost = false)
//            }
            if (isTransectWalkComplete.value)
                isTransectWalkComplete.value = false
            withContext(Dispatchers.Main) {
                tolaList.set(getIndexOfTola(id), updatedTola)
            }
        }
    }

    fun fetchTolaList(villageId: Int) {
        showLoader.value = true

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val tolaItemList = mutableListOf<TolaEntity>()
                val response = apiInterface.getCohortFromNetwork(villageId)
                if (response.status.equals(SUCCESS)) {
                    if (response.data != null) {
                        response.data.forEach { it2 ->
                            tolaDao.insert(GetCohortResponseModel.convertToTolaEntity(it2))
                        }
                        tolaDao.getAllTolasForVillage(villageId).forEach { tola ->
                            tolaItemList.add(tola)
                        }
                    } else {
                        val tolaListFromDb = tolaDao.getAllTolasForVillage(villageId)
                        if (tolaListFromDb.isNotEmpty()) {
                            tolaListFromDb.forEach { tola ->
                                tolaItemList.add(tola)
                            }
                        }
                        withContext(Dispatchers.Main) {
                            showLoader.value = false
                        }
                    }
                } else {
                    val tolaListFromDb = tolaDao.getAllTolasForVillage(villageId)
                    if (tolaListFromDb.isNotEmpty()) {
                        tolaListFromDb.forEach { tola ->
                            tolaItemList.add(tola)
                        }
                    }
                    withContext(Dispatchers.Main) {
                        showLoader.value = false
                    }
                }
                withContext(Dispatchers.Main) {
                    tolaItemList.forEach {
                        tolaList.add(it)
                    }
                    prefRepo.savePref(TOLA_COUNT, tolaList.size)
                    showLoader.value = false
                }
            } catch (ex: Exception) {
                onError(tag = "TransectWalkViewModel", "Exception: ${ex.localizedMessage}")
                showLoader.value = false

            }
        }
    }

    fun setVillage(villageId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val village = villageListDao.getVillage(villageId)
            withContext(Dispatchers.Main) {
                villageEntity.value = village
            }
        }
    }

    private fun getIndexOfTola(id: Int): Int {
        return tolaList.map { it.id }.indexOf(id)
    }

    fun markTransectWalkComplete(villageId: Int, stepId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val existingList = villageListDao.getVillage(villageId).steps_completed
            val updatedCompletedStepsList = mutableListOf<Int>()
            if (!existingList.isNullOrEmpty()) {
                existingList.forEach {
                    updatedCompletedStepsList.add(it)
                }
            }
            updatedCompletedStepsList.add(stepId)
            villageListDao.updateLastCompleteStep(villageId, updatedCompletedStepsList)
            stepsListDao.markStepAsComplete(stepId, StepStatus.COMPLETED.ordinal)
        }
    }

    fun markTransectWalkIncomplete(stepId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            stepsListDao.markStepAsComplete(stepId, StepStatus.IN_PROGRESS.ordinal)
        }
    }

    fun isTransectWalkComplete(stepId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
//            val villageEntity = villageListDao.getVillage(prefRepo.getSelectedVillage().id)
            val isComplete = stepsListDao.isStepComplete(stepId) == StepStatus.COMPLETED.ordinal
//                villageEntity.steps_completed?.contains(stepId) ?: stepsListDao.isStepComplete(
//                    stepId
//                )

            withContext(Dispatchers.Main) {
                isTransectWalkComplete.value = isComplete
            }
        }
    }

}