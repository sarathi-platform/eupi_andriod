package com.nrlm.baselinesurvey.data.domain

import com.nrlm.baselinesurvey.DEFAULT_LANGUAGE_ID
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.dao.ActivityTaskDao
import com.nrlm.baselinesurvey.database.dao.DidiSectionProgressEntityDao
import com.nrlm.baselinesurvey.database.dao.MissionActivityDao
import com.nrlm.baselinesurvey.database.dao.MissionEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.model.datamodel.ActivityForSubjectDto
import com.nrlm.baselinesurvey.model.datamodel.SaveAnswerEventDto
import com.nrlm.baselinesurvey.model.datamodel.SaveAnswerEventForFormQuestionDto
import com.nrlm.baselinesurvey.model.datamodel.SaveAnswerEventOptionItemDto
import com.nrlm.baselinesurvey.model.datamodel.SaveAnswerEventQuestionItemDto
import com.nrlm.baselinesurvey.model.datamodel.SaveAnswerEventQuestionItemForFormQuestionDto
import com.nrlm.baselinesurvey.model.datamodel.SectionStatusUpdateEventDto
import com.nrlm.baselinesurvey.model.datamodel.UpdateActivityStatusEventDto
import com.nrlm.baselinesurvey.model.datamodel.UpdateMissionStatusEventDto
import com.nrlm.baselinesurvey.model.datamodel.UpdateTaskStatusEventDto
import com.nrlm.baselinesurvey.ui.common_components.common_domain.commo_repository.EventsWriterRepositoryImpl
import com.nrlm.baselinesurvey.utils.StatusReferenceType
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.nudge.core.toDate
import javax.inject.Inject

class EventWriterHelperImpl @Inject constructor(
    val prefRepo: PrefRepo,
    private val repositoryImpl: EventsWriterRepositoryImpl,
    private val eventsDao: EventsDao,
    private val eventDependencyDao: EventDependencyDao,
    private val surveyEntityDao: SurveyEntityDao,
    private val surveyeeEntityDao: SurveyeeEntityDao,
    private val taskDao: ActivityTaskDao,
    private val activityDao: MissionActivityDao,
    private val missionEntityDao: MissionEntityDao,
    private val didiSectionProgressEntityDao: DidiSectionProgressEntityDao
) : EventWriterHelper {


    override suspend fun createUpdateSectionStatusEvent(
        surveyId: Int,
        sectionId: Int,
        didiId: Int,
        sectionStatus: SectionStatus
    ): Events {

        return if (didiSectionProgressEntityDao.getSectionProgressForDidi(
                surveyId,
                sectionId,
                didiId
            ) == null
        ) {
            val addSectionProgressForDidiEventItem = SectionStatusUpdateEventDto(
                surveyId = surveyId,
                sectionId = sectionId,
                didiId = didiId,
                sectionStatus = sectionStatus.name
            )
            repositoryImpl.createEvent(
                addSectionProgressForDidiEventItem,
                EventName.ADD_SECTION_PROGRESS_FOR_DIDI_EVENT,
                EventType.STATEFUL
            ) ?: Events.getEmptyEvent()


        } else {
            if (sectionStatus == SectionStatus.COMPLETED) {
                val updateSectionProgressForDidiEventItem = SectionStatusUpdateEventDto(
                    surveyId = surveyId,
                    sectionId = sectionId,
                    didiId = didiId,
                    sectionStatus = sectionStatus.name
                )
                return repositoryImpl.createEvent(
                    updateSectionProgressForDidiEventItem,
                    EventName.UPDATE_SECTION_PROGRESS_FOR_DIDI_EVENT,
                    EventType.STATEFUL
                ) ?: Events.getEmptyEvent()
            } else {
                Events.getEmptyEvent()
            }
        }
    }

    override suspend fun createSaveAnswerEvent(
        surveyId: Int,
        sectionId: Int,
        didiId: Int,
        questionId: Int,
        questionType: String,
        questionTag: Int,
        showQuestion: Boolean,
        saveAnswerEventOptionItemDtoList: List<SaveAnswerEventOptionItemDto>
    ): Events {
        val languageId = prefRepo.getAppLanguageId() ?: DEFAULT_LANGUAGE_ID
        val surveyEntity = surveyEntityDao.getSurveyDetailForLanguage(surveyId, languageId)
        val activityForSubjectDto = getActivityFromSubjectId(didiId)

        val mSaveAnswerEventDto = SaveAnswerEventDto(
            surveyId = surveyId,
            dateCreated = System.currentTimeMillis(),
            languageId = languageId,
            subjectId = didiId,
            subjectType = activityForSubjectDto.subject,
            sectionId = sectionId,
            question = SaveAnswerEventQuestionItemDto(
                questionId = questionId,
                questionType = questionType,
                tag = questionTag,
                showQuestion = showQuestion,
                options = saveAnswerEventOptionItemDtoList
            ),
            referenceId = surveyEntity?.referenceId ?: 0
        )
        val mSaveAnswerEventDtoEvent = repositoryImpl.createEvent(
            mSaveAnswerEventDto,
            EventName.SAVE_RESPONSE_EVENT,
            EventType.STATEFUL
        )
        return mSaveAnswerEventDtoEvent ?: Events.getEmptyEvent()
    }

    override suspend fun createSaveAnswerEventForFormTypeQuestion(
        surveyId: Int,
        sectionId: Int,
        didiId: Int,
        questionId: Int,
        questionType: String,
        questionTag: Int,
        showQuestion: Boolean,
        saveAnswerEventOptionItemDtoList: List<SaveAnswerEventOptionItemDto>
    ): Events {
        val languageId = prefRepo.getAppLanguageId() ?: DEFAULT_LANGUAGE_ID
        val surveyEntity = surveyEntityDao.getSurveyDetailForLanguage(surveyId, languageId)
        val activityForSubjectDto = getActivityFromSubjectId(didiId)

        val saveAnswerEventOptionItemDtoListMap =
            saveAnswerEventOptionItemDtoList.groupBy { it.referenceId }
        val optionList = mutableListOf<List<SaveAnswerEventOptionItemDto>>()
        saveAnswerEventOptionItemDtoListMap.values.forEach {
            optionList.add(it)
        }

        val mSaveAnswerEventDto = SaveAnswerEventForFormQuestionDto(
            surveyId = surveyId,
            dateCreated = System.currentTimeMillis(),
            languageId = languageId,
            subjectId = didiId,
            subjectType = activityForSubjectDto.subject,
            sectionId = sectionId,
            question = SaveAnswerEventQuestionItemForFormQuestionDto(
                questionId = questionId,
                questionType = questionType,
                tag = questionTag,
                showQuestion = showQuestion,
                options = optionList
            ),
            referenceId = surveyEntity?.referenceId ?: 0
        )
        val mSaveAnswerEventDtoEvent = repositoryImpl.createEvent(
            mSaveAnswerEventDto,
            EventName.SAVE_RESPONSE_EVENT,
            EventType.STATEFUL
        )
        return mSaveAnswerEventDtoEvent ?: Events.getEmptyEvent()
    }

    override suspend fun createTaskStatusUpdateEvent(
        subjectId: Int,
        sectionStatus: SectionStatus
    ): Events {
        val languageId = prefRepo.getAppLanguageId() ?: DEFAULT_LANGUAGE_ID
        val activityForSubjectDto = activityDao.getActivityFromSubjectId(subjectId)

        val mUpdateTaskStatusEventDto = UpdateTaskStatusEventDto(
            missionId = activityForSubjectDto.missionId,
            activityId = activityForSubjectDto.activityId,
            taskId = activityForSubjectDto.taskId,
            subjectId = subjectId,
            subjectType = activityForSubjectDto.subject,
            referenceType = StatusReferenceType.TASK.name,
            status = sectionStatus.name,
            actualStartDate = activityForSubjectDto.actualStartDate,
            actualCompletedDate = activityForSubjectDto.actualCompletedDate
        )

        val mUpdateTaskStatusEvent = repositoryImpl.createEvent(
            mUpdateTaskStatusEventDto,
            EventName.UPDATE_TASK_STATUS_EVENT,
            EventType.STATEFUL
        )
        return mUpdateTaskStatusEvent ?: Events.getEmptyEvent()
    }

    override suspend fun createActivityStatusUpdateEvent(
        missionId: Int,
        activityId: Int,
        status: SectionStatus
    ): Events {
        val activity = activityDao.getActivity(activityId)

        val mUpdateActivityStatusEventDto = UpdateActivityStatusEventDto(
            missionId = activity.missionId,
            activityId = activityId,
            actualStartDate = activity.actualStartDate,
            completedDate = activity.actualCompleteDate,
            status = status.name,
            referenceType = StatusReferenceType.ACTIVITY.name
        )

        val mUpdateActivityStatusEvent = repositoryImpl.createEvent(
            mUpdateActivityStatusEventDto,
            EventName.UPDATE_ACTIVITY_STATUS_EVENT,
            eventType = EventType.STATEFUL
        )

        return mUpdateActivityStatusEvent ?: Events.getEmptyEvent()

    }

    override suspend fun createMissionStatusUpdateEvent(
        missionId: Int,
        status: SectionStatus
    ): Events {
        val mission = missionEntityDao.getMission(missionId)

        val mUpdateMissionStatusEventDto = UpdateMissionStatusEventDto(
            missionId = mission.missionId,
            actualStartDate = mission.actualStartDate,
            completedDate = mission.actualCompletedDate,
            referenceType = StatusReferenceType.MISSION.name,
            status = status
        )

        val mUpdateMissionStatusEvent = repositoryImpl.createEvent(
            mUpdateMissionStatusEventDto,
            EventName.UPDATE_MISSION_STATUS_EVENT,
            EventType.STATEFUL
        )

        return mUpdateMissionStatusEvent ?: Events.getEmptyEvent()

    }

    override suspend fun markMissionInProgress(missionId: Int, status: SectionStatus) {
        missionEntityDao.markMissionInProgress(
            missionId = missionId,
            status = status.name,
            actualStartDate = System.currentTimeMillis().toDate().toString()
        )
    }

    override suspend fun markActivityInProgress(
        missionId: Int,
        activityId: Int,
        status: SectionStatus
    ) {
        activityDao.markActivityStart(
            missionId = missionId,
            activityId = activityId,
            status = status.name,
            actualStartDate = System.currentTimeMillis().toDate().toString()
        )
    }

    override suspend fun markTaskInProgress(
        missionId: Int,
        activityId: Int,
        taskId: Int,
        status: SectionStatus
    ) {
        taskDao.markTaskInProgress(
            taskId = taskId,
            activityId,
            missionId,
            status = status.name,
            actualStartDate = System.currentTimeMillis().toDate().toString()
        )
    }

    override suspend fun markMissionActivityTaskInProgress(
        missionId: Int,
        activityId: Int,
        taskId: Int,
        status: SectionStatus
    ) {
        val missionEntity = missionEntityDao.getMission(missionId)
        val activityEntity = activityDao.getActivity(missionId, activityId)
        val taskEntity = taskDao.getTask(activityId, missionId, taskId)

        if (taskEntity.status != SectionStatus.COMPLETED.name && taskEntity.status != SectionStatus.INPROGRESS.name)
            markTaskInProgress(missionId, activityId, taskId, status)

        if (activityEntity.status != SectionStatus.COMPLETED.name && activityEntity.status != SectionStatus.INPROGRESS.name) {
            if (activityEntity.status == null) {
                markActivityInProgress(missionId, activityId, SectionStatus.INPROGRESS)
            } else {
                markActivityInProgress(missionId, activityId, status)
            }
        }
        if (missionEntity.status != SectionStatus.COMPLETED.name && missionEntity.status != SectionStatus.INPROGRESS.name) {
            if (missionEntity.status == null) {
                markMissionInProgress(missionId, SectionStatus.INPROGRESS)
            } else {
                markMissionInProgress(missionId, status)
            }
        }
    }

    override suspend fun markMissionCompleted(missionId: Int, status: SectionStatus) {
        missionEntityDao.markMissionCompleted(
            missionId = missionId,
            status = status.name,
            actualCompletedDate = System.currentTimeMillis().toDate().toString()
        )
    }

    override suspend fun markActivityCompleted(
        missionId: Int,
        activityId: Int,
        status: SectionStatus
    ) {
        activityDao.markActivityComplete(
            missionId = missionId,
            activityId = activityId,
            status = status.name,
            completedDate = System.currentTimeMillis().toDate().toString()
        )
    }

    override suspend fun markTaskCompleted(
        missionId: Int,
        activityId: Int,
        taskId: Int,
        status: SectionStatus
    ) {
        taskDao.markTaskCompleted(
            taskId = taskId,
            activityId = activityId,
            missionId = missionId,
            status = status.name,
            actualCompletedDate = System.currentTimeMillis().toDate().toString()
        )
    }

    override suspend fun markMissionActivityTaskICompleted(
        missionId: Int,
        activityId: Int,
        taskId: Int,
        status: SectionStatus
    ) {
        markTaskCompleted(missionId, activityId, taskId, status)
        markActivityCompleted(missionId, activityId, status)
        markMissionCompleted(missionId, status)
    }

    override suspend fun getActivityFromSubjectId(subjectId: Int): ActivityForSubjectDto {
        return activityDao.getActivityFromSubjectId(subjectId)
    }

    override suspend fun getMissionActivityTaskEventList(
        missionId: Int,
        activityId: Int,
        taskId: Int,
        status: SectionStatus
    ): List<Events> {
        val missionEntity = missionEntityDao.getMission(missionId)
        val activityEntity = activityDao.getActivity(missionId, activityId)
        val taskEntity = taskDao.getTask(activityId, missionId, taskId)

        val eventList = mutableListOf<Events>()

        if (taskEntity.status != SectionStatus.COMPLETED.name && taskEntity.status != SectionStatus.INPROGRESS.name) {
            val taskStatusUpdateEvent = createTaskStatusUpdateEvent(taskEntity.subjectId, status)
            eventList.add(taskStatusUpdateEvent)
        }

        if (activityEntity.status != SectionStatus.COMPLETED.name && activityEntity.status != SectionStatus.INPROGRESS.name) {
            if (activityEntity.status == null) {
                val activityStatusUpdateEvent =
                    createActivityStatusUpdateEvent(missionId, activityId, SectionStatus.INPROGRESS)
                eventList.add(activityStatusUpdateEvent)
            } else {
                val activityStatusUpdateEvent =
                    createActivityStatusUpdateEvent(missionId, activityId, status)
                eventList.add(activityStatusUpdateEvent)
            }
        }
        if (missionEntity.status != SectionStatus.COMPLETED.name && missionEntity.status != SectionStatus.INPROGRESS.name) {
            if (missionEntity.status == null) {
                val missionStatusUpdateEvent =
                    createMissionStatusUpdateEvent(missionId, SectionStatus.INPROGRESS)
                eventList.add(missionStatusUpdateEvent)
            } else {
                val missionStatusUpdateEvent = createMissionStatusUpdateEvent(missionId, status)
                eventList.add(missionStatusUpdateEvent)
            }
        }
        return eventList
    }

    /*override suspend fun creteSubjectStatusUpdateEvent(
        surveyId: Int,
        subjectId: Int,
        status: SectionStatus
    ): Events {
        val activityForSubjectDto = activityDao.getActivityFromSubjectId(subjectId)




    }*/


}