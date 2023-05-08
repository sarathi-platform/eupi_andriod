package com.patsurvey.nudge.activities.ui.transect_walk

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.CohortType
import com.patsurvey.nudge.utils.LocationCoordinates
import com.patsurvey.nudge.utils.Tola
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TransectWalkViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val apiInterface: ApiService,
    val tolaDao: TolaDao,
    val villageListDao: VillageListDao
): BaseViewModel() {

    val tolaList = mutableStateListOf<TolaEntity>()
    val villageEntity = mutableStateOf<VillageEntity?>(null)

    init {
        fetchTolaList()
    }

    fun addTola(tola: Tola){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaItem = TolaEntity(id = 0, name = tola.name, type = CohortType.TOLA.type, latitude = tola.location.lat ?: 0.0, longitude = tola.location.long ?: 0.0, villageEntity.value?.id ?: 0)
            tolaDao.insert(tolaItem)
            withContext(Dispatchers.Main) {
                tolaList.add(tolaItem)
            }
        }
    }

    fun removeTola(tolaId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            tolaDao.removeTola(tolaId)
            withContext(Dispatchers.Main) {
//                tolaList.indexOf(tolaList.)
//                tolaList.remove(tolaId)
            }
        }
    }

    fun fetchTolaList() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaItemList = tolaDao.getAllTolas()
            withContext(Dispatchers.Main) {
                tolaItemList.forEach {
                    tolaList.add(it)
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

}