package com.sarathi.missionactivitytask.ui.grantTask.viewmodel

import com.nudge.core.model.uiModel.LivelihoodModel
import com.sarathi.contentmodule.ui.content_screen.domain.usecase.FetchContentUseCase
import com.sarathi.dataloadingmangement.data.entities.livelihood.LivelihoodEntity
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity
import com.sarathi.dataloadingmangement.domain.use_case.FetchAllDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FormUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUiConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetFormUiConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.GetLivelihoodListFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.GetSubjectLivelihoodMappingFromUseCase
import com.sarathi.dataloadingmangement.model.uiModel.TaskUiModel
import com.sarathi.missionactivitytask.ui.grantTask.domain.usecases.GetActivityConfigUseCase
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    getActivityUseCase: GetActivityUseCase,
    private val formUseCase: FormUseCase,
    private val formUiConfigUseCase: GetFormUiConfigUseCase,
    fetchAllDataUseCase: FetchAllDataUseCase,
  var  getLivelihoodListFromDbUseCase:GetLivelihoodListFromDbUseCase,
   var getLivelihoodMappingUseCase: GetSubjectLivelihoodMappingFromUseCase,

    ) : TaskScreenViewModel(
    getTaskUseCase,
    surveyAnswerUseCase,
    getActivityUiConfigUseCase,
    getActivityConfigUseCase,
    fetchContentUseCase,
    taskStatusUseCase,
    eventWriterUseCase,
    getActivityUseCase,
    fetchAllDataUseCase
) {
        val livelihoodsEntityList=ArrayList<LivelihoodModel>()
        val subjectLivelihoodMapiingMap:MutableMap<Int,SubjectLivelihoodMappingEntity> =HashMap()


    override fun <T> onEvent(event: T) {
        super.onEvent(event)
        when (event) {
            is InitDataEvent.InitLivelihoodPlanningScreenState -> {

                initLivelihoodPlanningScreen(event.missionId, event.activityId)
            }
        }
    }

    private fun initLivelihoodPlanningScreen(missionId: Int, activityId: Int) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            activityConfigUiModelWithoutSurvey =
                getActivityUiConfigUseCase.getActivityConfig(activityId, missionId)
            livelihoodsEntityList.clear()
            livelihoodsEntityList.addAll(getLivelihoodListFromDbUseCase.invoke())

           var getLivelihoodSupbjectMapping= taskUiModel?.map { it.subjectId }
                ?.let { getLivelihoodMappingUseCase.getLivelihoodMappingForSubject(it) }

            getLivelihoodSupbjectMapping?.associateBy { it.subjectId }
                ?.let {
                    subjectLivelihoodMapiingMap.clear()
                    subjectLivelihoodMapiingMap.putAll(it) }
        }
    }


}