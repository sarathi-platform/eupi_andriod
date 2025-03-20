package com.patsurvey.nudge.activities.ui.state_screen

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.nudge.core.database.entities.state.StateEntity
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.network.interfaces.ApiBaseUrlManager
import com.patsurvey.nudge.utils.NudgeLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StateScreenViewModel @Inject constructor(
    val prefs: PrefRepo
) : BaseViewModel() {
    private val _stateList = MutableStateFlow<List<StateEntity>?>(emptyList())
    val stateList = _stateList.asStateFlow()
    val list = mutableStateListOf<StateEntity>()
    val statePosition = mutableStateOf(0)

    init {
        fetchStateList()
    }

    fun fetchStateList() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                //  val list = languageRepository.getAllLanguages()
                if (list.isNullOrEmpty()) {
                    _stateList.value = getDefaultState()
                } else {
                    _stateList.value = list
                }
            } catch (ex: Exception) {
                NudgeLogger.e("LanguageViewModel", "fetchLanguageList: ", ex)
                _stateList.value = getDefaultState()
            }

        }
    }

    fun getDefaultState(): List<StateEntity> {
        return listOf(
            StateEntity(
                id = 1,
                orderNumber = 1,
                state = "Tripura",
                localName = "Tripura"
            ),
            StateEntity(id = 2, orderNumber = 2, state = "Assam", localName = "Assam"),
            StateEntity(id = 3, orderNumber = 3, state = "West Bengal", localName = "West Bengal"),
            StateEntity(id = 4, orderNumber = 4, state = "Rajasthan", localName = "Rajasthan"),
            StateEntity(id = 5, orderNumber = 5, state = "Meghalaya", localName = "Meghalaya"),
            StateEntity(id = 6, orderNumber = 6, state = "Gujarat", localName = "Gujarat"),
            StateEntity(id = 7, orderNumber = 7, state = "Jharkhand", localName = "Jharkhand"),
            StateEntity(
                id = 8,
                orderNumber = 8,
                state = "Uttar Pradesh",
                localName = "Uttar Pradesh"
            ),
            StateEntity(
                id = 9,
                orderNumber = 9,
                state = "Madhya Pradesh",
                localName = "Madhya Pradesh"
            ),
            StateEntity(
                id = 10,
                orderNumber = 10,
                state = "Uttarakhand",
                localName = "Uttarakhand"
            ),
        )

    }


    override fun onServerError(error: ErrorModel?) {
        networkErrorMessage.value = error?.message.toString()
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        networkErrorMessage.value = errorModel?.message.toString()
    }

    fun setBaseUrl(baseUrl: String) {
        ApiBaseUrlManager.updateBaseUrl(baseUrl)
    }

}