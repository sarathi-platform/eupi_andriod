package com.sarathi.missionactivitytask.ui.add_image_screen.screen

import android.text.TextUtils
import androidx.compose.runtime.mutableStateOf
import com.nudge.core.BLANK_STRING
import com.nudge.core.generateUUID
import com.nudge.core.helper.TranslationEnum
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.toDate
import com.sarathi.dataloadingmangement.DELEGATE_COMM
import com.sarathi.dataloadingmangement.domain.use_case.DocumentEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.DocumentUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FormEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FormUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetFormUiConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.download_manager.FileType
import com.sarathi.dataloadingmangement.util.constants.GrantTaskFormSlots
import com.sarathi.missionactivitytask.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class SubmitPhysicalFormScreenViewModel @Inject constructor(
    private val documentUseCase: DocumentUseCase,
    private val formUseCase: FormUseCase,
    private val formEventWriterUseCase: FormEventWriterUseCase,
    private val documentEventWriterUseCase: DocumentEventWriterUseCase,
    private val coreSharedPrefs: CoreSharedPrefs,
    private val taskStatusUseCase: UpdateMissionActivityTaskStatusUseCase,
    private val getTaskUseCase: GetTaskUseCase,
    private val matStatusEventWriterUseCase: MATStatusEventWriterUseCase,
    private val formUiConfigUseCase: GetFormUiConfigUseCase
) : BaseViewModel() {
    val documentValues = mutableStateOf<ArrayList<DocumentUiModel>>(arrayListOf())
    val isButtonEnable = mutableStateOf<Boolean>(false)
    val totalDidi = mutableStateOf(0)
    var submitPhysicalFormButtonText = mutableStateOf(BLANK_STRING)
    var attachPhyicalFormTitle = mutableStateOf(BLANK_STRING)

    override fun <T> onEvent(event: T) {
        super.onEvent(event)
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

    fun setTotalDidi(activityId: Int, missionId: Int) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            attachPhyicalFormTitle.value = formUiConfigUseCase.getFormConfigValue(
                activityId = activityId,
                missionId = missionId,
                key = GrantTaskFormSlots.TASK_ATTACH_PHYSICAL_TITLE_FORM.name
            )
            submitPhysicalFormButtonText.value = formUiConfigUseCase.getFormConfigValue(
                activityId = activityId,
                missionId = missionId,
                key = GrantTaskFormSlots.TASK_SUBMIT_BUTTON_FORM.name
            )
            totalDidi.value = formUseCase.getNonGeneratedFormSummaryData(activityId = activityId)
                .distinctBy { it.taskid }.size
        }
    }

    fun updateFormTable(
        missionId: Int,
        activityId: Int,
        taskIdList: String,
        onCompleted: (successMessage: String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val successMessage = formUiConfigUseCase.getFormConfigValue(
                activityId = activityId,
                missionId = missionId,
                key = GrantTaskFormSlots.TASK_SUCCESS_MESSAGE_FORM.name
            )
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
            documentEventWriter(formGeneratedDate, activityId)

            updateTaskStatus(taskIdList, subjectType)
            withContext(Dispatchers.Main) {
                onCompleted(successMessage)
            }
        }
    }

    private fun documentEventWriter(formGeneratedDate: String, activityId: Int) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            documentValues.value.firstOrNull()?.filePath?.split(DELEGATE_COMM)?.forEach {
                documentEventWriterUseCase.writeSaveDocumentEvent(
                    generatedDate = formGeneratedDate,
                    documentType = FileType.IMAGE.name,
                    documentName = it,
                    activityId = activityId
                )
            }
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
                        surveyName = BLANK_STRING,
                        subjectType = subjectType
                    )
                }
            }

        }
    }

    override fun getScreenName(): TranslationEnum {
        return TranslationEnum.SubmitPhysicalFormScreen
    }
}