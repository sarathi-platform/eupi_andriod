package com.patsurvey.nudge.activities

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.nudge.core.EventSyncStatus
import com.nudge.core.SELECTION_MISSION
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
import com.patsurvey.nudge.activities.settings.TransactionIdRequest
import com.patsurvey.nudge.activities.settings.TransactionIdResponse
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.CasteEntity
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.LastTolaSelectedEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.converters.BeneficiaryProcessStatusModel
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.CasteListDao
import com.patsurvey.nudge.database.dao.LastSelectedTolaDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.request.AddDidiRequest
import com.patsurvey.nudge.model.request.EditDidiRequest
import com.patsurvey.nudge.model.request.EditWorkFlowRequest
import com.patsurvey.nudge.model.request.getAddDidiRequestPayloadFromString
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.model.response.DidiApiResponse
import com.patsurvey.nudge.model.response.WorkFlowResponse
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.getParentEntityMapForEvent
import com.patsurvey.nudge.utils.getPatScoreSaveEvent
import com.patsurvey.nudge.utils.getPatSummarySaveEventPayload
import com.patsurvey.nudge.utils.updateLastSyncTime
import javax.inject.Inject


class AddDidiRepository @Inject constructor(
    val prefRepo: PrefRepo,
    val casteListDao: CasteListDao,
    val tolaDao: TolaDao,
    val stepsListDao: StepsListDao,
    val villageListDao: VillageListDao,
    val lastSelectedTolaDao: LastSelectedTolaDao,
    val questionListDao: QuestionListDao,
    val answerDao: AnswerDao,
    val numericAnswerDao: NumericAnswerDao,
) : BaseRepository() {
    fun getSelectedVillage(): VillageEntity {
        return this.prefRepo.getSelectedVillage()
    }

    fun getAppLanguageId(): Int? {
        return this.prefRepo.getAppLanguageId()
    }

    fun updateLastSyncTime(lastSyncTime: String) {
        updateLastSyncTime(this.prefRepo, lastSyncTime)
    }

    fun savePref(key: String, value: Int) {
        this.prefRepo.savePref(key, value)
    }

    fun savePref(key: String, value: Boolean) {
        this.prefRepo.savePref(key, value)
    }

    fun savePref(key: String, value: String) {
        this.prefRepo.savePref(key, value)
    }

    fun savePref(key: String, value: Long) {
        this.prefRepo.savePref(key, value)
    }

    fun getPref(key: String, defaultValue: Int): Int {
        return this.prefRepo.getPref(key, defaultValue)
    }

    fun getPref(key: String, defaultValue: String): String? {
        return this.prefRepo.getPref(key, defaultValue)
    }

    fun getPref(key: String, defaultValue: Boolean): Boolean {
        return this.prefRepo.getPref(key, defaultValue)
    }

    fun getPref(key: String, defaultValue: Long): Long {
        return this.prefRepo.getPref(key, defaultValue)
    }

    fun getPref(key: String, defaultValue: Float): Float {
        return this.prefRepo.getPref(key, defaultValue)
    }

    fun getAllCasteForLanguage(languageId: Int): List<CasteEntity> {
        return this.casteListDao.getAllCasteForLanguage(languageId)
    }

    fun fetchSingleTola(id: Int): TolaEntity? {
        return this.tolaDao.fetchSingleTola(id)
    }

    fun getAllTolasForVillage(villageId: Int): List<TolaEntity> {
        return this.tolaDao.getAllTolasForVillage(villageId)
    }

    fun fetchSingleTolaFromServerId(id: Int): TolaEntity? {
        return this.tolaDao.fetchSingleTolaFromServerId(id)
    }


    fun getAllDidis(): List<DidiEntity> {
        return this.didiDao.getAllDidis()
    }

    fun getAllDidisForVillage(villageId: Int): List<DidiEntity> {
        return this.didiDao.getAllDidisForVillage(villageId)
    }

    fun getDidi(id: Int): DidiEntity {
        return this.didiDao.getDidi(id)
    }

    fun getDidiExist(
        name: String,
        address: String,
        guardianName: String,
        tolaId: Int,
        villageId: Int
    ): Int {
        return this.didiDao.getDidiExist(name, address, guardianName, tolaId, villageId)
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

    override suspend fun <T> createEvent(
        eventItem: T,
        eventName: EventName,
        eventType: EventType
    ): Events? {

        if (eventType != EventType.STATEFUL)
            return super.createEvent(eventItem, eventName, eventType)

        if (eventItem !is DidiEntity)
            return super.createEvent(eventItem, eventName, eventType)

        when(eventName) {
            EventName.ADD_DIDI -> {
                val selectedTolaEntity= fetchSingleTolaFromServerId( (eventItem as  DidiEntity).cohortId)
                val requestPayload = AddDidiRequest.getRequestObjectForDidi(
                    eventItem as DidiEntity,
                    selectedTolaEntity?.serverId,
                    selectedTolaEntity?.localUniqueId
                ).json()

                var addDidiEvent = Events(
                    name = eventName.name,
                    type = eventName.topicName,
                    createdBy = prefRepo.getUserId(),
                    mobile_number = prefRepo.getMobileNumber(),
                    request_payload = requestPayload,
                    status = EventSyncStatus.OPEN.name,
                    modified_date = System.currentTimeMillis().toDate(),
                    result = null,
                    consumer_status = BLANK_STRING,
                    payloadLocalId = (eventItem as DidiEntity).localUniqueId,
                    metadata = MetadataDto(
                        mission = SELECTION_MISSION,
                        depends_on = listOf(),
                        request_payload_size = requestPayload.getSizeInLong(),
                        parentEntity = getParentEntityMapForEvent(eventItem, eventName)
                    ).json()
                )
                val dependsOn = createEventDependency(eventItem, eventName, addDidiEvent)
                val metadata = addDidiEvent.metadata?.getMetaDataDtoFromString()
                val updatedMetaData = metadata?.copy(depends_on = dependsOn.getDependentEventsId())
                addDidiEvent = addDidiEvent.copy(
                    metadata = updatedMetaData?.json()
                )

                return addDidiEvent
            }
            EventName.UPDATE_DIDI -> {
                val selectedTolaEntity =
                    fetchSingleTolaFromServerId((eventItem as DidiEntity).cohortId)
                val requestPayload = AddDidiRequest.getRequestObjectForDidi(
                    eventItem as DidiEntity,
                    selectedTolaEntity?.serverId,
                    selectedTolaEntity?.localUniqueId
                ).json()

                var updateDidiEvent = Events(
                    name = eventName.name,
                    type = eventName.topicName,
                    createdBy = prefRepo.getUserId(),
                    mobile_number = prefRepo.getMobileNumber(),
                    request_payload = requestPayload,
                    status = EventSyncStatus.OPEN.name,
                    modified_date = System.currentTimeMillis().toDate(),
                    result = null,
                    consumer_status = BLANK_STRING,
                    payloadLocalId = (eventItem as DidiEntity).localUniqueId,
                    metadata = MetadataDto(
                        mission = SELECTION_MISSION,
                        depends_on = listOf(),
                        request_payload_size = requestPayload.getSizeInLong(),
                        parentEntity = getParentEntityMapForEvent(eventItem, eventName)
                    ).json()
                )
                val dependsOn = createEventDependency(eventItem, eventName, updateDidiEvent)
                val metadata = updateDidiEvent.metadata?.getMetaDataDtoFromString()
                val updatedMetaData = metadata?.copy(depends_on = dependsOn.getDependentEventsId())
                updateDidiEvent = updateDidiEvent.copy(
                    metadata = updatedMetaData?.json()
                )

                return updateDidiEvent
            }
            EventName.DELETE_DIDI -> {
                val selectedTolaEntity =
                    fetchSingleTolaFromServerId((eventItem as DidiEntity).cohortId)

                val requestPayload = AddDidiRequest.getRequestObjectForDidi(
                    eventItem as DidiEntity,
                    selectedTolaEntity?.serverId,
                    selectedTolaEntity?.localUniqueId
                ).json()

                var deleteDidiRequest = Events(
                    name = eventName.name,
                    type = eventName.topicName,
                    createdBy = prefRepo.getUserId(),
                    mobile_number = prefRepo.getMobileNumber(),
                    request_payload = requestPayload,
                    status = EventSyncStatus.OPEN.name,
                    modified_date = System.currentTimeMillis().toDate(),
                    result = null,
                    consumer_status = BLANK_STRING,
                    payloadLocalId = (eventItem as DidiEntity).localUniqueId,
                    metadata = MetadataDto(
                        mission = SELECTION_MISSION,
                        depends_on = listOf(),
                        request_payload_size = requestPayload.getSizeInLong(),
                        parentEntity = getParentEntityMapForEvent(eventItem, eventName)
                    ).json()
                )
                val dependsOn = createEventDependency(eventItem, eventName, deleteDidiRequest)
                val metadata = deleteDidiRequest.metadata?.getMetaDataDtoFromString()
                val updatedMetaData = metadata?.copy(depends_on = dependsOn.getDependentEventsId())
                deleteDidiRequest = deleteDidiRequest.copy(
                    metadata = updatedMetaData?.json()
                )

                return deleteDidiRequest
            }
            EventName.SAVE_PAT_ANSWERS -> {
                val requestPayload = getPatSummarySaveEventPayload(
                    didiEntity = (eventItem as DidiEntity),
                    answerDao = answerDao,
                    numericAnswerDao = numericAnswerDao,
                    questionListDao = questionListDao,
                    prefRepo= prefRepo
                )

                var savePatSummeryEvent = getPatSaveAnswersEvent(eventItem = eventItem, eventName = eventName, eventType = eventType, patSummarySaveRequest = requestPayload, prefRepo = prefRepo)

                val dependsOn = createEventDependency(eventItem, eventName, savePatSummeryEvent)
                val metadata = savePatSummeryEvent.metadata?.getMetaDataDtoFromString()
                val updatedMetaData = metadata?.copy(depends_on = dependsOn.getDependentEventsId())
                savePatSummeryEvent = savePatSummeryEvent.copy(
                    metadata = updatedMetaData?.json()
                )

                return savePatSummeryEvent
            }
            EventName.SAVE_PAT_SCORE -> {
                val requestPayload = getPatScoreSaveEvent(didiEntity = (eventItem as DidiEntity), questionListDao = questionListDao, prefRepo = prefRepo)

                var savePatScoreEvent = getPatSaveScoreEvent(eventItem = eventItem, eventName = eventName, eventType = eventType, patScoreSaveEvent = requestPayload, prefRepo = prefRepo)

                val dependsOn = createEventDependency(eventItem, eventName, savePatScoreEvent)
                val metadata = savePatScoreEvent.metadata?.getMetaDataDtoFromString()
                val updatedMetaData = metadata?.copy(depends_on = dependsOn.getDependentEventsId())
                savePatScoreEvent = savePatScoreEvent.copy(
                    metadata = updatedMetaData?.json()
                )

                return savePatScoreEvent
            }
            else -> {
                return null
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
                EventName.ADD_DIDI -> {
                    filteredList = eventList.filter {
                        val eventPayload =
                            dependentEvent.request_payload?.getAddDidiRequestPayloadFromString()
                        it.payloadLocalId == eventPayload?.cohortDeviceId
                    }
                }
                EventName.UPDATE_DIDI -> {
                    filteredList = eventList.filter {
                        it.payloadLocalId == dependentEvent.payloadLocalId
                    }

                }
                EventName.DELETE_DIDI, EventName.SAVE_PAT_ANSWERS, EventName.SAVE_PAT_SCORE -> {
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
             immediateDependentOn.add(filteredList.first())

             eventDependencyList.addAll(
                 immediateDependentOn.getEventDependencyEntityListFromEvents(
                     dependentEvent
                 )
             )
         }
        return eventDependencyList
    }

    fun insertDidi(didi: DidiEntity) {
        this.didiDao.insertDidi(didi)
    }

    fun updateDidi(didi: DidiEntity) {
        this.didiDao.updateDidi(didi)
    }

    fun deleteDidiTable() {
        this.didiDao.deleteDidiTable()
    }

    fun deleteDidiForVillage(villageId: Int) {
        this.didiDao.deleteDidiForVillage(villageId)
    }

    fun setNeedToPost(ids: List<Int>, needsToPost: Boolean) {
        this.didiDao.setNeedToPost(ids, needsToPost)
    }

    fun updateNeedToPost(id: Int, needsToPost: Boolean) {
        this.didiDao.updateNeedToPost(id, needsToPost)
    }

    fun updateDidiDetailAfterSync(
        id: Int,
        serverId: Int,
        needsToPost: Boolean,
        transactionId: String,
        createdDate: Long,
        modifiedDate: Long
    ) {
        this.didiDao.updateDidiDetailAfterSync(
            id,
            serverId,
            needsToPost,
            transactionId,
            createdDate,
            modifiedDate
        )
    }

    fun setNeedToPostRanking(id: Int, needsToPostRanking: Boolean) {
        this.didiDao.setNeedToPostRanking(id, needsToPostRanking)
    }

    fun setNeedToPostRankingServerId(id: Int, needsToPostRanking: Boolean) {
        this.didiDao.setNeedToPostRankingServerId(id, needsToPostRanking)
    }

    fun updateDidiRank(didiId: Int, rank: String) {
        this.didiDao.updateDidiRank(didiId, rank)
    }

    fun fetchPendingVOStatusStatusDidi(
        needsToPostVo: Boolean,
        transactionId: String?
    ): List<DidiEntity> {
        return this.didiDao.fetchPendingVOStatusStatusDidi(needsToPostVo, transactionId)
    }

    fun deleteAllDidi() {
        this.didiDao.deleteAllDidi()
    }

    fun fetchAllDidiNeedToAdd(
        needsToPost: Boolean,
        transactionId: String?,
        serverId: Int
    ): List<DidiEntity> {
        return this.didiDao.fetchAllDidiNeedToAdd(needsToPost, transactionId, serverId, DidiStatus.DIDI_ACTIVE.ordinal)
    }

    fun fetchAllDidiNeedToUpdate(
        needsToPost: Boolean,
        transactionId: String?,
        serverId: Int
    ): List<DidiEntity> {
        return this.didiDao.fetchAllDidiNeedToUpdate(needsToPost, transactionId)
    }

    fun fetchAllDidiNeedToDelete(status: Int): List<DidiEntity> {
        return this.didiDao.fetchAllDidiNeedToDelete(DidiStatus.DIID_DELETED.ordinal, true, "", 0)
    }

    fun fetchAllPendingDidiNeedToUpdate(
        needsToPost: Boolean,
        transactionId: String?,
        serverId: Int
    ): List<DidiEntity> {
        return didiDao.fetchAllPendingDidiNeedToUpdate(needsToPost, transactionId)
    }

    fun fetchAllPendingDidiNeedToDelete(
        status: Int,
        transactionId: String?,
        serverId: Int
    ): List<DidiEntity> {
        return this.didiDao.fetchAllPendingDidiNeedToDelete(status, transactionId)
    }

    fun deleteDidi(id: Int) {
        this.didiDao.deleteDidi(id)
    }

    fun updateDidiScore(score: Double, comment: String, isDidiAccepted: Boolean, didiId: Int) {
        this.didiDao.updateDidiScore(score, comment, isDidiAccepted, didiId)
    }

    fun fetchVOEndorseStatusDidi(villageId: Int): List<DidiEntity> {
        return this.didiDao.fetchVOEndorseStatusDidi(villageId)
    }

    fun updateModifiedDate(localModifiedDate: Long, didiId: Int) {
        this.didiDao.updateModifiedDate(localModifiedDate, didiId)
    }

    fun updateModifiedDateServerId(localModifiedDate: Long, didiId: Int) {
        this.didiDao.updateModifiedDateServerId(localModifiedDate, didiId)
    }

    fun getAllNeedToPostVoDidi(needsToPostVo: Boolean, villageId: Int): List<DidiEntity> {
        return this.didiDao.getAllNeedToPostVoDidi(needsToPostVo, villageId)
    }

    fun updateImageLocalPath(didiId: Int, localPath: String) {
        this.didiDao.updateImageLocalPath(didiId, localPath)
    }

    fun updateNeedsToPostImage(id: Int, needsToPostImage: Boolean) {
        this.didiDao.updateNeedsToPostImage(id, needsToPostImage)
    }

    fun fetchAllDidiNeedsToPostImage(needsToPostImage: Boolean): List<DidiEntity> {
        return this.didiDao.fetchAllDidiNeedsToPostImage(needsToPostImage)
    }

    fun updateRankEditFlag(villageId: Int, rankingEdit: Boolean) {
        this.didiDao.updateRankEditFlag(villageId, rankingEdit)
    }

    fun updatePatEditFlag(villageId: Int, patEdit: Boolean) {
        this.didiDao.updatePatEditFlag(villageId, patEdit)
    }

    fun updateVoEndorsementEditFlag(villageId: Int, voEndorsementEdit: Boolean) {
        this.didiDao.updateVoEndorsementEditFlag(villageId, voEndorsementEdit)
    }

    fun getDidiScoreFromDb(didiId: Int): Double {
        return this.didiDao.getDidiScoreFromDb(didiId)
    }

    fun fetchAllDidiNeedsToPostImageWithLimit(needsToPostImage: Boolean): List<DidiEntity> {
        return this.didiDao.fetchAllDidiNeedsToPostImageWithLimit(needsToPostImage)
    }

    fun updatePATEditStatus(didiId: Int, patEdit: Boolean) {
        this.didiDao.updatePATEditStatus(didiId, patEdit)
    }

    fun updateDidiAbleBodiedStatus(didiId: Int, ableBodiedFlag: Int) {
        this.didiDao.updateDidiAbleBodiedStatus(didiId, ableBodiedFlag)
    }

    fun fetchPendingVerificationDidiCount(villageId: Int): Int {
        return this.didiDao.fetchPendingVerificationDidiCount(villageId)
    }

    fun deleteDidisForTola(tolaId: Int, activeStatus: Int, needsToPostDeleteStatus: Boolean) {
        this.didiDao.deleteDidisForTola(tolaId, activeStatus, needsToPostDeleteStatus)
    }

    fun updateBeneficiaryProcessStatus(didiId: Int, status: List<BeneficiaryProcessStatusModel>) {
        this.didiDao.updateBeneficiaryProcessStatus(didiId, status)
    }

    fun updateBeneficiaryProcessStatusServerId(
        didiId: Int,
        status: List<BeneficiaryProcessStatusModel>
    ) {
        this.didiDao.updateBeneficiaryProcessStatusServerId(didiId, status)
    }


    fun updateQuesSectionStatus(didiId: Int, patSurveyProgress: Int) {
        this.didiDao.updateQuesSectionStatus(didiId, patSurveyProgress)
    }

    fun getDidisForTola(tolaId: Int): List<DidiEntity> {
        return this.didiDao.getDidisForTola(tolaId)
    }

    fun deleteDidisForTola(tolaId: Int) {
        this.didiDao.deleteDidisForTola(tolaId)
    }

    fun getAllPendingPATDidisCount(villageId: Int): Int {
        return this.didiDao.getAllPendingPATDidisCount(villageId)
    }

    fun updateDidiTransactionId(id: Int, transactionId: String) {
        this.didiDao.updateDidiTransactionId(id, transactionId)
    }

    fun fetchLastDidiDetails(): DidiEntity {
        return this.didiDao.fetchLastDidiDetails()
    }

    fun fetchPendingDidi(needsToPost: Boolean, transactionId: String?): List<DidiEntity> {
        return this.didiDao.fetchPendingDidi(needsToPost, transactionId)
    }

    fun updateNeedToPostPAT(needsToPostPAT: Boolean, didiId: Int, villageId: Int) {
        this.didiDao.updateNeedToPostPAT(needsToPostPAT, didiId, villageId)
        if (this.prefRepo.isUserBPC()) {
            didiDao.updateNeedsToPostBPCProcessStatus(true, didiId)
        }

    }

    fun deleteDidiOffline(id: Int, activeStatus: Int, needsToPostDeleteStatus: Boolean) {
        this.didiDao.deleteDidiOffline(id, activeStatus, needsToPostDeleteStatus)
    }

    fun updateDeletedDidiNeedToPostStatus(id: Int, needsToPostDeleteStatus: Boolean) {
        this.didiDao.updateDeletedDidiNeedToPostStatus(id, needsToPostDeleteStatus)
    }

    fun fetchVillageDetailsForLanguage(villageId: Int, languageId: Int): VillageEntity {
        return this.villageListDao.fetchVillageDetailsForLanguage(villageId, languageId)
    }

    fun getVillage(id: Int): VillageEntity {
        return this.villageListDao.getVillage(id)
    }

    fun updateLastCompleteStep(villageId: Int, stepId: List<Int>) {
        this.villageListDao.updateLastCompleteStep(villageId, stepId)
    }

    fun getAllQuestionsForLanguage(languageId: Int): List<QuestionEntity> {
        return this.questionListDao.getAllQuestionsForLanguage(languageId)
    }


    fun getAnswerForDidi(actionType: String, didiId: Int): List<SectionAnswerEntity> {
        return this.answerDao.getAnswerForDidi(actionType, didiId)
    }

    fun fetchOptionYesCount(didiId: Int, type: String, actionType: String): Int {
        return this.answerDao.fetchOptionYesCount(didiId, type, actionType)
    }

    suspend fun deleteDidi(didiId: JsonArray): ApiResponseModel<List<DidiEntity>> {
        NudgeLogger.d("AddDidiRepository","deleteDidi Request=>${Gson().toJson(didiId)}")
        return this.apiInterface.deleteDidi(didiId)
    }

    suspend fun getPendingStatus(transactionIdRequest: TransactionIdRequest): ApiResponseModel<List<TransactionIdResponse>> {
        return this.apiInterface.getPendingStatus(transactionIdRequest)
    }

    suspend fun updateDidis(didiWealthRankingRequest: List<EditDidiRequest>): ApiResponseModel<List<DidiEntity>> {
        NudgeLogger.d("AddDidiRepository","updateDidis Request=>${Gson().toJson(didiWealthRankingRequest)}")
        return this.apiInterface.updateDidis(didiWealthRankingRequest)
    }

    suspend fun addDidis(didiList: JsonArray): ApiResponseModel<List<DidiApiResponse>> {
        NudgeLogger.d("AddDidiRepository","addDidis Request=>${Gson().toJson(didiList)}")
        return this.apiInterface.addDidis(didiList)
    }

    suspend fun editWorkFlow(addWorkFlowRequest: List<EditWorkFlowRequest>): ApiResponseModel<List<WorkFlowResponse>> {
        NudgeLogger.d("AddDidiRepository","editWorkFlow Request=> ${Gson().toJson(addWorkFlowRequest)}")
        return this.apiInterface.editWorkFlow(addWorkFlowRequest)
    }


    fun getAllSteps(): List<StepListEntity> {
        return this.stepsListDao.getAllSteps()
    }

    fun insert(step: StepListEntity) {
        this.stepsListDao.insert(step)
    }


    fun markStepAsCompleteOrInProgress(stepId: Int, isComplete: Int = 0, villageId: Int) {
        this.stepsListDao.markStepAsCompleteOrInProgress(stepId, isComplete, villageId)

    }

    fun markStepAsInProgress(orderNumber: Int, inProgress: Int = 1, villageId: Int) {
        this.stepsListDao.markStepAsInProgress(orderNumber, inProgress, villageId)
    }

    fun isStepComplete(id: Int, villageId: Int): Int {
        return this.stepsListDao.isStepComplete(id, villageId)
    }


    fun getAllStepsForVillage(villageId: Int): List<StepListEntity> {
        return this.stepsListDao.getAllStepsForVillage(villageId)
    }

    fun getStepForVillage(villageId: Int, stepId: Int): StepListEntity {
        return this.stepsListDao.getStepForVillage(villageId = villageId, stepId = stepId)
    }


    fun updateWorkflowId(stepId: Int, workflowId: Int, villageId: Int, status: String) {
        this.stepsListDao.updateWorkflowId(stepId, workflowId, villageId, status)
    }

    fun updateWorkflowId(stepId: Int, workflowId: Int, status: String) {
        this.stepsListDao.updateWorkflowId(stepId, workflowId, status)
    }

    fun getAllCompleteStepsForVillage(villageId: Int): List<StepListEntity> {
        return this.stepsListDao.getAllCompleteStepsForVillage(villageId)
    }

    fun updateNeedToPost(id: Int, villageId: Int, needsToPost: Boolean) {
        this.stepsListDao.updateNeedToPost(id, villageId, needsToPost)
    }

    fun getTolaCountForVillage(villageId: Int): Long {
        return this.lastSelectedTolaDao.getTolaCountForVillage(villageId)
    }

    fun getTolaForVillage(villageId: Int): LastTolaSelectedEntity {
        return this.lastSelectedTolaDao.getTolaForVillage(villageId)
    }

    fun updateSelectedTola(tolaId: Int, tolaName: String, villageId: Int) {
        this.lastSelectedTolaDao.updateSelectedTola(tolaId, tolaName, villageId)
    }

    fun insertSelectedTola(caste: LastTolaSelectedEntity) {
        this.lastSelectedTolaDao.insertSelectedTola(caste)
    }

    fun saveStepId(stepId: Int) {
        this.prefRepo.saveStepId(stepId)
    }

    fun getFromPage(): String {
        return this.prefRepo.getFromPage()
    }

    fun saveSummaryScreenOpenFrom(openFrom: Int) {
        this.prefRepo.saveSummaryScreenOpenFrom(openFrom)
    }

    fun saveQuestionScreenOpenFrom(openFrom: Int) {
        this.prefRepo.saveQuestionScreenOpenFrom(openFrom)
    }

    fun getDidisToBeDeletedForVillage(
        villageId: Int,
        activeStatus: Int,
        needsToPostDeleteStatus: Boolean,
        transactionId: String?,
        serverId: Int
    ): List<DidiEntity> {
        return this.didiDao.getDidisToBeDeletedForVillage(
            villageId,
            activeStatus,
            needsToPostDeleteStatus,
            transactionId,
            serverId
        )
    }
}

