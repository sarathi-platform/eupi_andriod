package com.nrlm.baselinesurvey.ui.section_screen.viewmode

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.model.datamodel.Sections
import com.nrlm.baselinesurvey.ui.section_screen.domain.use_case.SectionListScreenUseCase
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.LoaderState
import com.nrlm.baselinesurvey.utils.SectionState
import com.nrlm.baselinesurvey.utils.SectionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SectionListScreenViewModel @Inject constructor(
    val sectionScreenUseCase: SectionListScreenUseCase
): BaseViewModel() {

    private val _loaderState = mutableStateOf<LoaderState>(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState

    private val _sectionsList = mutableStateOf<List<Sections>>(listOf())
    val sectionsList: State<List<Sections>> get() = _sectionsList

    private val _sectionItemStateList = mutableStateListOf<SectionState>()
    val sectionItemStateList: SnapshotStateList<SectionState> get() = _sectionItemStateList


    init {
        onEvent(LoaderEvent.UpdateLoaderState(true))
        _sectionsList.value = sectionScreenUseCase.getSectionListUseCase.invoke()
        sectionsList.value.forEach { section ->
            _sectionItemStateList.add(SectionState(section, SectionStatus.INPROGRESS))
        }

        /*sectionsList.value.forEach { section ->
            _sectionItemStateList.add(SectionState(section, if (section.sectionId == 2) SectionStatus.NOT_STARTED else SectionStatus.INPROGRESS))
        }*/

        /*sectionsList.value.forEach { section ->
            _sectionItemStateList.add(SectionState(section, if (section.sectionId == 2) SectionStatus.INPROGRESS else SectionStatus.COMPLETED))
        }*/

        onEvent(LoaderEvent.UpdateLoaderState(false))
    }



    override fun <T> onEvent(event: T) {
        when (event) {
            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
        }
    }

}
