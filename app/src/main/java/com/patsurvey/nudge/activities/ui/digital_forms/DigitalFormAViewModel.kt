package com.patsurvey.nudge.activities.ui.digital_forms

import androidx.lifecycle.viewModelScope
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.model.dataModel.DidiDetailsModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DigitalFormAViewModel @Inject constructor(
    prefRepo: PrefRepo
):BaseViewModel()  {
    private val _didiDetailList = MutableStateFlow(listOf<DidiDetailsModel>())
    val didiDetailList: StateFlow<List<DidiDetailsModel>> get() = _didiDetailList
    init {
        viewModelScope.launch {
            withContext(Dispatchers.Default){
                val detailsList= arrayListOf<DidiDetailsModel>()
                repeat(12){ detailsList+= DidiDetailsModel(id=it,"Radhika Singh: $it","Sunar Pahad")
                }
                _didiDetailList.emit(detailsList)
            }
        }
    }
}