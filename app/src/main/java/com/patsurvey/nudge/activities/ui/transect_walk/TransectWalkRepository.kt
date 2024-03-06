package com.patsurvey.nudge.activities.ui.transect_walk

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.nudge.core.EventSyncStatus
import com.nudge.core.KEY_PARENT_ENTITY_ADDRESS
import com.nudge.core.KEY_PARENT_ENTITY_DADA_NAME
import com.nudge.core.KEY_PARENT_ENTITY_DIDI_NAME
import com.nudge.core.KEY_PARENT_ENTITY_TOLA_NAME
import com.nudge.core.KEY_PARENT_ENTITY_VILLAGE_ID
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
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.request.AddCohortRequest
import com.patsurvey.nudge.model.request.AddDidiRequest
import com.patsurvey.nudge.model.request.DeleteTolaRequest
import com.patsurvey.nudge.model.request.EditWorkFlowRequest
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.model.response.TolaApiResponse
import com.patsurvey.nudge.model.response.WorkFlowResponse
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.getParentEntityMapForEvent
import com.patsurvey.nudge.utils.updateLastSyncTime
import javax.inject.Inject


class TransectWalkRepository @Inject constructor(
    val prefRepo: PrefRepo,
    val tolaDao: TolaDao,
    val stepsListDao: StepsListDao,
    val villageListDao: VillageListDao,
) : BaseRepository() {

    fun getSelectedVillage(): VillageEntity {
        return this.prefRepo.getSelectedVillage()
    }

    fun savePref(key: String, value: Int) {
        this.prefRepo.savePref(key, value)
    }


    fun updateLastSyncTime(lastSyncTime: String) {
        updateLastSyncTime(this.prefRepo, lastSyncTime)
    }

    fun getAppLanguageId(): Int? {
        return this.prefRepo.getAppLanguageId()
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

    fun getPref(key: String, defaultValue: Long): Long {
        return this.prefRepo.getPref(key, defaultValue)
    }

    fun getTolaExist(name: String, villageId: Int): Int {
        return this.tolaDao.getTolaExist(name, villageId)
    }

    fun tolaInsert(tola: TolaEntity) {
        this.tolaDao.insert(tola)
    }

    override suspend fun <T> createEvent(
        eventItem: T,
        eventName: EventName,
        eventType: EventType
    ): Events? {
        if (eventType != EventType.STATEFUL)
            return super.createEvent(eventItem, eventName, eventType)

        if (eventItem !is TolaEntity)
            return super.createEvent(eventItem, eventName, eventType)

        when (eventName) {
            EventName.ADD_TOLA -> {

                val requestPayload =
                    AddCohortRequest.getRequestObjectForTola(eventItem as TolaEntity).json()
                val addTolaEvent = Events(
                    name = eventName.name,
                    type = eventName.topicName,
                    createdBy = prefRepo.getUserId(),
                    mobile_number = prefRepo.getMobileNumber(),
                    modified_date = System.currentTimeMillis().toDate(),
                    request_payload = requestPayload,
                    payloadLocalId = (eventItem as TolaEntity).localUniqueId,
                    status = EventSyncStatus.OPEN.name,
                    metadata = MetadataDto(
                        mission = SELECTION_MISSION,
                        depends_on = emptyList(),
                        request_payload_size = requestPayload.getSizeInLong(),
                    ).json(),
                    consumer_status = BLANK_STRING,
                    result = null
                )
                return addTolaEvent
            }

            EventName.UPDATE_TOLA -> {
                val requestPayload =
                    AddCohortRequest.getRequestObjectForTola(eventItem as TolaEntity).json()
                var updateTolaEvent = Events(
                    name = eventName.name,
                    type = eventName.topicName,
                    createdBy = prefRepo.getUserId(),
                    mobile_number = prefRepo.getMobileNumber(),
                    modified_date = System.currentTimeMillis().toDate(),
                    request_payload = requestPayload,
                    status = EventSyncStatus.OPEN.name,
                    payloadLocalId = (eventItem as TolaEntity).localUniqueId,
                    metadata = MetadataDto(
                        mission = SELECTION_MISSION,
                        depends_on = emptyList(),
                        request_payload_size = requestPayload.getSizeInLong(),
                        parentEntity = getParentEntityMapForEvent(eventItem, EventName.UPDATE_TOLA)
                    ).json(),
                    consumer_status = BLANK_STRING,
                    result = null
                )
                val dependsOn = createEventDependency(eventItem, eventName, updateTolaEvent)
                val metadata = updateTolaEvent.metadata?.getMetaDataDtoFromString()
                val updatedMetaData = metadata?.copy(depends_on = dependsOn.getDependentEventsId())
                updateTolaEvent = updateTolaEvent.copy(
                    metadata = updatedMetaData?.json()
                )
                return updateTolaEvent
            }

            EventName.DELETE_TOLA -> {

                val requestPayload =
                    DeleteTolaRequest.getRequestObjectForDeleteTola(eventItem as TolaEntity).json()
                val deleteTolaEvent = Events(
                    name = eventName.name,
                    type = eventName.topicName,
                    createdBy = prefRepo.getUserId(),
                    mobile_number = prefRepo.getMobileNumber(),
                    modified_date = System.currentTimeMillis().toDate(),
                    request_payload = requestPayload,
                    status = EventSyncStatus.OPEN.name,
                    payloadLocalId = (eventItem as TolaEntity).localUniqueId,

                    metadata = MetadataDto(
                        mission = SELECTION_MISSION,
                        depends_on = emptyList(),
                        request_payload_size = requestPayload.getSizeInLong(),
                        parentEntity = getParentEntityMapForEvent(eventItem, eventName)
                    ).json(),
                    consumer_status = BLANK_STRING,
                    result = null
                )

                return deleteTolaEvent
            }

            EventName.DELETE_DIDI -> {

                var tola = tolaDao.fetchSingleTola((eventItem as DidiEntity).cohortId)
                val requestPayload =
                    AddDidiRequest.getRequestObjectForDidi(
                        eventItem as DidiEntity,
                        tola?.serverId ?: 0,
                        tola?.localUniqueId
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
                EventName.ADD_TOLA -> {
                    filteredList = emptyList()
                }

                EventName.UPDATE_TOLA -> {
                    filteredList = eventList.filter {
                        it.payloadLocalId == dependentEvent.payloadLocalId
                    }
                }

                EventName.UPDATE_DIDI -> {
                    filteredList = eventList.filter {
                        it.payloadLocalId == dependentEvent.payloadLocalId
                    }
                }

                EventName.DELETE_TOLA -> {
                    filteredList = eventList.filter {

                        val eventPayload = Gson().fromJson(
                            it.request_payload,
                            AddCohortRequest::class.java
                        )
                        dependentEvent.metadata?.getMetaDataDtoFromString()?.parentEntity
                            ?.get(KEY_PARENT_ENTITY_TOLA_NAME)
                            ?.equals(eventPayload.id.toString(), true)!!
                                && dependentEvent.metadata?.getMetaDataDtoFromString()?.parentEntity
                            ?.get(KEY_PARENT_ENTITY_VILLAGE_ID)
                            ?.equals(eventPayload.villageId.toString(), true)!!
                    }
                }

                EventName.DELETE_DIDI -> {
                    filteredList = eventList.filter {

                        val eventPayload = Gson().fromJson(
                            it.request_payload,
                            AddDidiRequest::class.java
                        )
                        dependentEvent.metadata?.getMetaDataDtoFromString()?.parentEntity
                            ?.get(KEY_PARENT_ENTITY_DIDI_NAME)?.equals(eventPayload?.name, true)!!
                                && dependentEvent.metadata?.getMetaDataDtoFromString()?.parentEntity
                            ?.get(KEY_PARENT_ENTITY_DADA_NAME)
                            ?.equals(eventPayload?.guardianName, true)!!
                                && dependentEvent.metadata?.getMetaDataDtoFromString()?.parentEntity
                            ?.get(KEY_PARENT_ENTITY_ADDRESS).equals(eventPayload?.address, true)
                                && dependentEvent.metadata?.getMetaDataDtoFromString()?.parentEntity
                            ?.get(KEY_PARENT_ENTITY_TOLA_NAME)
                            ?.equals(eventPayload?.cohortName, true)!!
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
                saveEventToMultipleSources(event, eventDependencies)

            }
        }
    }

    fun getAllTolasForVillage(villageId: Int): List<TolaEntity> {
        return this.tolaDao.getAllTolasForVillage(villageId)
    }

    fun fetchTolaNeedToPost(
        needsToPost: Boolean,
        transactionId: String?,
        serverId: Int
    ): List<TolaEntity> {
        return this.tolaDao.fetchTolaNeedToPost(needsToPost, transactionId, serverId)
    }


    fun updateTolaDetailAfterSync(
        id: Int,
        serverId: Int,
        needsToPost: Boolean,
        transactionId: String,
        createdDate: Long,
        modifiedDate: Long
    ) {
        this.tolaDao.updateTolaDetailAfterSync(
            id,
            serverId,
            needsToPost,
            transactionId,
            createdDate,
            modifiedDate
        )
    }

    fun fetchAllTolaNeedToUpdate(
        needsToPost: Boolean,
        transactionId: String?,
        serverId: Int
    ): List<TolaEntity> {
        return this.tolaDao.fetchAllTolaNeedToUpdate(needsToPost, transactionId, serverId)
    }


    fun updateTolaTransactionId(id: Int, transactionId: String) {
        this.tolaDao.updateTolaTransactionId(id, transactionId)
    }

    fun fetchPendingTola(needsToPost: Boolean, transactionId: String?): List<TolaEntity> {
        return this.tolaDao.fetchPendingTola(needsToPost, transactionId)
    }

    fun fetchAllTolaNeedToDelete(status: Int): List<TolaEntity> {
        return this.tolaDao.fetchAllTolaNeedToDelete(status)
    }

    fun fetchAllPendingTolaNeedToDelete(status: Int, transactionId: String?): List<TolaEntity> {
        return this.tolaDao.fetchAllPendingTolaNeedToDelete(status, transactionId)
    }

    fun fetchAllPendingTolaNeedToUpdate(
        needsToPost: Boolean,
        transactionId: String?
    ): List<TolaEntity> {
        return this.tolaDao.fetchAllPendingTolaNeedToUpdate(needsToPost, transactionId)
    }

    fun deleteTola(id: Int) {
        this.tolaDao.deleteTola(id)
    }

    fun removeTola(id: Int) {
        this.tolaDao.removeTola(id)
    }


    fun setNeedToPost(ids: List<Int>, needsToPost: Boolean) {
        this.tolaDao.setNeedToPost(ids, needsToPost)
    }

    fun updateNeedToPost(id: Int, needsToPost: Boolean) {
        this.tolaDao.updateNeedToPost(id, needsToPost)
    }


    fun getTola(id: Int): TolaEntity {
        return this.tolaDao.getTola(id)
    }

    fun insertAll(tolas: List<TolaEntity>) {
        this.tolaDao.insertAll(tolas)
    }

    fun deleteTolaOffline(id: Int, status: Int) {
        this.tolaDao.deleteTolaOffline(id, status)
    }

    fun fetchSingleTola(id: Int): TolaEntity? {
        return this.tolaDao.fetchSingleTola(id)
    }

    fun getStepForVillage(villageId: Int, stepId: Int): StepListEntity {
        return this.stepsListDao.getStepForVillage(villageId, stepId)
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

    fun getAllStepsForVillage(villageId: Int): List<StepListEntity> {
        return this.stepsListDao.getAllStepsForVillage(villageId)
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

    fun getDidisForTola(tolaId: Int): List<DidiEntity> {
        return this.didiDao.getDidisForTola(tolaId)
    }

    fun deleteDidisForTola(tolaId: Int, activeStatus: Int, needsToPostDeleteStatus: Boolean) {
        this.didiDao.deleteDidisForTola(tolaId, activeStatus, needsToPostDeleteStatus)
    }

    fun updateTolaName(id: Int, newName: String) {
        this.didiDao.updateTolaName(id, newName)
        this.tolaDao.updateTolaName(id, newName)
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

    suspend fun addCohort(cohortList: JsonArray): ApiResponseModel<List<TolaApiResponse>> {
        NudgeLogger.d("TransectWalkRepository","addCohort Request=> ${Gson().toJson(cohortList)}")
        return this.apiInterface.addCohort(cohortList)
    }

    suspend fun getPendingStatus(transactionIdRequest: TransactionIdRequest): ApiResponseModel<List<TransactionIdResponse>> {
        return this.apiInterface.getPendingStatus(transactionIdRequest)
    }

    suspend fun deleteCohort(deleteCohort: JsonArray): ApiResponseModel<List<TolaApiResponse?>> {
        NudgeLogger.d("TransectWalkRepository","deleteCohort Request=>${Gson().toJson(deleteCohort)}")
        return this.apiInterface.deleteCohort(deleteCohort)
    }

    suspend fun editCohort(updatedCohort: JsonArray): ApiResponseModel<List<TolaApiResponse>> {
        NudgeLogger.d("TransectWalkRepository","editCohort Request=> ${Gson().toJson(updatedCohort)}")
        return this.apiInterface.editCohort(updatedCohort)
    }

    suspend fun deleteDidi(didiId: JsonArray): ApiResponseModel<List<DidiEntity>> {
        NudgeLogger.d("TransectWalkRepository","deleteDidi Request=> ${didiId.json()}")
        return this.apiInterface.deleteDidi(didiId)
    }

    suspend fun editWorkFlow(addWorkFlowRequest: List<EditWorkFlowRequest>): ApiResponseModel<List<WorkFlowResponse>> {
        NudgeLogger.d("TransectWalkRepository","addWorkFlowRequest Request=> ${Gson().toJson(addWorkFlowRequest)}")
        return this.apiInterface.editWorkFlow(addWorkFlowRequest)
    }


}