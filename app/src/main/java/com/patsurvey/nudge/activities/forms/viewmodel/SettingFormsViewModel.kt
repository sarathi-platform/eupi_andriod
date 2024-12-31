package com.patsurvey.nudge.activities.forms.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nudge.core.CoreDispatchers
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.model.SettingOptionModel
import com.patsurvey.nudge.BuildConfig
import com.patsurvey.nudge.activities.forms.domain.usecase.SettingFormsUseCase
import com.patsurvey.nudge.activities.settings.domain.SettingTagEnum
import com.patsurvey.nudge.utils.BPC_USER_TYPE
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.UPCM_USER
import com.patsurvey.nudge.utils.VO_ENDORSEMENT_CONSTANT
import com.patsurvey.nudge.utils.WealthRank
import com.sarathi.dataloadingmangement.FORM_E
import com.sarathi.dataloadingmangement.domain.use_case.FormUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetFormUiConfigUseCase
import com.sarathi.dataloadingmangement.model.uiModel.ActivityFormUIModel
import com.sarathi.dataloadingmangement.util.constants.GrantTaskFormSlots
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@HiltViewModel
class SettingFormsViewModel @Inject constructor(
    private val getActivityUseCase: GetActivityUseCase,
    private val formUiConfigUseCase: GetFormUiConfigUseCase,
    private val formUseCase: FormUseCase,
    private val settingFormsUseCase: SettingFormsUseCase
) : BaseViewModel() {
    val _optionList = mutableStateOf<List<SettingOptionModel>>(emptyList())
    val optionList: State<List<SettingOptionModel>> get() = _optionList

    val _activityGenerateFormsList = mutableStateOf<List<ActivityFormUIModel>>(emptyList())
    val activityGenerateFormsList: State<List<ActivityFormUIModel>> get() = _activityGenerateFormsList
    private val _loaderState = mutableStateOf<LoaderState>(LoaderState(isLoaderVisible = false))
    val loaderState: State<LoaderState> get() = _loaderState

    val formAAvailable = mutableStateOf(false)
    val formBAvailable = mutableStateOf(false)
    val formCAvailable = mutableStateOf(false)
    val formEAvailableList = mutableStateOf<List<Pair<Int, Boolean>>>(emptyList())
    var applicationId = mutableStateOf(BLANK_STRING)
    lateinit var mAppContext: Context
    var userType: String = BLANK_STRING
    val activityFormGenerateNameMap = HashMap<Pair<Int, Int>, String>()


    fun initOptions() {
        applicationId.value =
            CoreAppDetails.getApplicationDetails()?.applicationID ?: BuildConfig.APPLICATION_ID
        userType = settingFormsUseCase.getUserDetailsUseCase.getUserType().toString()
        val villageId = settingFormsUseCase.getSelectedVillageId()
        mAppContext =
            if (userType != UPCM_USER) NudgeCore.getAppContext() else BaselineCore.getAppContext()

        val list = ArrayList<SettingOptionModel>()
        if (userType != UPCM_USER) {
            list.add(
                SettingOptionModel(
                    1,
                    mAppContext.getString(R.string.digital_form_a_title),
                    BLANK_STRING,
                    SettingTagEnum.FORMS.name,
                    icon = R.drawable.ic_forms
                )
            )
            list.add(
                SettingOptionModel(
                    2,
                    mAppContext.getString(R.string.digital_form_b_title),
                    BLANK_STRING,
                    SettingTagEnum.FORMS.name,
                    icon = R.drawable.ic_forms
                )
            )
            list.add(
                SettingOptionModel(
                    3,
                    mAppContext.getString(R.string.digital_form_c_title),
                    BLANK_STRING,
                    SettingTagEnum.FORMS.name,
                    icon = R.drawable.ic_forms
                )
            )
        } else {
            getActivityFormGenerateList {
                if (activityGenerateFormsList.value.isNotEmpty()) {
                    checkFromEAvailable(activityFormUIModelList = activityGenerateFormsList.value)
                }
            }
        }

        _optionList.value = list


        if (userType != UPCM_USER) {
            checkFormsAvailabilityForVillage(villageId)
        }
    }

    fun getActivityFormGenerateList(onGetData: () -> Unit) {
        CoroutineScope(CoreDispatchers.ioDispatcher + exceptionHandler).launch {
            _activityGenerateFormsList.value = getActivityUseCase.getActiveForm(formType = FORM_E)
            val formEOptionList = ArrayList<SettingOptionModel>()
            activityGenerateFormsList.value.forEach {
                activityFormGenerateNameMap[Pair(it.missionId, it.activityId)] =
                    formUiConfigUseCase.getFormConfigValue(
                        key = GrantTaskFormSlots.TASK_PDF_FORM_NAME.name,
                        missionId = it.missionId,
                        activityId = it.activityId
                    )

                formEOptionList.add(
                    SettingOptionModel(
                        1,
                        "${it.missionName} - ${
                            activityFormGenerateNameMap[Pair(
                                first = it.missionId,
                                second = it.activityId
                            )].toString()
                        }",
                        BLANK_STRING,
                        SettingTagEnum.FORMS.name,
                        icon = R.drawable.ic_forms
                    )
                )

            }
            _optionList.value = formEOptionList
            onGetData()

        }
    }

    private fun checkFromEAvailable(activityFormUIModelList: List<ActivityFormUIModel>) {
        if (activityFormUIModelList.isNotEmpty()) {
            CoroutineScope(CoreDispatchers.ioDispatcher + exceptionHandler).launch {
                val pairFormList = ArrayList<Pair<Int, Boolean>>()
                activityFormUIModelList.forEachIndexed { index, activityFormUIModel ->
                    val isFromEAvailable = formUseCase.getOnlyGeneratedFormSummaryData(
                        activityId = activityFormUIModel.activityId,
                        isFormGenerated = true
                    ).isNotEmpty()
                    pairFormList.add(Pair(index, isFromEAvailable))
                }
                formEAvailableList.value = pairFormList
            }

        } else {
            formEAvailableList.value = emptyList()
        }
    }

    fun checkFormsAvailabilityForVillage(villageId: Int) {
        job = CoroutineScope(CoreDispatchers.ioDispatcher + exceptionHandler).launch {

            checkFormAAvailability(villageId = villageId)
            checkFormBAvailability(villageId = villageId)
            checkFormCAvailability(villageId = villageId)
        }

    }

    suspend fun checkFormAAvailability(villageId: Int) {
        var didiList =
            settingFormsUseCase.getAllPoorDidiForVillageUseCase.getAllDidiForVillage(villageId)
        if (userType == BPC_USER_TYPE) {
            didiList =
                settingFormsUseCase.getAllPoorDidiForVillageUseCase.getAllPoorDidiForVillage(
                    villageId
                )
        }
        if (didiList.any { it.wealth_ranking == WealthRank.POOR.rank && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal && !it.rankingEdit }
        ) {
            withContext(CoreDispatchers.mainDispatcher) {
                formAAvailable.value = true
            }
        } else {
            withContext(
                CoreDispatchers.mainDispatcher
            ) {
                formAAvailable.value = false
            }
        }
    }


    suspend fun checkFormBAvailability(villageId: Int) {
        var didiList =
            settingFormsUseCase.getAllPoorDidiForVillageUseCase.getAllDidiForVillage(villageId)
        if (userType == BPC_USER_TYPE) {
            didiList =
                settingFormsUseCase.getAllPoorDidiForVillageUseCase.getAllPoorDidiForVillage(
                    villageId
                )
        }

        if (didiList.any { it.forVoEndorsement == 1 && !it.patEdit }
        ) {
            withContext(CoreDispatchers.mainDispatcher) {
                formBAvailable.value = true
            }
        } else {
            withContext(CoreDispatchers.mainDispatcher) {
                formBAvailable.value = false
            }

        }
    }


    suspend fun checkFormCAvailability(villageId: Int) {

        val stepList =
            settingFormsUseCase.getAllPoorDidiForVillageUseCase.getAllStepsForVillage(villageId)
        val filteredStepList = stepList.filter { it.name.equals(VO_ENDORSEMENT_CONSTANT, true) }
        if (filteredStepList[0] != null) {
            formCAvailable.value =
                filteredStepList[0].isComplete == StepStatus.COMPLETED.ordinal
        } else {
            formCAvailable.value = false
        }
    }

    fun getUserMobileNumber() = settingFormsUseCase.getUserDetailsUseCase.getUserMobileNumber()

    fun showLoaderForTime(time: Long) {
        onEvent(LoaderEvent.UpdateLoaderState(true))
        Timer().schedule(object : TimerTask() {
            override fun run() {
                job = CoroutineScope(CoreDispatchers.ioDispatcher + exceptionHandler).launch {
                    withContext(CoreDispatchers.mainDispatcher) {
                        onEvent(LoaderEvent.UpdateLoaderState(false))
                    }
                }
            }
        }, time)
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