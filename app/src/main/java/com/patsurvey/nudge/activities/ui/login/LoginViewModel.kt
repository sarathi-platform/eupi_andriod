package com.patsurvey.nudge.activities.ui.login

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.model.dataModel.LanguageSelectionModel
import com.patsurvey.nudge.network.NetworkResult
import com.patsurvey.nudge.repository.ConfigRepository
import com.patsurvey.nudge.utils.SUCCESS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val configRepository: ConfigRepository
):BaseViewModel() {
    val mobileNumber = mutableStateOf(TextFieldValue())
    private val _languageList= MutableStateFlow<List<String>?>(emptyList())
    val languageList=_languageList.asStateFlow()
        fun fetchDetails()=viewModelScope.launch {
            configRepository.getConfigurationDetails().collect(){
                when(it.status) {
                    NetworkResult.Status.SUCCESS -> {

                }
                    NetworkResult.Status.ERROR->
                        Log.d("TAG", "fetchDetails: ${it.message}")
                    NetworkResult.Status.LOADING->
                        Log.d("TAG", "fetchDetails: LOADING ")
                }
            }
        }

    //For testing purpose please delete when implementing you code
    fun createTestDb() {
        viewModelScope.launch(Dispatchers.IO) {
            configRepository.createTestDb()
        }
    }

}