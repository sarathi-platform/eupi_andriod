package com.patsurvey.nudge.activities.survey

import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventName
import com.nudge.core.enums.getDependsOnEventNameForEvent
import com.nudge.core.getEventDependencyEntityListFromEvents
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.CasteEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.CasteListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.network.interfaces.ApiService
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
}