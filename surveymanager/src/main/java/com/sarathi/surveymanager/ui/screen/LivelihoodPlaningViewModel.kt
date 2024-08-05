package com.sarathi.surveymanager.ui.screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.LivelihoodUseCase
import com.sarathi.dataloadingmangement.model.response.Asset
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.model.uiModel.livelihood.LivelihoodDropDownUiModel
import com.sarathi.dataloadingmangement.repository.liveihood.GetAssetsRepositoryImpl
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LivelihoodPlaningViewModel @Inject constructor(
    private val getAssetsRepository: GetAssetsRepositoryImpl,
    private val coreSharedPrefs: CoreSharedPrefs
) : BaseViewModel() {


    val isButtonEnable = mutableStateOf<Boolean>(false)
    private val _livelihood_drop_down = mutableStateOf<List<LivelihoodDropDownUiModel>>(emptyList())
    val livelihood_drop_down: State<List<LivelihoodDropDownUiModel>> get() = _livelihood_drop_down


    fun intiAssets() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _livelihood_drop_down.value=  getAssetsRepository.getAssets(coreSharedPrefs.getUniqueUserIdentifier(),
               coreSharedPrefs.getAppLanguage())
        }
//            isTaskStatusCompleted()
//            withContext(Dispatchers.Main) {
//                onEvent(LoaderEvent.UpdateLoaderState(false))
//            }
        }

    override fun <T> onEvent(event: T) {
    }
}



//    fun checkButtonValidation() {
//        questionUiModel.value.filter { it.isMandatory }.forEach { questionUiModel ->
//            if (questionUiModel.tagId.contains(DISBURSED_AMOUNT_TAG)) {
//                val disbursedAmount =
//                    if (TextUtils.isEmpty(questionUiModel.options?.firstOrNull()?.selectedValue)) 0 else questionUiModel.options?.firstOrNull()?.selectedValue?.toInt()
//                if (sanctionAmount != 0 && (disbursedAmount
//                        ?: 0) + totalRemainingAmount > sanctionAmount
//                ) {
//                    isButtonEnable.value = false
//                    return
//                }
//            }
//            val result = (questionUiModel.options?.filter { it.isSelected == true }?.size ?: 0) > 0
//            if (!result) {
//                isButtonEnable.value = false
//                return
//            }
//
//        }
//        isButtonEnable.value = true
//
//
//    }

//    fun saveButtonClicked() {
//        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
//            saveAnswerIntoDB()
//        }
//    }


//}