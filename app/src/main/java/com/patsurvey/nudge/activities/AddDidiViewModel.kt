package com.patsurvey.nudge.activities

import androidx.compose.runtime.*
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.model.dataModel.DidiDetailsModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddDidiViewModel @Inject constructor(
    val prefRepo: PrefRepo
): BaseViewModel() {
    var didiList  =  mutableStateListOf<DidiDetailsModel>()
        private set

    var filterMapList  by mutableStateOf(mapOf<String, List<DidiDetailsModel>>())
        private set


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