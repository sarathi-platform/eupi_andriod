package com.sarathi.smallgroupmodule.ui.didiTab.viewModel

import androidx.compose.runtime.mutableStateOf
import com.nudge.core.helper.TranslationEnum
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DidiShgVerificationViewModel @Inject constructor(
) : BaseViewModel() {
    var isSubmitButtonEnable = mutableStateOf(false)

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                setTranslationConfig()
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
        }


    }


    override fun getScreenName(): TranslationEnum {
        return TranslationEnum.DidiShgVerificationScreen
    }
}
