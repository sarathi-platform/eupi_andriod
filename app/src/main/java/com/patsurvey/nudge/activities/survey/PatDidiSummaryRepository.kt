package com.patsurvey.nudge.activities.survey

import com.nudge.core.BLANK_STRING
import com.nudge.core.EventSyncStatus
import com.nudge.core.SELECTION_MISSION
import com.nudge.core.database.dao.CasteListDao
import com.nudge.core.database.entities.CasteEntity
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventName
import com.nudge.core.enums.getDependsOnEventNameForEvent
import com.nudge.core.getEventDependencyEntityListFromEvents
import com.nudge.core.getSizeInLong
import com.nudge.core.json
import com.nudge.core.model.MetadataDto
import com.nudge.core.toDate
import com.nudge.core.value
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.data.prefs.SharedPrefs.Companion.PREF_KEY_TEMP_CRP_FILE_PATH
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.model.dataModel.AbleBodiedFlagEventModel
import com.patsurvey.nudge.model.dataModel.ShgFlagEventModel
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.getParentEntityMapForEvent
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class PatDidiSummaryRepository @Inject constructor(
    val prefRepo: PrefRepo,
    val answerDao: AnswerDao,
    val apiService: ApiService,
    val casteListDao: CasteListDao,
    val stepsListDao: StepsListDao,
    val tolaDao: TolaDao
) :BaseRepository() {

    fun getAppLanguageId(): Int? {
        return prefRepo.getAppLanguageId()
    }

    fun getAllCasteForLanguage(): List<CasteEntity>? {
        return casteListDao.getAllCasteForLanguage(prefRepo.getAppLanguageId() ?: 2)
    }

    fun saveDidiLocalImagePath(finalPathWithCoordinates: String, didiId: Int) {
        didiDao.saveLocalImagePath(path = finalPathWithCoordinates, didiId = didiId)
    }

    fun updateDidiSHGFlag(didiId: Int, shgFlag: Int) {
        didiDao.updateDidiShgStatus(didiId = didiId, shgFlag = shgFlag)
    }
    fun updateDidiAbleBodiedFlag(didiId:Int, ableBodiedFlag:Int){
        didiDao.updateDidiAbleBodiedStatus(didiId = didiId, ableBodiedFlag = ableBodiedFlag)
    }
    suspend fun uploadDidiImage(image: MultipartBody.Part,didiId: RequestBody,location:RequestBody,userType:RequestBody): ApiResponseModel<Object> {
        return apiService.uploadDidiImage(didiId =  didiId, image = image, location = location, userType = userType)
    }

    suspend fun getTolaFromServerId(id: Int): TolaEntity? {
        return tolaDao.fetchSingleTolaFromServerId(id)
    }
    fun updateNeedToPostImage(didiId: Int,needsToPostImage:Boolean){
        didiDao.updateNeedsToPostImage(didiId, needsToPostImage)
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
                EventName.CRP_IMAGE -> {
                    filteredList = eventList.filter {
                        it.payloadLocalId == dependentEvent.payloadLocalId
                    }
                }

                EventName.BPC_IMAGE -> {
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

    fun createShgFlagEvent(
        didiEntity: DidiEntity,
        tolaDeviceIdMap: Map<Int, String>,
        mobileNumber: String,
        userID: String
    ): Events {

        val payload = ShgFlagEventModel.getShgFlagEventModel(
            didiEntity = didiEntity,
            tolaDeviceId = tolaDeviceIdMap[didiEntity.cohortId].value()
        ).json()

        val event = Events(
            name = EventName.SHG_FLAG_EVENT.name,
            type = EventName.SHG_FLAG_EVENT.topicName,
            createdBy = userID,
            mobile_number = mobileNumber,
            request_payload = payload,
            status = EventSyncStatus.OPEN.name,
            modified_date = System.currentTimeMillis().toDate(),
            result = null,
            consumer_status = BLANK_STRING,
            payloadLocalId = "",
            metadata = MetadataDto(
                mission = SELECTION_MISSION,
                depends_on = listOf(),
                request_payload_size = payload.getSizeInLong(),
                parentEntity = getParentEntityMapForEvent(
                    didiEntity,
                    EventName.SHG_FLAG_EVENT
                )
            ).json()
        )
        return event
    }

    fun createAbleBodiedFlagEvent(

        didiEntity: DidiEntity,
        tolaDeviceIdMap: Map<Int, String>,
        mobileNumber: String,
        userID: String
    ): Events {

        val payload = AbleBodiedFlagEventModel.getAbleBodiedFlagEventModel(
            didiEntity = didiEntity,
            tolaDeviceId = tolaDeviceIdMap[didiEntity.cohortId].value()
        ).json()

        val event = Events(
            name = EventName.ABLE_BODIED_FLAG_EVENT.name,
            type = EventName.ABLE_BODIED_FLAG_EVENT.topicName,
            createdBy = userID,
            mobile_number = mobileNumber,
            request_payload = payload,
            status = EventSyncStatus.OPEN.name,
            modified_date = System.currentTimeMillis().toDate(),
            result = null,
            consumer_status = BLANK_STRING,
            payloadLocalId = "",
            metadata = MetadataDto(
                mission = SELECTION_MISSION,
                depends_on = listOf(),
                request_payload_size = payload.getSizeInLong(),
                parentEntity = getParentEntityMapForEvent(
                    didiEntity,
                    EventName.ABLE_BODIED_FLAG_EVENT
                )
            ).json()
        )
        return event
    }


    fun saveTempImagePath(filePath: String) {
        prefRepo.savePref(PREF_KEY_TEMP_CRP_FILE_PATH, filePath)
    }

    fun getTempImagePath(): String {
        return prefRepo.getPref(PREF_KEY_TEMP_CRP_FILE_PATH, BLANK_STRING) ?: BLANK_STRING
    }
}