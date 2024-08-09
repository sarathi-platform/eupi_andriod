package com.sarathi.surveymanager.ui.screen.livelihood

import android.annotation.SuppressLint
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nudge.core.DEFAULT_ID
import com.nudge.core.LIVELIHOOD
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.utils.CoreLogger
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchLivelihoodOptionNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.GetLivelihoodListFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.GetSubjectLivelihoodMappingFromUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.LivelihoodEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.SaveLivelihoodMappingUseCase
import com.sarathi.dataloadingmangement.model.events.LivelihoodPlanActivityEventDto
import com.sarathi.dataloadingmangement.model.uiModel.livelihood.LivelihoodUiEntity
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
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
    private val fetchLivelihoodOptionNetworkUseCase: FetchLivelihoodOptionNetworkUseCase,
    val coreSharedPrefs: CoreSharedPrefs


    ) : BaseViewModel() {

    private val TAG = LivelihoodPlaningViewModel::class.java.simpleName
    val isButtonEnable = mutableStateOf<Boolean>(false)
    private val _livelihoodList = mutableStateOf<List<LivelihoodUiEntity>>(emptyList())
    val livelihoodList: State<List<LivelihoodUiEntity>> get() = _livelihoodList

    var taskId: Int? = null
    var subjectId: Int? = null
    var activityId: Int? = null
    var missionId: Int? = null
    var subjectName: String? = null
    private var taskEntity: ActivityTaskEntity? = null

    var primaryLivelihoodId = mutableStateOf(-1)
    var secondaryLivelihoodId: MutableState<Int> = mutableStateOf(-1)

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

                    if (subjectLivelihoodMapping != null) {
                        val mLivelihoodUiEntityList = LivelihoodUiEntity.getLivelihoodUiEntityList(
                            livelihoodUiModelList = livelihoodList,
                            selectedIds = listOf(
                                subjectLivelihoodMapping.primaryLivelihoodId,
                                subjectLivelihoodMapping.secondaryLivelihoodId
                            )
                        )
                        _livelihoodList.value = mLivelihoodUiEntityList
                        primaryLivelihoodId.value = subjectLivelihoodMapping.primaryLivelihoodId
                        secondaryLivelihoodId.value = subjectLivelihoodMapping.secondaryLivelihoodId
                    } else {
                        val mLivelihoodUiEntityList =
                            LivelihoodUiEntity.getLivelihoodUiEntityList(
                                livelihoodUiModelList = livelihoodList,
                                selectedIds = listOf()
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
            isButtonEnable.value = primaryLivelihoodId.value != -1 && secondaryLivelihoodId.value != -1 }
    }

    fun saveButtonClicked() {
        ioViewModelScope {
            saveLivelihoodMappingToDb()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private suspend fun saveLivelihoodMappingToDb() {
        ioViewModelScope {
            val subjectLivelihoodMappingEntity: SubjectLivelihoodMappingEntity? = subjectId?.let {
                SubjectLivelihoodMappingEntity.getSubjectLivelihoodMappingEntity(
                    userId = saveLivelihoodMappingUseCase.getUserId(),
                    subjectId = it,
                    primaryLivelihoodId.value, secondaryLivelihoodId.value
                )
            }
            fetchLivelihoodOptionNetworkUseCase.saveFormEData(activityId = activityId!!,
                subjectId = subjectId!!,
               selectedPrimaryLivelihood =  primaryLivelihoodId.value,
                selectedSecondaryLivelihood =  secondaryLivelihoodId.value,
                )

            val livelihoodPlanActivityDto = LivelihoodPlanActivityEventDto(coreSharedPrefs.getUserName(), primaryLivelihoodId.value, secondaryLivelihoodId.value,activityId!!,missionId!!,subjectId!!,coreSharedPrefs.getUserType())
            livelihoodEventWriterUseCase.writeLivelihoodEvent(
               livelihoodPlanActivityDto,

                )
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
            subjectLivelihoodMappingEntity?.let { saveLivelihoodMappingUseCase.invoke(it) }

//                subjectLivelihoodMappingEntity?.let { saveLivelihoodMappingUseCase.saveAndUpdateSubjectLivelihoodMappingForSubject(it) }
        }
    }

}