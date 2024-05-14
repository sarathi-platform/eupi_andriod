package com.sarathi.missionactivitytask.viewmodels

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.sarathi.missionactivitytask.domain.usecases.GetMissionsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MissionScreenViewModel @Inject constructor(
    val missionsUseCase: GetMissionsUseCase
) : BaseViewModel() {


    init {

        viewModelScope.launch(Dispatchers.IO) {

            val missionList = missionsUseCase.getAllMission()
            Log.d("Mission", missionList.first().missionName)

        }

    }

}