package com.patsurvey.nudge.activities.survey

import com.nudge.core.EventSyncStatus
import com.nudge.core.KEY_PARENT_ENTITY_ADDRESS
import com.nudge.core.KEY_PARENT_ENTITY_DADA_NAME
import com.nudge.core.KEY_PARENT_ENTITY_DIDI_NAME
import com.nudge.core.KEY_PARENT_ENTITY_TOLA_NAME
import com.nudge.core.SELECTION_MISSION
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.database.entities.getDependentEventsId
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.nudge.core.enums.getDependsOnEventNameForEvent
import com.nudge.core.getEventDependencyEntityListFromEvents
import com.nudge.core.getSizeInLong
import com.nudge.core.json
import com.nudge.core.model.MetadataDto
import com.nudge.core.model.getMetaDataDtoFromString
import com.nudge.core.toDate
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
import com.patsurvey.nudge.model.request.AnswerDetailDTOListItem
import com.patsurvey.nudge.model.request.EditDidiWealthRankingRequest
import com.patsurvey.nudge.model.request.PATSummarySaveRequest
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.BPC_USER_TYPE
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_NAME
import com.patsurvey.nudge.utils.QuestionType
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.USER_BPC
import com.patsurvey.nudge.utils.USER_CRP
import com.patsurvey.nudge.utils.getParentEntityMapForEvent
import com.patsurvey.nudge.utils.updateStepStatus
import javax.inject.Inject

class PatSectionSummaryRepository @Inject constructor(
    val prefRepo: PrefRepo,
    private val questionListDao: QuestionListDao,
    private val answerDao: AnswerDao,
    private val numericAnswerDao: NumericAnswerDao,
    private val stepsListDao: StepsListDao,
    private val eventsDao: EventsDao
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

    private fun getAllAnswersForDidi(didiId: Int): List<SectionAnswerEntity> {
        return answerDao.getAllNeedToPostQuesForDidi(didiId)
    }

    private fun getAllNumericAnswersForDidi(didiId: Int): List<NumericAnswerEntity> {
        return numericAnswerDao.getAllAnswersForDidi(didiId)
    }

    private fun getSurveyId(questionId: Int): Int {
        return questionListDao.getQuestion(questionId).surveyId ?: 0
    }

    suspend fun getPatSummarySaveEventPayload(didiEntity: DidiEntity): PATSummarySaveRequest {
        val sectionAnswerEntityList = getAllAnswersForDidi(didiEntity.id)
        val numericAnswerEntityList = getAllNumericAnswersForDidi(didiEntity.id)
        val answerDetailDTOListItem = AnswerDetailDTOListItem.getAnswerDetailDtoListItem(sectionAnswerEntityList, numericAnswerEntityList)
        val patSummarySaveRequest = PATSummarySaveRequest.getPatSummarySaveRequest(
            didiEntity = didiEntity,
            answerDetailDTOList = answerDetailDTOListItem,
            languageId = (prefRepo.getAppLanguageId() ?: 2),
            surveyId = getSurveyId(sectionAnswerEntityList.first().questionId),
            villageEntity = prefRepo.getSelectedVillage(),
            userType = if((prefRepo.getPref(PREF_KEY_TYPE_NAME, "") ?: "").equals(BPC_USER_TYPE, true)) USER_BPC else USER_CRP
        )

        return patSummarySaveRequest
    }

    suspend fun getPatScoreSaveEvent(didiEntity: DidiEntity): EditDidiWealthRankingRequest {
        val passingMark = questionListDao.getPassingScore()
        val patScoreSaveRequest = EditDidiWealthRankingRequest.getRequestPayloadForPatScoreSave(
            didiEntity,
            passingMark,
            isBpcUserType = prefRepo.isUserBPC()
        )
        return patScoreSaveRequest
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
                val requestPayload = getPatSummarySaveEventPayload((eventItem as DidiEntity)).json()

                var savePatSummeryEvent = Events(
                    name = eventName.name,
                    type = eventType.name,
                    createdBy = prefRepo.getUserId(),
                    mobile_number = prefRepo.getMobileNumber(),
                    request_payload = requestPayload,
                    request_status = EventSyncStatus.OPEN.name,
                    modified_date = System.currentTimeMillis().toDate(),
                    consumer_response_payload = null,
                    consumer_status = BLANK_STRING,
                    metadata = MetadataDto(
                        mission = SELECTION_MISSION,
                        depends_on = listOf(),
                        request_payload_size = requestPayload.getSizeInLong(),
                        parentEntity = getParentEntityMapForEvent(eventItem, eventName)
                    ).json()
                )

                val dependsOn = createEventDependency(eventItem, eventName, savePatSummeryEvent)
                val metadata = savePatSummeryEvent.metadata?.getMetaDataDtoFromString()
                val updatedMetaData = metadata?.copy(depends_on = dependsOn.getDependentEventsId())
                savePatSummeryEvent = savePatSummeryEvent.copy(
                    metadata = updatedMetaData?.json()
                )

                return savePatSummeryEvent
            }
            EventName.SAVE_PAT_SCORE -> {
                val requestPayload = getPatScoreSaveEvent((eventItem as DidiEntity)).json()

                var savePatScoreEvent = Events(
                    name = eventName.name,
                    type = eventType.name,
                    createdBy = prefRepo.getUserId(),
                    mobile_number = prefRepo.getMobileNumber(),
                    request_payload = requestPayload,
                    request_status = EventSyncStatus.OPEN.name,
                    modified_date = System.currentTimeMillis().toDate(),
                    consumer_response_payload = null,
                    consumer_status = BLANK_STRING,
                    metadata = MetadataDto(
                        mission = SELECTION_MISSION,
                        depends_on = listOf(),
                        request_payload_size = requestPayload.getSizeInLong(),
                        parentEntity = getParentEntityMapForEvent(eventItem, eventName)
                    ).json()
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

        eventName.getDependsOnEventNameForEvent().forEach { dependsOnEvent ->
            val eventList = eventsDao.getAllEventsForEventName(dependsOnEvent.name)
            when (eventName) {
                EventName.SAVE_PAT_ANSWERS -> {
                    filteredList = eventList.filter {
                        val eventPayload = (eventItem as DidiEntity)
                        dependentEvent.metadata?.getMetaDataDtoFromString()?.parentEntity
                            ?.get(KEY_PARENT_ENTITY_DIDI_NAME)?.equals(eventPayload.name, true)!!
                                && dependentEvent.metadata?.getMetaDataDtoFromString()?.parentEntity
                            ?.get(KEY_PARENT_ENTITY_DADA_NAME)?.equals(eventPayload.guardianName, true)!!
                                && dependentEvent.metadata?.getMetaDataDtoFromString()?.parentEntity
                            ?.get(KEY_PARENT_ENTITY_ADDRESS).equals(eventPayload.address, true)
                                && dependentEvent.metadata?.getMetaDataDtoFromString()?.parentEntity
                            ?.get(KEY_PARENT_ENTITY_TOLA_NAME)?.equals(eventPayload.cohortName, true)!!
                    }
                }

                else -> {
                    filteredList = emptyList()
                }
            }
        }

        eventDependencyList.addAll(filteredList.getEventDependencyEntityListFromEvents(dependentEvent))

        return eventDependencyList
    }

    override suspend fun <T> insertEventIntoDb(
        eventItem: T,
        eventName: EventName,
        eventType: EventType
    ) {
        val eventObserver = NudgeCore.getEventObserver()

        val event = this.createEvent(
            eventItem,
            eventName,
            eventType
        )

        if (event?.id?.equals(BLANK_STRING) != true) {
            event?.let {
                eventObserver?.addEvent(it)
                val eventDependencies = this.createEventDependency(eventItem, eventName, it)
                if (eventDependencies.isNotEmpty()) {
                    eventObserver?.addEventDependencies(eventDependencies)
                }
            }
        }
    }


}