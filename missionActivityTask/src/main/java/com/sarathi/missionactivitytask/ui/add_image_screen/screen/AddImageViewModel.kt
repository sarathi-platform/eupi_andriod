package com.sarathi.missionactivitytask.ui.add_image_screen.screen

import androidx.compose.runtime.mutableStateOf
import com.nudge.core.generateUUID
import com.sarathi.dataloadingmangement.domain.use_case.DocumentUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FormUseCase
import com.sarathi.missionactivitytask.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddImageViewModel @Inject constructor(
    private val documentUseCase: DocumentUseCase,
    private val formUseCase: FormUseCase
) : BaseViewModel() {
    val documentValues = mutableStateOf<ArrayList<DocumentUiModel>>(arrayListOf())
    val isButtonEnable = mutableStateOf<Boolean>(false)

    override fun <T> onEvent(event: T) {
    }

    fun saveMultiImage() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            documentValues.value.firstOrNull()?.filePath?.let {
                documentUseCase.saveDocumentToDB(
                    referenceId = generateUUID(),
                    documentValue = it
                )
            }
        }
    }

    fun checkButtonValidation() {
        isButtonEnable.value = documentValues.value.isNotEmpty()
    }

    fun updateFromTable() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            formUseCase.updateFormData(isFormGenerated = true)
        }
    }


}