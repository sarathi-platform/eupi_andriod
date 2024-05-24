package com.sarathi.smallgroupmodule.ui.didiTab.viewModel

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import com.sarathi.smallgroupmodule.ui.didiTab.domain.use_case.DidiTabUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DidiTabViewModel @Inject constructor(
    val didiTabUseCase: DidiTabUseCase
) : BaseViewModel() {

    private val _totalDidiCount: MutableState<Int> = mutableStateOf(0)
    val totalCount: State<Int> get() = _totalDidiCount

    private val _didiList: MutableState<List<SubjectEntity>> = mutableStateOf(mutableListOf())
    val didiList: State<List<SubjectEntity>> get() = _didiList

    override fun <T> onEvent(event: T) {
        when (event) {
            is TestClass.TestDataLoadingEvent -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val userId = CoreSharedPrefs.getInstance(event.context).getUserId()
                    didiTabUseCase.fetchDidiDetailsFromNetworkUseCase.invoke(userId.toInt())
                    didiTabUseCase.fetchSmallGroupFromNetworkUseCase.invoke(userId.toInt())
                    delay(300)
                    _didiList.value = didiTabUseCase.fetchDidiDetailsFromDbUseCase.invoke()
                    _totalDidiCount.value = didiList.value.size
                }
            }
        }
    }

}

sealed class TestClass {
    data class TestDataLoadingEvent(val context: Context)
}