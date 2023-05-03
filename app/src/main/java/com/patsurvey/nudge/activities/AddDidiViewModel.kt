package com.patsurvey.nudge.activities

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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


    fun addDidi(didiDetailsModel: DidiDetailsModel) {
        didiList.add(didiDetailsModel)
    }

    fun addDidiFromData(houseNumber: String, didiName: String, dadaName: String, caste: String, tola: String ) {
        didiList.add(DidiDetailsModel(didiList.size, didiName,tola, tola, caste, houseNumber, dadaName))
    }
}