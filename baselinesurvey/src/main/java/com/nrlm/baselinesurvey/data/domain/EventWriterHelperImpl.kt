package com.nrlm.baselinesurvey.data.domain

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
import com.nrlm.baselinesurvey.ui.common_components.SHGFlag
import com.nrlm.baselinesurvey.ui.common_components.common_domain.commo_repository.EventsWriterRepositoryImpl
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.OptionItemEntityState
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.StatusReferenceType
import com.nrlm.baselinesurvey.utils.convertFormQuestionResponseEntityToSaveAnswerEventOptionItemDto
import com.nrlm.baselinesurvey.utils.convertInputTypeQuestionToEventOptionItemDto
import com.nrlm.baselinesurvey.utils.convertToSaveAnswerEventOptionItemDto
import com.nrlm.baselinesurvey.utils.findTagForId
import com.nrlm.baselinesurvey.utils.getFileNameFromURL
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nrlm.baselinesurvey.utils.tagList
import com.nudge.core.EventSyncStatus
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

        return if (didiSectionProgressEntityDao.getSectionProgressForDidi(
                userId = getUserId(),
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
        val activityForSubjectDto = activityDao.getActivityFromSubjectId(getUserId(), subjectId)

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
        val activity = activityDao.getActivity(getUserId(), activityId)

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
        val mission = missionEntityDao.getMission(getUserId(), missionId)

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
            userId = getUserId(),
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
            userId = getUserId(),
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
            userId = getUserId(),
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
        val missionEntity = missionEntityDao.getMission(getUserId(), missionId)
        val activityEntity = activityDao.getActivity(getUserId(), missionId, activityId)
        val taskEntity = taskDao.getTask(getUserId(), activityId, missionId, taskId)

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
            userId = getUserId(),
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
            userId = getUserId(),
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
            userId = getUserId(),
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
        return activityDao.getActivityFromSubjectId(userId = getUserId(), subjectId)
    }

    override suspend fun getMissionActivityTaskEventList(
        missionId: Int,
        activityId: Int,
        taskId: Int,
        status: SectionStatus
    ): List<Events> {
        val missionEntity = missionEntityDao.getMission(getUserId(), missionId)
        val activityEntity = activityDao.getActivity(getUserId(), missionId, activityId)
        val taskEntity = taskDao.getTask(getUserId(), activityId, missionId, taskId)

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

        changeFileName("regenerate_")
        generateResponseEvent()
        regenerateDidiInfoResponseEvent()
        regenerateImageUploadEvent()
        regenerateFromResponseEvent()
        regenerateMATStatusEvent()
        changeFileName("")

    }

    private fun changeFileName(prefix: String) {
        val coreSharedPrefs = CoreSharedPrefs.getInstance(BaselineCore.getAppContext())
        coreSharedPrefs.setBackupFileName(getDefaultBackUpFileName(prefix + prefRepo.getMobileNumber()))
        coreSharedPrefs.setImageBackupFileName(getDefaultImageBackUpFileName(prefix + prefRepo.getMobileNumber()))
    }

    private suspend fun generateResponseEvent() {
        baselineDatabase.inputTypeQuestionAnswerDao()
            .getAllInputTypeAnswersForQuestion(prefRepo.getUniqueUserId()).forEach {
                val tag = baselineDatabase.questionEntityDao()
                    .getQuestionTag(it.surveyId, it.sectionId, it.questionId)
                val optionList = baselineDatabase.optionItemDao()
                    .getSurveySectionQuestionOptions(it.sectionId, it.surveyId, it.questionId, 2)
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
                repositoryImpl.saveEventToMultipleSources(
                    createSaveAnswerEvent(
                        it.surveyId,
                        it.sectionId,
                        it.didiId,
                        it.questionId,
                        QuestionType.Input.name,
                        tag,
                        true,
                        listOf(it).convertInputTypeQuestionToEventOptionItemDto(
                            it.questionId,
                            QuestionType.Input,
                            optionItemEntityState
                        )
                    ),
                    listOf(), eventType = EventType.STATEFUL,
                )


            }

        baselineDatabase.sectionAnswerEntityDao().getAllAnswer(prefRepo.getUniqueUserId()).forEach {
            val tag = baselineDatabase.questionEntityDao()
                .getQuestionTag(it.surveyId, it.sectionId, it.questionId)
            val optionList = baselineDatabase.optionItemDao()
                .getSurveySectionQuestionOptions(it.sectionId, it.surveyId, it.questionId, 2)


            repositoryImpl.saveEventToMultipleSources(
                createSaveAnswerEvent(
                    it.surveyId,
                    it.sectionId,
                    it.didiId,
                    it.questionId,
                    it.questionType,
                    tag,
                    true,
                    optionList.convertToSaveAnswerEventOptionItemDto(QuestionType.valueOf(it.questionType))
                ), eventType = EventType.STATEFUL, eventDependencies = listOf()
            )
        }
    }

    private suspend fun regenerateFromResponseEvent() {
        val formResponseList = baselineDatabase.formQuestionResponseDao()
            .getAllFormResponses(prefRepo.getUniqueUserId())
        val formResponseAndQuestionMap = formResponseList.groupBy { it.questionId }

        formResponseAndQuestionMap.forEach { mapItem ->
            val tempItem = mapItem.value.first()
            val question = baselineDatabase.questionEntityDao().getFormQuestionForId(
                surveyId = tempItem.surveyId,
                sectionId = tempItem.sectionId,
                questionId = mapItem.key,
                languageId = DEFAULT_LANGUAGE_ID
            )
            val optionItemEntityStateList = ArrayList<OptionItemEntityState>()
            baselineDatabase.optionItemDao().getSurveySectionQuestionOptions(
                surveyId = tempItem.surveyId,
                sectionId = tempItem.sectionId,
                questionId = mapItem.key,
                languageId = DEFAULT_LANGUAGE_ID
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
            baselineDatabase.didiInfoEntityDao().getAllDidi(prefRepo.getUniqueUserId())
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
            languageId = DEFAULT_LANGUAGE_ID
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
                    tag = it.optionTag
                )
                saveAnswerEventOptionItemDtoList.add(saveAnswerEventOptionItemDto)
            }

        return saveAnswerEventOptionItemDtoList
    }

    private suspend fun regenerateImageUploadEvent() {

        val didiInfoEntityList =
            baselineDatabase.didiInfoEntityDao().getAllDidi(prefRepo.getUniqueUserId())
        val didiInfoQuestion =
            baselineDatabase.questionEntityDao().getQuestionForType(QuestionType.DidiDetails.name)
        val sectionDetails = baselineDatabase.sectionEntityDao().getSurveySectionForLanguage(
            userId = prefRepo.getUniqueUserId(),
            surveyId = didiInfoQuestion.surveyId,
            sectionId = didiInfoQuestion.sectionId,
            languageId = DEFAULT_LANGUAGE_ID
        )
        val optionItemEntity = baselineDatabase.optionItemDao().getSurveySectionQuestionOptions(
            surveyId = didiInfoQuestion.surveyId,
            sectionId = didiInfoQuestion.sectionId,
            questionId = didiInfoQuestion.questionId ?: 0,
            languageId = DEFAULT_LANGUAGE_ID
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
        val userID = prefRepo.getUniqueUserId()
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
            .getAllSectionProgress(prefRepo.getUniqueUserId()).forEach {
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
}