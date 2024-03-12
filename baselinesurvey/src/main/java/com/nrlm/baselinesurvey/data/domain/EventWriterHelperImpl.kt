package com.nrlm.baselinesurvey.data.domain

import com.nrlm.baselinesurvey.DEFAULT_LANGUAGE_ID
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.dao.ActivityTaskDao
import com.nrlm.baselinesurvey.database.dao.DidiSectionProgressEntityDao
import com.nrlm.baselinesurvey.database.dao.MissionActivityDao
import com.nrlm.baselinesurvey.database.dao.SurveyEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.model.datamodel.SaveAnswerEventDto
import com.nrlm.baselinesurvey.model.datamodel.SaveAnswerEventOptionItemDto
import com.nrlm.baselinesurvey.model.datamodel.SaveAnswerEventQuestionItemDto
import com.nrlm.baselinesurvey.model.datamodel.SaveAnswerEventSectionItemDto
import com.nrlm.baselinesurvey.model.datamodel.SectionStatusUpdateEventDto
import com.nrlm.baselinesurvey.model.datamodel.UpdateTaskStatusEventDto
import com.nrlm.baselinesurvey.ui.common_components.common_domain.commo_repository.EventsWriterRepositoryImpl
import com.nrlm.baselinesurvey.utils.StatusReferenceType
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nrlm.baselinesurvey.utils.states.SurveyState
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
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
        saveAnswerEventOptionItemDtoList: List<SaveAnswerEventOptionItemDto>
    ): Events {
        val languageId = prefRepo.getAppLanguageId() ?: DEFAULT_LANGUAGE_ID
        val surveyEntity = surveyEntityDao.getSurveyDetailForLanguage(surveyId, languageId)
        val activityForSubjectDto = activityDao.getActivityFromSubjectId(didiId)

        val mSaveAnswerEventDto = SaveAnswerEventDto(
            surveyId = surveyId,
            dateCreated = System.currentTimeMillis(),
            languageId = languageId,
            subjectId = didiId,
            subjectType = activityForSubjectDto.subject,
            sections = listOf(
                SaveAnswerEventSectionItemDto(
                    sectionId = sectionId,
                    questions = listOf(
                        SaveAnswerEventQuestionItemDto(
                            questionId = questionId,
                            questionType = questionType,
                            options = saveAnswerEventOptionItemDtoList
                        )
                    )
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

    override suspend fun createTaskStatusUpdateEvent(
        subjectId: Int,
        sectionStatus: SurveyState
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
            status = sectionStatus.name
        )

        val mUpdateTaskStatusEvent = repositoryImpl.createEvent(
            mUpdateTaskStatusEventDto,
            EventName.UPDATE_TASK_STATUS_EVENT,
            EventType.STATEFUL
        )
        return mUpdateTaskStatusEvent ?: Events.getEmptyEvent()
    }

    /*override suspend fun creteSubjectStatusUpdateEvent(
        surveyId: Int,
        subjectId: Int,
        status: SectionStatus
    ): Events {
        val activityForSubjectDto = activityDao.getActivityFromSubjectId(subjectId)




    }*/


}