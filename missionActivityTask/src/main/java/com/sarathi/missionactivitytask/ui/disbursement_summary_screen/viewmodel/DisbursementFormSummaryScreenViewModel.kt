package com.sarathi.missionactivitytask.ui.disbursement_summary_screen.viewmodel

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nudge.core.BLANK_STRING
import com.nudge.core.DD_MMM_YYYY_FORMAT
import com.nudge.core.PDF_MIME_TYPE
import com.nudge.core.PDF_TYPE
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.openShareSheet
import com.nudge.core.saveFileToDownload
import com.nudge.core.toInMillisec
import com.nudge.core.uriFromFile
import com.nudge.core.utils.PdfGenerator
import com.nudge.core.utils.PdfModel
import com.sarathi.dataloadingmangement.data.entities.FormEntity
import com.sarathi.dataloadingmangement.domain.use_case.FormUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetFormUiConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.model.uiModel.ActivityConfigUiModel
import com.sarathi.dataloadingmangement.model.uiModel.DisbursementFormSummaryUiModel
import com.sarathi.dataloadingmangement.model.uiModel.UiConfigAttributeType
import com.sarathi.dataloadingmangement.util.constants.ComponentEnum
import com.sarathi.dataloadingmangement.util.constants.GrantTaskFormSlots
import com.sarathi.missionactivitytask.ui.grantTask.domain.usecases.GetActivityConfigUseCase
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent
import com.sarathi.missionactivitytask.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DisbursementFormSummaryScreenViewModel @Inject constructor(
    private val formUseCase: FormUseCase,
    private val getFormUiConfigUseCase: GetFormUiConfigUseCase,
    private val getTaskUseCase: GetTaskUseCase,
    private val surveyAnswerUseCase: SaveSurveyAnswerUseCase,
    private val getActivityConfigUseCase: GetActivityConfigUseCase,
) :
    BaseViewModel() {
    private val _disbursementFormList =
        mutableStateOf<List<DisbursementFormSummaryUiModel>>(emptyList())
    val disbursementFormList: State<List<DisbursementFormSummaryUiModel>> get() = _disbursementFormList
    private val _formList =
        mutableStateOf<Map<Pair<String, String>, List<DisbursementFormSummaryUiModel>>>(hashMapOf())
    val formList: State<Map<Pair<String, String>, List<DisbursementFormSummaryUiModel>>> get() = _formList
    var activityConfigUiModel: ActivityConfigUiModel? = null

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDisbursmentScreenState -> {
                initDisbursementSummaryScreen(event.activityId, event.missionId)
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
        }
    }

    private fun initDisbursementSummaryScreen(activityId: Int, missionId: Int) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            getSurveyDetail(activityId = activityId)
            _formList.value =
                getFormData(activityId, missionId).sortedByDescending {
                    it.date.toInMillisec(
                        DD_MMM_YYYY_FORMAT
                    )
                }.groupBy { Pair(it.date, it.voName) }
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    private suspend fun getFormData(
        activityId: Int,
        missionId: Int
    ): List<DisbursementFormSummaryUiModel> {
        val fromData = formUseCase.getFormSummaryData(activityId = activityId)
        val list = ArrayList<DisbursementFormSummaryUiModel>()
        fromData.forEach { form ->
            val _data = getFormAttributeDate(
                form = form,
                missionId = missionId,
                activityId = activityId
            )

            list.add(
                DisbursementFormSummaryUiModel(
                    subjectType = form.subjectType,
                    date = _data[GrantTaskFormSlots.GRANT_TASK_SUBTITLE_7_FORM.name]
                        ?: BLANK_STRING,
                    noOfDidi = 1,
                    subjectName = _data[GrantTaskFormSlots.GRANT_TASK_TITLE_FORM.name]
                        ?: BLANK_STRING,
                    subjectId = form.subjectid,
                    villageName = _data[GrantTaskFormSlots.GRANT_TASK_SUBTITLE_2_FORM.name]
                        ?: BLANK_STRING,
                    mode = _data[GrantTaskFormSlots.GRANT_TASK_SUBTITLE_4_FORM.name]
                        ?: BLANK_STRING,
                    nature = _data[GrantTaskFormSlots.GRANT_TASK_SUBTITLE_6_FORM.name]
                        ?: BLANK_STRING,
                    amount = _data[GrantTaskFormSlots.GRANT_TASK_SUBTITLE_5_FORM.name]
                        ?: BLANK_STRING,
                    didiImage = _data[GrantTaskFormSlots.GRANT_TASK_IMAGE_FORM.name]
                        ?: BLANK_STRING,
                    voName = _data[GrantTaskFormSlots.GRANT_TASK_SUBTITLE_3_FORM.name]
                        ?: BLANK_STRING,
                    dadaName = _data[GrantTaskFormSlots.GRANT_TASK_SUBTITLE_FORM.name]
                        ?: BLANK_STRING,
                )
            )
        }
        return list
    }

    private suspend fun getFormAttributeDate(
        form: FormEntity,
        activityId: Int,
        missionId: Int
    ): HashMap<String, String> {
        return getUiComponentValues(
            taskId = form.taskid,
            subjectId = form.subjectid,
            componentType = ComponentEnum.Form.name,
            referenceId = form.localReferenceId,
            missionId = missionId,
            activityId = activityId
        )
    }

    private suspend fun getTaskAttributeValue(key: String, taskId: Int): String {
        return getTaskUseCase.getSubjectAttributes(taskId).find { it.key == key }?.value
            ?: BLANK_STRING
    }


    private suspend fun getUiComponentValues(
        taskId: Int,
        subjectId: Int,
        componentType: String,
        referenceId: String,
        missionId: Int,
        activityId: Int
    ): HashMap<String, String> {
        val cardAttributesWithValue = HashMap<String, String>()
        val activityConfig =
            getFormUiConfigUseCase.getFormUiConfig(activityId = activityId, missionId = missionId)
        val cardConfig = activityConfig.filter { it.componentType.equals(componentType, true) }
        cardConfig.forEach { cardAttribute ->
            cardAttributesWithValue[cardAttribute.key] = when (cardAttribute.type.toUpperCase()) {
                UiConfigAttributeType.STATIC.name -> cardAttribute.value
                UiConfigAttributeType.DYNAMIC.name, UiConfigAttributeType.ATTRIBUTE.name -> getTaskAttributeValue(
                    cardAttribute.value, taskId
                )

                UiConfigAttributeType.TAG.name -> surveyAnswerUseCase.getAnswerForFormTag(
                    taskId = taskId,
                    subjectId = subjectId,
                    activityConfigId = activityConfigUiModel?.activityConfigId ?: 0,
                    referenceId = referenceId,
                    tagId = getTaskAttributeValue(
                        cardAttribute.value, taskId
                    )
                )

                else -> {
                    BLANK_STRING
                }
            }
        }

        return cardAttributesWithValue
    }

    fun getFilePathUri(filePath: String): Uri? {
        return formUseCase.getFilePathUri(filePath)
    }

    fun generateFormE(isDownload: Boolean, callBack: (filepath: String) -> Unit) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val pdfModels = ArrayList<PdfModel>()
            formList.value.forEach {
                pdfModels.add(
                    PdfModel(
                        pdfLeftTitle = "VO Name: ${it.key.second}",
                        pdfCenterTitle = "FORMAT E",
                        pdfDescription = "Format to be used by the Village Organization/Nodal SHG for writing the meeting minute register\u2028during distribution of Immediate Assistance Grant amount to the poorest families",
                        pdfRightTitle = "Date: ${it.key.first}",
                        tableHeaders = getTableHeader(),
                        rows = getDidiWiseDetail(it.value),
                        pageNo = 1

                    )
                )

            }


            val filePath = PdfGenerator.generatePdf(pdfModels, formUseCase.getFormEFileName())
            val fileUri = uriFromFile(
                CoreAppDetails.getApplicationDetails()?.activity?.applicationContext,
                File(filePath),
                CoreAppDetails.getApplicationDetails()?.applicationID ?: BLANK_STRING
            )
            if (isDownload) {
                saveFileToDownload(
                    fileUri, PDF_MIME_TYPE,
                    CoreAppDetails.getApplicationDetails()?.activity?.applicationContext!!
                )
                withContext(Dispatchers.Main) {
                    callBack(filePath)
                }


            } else {
                val fileUriList = ArrayList<Uri>()
                fileUriList.add(fileUri)
                openShareSheet(
                    fileUriList = fileUriList, "Form E", PDF_TYPE,
                    CoreAppDetails.getApplicationDetails()?.activity?.applicationContext!!
                )
            }
        }
    }

    fun getDidiWiseDetail(disbursementFormSummaryUiModel: List<DisbursementFormSummaryUiModel>): List<List<String>> {
        val rowList = ArrayList<List<String>>()
        disbursementFormSummaryUiModel.forEachIndexed { index, it ->
            val row = ArrayList<String>()
            row.add("${index + 1}")
            row.add(it.subjectName)
            row.add(it.dadaName)
            row.add(it.villageName)
            row.add(it.mode)
            row.add(it.amount)
            rowList.add(row)
        }
        return rowList
    }

    fun getTableHeader(): List<String> {
        return listOf(
            "S.No",
            "Name of Didi",
            "Name of husband/father",
            "Village/Hamlet Name",
            "Grant Received (Mode)",
            "Value of grant received"
        )
    }

    suspend fun getSurveyDetail(activityId: Int) {
        activityConfigUiModel = getActivityConfigUseCase.getActivityUiConfig(activityId)
    }

}

