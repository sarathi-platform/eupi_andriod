package com.sarathi.missionactivitytask.ui.add_image_screen.screen

import android.text.TextUtils
import androidx.compose.runtime.mutableStateOf
import com.nudge.core.BLANK_STRING
import com.nudge.core.generateUUID
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.toDate
import com.sarathi.contentmodule.download_manager.FileType
import com.sarathi.dataloadingmangement.DELEGATE_COMM
import com.sarathi.dataloadingmangement.domain.use_case.DocumentEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.DocumentUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FormEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FormUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateTaskStatusUseCase
import com.sarathi.missionactivitytask.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SubmitPhysicalFormScreenViewModel @Inject constructor(
    private val documentUseCase: DocumentUseCase,
    private val formUseCase: FormUseCase,
    private val formEventWriterUseCase: FormEventWriterUseCase,
    private val documentEventWriterUseCase: DocumentEventWriterUseCase,
    private val coreSharedPrefs: CoreSharedPrefs,
    private val taskStatusUseCase: UpdateTaskStatusUseCase,
    private val getTaskUseCase: GetTaskUseCase,
    private val matStatusEventWriterUseCase: MATStatusEventWriterUseCase,
) : BaseViewModel() {
    val documentValues = mutableStateOf<ArrayList<DocumentUiModel>>(arrayListOf())
    val isButtonEnable = mutableStateOf<Boolean>(false)
    val totalDidi = mutableStateOf(0)
    override fun <T> onEvent(event: T) {
    }

    fun saveMultiImage(activityId: Int) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            documentValues.value.firstOrNull()?.filePath?.let {
                documentUseCase.saveDocumentToDB(
                    referenceId = generateUUID(),
                    documentValue = it,
                    activityId = activityId
                )
            }
        }
    }

    fun checkButtonValidation() {
        isButtonEnable.value = documentValues.value.isNotEmpty()
    }

    fun setTotalDidi(activityId: Int) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            totalDidi.value = formUseCase.getNonGeneratedFormSummaryData(activityId = activityId)
                .distinctBy { it.taskid }.size
        }
    }

    fun updateFromTable(activityId: Int, taskIdList: String) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val formGeneratedDate = System.currentTimeMillis().toDate().toString()
            var subjectType: String = BLANK_STRING
            formUseCase.getNonGeneratedFormSummaryData(activityId = activityId).forEach {
                subjectType = it.subjectType
                it.isFormGenerated = true
                it.formGenerateDate = formGeneratedDate
                formEventWriterUseCase.writeFormEvent(BLANK_STRING, formEntity = it)
                formUseCase.updateFormData(
                    isFormGenerated = true,
                    generatedDate = formGeneratedDate,
                    localReferenceId = it.localReferenceId
                )
            }
            documentValues.value.firstOrNull()?.filePath?.split(DELEGATE_COMM)?.forEach {
                documentEventWriterUseCase.writeSaveDocumentEvent(
                    generatedDate = formGeneratedDate,
                    documentType = FileType.IMAGE.name,
                    documentName = it,
                    activityId = activityId
                )

            }

            updateTaskStatus(taskIdList, subjectType)
        }
    }

    fun getPrefixFileName(): String {
        return "${coreSharedPrefs.getMobileNo()}_${coreSharedPrefs.getUserRole()}_form_attachment_"
    }

    suspend fun updateTaskStatus(taskIdList: String, subjectType: String) {
        if (!TextUtils.isEmpty(taskIdList)) {
            taskIdList.split(DELEGATE_COMM)?.forEach {
                getTaskUseCase.getTask(it.toInt())
                taskStatusUseCase.markTaskCompleted(
                    taskId = it.toInt()
                )
                val taskEntity = getTaskUseCase.getTask(it.toInt())

                taskEntity?.let {
                    matStatusEventWriterUseCase.updateTaskStatus(
                        taskEntity = it,
                        surveyName = "",
                        subjectType = subjectType
                    )
                }
            }

        }
    }

}