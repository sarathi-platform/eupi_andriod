package com.sarathi.smallgroupmodule.ui.didiTab.viewModel

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupSubTabUiModel
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
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

    private val _totalSmallGroupCount: MutableState<Int> = mutableStateOf(0)
    val totalSmallGroupCount: State<Int> get() = _totalSmallGroupCount

    private val _didiList: MutableState<List<SubjectEntity>> = mutableStateOf(mutableListOf())
    val didiList: State<List<SubjectEntity>> get() = _didiList

    private val _smallGroupList: MutableState<List<SmallGroupSubTabUiModel>> =
        mutableStateOf(mutableListOf())
    val smallGroupList: State<List<SmallGroupSubTabUiModel>> get() = _smallGroupList

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                viewModelScope.launch(Dispatchers.IO) {
                    didiTabUseCase.fetchDidiDetailsFromNetworkUseCase.invoke()
                    didiTabUseCase.fetchSmallGroupFromNetworkUseCase.invoke()
                    delay(300)
                    _didiList.value = didiTabUseCase.fetchDidiDetailsFromDbUseCase.invoke()
                    _smallGroupList.value =
                        didiTabUseCase.fetchSmallGroupListsFromDbUseCase.invoke()
                    _totalDidiCount.value = didiList.value.size
                    _totalSmallGroupCount.value = smallGroupList.value.size
                }
            }
        }
    }

}

sealed class TestClass {
    data class TestDataLoadingEvent(val context: Context)
}