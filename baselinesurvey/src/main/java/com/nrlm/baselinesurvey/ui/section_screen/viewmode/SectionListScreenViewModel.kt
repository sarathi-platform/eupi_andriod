package com.nrlm.baselinesurvey.ui.section_screen.viewmode

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.ui.section_screen.domain.use_case.SectionListScreenUseCase
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nrlm.baselinesurvey.utils.states.SectionState
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nrlm.baselinesurvey.utils.findItemBySectionId
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

    private val _sectionItemStateList = mutableStateOf(mutableListOf<SectionState>())
    val sectionItemStateList: State<List<SectionState>> get() = _sectionItemStateList


    fun init(didiId: Int) {
        onEvent(LoaderEvent.UpdateLoaderState(true))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val selectedLanguageId = sectionScreenUseCase.getSectionListUseCase.getSelectedLanguage()
                if (_sectionsList.value.isEmpty()) {
                    _sectionsList.value = sectionScreenUseCase.getSectionListUseCase.invoke(didiId, selectedLanguageId)
                    val sectionProgressForDidi = sectionScreenUseCase.getSectionProgressForDidiUseCase.invoke(didiId, selectedLanguageId)
                    sectionsList.value.forEachIndexed { index, section ->
                        val sectionState = SectionState(
                            section,
                            sectionStatus = if (sectionProgressForDidi.map { it.sectionId }.contains(section.sectionId)) {
                                SectionStatus.getSectionStatusFromOrdinal(
                                    sectionProgressForDidi.findItemBySectionId(section.sectionId).sectionStatus
                                )
                            } else {
                                SectionStatus.INPROGRESS
                            }
                        )
                        _sectionItemStateList.value.add(sectionState)
                    }
                }
                withContext(Dispatchers.Main) {
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
