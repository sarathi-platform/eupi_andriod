package com.sarathi.missionactivitytask.viewmodels

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MissionScreenViewModel @Inject constructor(
) : BaseViewModel() {


    init {

        viewModelScope.launch(Dispatchers.IO) {

//            Log.d("Mission", missionList.first().missionName)

        }

    }

}