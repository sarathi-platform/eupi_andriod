package com.patsurvey.nudge.activities

import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.CasteEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.CasteListDao
import com.patsurvey.nudge.model.dataModel.DidiDetailsModel
import com.patsurvey.nudge.utils.BLANK_STRING
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
    val casteListDao: CasteListDao
): BaseViewModel() {
    val houseNumber = mutableStateOf(BLANK_STRING)
    val didiName = mutableStateOf(BLANK_STRING)
    val dadaName = mutableStateOf(BLANK_STRING)
    val selectedCast = mutableStateOf(Pair<Int,String>(0,""))
    val selectedTola = mutableStateOf(Pair<Int,String>(0,""))
    private val _casteList = MutableStateFlow(listOf<CasteEntity>())
    val casteList: StateFlow<List<CasteEntity>> get() = _casteList
    var didiList  =  mutableStateListOf<DidiDetailsModel>()
        private set

    var filterMapList  by mutableStateOf(mapOf<String, List<DidiDetailsModel>>())
        private set

init {

    job=CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
       withContext(Dispatchers.IO){
           casteListDao.insertCaste(CasteEntity(1,"Hindu"))
           casteListDao.insertCaste(CasteEntity(2,"Muslim"))
           casteListDao.insertCaste(CasteEntity(3,"Sikh"))
           casteListDao.insertCaste(CasteEntity(4,"Isai"))
           _casteList.emit(casteListDao.getAllCaste())
       }
    }

}
    fun addDidi(didiDetailsModel: DidiDetailsModel) {
        didiList.add(didiDetailsModel)
    }

    fun addDidiFromData(houseNumber: String, didiName: String, dadaName: String, caste: String, tola: String ) {
        didiList.add(DidiDetailsModel(didiList.size, didiName,tola, tola, caste, houseNumber, dadaName))
    }

    fun filterList(){
        val map = mutableMapOf<String, MutableList<DidiDetailsModel>>()
        didiList.forEachIndexed { index, didiDetailsModel ->
            if(map.containsKey(didiDetailsModel.tola)){
                map[didiDetailsModel.tola]?.add(didiDetailsModel)
            } else {
                map[didiDetailsModel.tola] = mutableListOf(didiDetailsModel)
            }
        }

        filterMapList = map
    }
}