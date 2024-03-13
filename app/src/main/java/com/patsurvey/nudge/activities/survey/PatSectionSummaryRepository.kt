package com.patsurvey.nudge.activities.survey

import com.google.gson.Gson
import com.nudge.core.KEY_PARENT_ENTITY_ADDRESS
import com.nudge.core.KEY_PARENT_ENTITY_DADA_NAME
import com.nudge.core.KEY_PARENT_ENTITY_DIDI_NAME
import com.nudge.core.KEY_PARENT_ENTITY_TOLA_NAME
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.database.entities.getDependentEventsId
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.nudge.core.enums.getDependsOnEventNameForEvent
import com.nudge.core.getEventDependencyEntityListFromEvents
import com.nudge.core.json
import com.nudge.core.model.getMetaDataDtoFromString
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.NumericAnswerEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.model.request.EditDidiWealthRankingRequest
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.QuestionType
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.getPatScoreSaveEvent
import com.patsurvey.nudge.utils.getPatSummarySaveEventPayload
import com.patsurvey.nudge.utils.updateStepStatus
import javax.inject.Inject

class PatSectionSummaryRepository @Inject constructor(
    val prefRepo: PrefRepo,
    private val questionListDao: QuestionListDao,
    private val answerDao: AnswerDao,
    private val numericAnswerDao: NumericAnswerDao,
    private val stepsListDao: StepsListDao,
    private val tolaDao: TolaDao
):BaseRepository() {

    fun getAllStepsForVillage():List<StepListEntity>{
       return stepsListDao.getAllStepsForVillage(prefRepo.getSelectedVillage().id)
    }

    fun getAllQuestionForLanguage():List<QuestionEntity>{
        return questionListDao.getAllQuestionsForLanguage(prefRepo.getAppLanguageId()?:2)
    }
    fun getQuestionForType(questionType: String):List<QuestionEntity> {
       return questionListDao.getQuestionForType(questionType,prefRepo.getAppLanguageId()?:2)
    }

    fun getAnswerForDidi(actionType: String,didiId:Int): List<SectionAnswerEntity>{
      return answerDao.getAnswerForDidi(actionType, didiId = didiId)
    }

    fun setPatSurveyComplete(didiId: Int,status: Int){
        didiDao.updateQuesSectionStatus(didiId,status)
        didiDao.updateDidiNeedToPostPat(didiId, true)
    }

    fun updatePatSection1Status(didiId: Int, section1: Int){
        didiDao.updatePatSection1Status(didiId,section1)
    }
    fun updatePatSection2Status(didiId: Int, section2: Int){
        didiDao.updatePatSection2Status(didiId,section2)
    }

    fun updateVillageStepStatus(didiId:Int){
        updateStepStatus(
            stepsListDao = stepsListDao,
            didiDao = didiDao,
            didiId = didiId,
            prefRepo = prefRepo,
            printTag = "PatSectionSummaryRepository ONE"
        )
    }

    fun fetchOptionYesCount(didiId: Int):Int{
       return answerDao.fetchOptionYesCount(didiId = didiId, QuestionType.RadioButton.name,TYPE_EXCLUSION)
    }

    fun updateExclusionStatus(didiId: Int, patExclusionStatus: Int, crpComment:String){
        didiDao.updateExclusionStatus(didiId,patExclusionStatus, crpComment)
    }

    fun getAllInclusiveQues(didiId: Int):List<SectionAnswerEntity>{
       return answerDao.getAllInclusiveQues(didiId = didiId)
    }

    fun getTotalWeightWithoutNumQues(didiId: Int):Double{
      return answerDao.getTotalWeightWithoutNumQues(didiId)
    }

    fun getQuestion(questionId:Int):QuestionEntity{
       return questionListDao.getQuestion(questionId)
    }

    fun updateVOEndorsementDidiStatus(didiId:Int,status: Int){
        didiDao.updateVOEndorsementDidiStatus(
            prefRepo.getSelectedVillage().id,
            didiId,
            status
        )
    }

    fun updateVOEndorsementStatus(didiId: Int,status:Int){
        didiDao.updateVOEndorsementStatus(prefRepo.getSelectedVillage().id,
            didiId,
            status
        )
    }


    fun updateDidiScoreInDB(score: Double,comment:String,isDidiAccepted:Boolean,didiId: Int){
        didiDao.updateDidiScore(
            score = score,
            comment = comment,
            didiId = didiId,
            isDidiAccepted = isDidiAccepted
        )
    }

    fun updateModifiedDateServerId(didiId: Int){
        didiDao.updateModifiedDateServerId(System.currentTimeMillis(), didiId)

    }

    override suspend fun <T> createEvent(
        eventItem: T,
        eventName: EventName,
        eventType: EventType
    ): Events? {
        if (eventType != EventType.STATEFUL)
            return super.createEvent(eventItem, eventName, eventType)

        if (eventItem !is DidiEntity)
            return super.createEvent(eventItem, eventName, eventType)

        when (eventName) {
            EventName.SAVE_PAT_ANSWERS -> {
                val requestPayload = getPatSummarySaveEventPayload(
                    didiEntity = (eventItem as DidiEntity),
                    answerDao = answerDao,
                    numericAnswerDao = numericAnswerDao,
                    questionListDao = questionListDao,
                    prefRepo = prefRepo
                )

                var savePatSummeryEvent = getPatSaveAnswersEvent(
                    eventItem = eventItem,
                    eventName = eventName,
                    eventType = eventType,
                    patSummarySaveRequest = requestPayload,
                    prefRepo = prefRepo
                )

                val dependsOn = createEventDependency(eventItem, eventName, savePatSummeryEvent)
                val metadata = savePatSummeryEvent.metadata?.getMetaDataDtoFromString()
                val updatedMetaData = metadata?.copy(depends_on = dependsOn.getDependentEventsId())
                savePatSummeryEvent = savePatSummeryEvent.copy(
                    metadata = updatedMetaData?.json()
                )

                return savePatSummeryEvent
            }

            EventName.REJECTED_PAT_SCORE, EventName.INPROGRESS_PAT_SCORE, EventName.COMPLETED_PAT_SCORE, EventName.NOT_AVAILBLE_PAT_SCORE -> {
                val didiEntity = (eventItem as DidiEntity)
                val selectedTolaEntity = tolaDao.fetchSingleTolaFromServerId(didiEntity.cohortId)


                val requestPayload = getPatScoreSaveEvent(
                    didiEntity = (eventItem as DidiEntity),
                    questionListDao = questionListDao,
                    prefRepo = prefRepo,
                    tolaDeviceId = selectedTolaEntity?.localUniqueId ?: "",
                    tolaServerId = selectedTolaEntity?.serverId ?: 0
                )

                var savePatScoreEvent = getPatSaveScoreEvent(
                    eventItem = eventItem,
                    eventName = eventName,
                    eventType = eventType,
                    patScoreSaveEvent = requestPayload,
                    prefRepo = prefRepo
                )

                val dependsOn = createEventDependency(eventItem, eventName, savePatScoreEvent)
                val metadata = savePatScoreEvent.metadata?.getMetaDataDtoFromString()
                val updatedMetaData = metadata?.copy(depends_on = dependsOn.getDependentEventsId())
                savePatScoreEvent = savePatScoreEvent.copy(
                    metadata = updatedMetaData?.json()
                )

                return savePatScoreEvent
            }

            else -> {
                return super.createEvent(eventItem, eventName, eventType)
            }
        }

    }

    override suspend fun <T> createEventDependency(
        eventItem: T,
        eventName: EventName,
        dependentEvent: Events
    ): List<EventDependencyEntity> {
        val eventDependencyList = mutableListOf<EventDependencyEntity>()
        var filteredList = listOf<Events>()
        var dependentEventsName = eventName.getDependsOnEventNameForEvent()
        for (dependsOnEvent in dependentEventsName) {
            val eventList = eventsDao.getAllEventsForEventName(dependsOnEvent.name)
            when (eventName) {
                EventName.SAVE_PAT_ANSWERS -> {
                    filteredList = eventList.filter {
                        var editRequest = Gson().fromJson(
                            it.request_payload,
                            EditDidiWealthRankingRequest::class.java
                        )
                        dependentEvent.metadata?.getMetaDataDtoFromString()?.parentEntity
                            ?.get(KEY_PARENT_ENTITY_DIDI_NAME)?.equals(editRequest.name, true)!!
                                && dependentEvent.metadata?.getMetaDataDtoFromString()?.parentEntity
                            ?.get(KEY_PARENT_ENTITY_DADA_NAME)
                            ?.equals(editRequest.guardianName, true)!!
                                && dependentEvent.metadata?.getMetaDataDtoFromString()?.parentEntity
                            ?.get(KEY_PARENT_ENTITY_ADDRESS).equals(editRequest.address, true)
                                && dependentEvent.metadata?.getMetaDataDtoFromString()?.parentEntity
                            ?.get(KEY_PARENT_ENTITY_TOLA_NAME)
                            ?.equals(editRequest.cohortName, true)!!
                    }

                }

                EventName.REJECTED_PAT_SCORE, EventName.INPROGRESS_PAT_SCORE, EventName.COMPLETED_PAT_SCORE, EventName.NOT_AVAILBLE_PAT_SCORE -> {
                    filteredList = eventList.filter {
                        it.payloadLocalId == dependentEvent.payloadLocalId

                    }
                }


                else -> {
                    filteredList = emptyList()
                }
            }
            if (filteredList.isNotEmpty()) {
                break
            }

        }


        if (filteredList.isNotEmpty()) {

            val immediateDependentOn = ArrayList<Events>()
            immediateDependentOn.add(filteredList.last())

            eventDependencyList.addAll(
                immediateDependentOn.getEventDependencyEntityListFromEvents(
                    dependentEvent
                )
            )
        }
        return eventDependencyList
    }

    override suspend fun <T> saveEvent(
        eventItem: T,
        eventName: EventName,
        eventType: EventType
    ) {

        val event = this.createEvent(
            eventItem,
            eventName,
            eventType
        )

        if (event?.id?.equals(BLANK_STRING) != true) {
            event?.let {
                val eventDependencies = this.createEventDependency(eventItem, eventName, it)
                saveEventToMultipleSources(it, eventDependencies)

            }
        }
    }

    private fun getAllAnswersForDidi(didiId: Int): List<SectionAnswerEntity> {
        return answerDao.getAllNeedToPostQuesForDidi(didiId)
    }

    private fun getAllNumericAnswersForDidi(didiId: Int): List<NumericAnswerEntity> {
        return numericAnswerDao.getAllAnswersForDidi(didiId)
    }

}