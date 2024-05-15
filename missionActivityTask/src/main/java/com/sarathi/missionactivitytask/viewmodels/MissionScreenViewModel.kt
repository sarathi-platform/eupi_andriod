package com.sarathi.missionactivitytask.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarathi.missionactivitytask.domain.usecases.GetMissionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MissionScreenViewModel @Inject constructor(
    private val missionsUseCase: GetMissionsUseCase
) : ViewModel() {


    fun getMission() {

        viewModelScope.launch(Dispatchers.IO) {

            val missionList = missionsUseCase.getAllMission()
            Log.d("Mission", missionList.first().missionName)

        }

    }

}