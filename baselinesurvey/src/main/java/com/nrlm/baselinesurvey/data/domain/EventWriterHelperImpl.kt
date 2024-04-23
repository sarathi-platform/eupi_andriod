package com.nrlm.baselinesurvey.data.domain

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.DEFAULT_LANGUAGE_ID
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.dao.ActivityTaskDao
import com.nrlm.baselinesurvey.database.dao.DidiSectionProgressEntityDao
import com.nrlm.baselinesurvey.database.dao.MissionActivityDao
import com.nrlm.baselinesurvey.database.dao.MissionEntityDao
import com.nrlm.baselinesurvey.database.dao.OptionItemDao
import com.nrlm.baselinesurvey.database.dao.QuestionEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.model.datamodel.ActivityForSubjectDto
import com.nrlm.baselinesurvey.model.datamodel.ImageUploadRequest
import com.nrlm.baselinesurvey.model.datamodel.SaveAnswerEventDto
import com.nrlm.baselinesurvey.model.datamodel.SaveAnswerEventForFormQuestionDto
import com.nrlm.baselinesurvey.model.datamodel.SaveAnswerEventOptionItemDto
import com.nrlm.baselinesurvey.model.datamodel.SaveAnswerEventQuestionItemDto
import com.nrlm.baselinesurvey.model.datamodel.SaveAnswerEventQuestionItemForFormQuestionDto
import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.model.datamodel.SectionStatusUpdateEventDto
import com.nrlm.baselinesurvey.model.datamodel.UpdateActivityStatusEventDto
import com.nrlm.baselinesurvey.model.datamodel.UpdateMissionStatusEventDto
import com.nrlm.baselinesurvey.model.datamodel.UpdateTaskStatusEventDto
import com.nrlm.baselinesurvey.ui.Constants.QuestionType
import com.nrlm.baselinesurvey.ui.common_components.common_domain.commo_repository.EventsWriterRepositoryImpl
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.OptionItemEntityState
import com.nrlm.baselinesurvey.utils.StatusReferenceType
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nudge.core.EventSyncStatus
import com.nudge.core.SELECTION_MISSION
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.nudge.core.getSizeInLong
import com.nudge.core.json
import com.nudge.core.model.MetadataDto
import com.nudge.core.toDate
import java.util.UUID
import javax.inject.Inject

class EventWriterHelperImpl @Inject constructor(
    val prefRepo: PrefRepo,
    private val repositoryImpl: EventsWriterRepositoryImpl,
    private val eventsDao: EventsDao,
    private val eventDependencyDao: EventDependencyDao,
    private val surveyEntityDao: SurveyEntityDao,
    private val surveyeeEntityDao: SurveyeeEntityDao,
    private val questionEntityDao: QuestionEntityDao,
    private val optionItemDao: OptionItemDao,
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
                userId = getBaseLineUserId(),
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
        questionDesc: String,
        showQuestion: Boolean,
        saveAnswerEventOptionItemDtoList: List<SaveAnswerEventOptionItemDto>
    ): Events {
        val languageId = prefRepo.getAppLanguageId() ?: DEFAULT_LANGUAGE_ID
        val surveyEntity = surveyEntityDao.getSurveyDetailForLanguage(
            userId = getBaseLineUserId(),
            surveyId,
            languageId
        )
        val activityForSubjectDto = getActivityFromSubjectId(didiId)

        val questionItem = questionEntityDao.getFormQuestionForId(
            userid = getBaseLineUserId(),
            surveyId,
            sectionId,
            questionId,
            DEFAULT_LANGUAGE_ID
        )

        val referenceOptionList = ArrayList<OptionItemEntityState>()
        optionItemDao.getSurveySectionQuestionOptions(
            userId = getBaseLineUserId(),
            sectionId = sectionId,
            surveyId = surveyId,
            questionId = questionId,
            languageId = DEFAULT_LANGUAGE_ID
        ).forEach {
            referenceOptionList.add(OptionItemEntityState(it.optionId, it, !it.conditional))
        }

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
                questionDesc = questionItem?.questionDisplay ?: BLANK_STRING,
                options = saveAnswerEventOptionItemDtoList.getOptionDescriptionInEnglish(
                    surveyId,
                    sectionId,
                    questionId,
                    questionType,
                    referenceOptionList
                )
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
        questionDesc: String,

        referenceOptionList: List<OptionItemEntityState>,
        showQuestion: Boolean,
        saveAnswerEventOptionItemDtoList: List<SaveAnswerEventOptionItemDto>
    ): Events {
        val languageId = prefRepo.getAppLanguageId() ?: DEFAULT_LANGUAGE_ID
        val surveyEntity = surveyEntityDao.getSurveyDetailForLanguage(
            userId = getBaseLineUserId(),
            surveyId,
            languageId
        )
        val activityForSubjectDto = getActivityFromSubjectId(didiId)

        val questionItem = questionEntityDao.getFormQuestionForId(
            userid = getBaseLineUserId(),
            surveyId,
            sectionId,
            questionId,
            DEFAULT_LANGUAGE_ID
        )

        val saveAnswerEventOptionItemDtoListMap =
            saveAnswerEventOptionItemDtoList.groupBy { it.referenceId }
        val optionList = mutableListOf<List<SaveAnswerEventOptionItemDto>>()
        saveAnswerEventOptionItemDtoListMap.values.forEach {
            optionList.add(
                it.getOptionDescriptionInEnglish(
                    surveyId,
                    sectionId,
                    questionId,
                    questionType,
                    referenceOptionList
                )
            )
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
                options = optionList,
                questionDesc = questionItem?.questionDisplay ?: BLANK_STRING
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
        val activityForSubjectDto =
            activityDao.getActivityFromSubjectId(getBaseLineUserId(), subjectId)

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
        val activity = activityDao.getActivity(getBaseLineUserId(), activityId)

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
        val mission = missionEntityDao.getMission(getBaseLineUserId(), missionId)

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
            userId = getBaseLineUserId(),
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
            userId = getBaseLineUserId(),
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
            userId = getBaseLineUserId(),
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
        val missionEntity = missionEntityDao.getMission(getBaseLineUserId(), missionId)
        val activityEntity = activityDao.getActivity(getBaseLineUserId(), missionId, activityId)
        val taskEntity = taskDao.getTask(getBaseLineUserId(), activityId, missionId, taskId)

//        if (taskEntity.status != SectionStatus.COMPLETED.name && taskEntity.status != SectionStatus.INPROGRESS.name)
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
            userId = getBaseLineUserId(),
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
            userId = getBaseLineUserId(),
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
            userId = getBaseLineUserId(),
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
        return activityDao.getActivityFromSubjectId(userId = getBaseLineUserId(), subjectId)
    }

    override suspend fun getMissionActivityTaskEventList(
        missionId: Int,
        activityId: Int,
        taskId: Int,
        status: SectionStatus
    ): List<Events> {
        val missionEntity = missionEntityDao.getMission(getBaseLineUserId(), missionId)
        val activityEntity = activityDao.getActivity(getBaseLineUserId(), missionId, activityId)
        val taskEntity = taskDao.getTask(getBaseLineUserId(), activityId, missionId, taskId)

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

    override fun createImageUploadEvent(
        didi: SurveyeeEntity,
        location: String,
        filePath: String,
        userType: String,
        questionId: Int,
        referenceId: String,
        sectionDetails: SectionListItem,
        subjectType: String
    ): Events? {

        val payload = ImageUploadRequest.getRequestObjectForUploadImage(
            didi = didi,
            location = location,
            filePath = filePath,
            userType = userType,
            questionId = questionId,
            referenceId = referenceId,
            sectionDetails = sectionDetails,
            subjectType = "Didi"
        ).json()

        val eventName = EventName.UPLOAD_IMAGE_RESPONSE_EVENT

        return Events(
            name = eventName.name,
            type = eventName.topicName,
            createdBy = prefRepo.getUserId(),
            mobile_number = prefRepo.getMobileNumber() ?: "",
            request_payload = payload,
            status = EventSyncStatus.OPEN.name,
            modified_date = System.currentTimeMillis().toDate(),
            result = null,
            consumer_status = BLANK_STRING,
            payloadLocalId = UUID.randomUUID().toString(),
            metadata = MetadataDto(
                mission = SELECTION_MISSION,
                depends_on = listOf(),
                request_payload_size = payload.getSizeInLong(),
                parentEntity = mapOf()
            ).json()
        ) ?: Events.getEmptyEvent()
    }

    fun getBaseLineUserId(): String {
        return prefRepo.getUniqueUserIdentifier()
    }

    suspend fun List<SaveAnswerEventOptionItemDto>.getOptionDescriptionInEnglish(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        questionType: String,
        referenceOptionList: List<OptionItemEntityState>
    ): List<SaveAnswerEventOptionItemDto> {
        val resultList = mutableListOf<SaveAnswerEventOptionItemDto>()
        if (questionType == QuestionType.InputNumber.name || questionType == QuestionType.Form.name) {
            this.forEach {
                val referenceOption =
                    referenceOptionList.find { refOption -> refOption.optionId == it.optionId }
//                val option = optionItemDao.getSurveySectionQuestionOptionForLanguage(
//                    sectionId = sectionId,
//                    surveyId = surveyId,
//                    questionId = questionId,
//                    optionId = referenceOption?.optionId ?: it.optionId,
//                    languageId = DEFAULT_LANGUAGE_ID
//                )
                resultList.add(
                    it.copy(
                        optionDesc = referenceOption?.optionItemEntity?.display ?: BLANK_STRING
                    )
                )
            }
        }
        return if (resultList.isEmpty()) this else resultList
    }

}