package com.nrlm.baselinesurvey.data.domain

import android.content.Context
import android.text.TextUtils
import androidx.core.net.toUri
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.PREF_KEY_TYPE_NAME
import com.nrlm.baselinesurvey.PREF_USER_TYPE
import com.nrlm.baselinesurvey.data.prefs.PrefBSRepo
import com.nrlm.baselinesurvey.database.NudgeBaselineDatabase
import com.nrlm.baselinesurvey.database.dao.ActivityTaskDao
import com.nrlm.baselinesurvey.database.dao.DidiSectionProgressEntityDao
import com.nrlm.baselinesurvey.database.dao.MissionActivityDao
import com.nrlm.baselinesurvey.database.dao.MissionEntityDao
import com.nrlm.baselinesurvey.database.dao.OptionItemDao
import com.nrlm.baselinesurvey.database.dao.QuestionEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.database.entity.DidiInfoEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.database.entity.SectionEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.model.Tuple4
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
import com.nrlm.baselinesurvey.ui.Constants.ResultType
import com.nrlm.baselinesurvey.ui.common_components.SHGFlag
import com.nrlm.baselinesurvey.ui.common_components.common_domain.commo_repository.EventsWriterRepositoryImpl
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.OptionItemEntityState
import com.nrlm.baselinesurvey.utils.StatusReferenceType
import com.nrlm.baselinesurvey.utils.convertFormQuestionResponseEntityToSaveAnswerEventOptionItemDto
import com.nrlm.baselinesurvey.utils.convertFormTypeQuestionListToOptionItemEntity
import com.nrlm.baselinesurvey.utils.convertInputTypeQuestionToEventOptionItemDto
import com.nrlm.baselinesurvey.utils.convertQuestionListToOptionItemEntity
import com.nrlm.baselinesurvey.utils.convertToOptionItemEntity
import com.nrlm.baselinesurvey.utils.convertToSaveAnswerEventOptionItemsDto
import com.nrlm.baselinesurvey.utils.findTagForId
import com.nrlm.baselinesurvey.utils.getFileNameFromURL
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nrlm.baselinesurvey.utils.states.SurveyState
import com.nrlm.baselinesurvey.utils.tagList
import com.nudge.core.DEFAULT_LANGUAGE_ID
import com.nudge.core.EventSyncStatus
import com.nudge.core.REGENERATE_PREFIX
import com.nudge.core.SELECTION_MISSION
import com.nudge.core.compressImage
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.nudge.core.getDefaultBackUpFileName
import com.nudge.core.getDefaultImageBackUpFileName
import com.nudge.core.getSizeInLong
import com.nudge.core.json
import com.nudge.core.model.MetadataDto
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.toDate
import com.sarathi.dataloadingmangement.data.dao.ActivityDao
import kotlinx.coroutines.delay
import java.io.File
import java.util.UUID
import javax.inject.Inject

class EventWriterHelperImpl @Inject constructor(
    val prefBSRepo: PrefBSRepo,
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
    private val matActivityDao: ActivityDao,
    private val didiSectionProgressEntityDao: DidiSectionProgressEntityDao,
    private val baselineDatabase: NudgeBaselineDatabase
) : EventWriterHelper {


    override suspend fun createUpdateSectionStatusEvent(
        surveyId: Int,
        sectionId: Int,
        didiId: Int,
        sectionStatus: SectionStatus
    ): Events {
        val taskLocalId = taskDao.getTaskLocalId(getBaseLineUserId(), didiId)

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
                sectionStatus = sectionStatus.name,
                localTaskId = taskLocalId ?: BLANK_STRING
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
                    sectionStatus = sectionStatus.name,
                    localTaskId = taskLocalId ?: BLANK_STRING

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
        val languageId = prefBSRepo.getAppLanguageId() ?: DEFAULT_LANGUAGE_ID
        val surveyEntity = surveyEntityDao.getSurveyDetailForLanguage(
            userId = getBaseLineUserId(),
            surveyId,
            languageId
        )
        val taskLocalId = taskDao.getTaskLocalId(getBaseLineUserId(), didiId)

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

        val mSaveAnswerEventDto = activityForSubjectDto?.subject?.let {
            SaveAnswerEventDto(
                surveyId = surveyId,
                dateCreated = System.currentTimeMillis(),
                languageId = languageId,
                subjectId = didiId,
                subjectType = it,
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
                referenceId = surveyEntity?.referenceId ?: 0,
                localTaskId = taskLocalId ?: BLANK_STRING
            )
        }
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
        val languageId = prefBSRepo.getAppLanguageId() ?: DEFAULT_LANGUAGE_ID
        val surveyEntity = surveyEntityDao.getSurveyDetailForLanguage(
            userId = getBaseLineUserId(),
            surveyId,
            languageId
        )
        val activityForSubjectDto = getActivityFromSubjectId(didiId)
        val taskLocalId = taskDao.getTaskLocalId(getBaseLineUserId(), didiId)

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

        val mSaveAnswerEventDto = activityForSubjectDto?.subject?.let {
            SaveAnswerEventForFormQuestionDto(
                surveyId = surveyId,
                dateCreated = System.currentTimeMillis(),
                languageId = languageId,
                subjectId = didiId,
                subjectType = it,
                sectionId = sectionId,
                question = SaveAnswerEventQuestionItemForFormQuestionDto(
                    questionId = questionId,
                    questionType = questionType,
                    tag = questionTag,
                    showQuestion = showQuestion,
                    options = optionList,
                    questionDesc = questionItem?.questionDisplay ?: BLANK_STRING
                ),
                referenceId = surveyEntity?.referenceId ?: 0,
                localTaskId = taskLocalId ?: BLANK_STRING
            )
        }
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
        val languageId = prefBSRepo.getAppLanguageId() ?: DEFAULT_LANGUAGE_ID
        val activityForSubjectDto =
            activityDao.getActivityFromSubjectId(getBaseLineUserId(), subjectId)
        val taskLocalId = taskDao.getTaskLocalId(getBaseLineUserId(), subjectId)

        val mUpdateTaskStatusEventDto = activityForSubjectDto?.let {
            UpdateTaskStatusEventDto(
                missionId = it.missionId,
                activityId = activityForSubjectDto.activityId,
                taskId = activityForSubjectDto.taskId,
                subjectId = subjectId,
                subjectType = activityForSubjectDto.subject,
                referenceType = StatusReferenceType.TASK.name,
                status = sectionStatus.name,
                actualStartDate = activityForSubjectDto.actualStartDate,
                actualCompletedDate = activityForSubjectDto.actualCompletedDate,
                localTaskId = taskLocalId ?: BLANK_STRING
            )
        }

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

        // Update Activity status in NudgeGrantDatabase for Grant and Baseline merge.
        activityDao.updateActivityStatus(
            userId = getBaseLineUserId(),
            activityId = activityId,
            missionId = missionId,
            status = status.name
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
        // Update Activity status in NudgeGrantDatabase for Grant and Baseline merge.
        activityDao.updateActivityStatus(
            userId = getBaseLineUserId(),
            activityId = activityId,
            missionId = missionId,
            status = status.name
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

    override suspend fun getActivityFromSubjectId(subjectId: Int): ActivityForSubjectDto? {
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
//TODO - Atul to optimize this to write only event when necessary.

//        if (taskEntity.status != SectionStatus.INPROGRESS.name && status != SectionStatus.INPROGRESS) {
            val taskStatusUpdateEvent = createTaskStatusUpdateEvent(taskEntity.subjectId, status)
            eventList.add(taskStatusUpdateEvent)
//        }

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
        val taskLocalId = taskDao.getTaskLocalId(getBaseLineUserId(), didiId = didi.didiId ?: 0)

        val payload = ImageUploadRequest.getRequestObjectForUploadImage(
            didi = didi,
            location = location,
            filePath = filePath,
            userType = userType,
            questionId = questionId,
            referenceId = referenceId,
            sectionDetails = sectionDetails,
            subjectType = "Didi",
            localTaskId = taskLocalId ?: BLANK_STRING
        ).json()

        val eventName = EventName.UPLOAD_IMAGE_RESPONSE_EVENT

        return Events(
            name = eventName.name,
            type = eventName.topicName,
            createdBy = prefBSRepo.getUserId(),
            mobile_number = prefBSRepo.getMobileNumber() ?: "",
            request_payload = payload,
            status = EventSyncStatus.OPEN.eventSyncStatus,
            modified_date = System.currentTimeMillis().toDate(),
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
        return prefBSRepo.getUniqueUserIdentifier()
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
    override fun createImageUploadEvent(
        didi: SurveyeeEntity,
        location: String,
        filePath: String,
        userType: String,
        questionId: Int,
        referenceId: String,
        questionEntity: QuestionEntity?,
        optionItemEntity: OptionItemEntity?,
        sectionDetails: SectionEntity,
        subjectType: String
    ): Events? {
        val taskLocalId = taskDao.getTaskLocalId(getBaseLineUserId(), didiId = didi.didiId ?: 0)

        val payload = ImageUploadRequest.getRequestObjectForUploadImage(
            didi = didi,
            location = location,
            filePath = filePath,
            userType = userType,
            questionId = questionId,
            referenceId = referenceId,
            questionEntity = questionEntity,
            optionItemEntity = optionItemEntity,
            sectionDetails = sectionDetails,
            subjectType = "Didi",
            localTaskId = taskLocalId
        ).json()

        val eventName = EventName.UPLOAD_IMAGE_RESPONSE_EVENT

        return Events(
            name = eventName.name,
            type = eventName.topicName,
            createdBy = prefBSRepo.getUserId(),
            mobile_number = prefBSRepo.getMobileNumber() ?: "",
            request_payload = payload,
            status = EventSyncStatus.OPEN.eventSyncStatus,
            modified_date = System.currentTimeMillis().toDate(),
            payloadLocalId = UUID.randomUUID().toString(),
            metadata = MetadataDto(
                mission = SELECTION_MISSION,
                depends_on = listOf(),
                request_payload_size = payload.getSizeInLong(),
                parentEntity = mapOf()
            ).json()
        ) ?: Events.getEmptyEvent()
    }

    fun getUserId(): String {
        return prefBSRepo.getMobileNumber() ?: BLANK_STRING
    }


    override suspend fun regenerateAllEvent(appContext:Context) {

        changeFileName(appContext,REGENERATE_PREFIX)
        generateResponseEvent().forEach {
            repositoryImpl.saveEventToMultipleSources(event = it, eventDependencies =  listOf(), eventType = EventType.STATEFUL)
        }
        regenerateDidiInfoResponseEvent().forEach {
            repositoryImpl.saveEventToMultipleSources(event = it, eventDependencies =  listOf(), eventType = EventType.STATEFUL)
        }
        regenerateImageUploadEvent(appContext)
        regenerateFromResponseEvent().forEach {
            repositoryImpl.saveEventToMultipleSources(event = it, eventDependencies =  listOf(), eventType = EventType.STATEFUL)
        }
        regenerateMATStatusEvent()
        changeFileName(appContext,"")

    }

    override suspend fun recheckMATStatus() {
        missionEntityDao.getMissions(getBaseLineUserId()).forEach { missionEntity ->
            baselineDatabase.missionActivityEntityDao()
                .getActivities(missionId = missionEntity.missionId, userId = getBaseLineUserId())
                .forEach { it ->
                    val totalTaskActivityCount = taskDao.getTaskCountForActivity(
                        userId = getBaseLineUserId(),
                        activityId = it.activityId,
                        missionId = missionEntity.missionId
                    )

                    if (totalTaskActivityCount > 0) {
                        val pendingCount = taskDao.getPendingTaskCount(
                            userId = getBaseLineUserId(),
                            activityId = it.activityId
                        )

                        if (pendingCount > 0) {
                            var currentActivityStatus=  baselineDatabase.missionActivityEntityDao().getActivity(userId =getBaseLineUserId() , missionId = missionEntity.missionId, activityId = it.activityId).status
                            if (currentActivityStatus !=SurveyState.INPROGRESS.name) {
                                baselineDatabase.missionActivityEntityDao().updateActivityStatus(
                                    userId = getBaseLineUserId(),
                                    missionId = missionEntity.missionId,
                                    activityId = it.activityId,
                                    status = SurveyState.INPROGRESS.name
                                )
                                saveActivityStatusEvent(
                                    missionId = it.missionId,
                                    activityId = it.activityId,
                                    activityStatus = SurveyState.INPROGRESS.ordinal
                                )
                            }
                        }
                    } else {
                        var currentActivityStatus=  baselineDatabase.missionActivityEntityDao().getActivity(userId =getBaseLineUserId() , missionId = missionEntity.missionId, activityId = it.activityId).status
                        if (currentActivityStatus !=SurveyState.COMPLETED.name) {
                            baselineDatabase.missionActivityEntityDao().updateActivityStatus(
                                userId = getBaseLineUserId(),
                                activityId = it.activityId,
                                missionId = missionEntity.missionId,
                                status = SurveyState.COMPLETED.name
                            )
                            saveActivityStatusEvent(
                                missionId = it.missionId,
                                activityId = it.activityId,
                                activityStatus = SurveyState.COMPLETED.ordinal
                            )
                        }
                    }
                }
            val totalActivityCount = baselineDatabase.missionActivityEntityDao()
                .getAllActivityCount(getBaseLineUserId(), missionId = missionEntity.missionId)

            if (totalActivityCount > 0) {
                val pendingActivityCount = baselineDatabase.missionActivityEntityDao()
                    .getPendingActivity(getBaseLineUserId(), missionId = missionEntity.missionId)


                if (pendingActivityCount > 0) {
             var currentMissionStatus=  baselineDatabase.missionEntityDao().getMission(userId =getBaseLineUserId() ,missionEntity.missionId).status
                    if (currentMissionStatus !=SurveyState.INPROGRESS.name) {
                        missionEntityDao.updateMissionStatus(
                            userId = getBaseLineUserId(),
                            missionId = missionEntity.missionId,
                            status = SurveyState.INPROGRESS.name
                        )
                        saveMissionStatusEvent(
                            missionStatus = SurveyState.INPROGRESS.ordinal,
                            missionId = missionEntity.missionId
                        )
                    }
                }
            } else {
                var currentMissionStatus=  baselineDatabase.missionEntityDao().getMission(userId =getBaseLineUserId() ,missionEntity.missionId).status
                if (currentMissionStatus !=SurveyState.COMPLETED.name) {
                    missionEntityDao.updateMissionStatus(
                        userId = getBaseLineUserId(),
                        missionId = missionEntity.missionId,
                        status = SurveyState.COMPLETED.name
                    )
                    saveMissionStatusEvent(
                        missionStatus = SurveyState.COMPLETED.ordinal,
                        missionId = missionEntity.missionId
                    )
                }
            }
        }
    }

    private fun changeFileName(appContext: Context,prefix: String) {
        val coreSharedPrefs = CoreSharedPrefs.getInstance(appContext)
        coreSharedPrefs.setBackupFileName(
            getDefaultBackUpFileName(
                prefix + prefBSRepo.getMobileNumber(),
                prefBSRepo.getPref(PREF_KEY_TYPE_NAME, BLANK_STRING) ?: BLANK_STRING
            )
        )
        coreSharedPrefs.setImageBackupFileName(
            getDefaultImageBackUpFileName(
                prefix + prefBSRepo.getMobileNumber(),
                prefBSRepo.getPref(PREF_KEY_TYPE_NAME, BLANK_STRING) ?: BLANK_STRING
            )
        )
        if (!TextUtils.isEmpty(prefix))
            coreSharedPrefs.setFileExported(false)
    }
    private fun getSaveAnswerEventOptionItemDtoForDidiInfo(
        didiInfoEntity: DidiInfoEntity,
        questionEntity: QuestionEntity
    ): List<SaveAnswerEventOptionItemDto> {

        val saveAnswerEventOptionItemDtoList = mutableListOf<SaveAnswerEventOptionItemDto>()
        val optionItemEntityList = baselineDatabase.optionItemDao().getSurveySectionQuestionOptions(
            surveyId = questionEntity.surveyId,
            sectionId = questionEntity.sectionId,
            questionId = questionEntity.questionId ?: 0,
            languageId = DEFAULT_LANGUAGE_ID,
            userId = getBaseLineUserId()
        )
        optionItemEntityList.filter { it.optionType != QuestionType.Image.name }
            .forEach {
                val saveAnswerEventOptionItemDto = SaveAnswerEventOptionItemDto(
                    optionId = it.optionId ?: 0,
                    selectedValue = if (tagList.findTagForId(it.optionTag)
                            .equals("Aadhar", true)
                    ) SHGFlag.fromInt(didiInfoEntity.isAdharCard ?: 0).name
                    else if (tagList.findTagForId(it.optionTag)
                            .equals("Voter", true)
                    ) SHGFlag.fromInt(
                        didiInfoEntity.isVoterCard ?: 0
                    ).name
                    else didiInfoEntity.phoneNumber ?: BLANK_STRING,
                    referenceId = didiInfoEntity.didiId.toString(),
                    tag = it.optionTag,
                    optionDesc = optionItemEntityList.find { option -> option.optionId == it.optionId }?.display
                        ?: BLANK_STRING,
                )
                saveAnswerEventOptionItemDtoList.add(saveAnswerEventOptionItemDto)
            }

        return saveAnswerEventOptionItemDtoList
    }

    private suspend fun regenerateImageUploadEvent(appContext: Context) {

        val didiInfoEntityList =
            baselineDatabase.didiInfoEntityDao().getAllDidi(prefBSRepo.getUniqueUserIdentifier())
        val didiInfoQuestion =
            baselineDatabase.questionEntityDao().getQuestionForType(QuestionType.DidiDetails.name)
        val sectionDetails = baselineDatabase.sectionEntityDao().getSurveySectionForLanguage(
            userId = prefBSRepo.getUniqueUserIdentifier(),
            surveyId = didiInfoQuestion.surveyId,
            sectionId = didiInfoQuestion.sectionId,
            languageId = DEFAULT_LANGUAGE_ID
        )
        val optionItemEntity = baselineDatabase.optionItemDao().getSurveySectionQuestionOptions(
            surveyId = didiInfoQuestion.surveyId,
            sectionId = didiInfoQuestion.sectionId,
            questionId = didiInfoQuestion.questionId ?: 0,
            languageId = DEFAULT_LANGUAGE_ID,
            userId = getBaseLineUserId()
        )
            .find { it.optionType == QuestionType.Image.name }
        didiInfoEntityList.forEach { didiInfoEntity ->
            val surveyeeEntity = baselineDatabase.didiDao()
                .getDidi(didiInfoEntity.didiId ?: 0, prefBSRepo.getUniqueUserIdentifier())

            val event = createImageUploadEvent(
                didi = surveyeeEntity,
                location = surveyeeEntity.crpImageLocalPath.split("|").last().toString(),
                filePath = surveyeeEntity.crpImageLocalPath.split("|").first().toString(),
                userType = prefBSRepo.getPref(PREF_USER_TYPE, "") ?: "Ultra Poor change maker (UPCM)",
                questionId = didiInfoQuestion.questionId ?: 0,
                referenceId = surveyeeEntity.didiId.toString(),
                questionEntity = didiInfoQuestion,
                optionItemEntity = optionItemEntity,
                sectionDetails = sectionDetails,
                subjectType = "Didi"
            ) ?: Events.getEmptyEvent()

            delay(500)
            val path = surveyeeEntity.crpImageLocalPath.split("|").first().toString()
            val compressedDidi = compressImage(
                path,
                appContext,
                getFileNameFromURL(path)
            )
            val photoUri = File(compressedDidi).toUri()

            repositoryImpl.saveImageEventToMultipleSources(
                event,
                photoUri
            )
        }
    }

    private suspend fun regenerateMATStatusEvent() {
        val userID = prefBSRepo.getUniqueUserIdentifier()
        baselineDatabase.missionEntityDao().getMissions(userID).forEach { missionEntity ->


            saveMissionStatusEvent(
                missionId = missionEntity.missionId,
                missionStatus = missionEntity.missionStatus
            )
        }
        baselineDatabase.missionActivityEntityDao().getAllActivities(userID).forEach {

            saveActivityStatusEvent(
                missionId = it.missionId,
                activityId = it.activityId,
                activityStatus = it.activityStatus
            )


        }

        baselineDatabase.activityTaskEntityDao().getAllActivityTask(userID).forEach {

            val event = createTaskStatusUpdateEvent(
                subjectId = it.subjectId,

                sectionStatus = SectionStatus.valueOf(it.status ?: "")

            )
            repositoryImpl.saveEventToMultipleSources(
                event,
                eventDependencies = listOf(),
                eventType = EventType.STATEFUL
            )

        }
        baselineDatabase.didiSectionProgressEntityDao()
            .getAllSectionProgress(prefBSRepo.getUniqueUserIdentifier()).forEach {
                val event = createUpdateSectionStatusEvent(
                    it.surveyId,
                    it.sectionId,
                    it.didiId,
                    SectionStatus.valueOf(SectionStatus.getSectionStatusNameFromOrdinal(it.sectionStatus))
                )
                repositoryImpl.saveEventToMultipleSources(
                    event,
                    eventDependencies = listOf(),
                    eventType = EventType.STATEFUL
                )

            }
    }

    suspend fun saveActivityStatusEvent(
        missionId: Int,
        activityId: Int,
        activityStatus: Int
    ) {
        val event = createActivityStatusUpdateEvent(
            missionId = missionId,
            activityId = activityId,
            status =
            SectionStatus.getSectionStatusFromOrdinal(
                activityStatus
            )
        )
        repositoryImpl.saveEventToMultipleSources(
            event,
            eventDependencies = listOf(),
            eventType = EventType.STATEFUL
        )
    }

    suspend fun saveMissionStatusEvent(
        missionId: Int, missionStatus: Int
    ) {
        val event = createMissionStatusUpdateEvent(
            missionId = missionId,
            SectionStatus.getSectionStatusFromOrdinal(
                missionStatus
            )
        )

        repositoryImpl.saveEventToMultipleSources(
            event,
            eventDependencies = listOf(),
            eventType = EventType.STATEFUL
        )
    }

    suspend fun getOptionsInDefaultLanguage(
        surveyId: Int,
        sectionId: Int,
        questionId: Int
    ): List<OptionItemEntityState> {
        val updatedOptionListInDefaultLanguage = ArrayList<OptionItemEntityState>()
        val optionList = baselineDatabase.optionItemDao().getSurveySectionQuestionOptions(
            userId = getBaseLineUserId(),
            surveyId = surveyId,
            sectionId = sectionId,
            questionId = questionId,
            languageId = DEFAULT_LANGUAGE_ID
        )
        optionList.forEach { optionItemEntity ->
            updatedOptionListInDefaultLanguage.add(
                OptionItemEntityState(
                    optionId = optionItemEntity.optionId,
                    optionItemEntity = optionItemEntity,
                    showQuestion = true
                )
            )
            optionItemEntity.conditions?.forEach { conditionsDto ->
                when (conditionsDto?.resultType) {
                    ResultType.Questions.name -> {
                        conditionsDto?.resultList?.forEach { questionList ->
                            if (questionList.type?.equals(QuestionType.Form.name, true) == true) {
                                val mOptionItemEntityList =
                                    questionList.convertFormTypeQuestionListToOptionItemEntity(
                                        optionItemEntity.sectionId,
                                        optionItemEntity.surveyId,
                                        optionItemEntity.languageId ?: DEFAULT_LANGUAGE_ID
                                    )
                                mOptionItemEntityList.forEach { mOptionItemEntity ->
                                    updatedOptionListInDefaultLanguage.add(
                                        OptionItemEntityState(
                                            mOptionItemEntity.optionId,
                                            mOptionItemEntity,
                                            false
                                        )
                                    )
                                }
                            }
                            val mOptionItemEntity =
                                questionList.convertQuestionListToOptionItemEntity(
                                    optionItemEntity.sectionId,
                                    optionItemEntity.surveyId
                                )
                            updatedOptionListInDefaultLanguage.add(
                                OptionItemEntityState(
                                    mOptionItemEntity.optionId,
                                    mOptionItemEntity,
                                    false
                                )
                            )

                            // TODO Handle later correctly
                            mOptionItemEntity.conditions?.forEach { conditionsDto2 ->
                                if (conditionsDto2?.resultType.equals(
                                        ResultType.Questions.name,
                                        true
                                    )
                                ) {
                                    conditionsDto2?.resultList?.forEach { subQuestionList ->
                                        val mOptionItemEntity2 =
                                            subQuestionList.convertQuestionListToOptionItemEntity(
                                                mOptionItemEntity.sectionId,
                                                mOptionItemEntity.surveyId
                                            )
                                        updatedOptionListInDefaultLanguage.add(
                                            OptionItemEntityState(
                                                mOptionItemEntity2.optionId,
                                                mOptionItemEntity2,
                                                false
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    ResultType.Options.name -> {
                        conditionsDto?.resultList?.forEach { questionList ->
                            val mOptionItemEntity =
                                questionList.convertToOptionItemEntity(
                                    sectionId,
                                    surveyId,
                                    questionId,
                                    languageId = optionItemEntity.languageId ?: DEFAULT_LANGUAGE_ID
                                )
                            updatedOptionListInDefaultLanguage.add(
                                OptionItemEntityState(
                                    mOptionItemEntity.optionId,
                                    mOptionItemEntity,
                                    false
                                )
                            )
                        }
                    }

                    ResultType.Formula.name -> {

                    }
                }
            }
        }

        return updatedOptionListInDefaultLanguage
    }


     suspend fun generateResponseEvent(): List<Events> {
        val events = mutableListOf<Events>()
         baselineDatabase.inputTypeQuestionAnswerDao()
             .getAllInputTypeAnswersForQuestion(prefBSRepo.getUniqueUserIdentifier())
             .groupBy {
                 Tuple4<Int, Int, Int, Int>(
                     it.questionId,
                     it.sectionId,
                     it.surveyId,
                     it.didiId
                 )
             }.forEach {
                 val questionId = it.key.first
                 val sectionId = it.key.second
                 val surveyId = it.key.third
                 val didiId = it.key.fourth

                 val questionEntity = baselineDatabase.questionEntityDao()
                     .getQuestionEntity(
                         getBaseLineUserId(),
                         surveyId = surveyId,
                         sectionId = sectionId,
                        questionId = questionId
                    )

                val optionList = baselineDatabase.optionItemDao()
                    .getSurveySectionQuestionOptions(
                        getBaseLineUserId(),
                        surveyId = surveyId,
                        sectionId = sectionId,
                        questionId = questionId,
                        languageId = 2
                    )
                var optionItemEntityState = ArrayList<OptionItemEntityState>()
                optionList.forEach { optionItemEntity ->
                    optionItemEntityState.add(
                        OptionItemEntityState(
                            optionItemEntity.optionId,
                            optionItemEntity,
                            !optionItemEntity.conditional
                        )
                    )
                }
                events.add(
                    createSaveAnswerEvent(
                        surveyId = surveyId,
                        sectionId = sectionId,
                        questionId = questionId,
                        didiId = didiId,
                        questionType = QuestionType.Input.name,
                        questionTag = questionEntity?.tag ?: 0,
                        questionDesc = questionEntity?.questionDisplay ?: "",
                        showQuestion = true,
                        saveAnswerEventOptionItemDtoList = it.value.convertInputTypeQuestionToEventOptionItemDto(
                            it.key.first,
                            QuestionType.valueOf(questionEntity?.type ?: ""),
                            optionItemEntityState
                        )
                    )
                )
            }

        baselineDatabase.sectionAnswerEntityDao().getAllAnswer(prefBSRepo.getUniqueUserIdentifier())
            .forEach {
                val tag = baselineDatabase.questionEntityDao()
                    .getQuestionTag(getBaseLineUserId(), it.surveyId, it.sectionId, it.questionId)
                val questionDisplay = baselineDatabase.questionEntityDao()
                    .getQuestionDisplayName(
                        getBaseLineUserId(),
                        it.surveyId,
                        it.sectionId,
                        it.questionId
                    )

                events.add(
                    createSaveAnswerEvent(
                        it.surveyId,
                        it.sectionId,
                        it.didiId,
                        it.questionId,
                        it.questionType,
                        tag,
                        questionDisplay,
                        true,
                        it.optionItems.convertToSaveAnswerEventOptionItemsDto(QuestionType.valueOf(it.questionType))
                    )
                )
            }

        return events
    }



    private suspend fun regenerateFromResponseEvent(forExcel: Boolean = false): List<Events> {
        val events = mutableListOf<Events>()

        val formResponseList = baselineDatabase.formQuestionResponseDao()
            .getAllFormResponses(prefBSRepo.getUniqueUserIdentifier())
        val formResponseAndQuestionMap = formResponseList.groupBy {
            Tuple4<Int, Int, Int, Int>(
                it.questionId,
                it.sectionId,
                it.surveyId,
                it.didiId
            )
        }
        val uniqueId = getBaseLineUserId()
        formResponseAndQuestionMap.forEach { mapItem ->
            val questionId = mapItem.key.first
            val sectionId = mapItem.key.second
            val surveyId = mapItem.key.third
            val didiId = mapItem.key.fourth

            val question = baselineDatabase.questionEntityDao().getFormQuestionForId(
                surveyId = surveyId,
                sectionId = sectionId,
                questionId = questionId,
                languageId = DEFAULT_LANGUAGE_ID,
                userid = uniqueId
            )
            val optionItemEntityStateList = ArrayList<OptionItemEntityState>()
            baselineDatabase.optionItemDao().getSurveySectionQuestionOptions(
                surveyId = surveyId,
                sectionId = sectionId,
                questionId = questionId ?: 0,
                languageId = DEFAULT_LANGUAGE_ID,
                userId = uniqueId
            ).forEach { optionItemEntity ->
                optionItemEntityStateList.add(
                    OptionItemEntityState(
                        optionItemEntity.optionId,
                        optionItemEntity,
                        !optionItemEntity.conditional
                    )
                )
                optionItemEntity.conditions?.forEach { conditionsDto ->
                    when (conditionsDto?.resultType) {
                        ResultType.Questions.name -> {
                            conditionsDto?.resultList?.forEach { questionList ->
                                if (questionList.type?.equals(QuestionType.Form.name, true) == true
                                    || questionList.type?.equals(
                                        QuestionType.FormWithNone.name,
                                        true
                                    ) == true
                                ) {
                                    val mOptionItemEntityList =
                                        questionList.convertFormTypeQuestionListToOptionItemEntity(
                                            optionItemEntity.sectionId,
                                            optionItemEntity.surveyId,
                                            optionItemEntity.languageId ?: DEFAULT_LANGUAGE_ID
                                        )
                                    mOptionItemEntityList.forEach { mOptionItemEntity ->
                                        optionItemEntityStateList.add(
                                            OptionItemEntityState(
                                                mOptionItemEntity.optionId,
                                                mOptionItemEntity,
                                                false
                                            )
                                        )
                                    }
                                }
                                val mOptionItemEntity =
                                    questionList.convertQuestionListToOptionItemEntity(
                                        optionItemEntity.sectionId,
                                        optionItemEntity.surveyId
                                    )
                                optionItemEntityStateList.add(
                                    OptionItemEntityState(
                                        mOptionItemEntity.optionId,
                                        mOptionItemEntity,
                                        false
                                    )
                                )

                                // TODO Handle later correctly
                                mOptionItemEntity.conditions?.forEach { conditionsDto2 ->
                                    if (conditionsDto2?.resultType.equals(
                                            ResultType.Questions.name,
                                            true
                                        )
                                    ) {
                                        conditionsDto2?.resultList?.forEach { subQuestionList ->
                                            val mOptionItemEntity2 =
                                                subQuestionList.convertQuestionListToOptionItemEntity(
                                                    mOptionItemEntity.sectionId,
                                                    mOptionItemEntity.surveyId
                                                )
                                            optionItemEntityStateList.add(
                                                OptionItemEntityState(
                                                    mOptionItemEntity2.optionId,
                                                    mOptionItemEntity2,
                                                    false
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            mapItem.value.let {
                val didiResponse = it.first()
                events.add(
                    createSaveAnswerEventForFormTypeQuestion(
                        surveyId = didiResponse.surveyId,
                        sectionId = didiResponse.sectionId,
                        questionId = didiResponse.questionId,
                        didiId = didiId,
                        questionTag = question?.tag ?: 0,
                        questionType = QuestionType.Form.name,
                        showQuestion = true,
                        questionDesc = question?.questionDisplay ?: "",
                        referenceOptionList = getOptionsInDefaultLanguage(
                            didiResponse.surveyId,
                            didiResponse.sectionId,
                            didiResponse.questionId ?: 0
                        ),
                        saveAnswerEventOptionItemDtoList = it.convertFormQuestionResponseEntityToSaveAnswerEventOptionItemDto(
                            QuestionType.Form,
                            optionItemEntityStateList,
                            forExcel = forExcel
                        )
                    )
                )
            }
        }
        return events
    }

    private suspend fun regenerateDidiInfoResponseEvent(): List<Events> {
        val events = mutableListOf<Events>()

        val didiInfoEntityList =
            baselineDatabase.didiInfoEntityDao().getAllDidi(prefBSRepo.getUniqueUserIdentifier())
        val didiInfoQuestion =
            baselineDatabase.questionEntityDao().getQuestionForType(QuestionType.DidiDetails.name)

        didiInfoEntityList.forEach { didiInfoEntity ->
            events.add(
                createSaveAnswerEventForFormTypeQuestion(
                    surveyId = didiInfoQuestion.surveyId,
                    sectionId = didiInfoQuestion.sectionId,
                    didiId = didiInfoEntity.didiId ?: 0,
                    questionId = didiInfoQuestion.questionId ?: 0,
                    questionType = didiInfoQuestion.type ?: QuestionType.DidiDetails.name,
                    questionTag = didiInfoQuestion.tag,
                    questionDesc = didiInfoQuestion.questionDisplay ?: "",
                    referenceOptionList = getOptionsInDefaultLanguage(
                        didiInfoQuestion.surveyId,
                        didiInfoQuestion.sectionId,
                        didiInfoQuestion.questionId ?: 0
                    ),
                    saveAnswerEventOptionItemDtoList = getSaveAnswerEventOptionItemDtoForDidiInfo(
                        didiInfoEntity,
                        didiInfoQuestion
                    )
                )
            )
        }

        return events
    }

     suspend fun generateFormTypeEventsForCSV(): List<Events> {
        val events = mutableListOf<Events>()
         events.addAll(regenerateDidiInfoResponseEvent())
         events.addAll(regenerateFromResponseEvent(forExcel = true))

        return events
    }

}