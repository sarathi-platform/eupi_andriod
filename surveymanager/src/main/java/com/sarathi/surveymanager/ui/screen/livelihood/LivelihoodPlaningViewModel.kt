package com.sarathi.surveymanager.ui.screen.livelihood

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nudge.core.DEFAULT_ID
import com.nudge.core.DEFAULT_LIVELIHOOD_ID
import com.nudge.core.DIDI
import com.nudge.core.LIVELIHOOD
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.ui.events.DialogEvents
import com.nudge.core.utils.CoreLogger
import com.nudge.core.utils.state.DialogState
import com.nudge.core.value
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity
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
    val coreSharedPrefs: CoreSharedPrefs
) : BaseViewModel() {

    private val TAG = LivelihoodPlaningViewModel::class.java.simpleName
    val isButtonEnable = mutableStateOf<Boolean>(false)
    private val _livelihoodList = mutableStateOf<List<LivelihoodUiEntity>>(emptyList())
    val livelihoodList: State<List<LivelihoodUiEntity>> get() = _livelihoodList
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


    override fun <T> onEvent(event: T) {

        when (event) {
            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(event.showLoader)
            }

            is InitDataEvent.InitDataState -> {
                initLivelihoodPlanningScreen()
            }

            is LivelihoodPlanningEvent.PrimaryLivelihoodPlanningEvent -> {
                primaryLivelihoodId.value = event.livelihoodId
                checkButtonValidation()
            }

            is LivelihoodPlanningEvent.SecondaryLivelihoodPlanningEvent -> {
                secondaryLivelihoodId.value = event.livelihoodId
                checkButtonValidation()
            }
            is DialogEvents.ShowDialogEvent -> {
                _showCustomDialog.value = _showCustomDialog.value.copy(
                        isDialogVisible = event.showDialog
                    )
            }
        }
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
                                subjectLivelihoodMapping.first()?.livelihoodId.value(),
                                subjectLivelihoodMapping.last()?.livelihoodId.value()
                            )
                        )
                        _livelihoodList.value = mLivelihoodUiEntityList
                        primaryLivelihoodId.value = subjectLivelihoodMapping.find { it?.type==LivelihoodTypeEnum.PRIMARY.typeId }?.livelihoodId.value()
                        secondaryLivelihoodId.value = subjectLivelihoodMapping.find { it?.type==LivelihoodTypeEnum.SECONDARY.typeId }?.livelihoodId.value()
                        checkDialogueValidation.value =  checkDialogueValidation(subjectLivelihoodMapping)
                    }
                else {
                        val mLivelihoodUiEntityList =
                            LivelihoodUiEntity.getLivelihoodUiEntityList(
                                livelihoodUiModelList = livelihoodList,
                                selectedIds = listOf(

                                )
                            )
                        _livelihoodList.value = mLivelihoodUiEntityList
                    }
                    checkButtonValidation()

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

    private fun checkDialogueValidation(subjectLivelihoodMapping: List<SubjectLivelihoodMappingEntity?>) :Boolean{
       return if((subjectLivelihoodMapping.first()?.livelihoodId!=null &&  subjectLivelihoodMapping.last()?.secondaryLivelihoodId!=null) || (primaryLivelihoodId.value==subjectLivelihoodMapping.first()?.livelihoodId) ||(secondaryLivelihoodId.value==subjectLivelihoodMapping.last()?.livelihoodId)) true else false
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

    fun saveButtonClicked() {
        ioViewModelScope {
            saveLivelihoodMappingToDb()
        }
    }

     fun saveLivelihoodMappingToDb() {
        ioViewModelScope {
            subjectId?.let {
                saveLivelihoodMappingUseCase.saveAndUpdateLivelihoodMappingForSubject(
                    primaryLivelihoodId.value, LivelihoodTypeEnum.PRIMARY.typeId,it
                )
                saveLivelihoodMappingUseCase.saveAndUpdateLivelihoodMappingForSubject(
                 secondaryLivelihoodId.value,LivelihoodTypeEnum.SECONDARY.typeId,it
                )
            }
            val livelihoodTypeEventDto = ArrayList<LivelihoodTypeEventDto>()
            livelihoodTypeEventDto.add(LivelihoodTypeEventDto(primaryLivelihoodId.value,LivelihoodTypeEnum.PRIMARY.typeId))
                    livelihoodTypeEventDto.add(LivelihoodTypeEventDto(secondaryLivelihoodId.value,LivelihoodTypeEnum.SECONDARY.typeId))
            val livelihoodPlanActivityDto =
                LivelihoodPlanActivityEventDto(coreSharedPrefs.getUserName(),
                    livelihoodTypeEventDto,
                    activityId!!,missionId!!,subjectId!!,DIDI)
                livelihoodEventWriterUseCase.writeLivelihoodEvent(
                    livelihoodPlanActivityDto)

            taskStatusUseCase.markTaskCompleted(
                    taskId = taskId!!
                )
            taskEntity = getTaskUseCase.getTask(taskId!!)
                taskEntity?.let {
                    matStatusEventWriterUseCase.markMATStatus(
                        surveyName = LIVELIHOOD,
                        subjectType = coreSharedPrefs.getUserType(),
                        missionId = missionId?: DEFAULT_ID,
                        activityId = activityId?: DEFAULT_ID,
                        taskId = taskId?: DEFAULT_ID

                    )
                }
        }
    }
}