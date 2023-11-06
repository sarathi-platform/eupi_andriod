package com.nrlm.baselinesurvey.ui.section_screen.viewmode

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.database.entity.SectionEntity
import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.model.datamodel.Sections
import com.nrlm.baselinesurvey.ui.section_screen.domain.use_case.SectionListScreenUseCase
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.LoaderState
import com.nrlm.baselinesurvey.utils.SectionState
import com.nrlm.baselinesurvey.utils.SectionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SectionListScreenViewModel @Inject constructor(
    val sectionScreenUseCase: SectionListScreenUseCase
): BaseViewModel() {

    private val _loaderState = mutableStateOf<LoaderState>(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState

    private val _sectionsList = mutableStateOf<List<SectionListItem>>(listOf())
    val sectionsList: State<List<SectionListItem>> get() = _sectionsList

    private val _sectionItemStateList = mutableStateListOf<SectionState>()
    val sectionItemStateList: SnapshotStateList<SectionState> get() = _sectionItemStateList


    fun init() {
        onEvent(LoaderEvent.UpdateLoaderState(true))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val selectedlanguageId = sectionScreenUseCase.getSectionListUseCase.getSelectedLanguage()
                _sectionsList.value = sectionScreenUseCase.getSectionListUseCase.invoke(selectedlanguageId)
                withContext(Dispatchers.Main) {
                    if (sectionsList.value.isEmpty()) {
                        sectionsList.value.forEach { section ->
                            _sectionItemStateList.add(
                                SectionState(
                                    section,
                                    SectionStatus.INPROGRESS
                                )
                            )
                        }
                    }
                    onEvent(LoaderEvent.UpdateLoaderState(false))
                }
            } catch (ex: Exception) {
                BaselineLogger.e("SectionListScreenViewModel", "init", ex)
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }

        }


        /*sectionsList.value.forEach { section ->
            _sectionItemStateList.add(SectionState(section, if (section.sectionId == 2) SectionStatus.NOT_STARTED else SectionStatus.INPROGRESS))
        }*/

        /*sectionsList.value.forEach { section ->
            _sectionItemStateList.add(SectionState(section, if (section.sectionId == 2) SectionStatus.INPROGRESS else SectionStatus.COMPLETED))
        }*/


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
