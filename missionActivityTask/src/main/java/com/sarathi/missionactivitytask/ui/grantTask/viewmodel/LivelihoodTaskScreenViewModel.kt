package com.sarathi.missionactivitytask.ui.grantTask.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nudge.auditTrail.domain.usecase.AuditTrailUseCase
import com.nudge.core.model.uiModel.LivelihoodModel
import com.nudge.core.value
import com.nudge.core.valueAsMinusTwo
import com.sarathi.contentmodule.ui.content_screen.domain.usecase.FetchContentUseCase
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity
import com.sarathi.dataloadingmangement.domain.use_case.FetchAllDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUiConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.GetLivelihoodListFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.GetSubjectLivelihoodMappingFromUseCase
import com.sarathi.dataloadingmangement.enums.LivelihoodTypeEnum
import com.sarathi.dataloadingmangement.model.uiModel.ActivityUiModel
import com.sarathi.missionactivitytask.ui.grantTask.domain.usecases.GetActivityConfigUseCase
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LivelihoodTaskScreenViewModel @Inject constructor(
    getTaskUseCase: GetTaskUseCase,
    surveyAnswerUseCase: SaveSurveyAnswerUseCase,
    val getActivityUiConfigUseCase: GetActivityUiConfigUseCase,
    getActivityConfigUseCase: GetActivityConfigUseCase,
    fetchContentUseCase: FetchContentUseCase,
    private val taskStatusUseCase: UpdateMissionActivityTaskStatusUseCase,
    eventWriterUseCase: MATStatusEventWriterUseCase,
    val getActivityUseCase: GetActivityUseCase,
    fetchAllDataUseCase: FetchAllDataUseCase,
    var getLivelihoodListFromDbUseCase: GetLivelihoodListFromDbUseCase,
    var getLivelihoodMappingUseCase: GetSubjectLivelihoodMappingFromUseCase,
    auditTrailUseCase: AuditTrailUseCase
    ) : TaskScreenViewModel(
    getTaskUseCase,
    surveyAnswerUseCase,
    getActivityUiConfigUseCase,
    getActivityConfigUseCase,
    fetchContentUseCase,
    taskStatusUseCase,
    eventWriterUseCase,
    getActivityUseCase,
    fetchAllDataUseCase,
    auditTrailUseCase,
) {
    private val _activityList = mutableStateOf<List<ActivityUiModel>>(emptyList())
    val activityList: State<List<ActivityUiModel>> get() = _activityList
        val livelihoodsEntityList=ArrayList<LivelihoodModel>()
        val subjectLivelihoodMappingMap:MutableMap<Int,List<SubjectLivelihoodMappingEntity>> =HashMap()


    override fun <T> onEvent(event: T) {
        super.onEvent(event)
        when (event) {
            is InitDataEvent.InitLivelihoodPlanningScreenState -> {

                initLivelihoodPlanningScreen()
            }
        }
    }
    fun getPrimaryLivelihoodValue(key: Int):String {
          return livelihoodsEntityList.find {
                it.livelihoodId == subjectLivelihoodMappingMap.get(
                    taskUiModel?.find { it.taskId == key }?.subjectId
                )
                    ?.find { it.type == LivelihoodTypeEnum.PRIMARY.typeId && it.status == 1 }?.livelihoodId.valueAsMinusTwo()
            }?.name.value() }

    fun getSecondaryLivelihoodValue(key: Int):String{
        return livelihoodsEntityList.find { it.livelihoodId==subjectLivelihoodMappingMap.get(taskUiModel?.find { it.taskId==key }?.subjectId)?.find { it.type==LivelihoodTypeEnum.SECONDARY.typeId && it.status==1  }?.livelihoodId.valueAsMinusTwo() }?.name.value()
    }
     fun getActivityList(missionId: Int){


         CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
             _activityList.value = getActivityUseCase.getActivities(missionId)
         }
    }

    private  fun initLivelihoodPlanningScreen() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(true))
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }

            livelihoodsEntityList.clear()
            livelihoodsEntityList.addAll(getLivelihoodListFromDbUseCase.invoke())

            var getLivelihoodSupbjectMapping = taskUiModel?.map { it.subjectId }
                ?.let { getLivelihoodMappingUseCase.getLivelihoodMappingForSubject(it) }
                getLivelihoodSupbjectMapping?.groupBy { it.subjectId }
                    ?.let {
                        subjectLivelihoodMappingMap.clear()
                        subjectLivelihoodMappingMap.putAll(it)
                    }
        }
    }
    }

