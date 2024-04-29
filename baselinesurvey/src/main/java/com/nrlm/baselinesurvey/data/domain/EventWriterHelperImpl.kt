package com.nrlm.baselinesurvey.data.domain

import android.util.Log
import androidx.core.net.toUri
import android.text.TextUtils
import android.util.Log
import androidx.core.net.toUri
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.DEFAULT_LANGUAGE_ID
import com.nrlm.baselinesurvey.PREF_USER_TYPE
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
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
import com.nrlm.baselinesurvey.utils.BaselineCore
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
import com.nrlm.baselinesurvey.utils.tagList
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
import kotlinx.coroutines.delay
import java.io.File
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
        val languageId = prefRepo.getAppLanguageId() ?: DEFAULT_LANGUAGE_ID
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
            referenceId = surveyEntity?.referenceId ?: 0,
            localTaskId = taskLocalId ?: BLANK_STRING
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
            referenceId = surveyEntity?.referenceId ?: 0,
            localTaskId = taskLocalId ?: BLANK_STRING
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
        val taskLocalId = taskDao.getTaskLocalId(getBaseLineUserId(), subjectId)

        val mUpdateTaskStatusEventDto = UpdateTaskStatusEventDto(
            missionId = activityForSubjectDto.missionId,
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

    fun getUserId(): String {
        return prefRepo.getMobileNumber() ?: BLANK_STRING
    }


    override suspend fun regenerateAllEvent() {

        changeFileName(REGENERATE_PREFIX)
        generateResponseEvent().forEach {
            repositoryImpl.saveEventToMultipleSources(event = it, eventDependencies =  listOf(), eventType = EventType.STATEFUL)
        }
        regenerateDidiInfoResponseEvent().forEach {
            repositoryImpl.saveEventToMultipleSources(event = it, eventDependencies =  listOf(), eventType = EventType.STATEFUL)
        }
        regenerateImageUploadEvent()
        regenerateFromResponseEvent().forEach {
            repositoryImpl.saveEventToMultipleSources(event = it, eventDependencies =  listOf(), eventType = EventType.STATEFUL)
        }
        regenerateMATStatusEvent()
        changeFileName("")

    }

    private fun changeFileName(prefix: String) {
        val coreSharedPrefs = CoreSharedPrefs.getInstance(BaselineCore.getAppContext())
        coreSharedPrefs.setBackupFileName(getDefaultBackUpFileName(prefix + prefRepo.getMobileNumber()))
        coreSharedPrefs.setImageBackupFileName(getDefaultImageBackUpFileName(prefix + prefRepo.getMobileNumber()))
        if (!TextUtils.isEmpty(prefix))
            coreSharedPrefs.setFileExported(false)
    }

    private suspend fun generateResponseEvent() {
        baselineDatabase.inputTypeQuestionAnswerDao()
            .getAllInputTypeAnswersForQuestion(prefRepo.getUniqueUserIdentifier()).forEach {
                val questionEntity = baselineDatabase.questionEntityDao()
                    .getQuestionEntity(
                        getBaseLineUserId(),
                        it.surveyId,
                        it.sectionId,
                        it.questionId
                    )

                val optionList = baselineDatabase.optionItemDao()
                    .getSurveySectionQuestionOptions(
                        getBaseLineUserId(),
                        it.sectionId,
                        it.surveyId,
                        it.questionId,
                        2
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
                if (questionEntity?.questionDisplay == "How much does didi get every month through PDS?") {
                    Log.e("dd", "dd")
                }
                repositoryImpl.saveEventToMultipleSources(
                    createSaveAnswerEvent(
                        it.surveyId,
                        it.sectionId,
                        it.didiId,
                        it.questionId,
                        QuestionType.Input.name,
                        questionEntity?.tag ?: 0,
                        questionEntity?.questionDisplay ?: "",
                        true,
                        listOf(it).convertInputTypeQuestionToEventOptionItemDto(
                            it.questionId,
                            QuestionType.valueOf(questionEntity?.type ?: ""),
                            optionItemEntityState
                        )
                    ),
                    listOf(), eventType = EventType.STATEFUL,
                )


            }

        baselineDatabase.sectionAnswerEntityDao().getAllAnswer(prefRepo.getUniqueUserIdentifier())
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


            repositoryImpl.saveEventToMultipleSources(
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
                ), eventType = EventType.STATEFUL, eventDependencies = listOf()
            )
        }
    }

    private suspend fun regenerateFromResponseEvent() {
        val formResponseList = baselineDatabase.formQuestionResponseDao()
            .getAllFormResponses(prefRepo.getUniqueUserIdentifier())
        val formResponseAndQuestionMap = formResponseList.groupBy { it.questionId }
        val uniqueId = getBaseLineUserId()
        formResponseAndQuestionMap.forEach { mapItem ->
            val tempItem = mapItem.value.first()
            val question = baselineDatabase.questionEntityDao().getFormQuestionForId(
                surveyId = tempItem.surveyId,
                sectionId = tempItem.sectionId,
                questionId = mapItem.key,
                languageId = DEFAULT_LANGUAGE_ID,
                userid = uniqueId
            )
            val optionItemEntityStateList = ArrayList<OptionItemEntityState>()
            baselineDatabase.optionItemDao().getSurveySectionQuestionOptions(
                surveyId = tempItem.surveyId,
                sectionId = tempItem.sectionId,
                questionId = mapItem.key,
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
            }


            mapItem.value.groupBy { it.referenceId }.forEach {
                val didiResponse = it.value.first()
                repositoryImpl.saveEventToMultipleSources(
                    createSaveAnswerEventForFormTypeQuestion(
                        surveyId = didiResponse.surveyId,
                        sectionId = didiResponse.sectionId,
                        questionId = didiResponse.questionId,
                        didiId = didiResponse.didiId,
                        questionTag = question?.tag ?: 0,
                        questionType = QuestionType.Form.name,
                        showQuestion = true,
                        questionDesc = question?.questionDisplay ?: "",
                        referenceOptionList = getOptionsInDefaultLanguage(
                            didiResponse.surveyId,
                            didiResponse.sectionId,
                            didiResponse.questionId ?: 0
                        ),
                        saveAnswerEventOptionItemDtoList = it.value.convertFormQuestionResponseEntityToSaveAnswerEventOptionItemDto(
                            QuestionType.Form,
                            optionItemEntityStateList
                        )
                    ), eventType = EventType.STATEFUL, eventDependencies = listOf()
                )

            }
        }
    }

    private suspend fun regenerateDidiInfoResponseEvent() {

        val didiInfoEntityList =
            baselineDatabase.didiInfoEntityDao().getAllDidi(prefRepo.getUniqueUserIdentifier())
        val didiInfoQuestion =
            baselineDatabase.questionEntityDao().getQuestionForType(QuestionType.DidiDetails.name)

        didiInfoEntityList.forEach { didiInfoEntity ->
            repositoryImpl.saveEventToMultipleSources(
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
                ), eventType = EventType.STATEFUL, eventDependencies = listOf()
            )
        }

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
                        ?: BLANK_STRING
                )
                saveAnswerEventOptionItemDtoList.add(saveAnswerEventOptionItemDto)
            }

        return saveAnswerEventOptionItemDtoList
    }

    private suspend fun regenerateImageUploadEvent() {

        val didiInfoEntityList =
            baselineDatabase.didiInfoEntityDao().getAllDidi(prefRepo.getUniqueUserIdentifier())
        val didiInfoQuestion =
            baselineDatabase.questionEntityDao().getQuestionForType(QuestionType.DidiDetails.name)
        val sectionDetails = baselineDatabase.sectionEntityDao().getSurveySectionForLanguage(
            userId = prefRepo.getUniqueUserIdentifier(),
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
            val surveyeeEntity = baselineDatabase.didiDao().getDidi(didiInfoEntity.didiId ?: 0)

            val event = createImageUploadEvent(
                didi = surveyeeEntity,
                location = surveyeeEntity.crpImageLocalPath.split("|").last().toString(),
                filePath = surveyeeEntity.crpImageLocalPath.split("|").first().toString(),
                userType = prefRepo.getPref(PREF_USER_TYPE, "") ?: "Ultra Poor change maker (UPCM)",
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
                BaselineCore.getAppContext(),
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
        val userID = prefRepo.getUniqueUserIdentifier()
        baselineDatabase.missionEntityDao().getMissions(userID).forEach { missionEntity ->


            val event = createMissionStatusUpdateEvent(
                missionId = missionEntity.missionId,
                SectionStatus.valueOf(
                    SectionStatus.getSectionStatusNameFromOrdinal(
                        missionEntity.missionStatus
                    )
                )
            )

            repositoryImpl.saveEventToMultipleSources(
                event,
                eventDependencies = listOf(),
                eventType = EventType.STATEFUL
            )
        }
        baselineDatabase.missionActivityEntityDao().getAllActivities(userID).forEach {

            val event = createActivityStatusUpdateEvent(
                missionId = it.missionId,
                activityId = it.activityId,
                status = SectionStatus.valueOf(SectionStatus.getSectionStatusNameFromOrdinal(it.activityStatus))

            )
            repositoryImpl.saveEventToMultipleSources(
                event,
                eventDependencies = listOf(),
                eventType = EventType.STATEFUL
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
            .getAllSectionProgress(prefRepo.getUniqueUserIdentifier()).forEach {
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

    fun getUserId(): String {
        return prefRepo.getMobileNumber() ?: BLANK_STRING
    }


    override suspend fun regenerateAllEvent() {

        changeFileName(REGENERATE_PREFIX)
        generateResponseEvent().forEach {
            repositoryImpl.saveEventToMultipleSources(event = it, eventDependencies =  listOf(), eventType = EventType.STATEFUL)
        }
        regenerateDidiInfoResponseEvent().forEach {
            repositoryImpl.saveEventToMultipleSources(event = it, eventDependencies =  listOf(), eventType = EventType.STATEFUL)
        }
        regenerateImageUploadEvent()
        regenerateFromResponseEvent().forEach {
            repositoryImpl.saveEventToMultipleSources(event = it, eventDependencies =  listOf(), eventType = EventType.STATEFUL)
        }
        regenerateMATStatusEvent()
        changeFileName("")

    }

    private fun changeFileName(prefix: String) {
        val coreSharedPrefs = CoreSharedPrefs.getInstance(BaselineCore.getAppContext())
        coreSharedPrefs.setBackupFileName(getDefaultBackUpFileName(prefix + prefRepo.getMobileNumber()))
        coreSharedPrefs.setImageBackupFileName(getDefaultImageBackUpFileName(prefix + prefRepo.getMobileNumber()))
    }

     suspend fun generateResponseEvent(): List<Events> {
        val events = mutableListOf<Events>()

        baselineDatabase.inputTypeQuestionAnswerDao()
            .getAllInputTypeAnswersForQuestion(prefRepo.getUniqueUserIdentifier()).forEach {
                val questionEntity = baselineDatabase.questionEntityDao()
                    .getQuestionEntity(
                        getBaseLineUserId(),
                        it.surveyId,
                        it.sectionId,
                        it.questionId
                    )

                val optionList = baselineDatabase.optionItemDao()
                    .getSurveySectionQuestionOptions(
                        getBaseLineUserId(),
                        it.sectionId,
                        it.surveyId,
                        it.questionId,
                        2
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
                if (questionEntity?.questionDisplay == "How much does didi get every month through PDS?") {
                    Log.e("dd", "dd")
                }
                events.add(
                    createSaveAnswerEvent(
                        it.surveyId,
                        it.sectionId,
                        it.didiId,
                        it.questionId,
                        QuestionType.Input.name,
                        questionEntity?.tag ?: 0,
                        questionEntity?.questionDisplay ?: "",
                        true,
                        listOf(it).convertInputTypeQuestionToEventOptionItemDto(
                            it.questionId,
                            QuestionType.valueOf(questionEntity?.type ?: ""),
                            optionItemEntityState
                        )
                    )
                )
            }

        baselineDatabase.sectionAnswerEntityDao().getAllAnswer(prefRepo.getUniqueUserIdentifier())
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



    private suspend fun regenerateFromResponseEvent(): List<Events> {
        val events = mutableListOf<Events>()

        val formResponseList = baselineDatabase.formQuestionResponseDao()
            .getAllFormResponses(prefRepo.getUniqueUserIdentifier())
        val formResponseAndQuestionMap = formResponseList.groupBy { it.questionId }
        val uniqueId = getBaseLineUserId()
        formResponseAndQuestionMap.forEach { mapItem ->
            val tempItem = mapItem.value.first()
            val question = baselineDatabase.questionEntityDao().getFormQuestionForId(
                surveyId = tempItem.surveyId,
                sectionId = tempItem.sectionId,
                questionId = mapItem.key,
                languageId = DEFAULT_LANGUAGE_ID,
                userid = uniqueId
            )
            val optionItemEntityStateList = ArrayList<OptionItemEntityState>()
            baselineDatabase.optionItemDao().getSurveySectionQuestionOptions(
                surveyId = tempItem.surveyId,
                sectionId = tempItem.sectionId,
                questionId = mapItem.key,
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
            }

            mapItem.value.groupBy { it.referenceId }.forEach {
                val didiResponse = it.value.first()
                events.add(
                    createSaveAnswerEventForFormTypeQuestion(
                        surveyId = didiResponse.surveyId,
                        sectionId = didiResponse.sectionId,
                        questionId = didiResponse.questionId,
                        didiId = didiResponse.didiId,
                        questionTag = question?.tag ?: 0,
                        questionType = QuestionType.Form.name,
                        showQuestion = true,
                        questionDesc = question?.questionDisplay ?: "",
                        referenceOptionList = getOptionsInDefaultLanguage(
                            didiResponse.surveyId,
                            didiResponse.sectionId,
                            didiResponse.questionId ?: 0
                        ),
                        saveAnswerEventOptionItemDtoList = it.value.convertFormQuestionResponseEntityToSaveAnswerEventOptionItemDto(
                            QuestionType.Form,
                            optionItemEntityStateList
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
            baselineDatabase.didiInfoEntityDao().getAllDidi(prefRepo.getUniqueUserIdentifier())
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
                        ?: BLANK_STRING
                )
                saveAnswerEventOptionItemDtoList.add(saveAnswerEventOptionItemDto)
            }

        return saveAnswerEventOptionItemDtoList
    }

    private suspend fun regenerateImageUploadEvent() {

        val didiInfoEntityList =
            baselineDatabase.didiInfoEntityDao().getAllDidi(prefRepo.getUniqueUserIdentifier())
        val didiInfoQuestion =
            baselineDatabase.questionEntityDao().getQuestionForType(QuestionType.DidiDetails.name)
        val sectionDetails = baselineDatabase.sectionEntityDao().getSurveySectionForLanguage(
            userId = prefRepo.getUniqueUserIdentifier(),
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
            val surveyeeEntity = baselineDatabase.didiDao().getDidi(didiInfoEntity.didiId ?: 0)

            val event = createImageUploadEvent(
                didi = surveyeeEntity,
                location = surveyeeEntity.crpImageLocalPath.split("|").last().toString(),
                filePath = surveyeeEntity.crpImageLocalPath.split("|").first().toString(),
                userType = prefRepo.getPref(PREF_USER_TYPE, "") ?: "Ultra Poor change maker (UPCM)",
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
                BaselineCore.getAppContext(),
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
        val userID = prefRepo.getUniqueUserIdentifier()
        baselineDatabase.missionEntityDao().getMissions(userID).forEach { missionEntity ->


            val event = createMissionStatusUpdateEvent(
                missionId = missionEntity.missionId,
                SectionStatus.valueOf(
                    SectionStatus.getSectionStatusNameFromOrdinal(
                        missionEntity.missionStatus
                    )
                )
            )

            repositoryImpl.saveEventToMultipleSources(
                event,
                eventDependencies = listOf(),
                eventType = EventType.STATEFUL
            )
        }
        baselineDatabase.missionActivityEntityDao().getAllActivities(userID).forEach {

            val event = createActivityStatusUpdateEvent(
                missionId = it.missionId,
                activityId = it.activityId,
                status = SectionStatus.valueOf(SectionStatus.getSectionStatusNameFromOrdinal(it.activityStatus))

            )
            repositoryImpl.saveEventToMultipleSources(
                event,
                eventDependencies = listOf(),
                eventType = EventType.STATEFUL
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
            .getAllSectionProgress(prefRepo.getUniqueUserIdentifier()).forEach {
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

    suspend fun getOptionsInDefaultLanguage(
        surveyId: Int,
        sectionId: Int,
        questionId: Int
    ): List<OptionItemEntityState> {
        val updatedOptionListInDefaultLanguage = ArrayList<OptionItemEntityState>()
        val optionList = baselineDatabase.optionItemDao().getSurveySectionQuestionOptions(
           userId =  getBaseLineUserId(),
            surveyId = surveyId,
           sectionId =  sectionId,
           questionId =  questionId,
           languageId =  DEFAULT_LANGUAGE_ID
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

     suspend fun generateFormTypeEventsForCSV(): List<Events> {
        val events = mutableListOf<Events>()
         events.addAll(regenerateDidiInfoResponseEvent())
        events.addAll(regenerateFromResponseEvent())

        return events
    }

}