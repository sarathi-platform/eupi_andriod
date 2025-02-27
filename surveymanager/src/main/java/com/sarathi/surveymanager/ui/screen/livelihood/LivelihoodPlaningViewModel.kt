package com.sarathi.surveymanager.ui.screen.livelihood

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nudge.core.BLANK_STRING
import com.nudge.core.DEFAULT_ID
import com.nudge.core.DEFAULT_LIVELIHOOD_ID
import com.nudge.core.DIDI
import com.nudge.core.LIVELIHOOD
import com.nudge.core.NOT_DECIDED_LIVELIHOOD_ID
import com.nudge.core.helper.TranslationEnum
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.ui.events.DialogEvents
import com.nudge.core.utils.CoreLogger
import com.nudge.core.utils.state.DialogState
import com.nudge.core.value
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.GetLivelihoodListFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.GetSubjectLivelihoodMappingFromUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.LivelihoodEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.SaveLivelihoodMappingUseCase
import com.sarathi.dataloadingmangement.enums.LivelihoodTypeEnum
import com.sarathi.dataloadingmangement.model.events.LivelihoodPlanActivityEventDto
import com.sarathi.dataloadingmangement.model.events.LivelihoodTypeEventDto
import com.sarathi.dataloadingmangement.model.uiModel.livelihood.LivelihoodUiEntity
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LivelihoodPlanningEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LivelihoodPlaningViewModel @Inject constructor(
    private val getTaskUseCase: GetTaskUseCase,
    private val getLivelihoodListFromDbUseCase: GetLivelihoodListFromDbUseCase,
    private val getSubjectLivelihoodMappingFromUseCase: GetSubjectLivelihoodMappingFromUseCase,
    private val saveLivelihoodMappingUseCase: SaveLivelihoodMappingUseCase,
    private val livelihoodEventWriterUseCase: LivelihoodEventWriterUseCase,
    private val taskStatusUseCase: UpdateMissionActivityTaskStatusUseCase,
    private val matStatusEventWriterUseCase: MATStatusEventWriterUseCase,
    private val getActivityUseCase: GetActivityUseCase,
    val coreSharedPrefs: CoreSharedPrefs
) : BaseViewModel() {

    private val TAG = LivelihoodPlaningViewModel::class.java.simpleName
    val isButtonEnable = mutableStateOf<Boolean>(false)
    private val _primaryLivelihoodList = mutableStateOf<List<LivelihoodUiEntity>>(emptyList())
    val primaryLivelihoodList: State<List<LivelihoodUiEntity>> get() = _primaryLivelihoodList
    private val _secondaryLivelihoodList = mutableStateOf<List<LivelihoodUiEntity>>(emptyList())
    val secondaryLivelihoodList: State<List<LivelihoodUiEntity>> get() = _secondaryLivelihoodList

    private val _livelihoodTypeList = mutableStateOf<List<LivelihoodUiEntity>>(emptyList())
    val livelihoodType: State<List<LivelihoodUiEntity>> get() = _livelihoodTypeList
    private val _SecondarylivelihoodTypeList = mutableStateOf<List<LivelihoodUiEntity>>(emptyList())
    val seondarylivelihoodTypeList: State<List<LivelihoodUiEntity>> get() = _SecondarylivelihoodTypeList

    private val _showCustomDialog = mutableStateOf<DialogState>(DialogState())
    val showCustomDialog: State<DialogState> get() = _showCustomDialog

    var taskId: Int? = null
    var subjectId: Int? = null
    var activityId: Int? = null
    var missionId: Int? = null
    var subjectName: String? = null
    private var taskEntity: ActivityTaskEntity? = null
    var checkDialogueValidation = mutableStateOf(false)
    var primaryLivelihoodId = mutableStateOf(DEFAULT_LIVELIHOOD_ID)
    var secondaryLivelihoodId: MutableState<Int> = mutableStateOf(DEFAULT_LIVELIHOOD_ID)
    var primaryLivelihoodType = mutableStateOf(BLANK_STRING)
    var secondaryLivelihoodType: MutableState<String> = mutableStateOf(BLANK_STRING)
    var livelihoodUiList: List<LivelihoodUiEntity> = listOf()
    var isActivityCompleted = mutableStateOf(false)

    override fun <T> onEvent(event: T) {

        when (event) {
            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(event.showLoader)
            }

            is InitDataEvent.InitDataState -> {
                setTranslationConfig()
                initLivelihoodPlanningScreen()
            }

            is LivelihoodPlanningEvent.PrimaryLivelihoodPlanningEvent -> {
                checkDialogueValidation.value = true
                primaryLivelihoodId.value = event.livelihoodId
                checkButtonValidation()
            }

            is LivelihoodPlanningEvent.SecondaryLivelihoodPlanningEvent -> {
                checkDialogueValidation.value = true
                secondaryLivelihoodId.value = event.livelihoodId
                checkButtonValidation()
            }
            is LivelihoodPlanningEvent.PrimaryLivelihoodTypePlanningEvent -> {
                checkDialogueValidation.value = true
                onPrimaryLivelihoodTypeSelect(event.livelihoodType)
            }

            is LivelihoodPlanningEvent.SecondaryLivelihoodTypePlanningEvent -> {
                checkDialogueValidation.value = true
                onSecondaryLivelihoodTypeSelect(event.livelihoodType)
            }
            is DialogEvents.ShowDialogEvent -> {
                _showCustomDialog.value = _showCustomDialog.value.copy(
                        isDialogVisible = event.showDialog
                    )
            }
        }
    }

    private fun onPrimaryLivelihoodTypeSelect(selectedLivelihoodType: String) {
        primaryLivelihoodId.value = DEFAULT_LIVELIHOOD_ID
        _primaryLivelihoodList.value = livelihoodUiList.filter {
            it.livelihoodEntity.type.equals(
                selectedLivelihoodType,
                ignoreCase = true
            ) || it.id == NOT_DECIDED_LIVELIHOOD_ID
        }.map { it.copy(isSelected = false) }
        primaryLivelihoodType.value = selectedLivelihoodType
        checkButtonValidation()
    }

    private fun onSecondaryLivelihoodTypeSelect(selectedLivelihoodType: String) {
        secondaryLivelihoodId.value = DEFAULT_LIVELIHOOD_ID

        _secondaryLivelihoodList.value = livelihoodUiList.filter {
            it.livelihoodEntity.type.equals(
                selectedLivelihoodType,
                ignoreCase = true
            ) || it.id == NOT_DECIDED_LIVELIHOOD_ID
        }.map { it.copy(isSelected = false) }
        secondaryLivelihoodType.value = selectedLivelihoodType
        checkButtonValidation()
    }
    private fun initLivelihoodPlanningScreen() {
        ioViewModelScope {
            try {
                taskId?.let {
                    subjectId = getTaskUseCase.getTask(it).subjectId
                    val livelihoodList = getLivelihoodListFromDbUseCase.invoke()
                    val subjectLivelihoodMapping =
                        getSubjectLivelihoodMappingFromUseCase.invoke(subjectId!!)


                    if (subjectLivelihoodMapping != null && subjectLivelihoodMapping.size!=0) {
                        val mLivelihoodUiEntityList = LivelihoodUiEntity.getLivelihoodUiEntityList(
                            livelihoodUiModelList = livelihoodList,

                            selectedIds = listOf(
                                subjectLivelihoodMapping.find { it?.type == LivelihoodTypeEnum.PRIMARY.typeId }?.livelihoodId.value(),
                                subjectLivelihoodMapping.find { it?.type == LivelihoodTypeEnum.SECONDARY.typeId }?.livelihoodId.value()
                            )
                        )
                        livelihoodUiList = mLivelihoodUiEntityList
                        _livelihoodTypeList.value =
                            LivelihoodUiEntity.getLivelihoodUiTypeEntityList(
                                livelihoodUiModelList = livelihoodList.distinctBy { it.type },
//Please change the logic from id to type
                                selectedType = listOf(
                                    subjectLivelihoodMapping.find { it?.type == LivelihoodTypeEnum.PRIMARY.typeId }?.livelihoodType.value()
                                        .uppercase()
                                )
                            ).sortedBy { it.livelihoodEntity.name.lowercase() }
                        _SecondarylivelihoodTypeList.value =
                            LivelihoodUiEntity.getLivelihoodUiTypeEntityList(
                                livelihoodUiModelList = livelihoodList.distinctBy { it.type },
//Please change the logic from id to type
                                selectedType = listOf(
                                    subjectLivelihoodMapping.find { it?.type == LivelihoodTypeEnum.SECONDARY.typeId }?.livelihoodType.value()
                                        .uppercase()
                                )
                            ).sortedBy { it.livelihoodEntity.name.lowercase() }
                        primaryLivelihoodId.value = subjectLivelihoodMapping.find { it?.type==LivelihoodTypeEnum.PRIMARY.typeId }?.livelihoodId.value()
                        secondaryLivelihoodId.value = subjectLivelihoodMapping.find { it?.type==LivelihoodTypeEnum.SECONDARY.typeId }?.livelihoodId.value()
                        primaryLivelihoodType.value =
                            subjectLivelihoodMapping.find { it?.type == LivelihoodTypeEnum.PRIMARY.typeId }?.livelihoodType.value()
                                .uppercase()
                        secondaryLivelihoodType.value =
                            subjectLivelihoodMapping.find { it?.type == LivelihoodTypeEnum.SECONDARY.typeId }?.livelihoodType.value()
                                .uppercase()
                        _primaryLivelihoodList.value = livelihoodUiList.filter {
                            it.livelihoodEntity.type.equals(
                                primaryLivelihoodType.value,
                                ignoreCase = true
                            ) || it.id == NOT_DECIDED_LIVELIHOOD_ID
                        }
                            .map { it.copy(isSelected = primaryLivelihoodId.value == it.livelihoodEntity.programLivelihoodId) }
                            .sortedBy { it.livelihoodEntity.name.lowercase() }
                        _secondaryLivelihoodList.value = livelihoodUiList.filter {
                            it.livelihoodEntity.type.equals(
                                secondaryLivelihoodType.value,
                                ignoreCase = true
                            ) || it.id == NOT_DECIDED_LIVELIHOOD_ID
                        }
                            .map { it.copy(isSelected = secondaryLivelihoodId.value == it.livelihoodEntity.programLivelihoodId) }
                            .sortedBy { it.livelihoodEntity.name.lowercase() }
                        checkButtonValidation()

                    }
                else {
                        val mLivelihoodUiEntityList =
                            LivelihoodUiEntity.getLivelihoodUiEntityList(
                                livelihoodUiModelList = livelihoodList,
                                selectedIds = listOf(

                                )
                            )
                        livelihoodUiList = mLivelihoodUiEntityList
                        _livelihoodTypeList.value =
                            LivelihoodUiEntity.getLivelihoodUiTypeEntityList(
                                livelihoodUiModelList = livelihoodList.distinctBy { it.type },
                                selectedType = listOf()
                            )
                        _SecondarylivelihoodTypeList.value =
                            LivelihoodUiEntity.getLivelihoodUiTypeEntityList(
                                livelihoodUiModelList = livelihoodList.distinctBy { it.type },
                                selectedType = listOf()
                            )
                    }
                    checkButtonValidation()
                    isActivityCompleted()
                }

            } catch (ex: Exception) {
                CoreLogger.e(
                    tag = TAG,
                    msg = "initLivelihoodPlanningScreen -> exception: ${ex.message}",
                    ex = ex
                )
            } finally {
                withContext(mainDispatcher) {
                    onEvent(LoaderEvent.UpdateLoaderState(false))
                }
            }

        }
    }


    fun setPreviousScreenData(
        mTaskId: Int,
        mActivityId: Int,
        mMissionId: Int,
        mSubjectName: String
    ) {
        this.taskId = mTaskId
        this.activityId = mActivityId
        this.missionId = mMissionId
        this.subjectName = mSubjectName
    }

    fun checkButtonValidation() {
        if (primaryLivelihoodId.value ==secondaryLivelihoodId.value)
        {
            isButtonEnable.value=false
        }
        else{
            isButtonEnable.value = primaryLivelihoodId.value != DEFAULT_LIVELIHOOD_ID && secondaryLivelihoodId.value != DEFAULT_LIVELIHOOD_ID }
    }

    fun saveButtonClicked(callBack: suspend () -> Unit) {
        ioViewModelScope {
            saveLivelihoodMappingToDb(callBack = callBack)
        }
    }

    private suspend fun saveLivelihoodMappingToDb(callBack: suspend () -> Unit) {
        subjectId?.let {
            saveLivelihoodMappingUseCase.saveAndUpdateLivelihoodMappingForSubject(
                primaryLivelihoodId.value,
                LivelihoodTypeEnum.PRIMARY.typeId,
                it,
                livelihoodType = primaryLivelihoodType.value
            )
            saveLivelihoodMappingUseCase.saveAndUpdateLivelihoodMappingForSubject(
                secondaryLivelihoodId.value,
                LivelihoodTypeEnum.SECONDARY.typeId,
                it,
                livelihoodType = secondaryLivelihoodType.value
            )
        }
        val livelihoodTypeEventDto = ArrayList<LivelihoodTypeEventDto>()
        livelihoodTypeEventDto.add(
            LivelihoodTypeEventDto(
                programLivelihoodId = primaryLivelihoodId.value,
                order = LivelihoodTypeEnum.PRIMARY.typeId,
                type = primaryLivelihoodType.value
            )
        )
        livelihoodTypeEventDto.add(
            LivelihoodTypeEventDto(
                programLivelihoodId = secondaryLivelihoodId.value,
                order = LivelihoodTypeEnum.SECONDARY.typeId,
                type = secondaryLivelihoodType.value
            )
        )
        val livelihoodPlanActivityDto =
            LivelihoodPlanActivityEventDto(
                coreSharedPrefs.getUserName(),
                livelihoodTypeEventDto,
                activityId!!, missionId!!, subjectId!!, DIDI
            )
        livelihoodEventWriterUseCase.writeLivelihoodEvent(
            livelihoodPlanActivityDto
        )

        taskStatusUseCase.markTaskCompleted(
            taskId = taskId!!
        )
        taskEntity = getTaskUseCase.getTask(taskId!!)
        taskEntity?.let {
            matStatusEventWriterUseCase.markMATStatus(
                surveyName = LIVELIHOOD,
                subjectType = coreSharedPrefs.getUserType(),
                missionId = missionId ?: DEFAULT_ID,
                activityId = activityId ?: DEFAULT_ID,
                taskId = taskId ?: DEFAULT_ID

            )
        }
        callBack()
    }
    suspend fun isActivityCompleted() {
        isActivityCompleted.value = getActivityUseCase.isActivityCompleted(
            missionId = missionId ?: DEFAULT_ID,
            activityId = activityId ?: DEFAULT_ID
        )
    }

    override fun getScreenName(): TranslationEnum {
        return TranslationEnum.LivelihoodDropDownScreen
    }

}