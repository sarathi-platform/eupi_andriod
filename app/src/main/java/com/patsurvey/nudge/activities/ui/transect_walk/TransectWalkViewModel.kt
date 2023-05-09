package com.patsurvey.nudge.activities.ui.transect_walk

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.google.gson.JsonArray
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.request.AddCohortRequest
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.CohortType
import com.patsurvey.nudge.utils.LocationCoordinates
import com.patsurvey.nudge.utils.Tola
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
): BaseViewModel() {

    val tolaList = mutableStateListOf<TolaEntity>()
    val villageEntity = mutableStateOf<VillageEntity?>(null)

    val isTransectWalkComplete = mutableStateOf(false)

//    init {
//        fetchTolaList(villageId)
//    }

    fun addTola(tola: Tola){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaItem = TolaEntity(id = 0, name = tola.name, type = CohortType.TOLA.type, latitude = tola.location.lat ?: 0.0, longitude = tola.location.long ?: 0.0, villageEntity.value?.id ?: 0)
            tolaDao.insert(tolaItem)
            withContext(Dispatchers.Main) {
                tolaList.add(tolaItem)
            }
        }
    }

    fun addTolasToNetwork() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val jsonTola = JsonArray()
            for (tola in tolaList) {
                jsonTola.add(AddCohortRequest.getRequestObjectForTola(tola).toJson())
            }
            Log.d("TransectWalkViewModel", "$jsonTola")
            tolaDao.setNeedToPost(tolaList.filter { it.needsToPost }.map { it.id }, false)
            apiInterface.addCohort(jsonTola)
        }
    }

    fun removeTola(tolaId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            tolaDao.removeTola(tolaId)
            withContext(Dispatchers.Main) {
                tolaList.removeAt(tolaList.map { it.id }.indexOf(tolaId))
            }
        }
    }

    fun update(id: Int, newName: String, newLocation: LocationCoordinates?) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val updatedTola = TolaEntity(id = id, name = newName, type = CohortType.TOLA.type, latitude = newLocation?.lat ?: 0.0, longitude = newLocation?.long ?: 0.0, villageId = tolaList[getIndexOfTola(id)].villageId)
            tolaDao.insert(updatedTola)
            withContext(Dispatchers.Main) {
                tolaList.set(getIndexOfTola(id), updatedTola)
            }
        }
    }

    fun fetchTolaList(villageId: Int) {
        if (tolaList.isEmpty()) {
            job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                val tolaItemList = tolaDao.getAllTolasForVillage(villageId)
                withContext(Dispatchers.Main) {
                    tolaItemList.forEach {
                        tolaList.add(it)
                    }
                }
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
            villageListDao.updateLastCompleteStep(villageId, listOf(stepId))
            stepsListDao.markStepAsComplete(stepId)
        }
    }

    fun isTransectWalkComplete(stepId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val isComplete = stepsListDao.isStepComplete(stepId)
            withContext(Dispatchers.Main) {
                isTransectWalkComplete.value = isComplete
            }

        }
    }

}