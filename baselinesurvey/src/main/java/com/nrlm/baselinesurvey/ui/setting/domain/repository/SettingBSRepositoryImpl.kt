package com.nrlm.baselinesurvey.ui.setting.domain.repository

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.LANGUAGE_OPEN_FROM_SETTING
import com.nrlm.baselinesurvey.data.domain.EventWriterHelper
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.NudgeBaselineDatabase
import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nrlm.baselinesurvey.network.interfaces.ApiService
import com.nrlm.baselinesurvey.ui.Constants.QuestionType
import com.nrlm.baselinesurvey.ui.question_screen.domain.use_case.GetSectionUseCase
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.OptionItemEntityState
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.convertInputTypeQuestionToEventOptionItemDto
import com.nrlm.baselinesurvey.utils.convertToSaveAnswerEventOptionItemDto
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nrlm.baselinesurvey.utils.states.SectionStatus.Companion.getSectionStatusNameFromOrdinal
import com.nudge.core.database.entities.Events
import com.nudge.core.getDefaultBackUpFileName
import com.nudge.core.getDefaultImageBackUpFileName
import com.nudge.core.preference.CoreSharedPrefs

class SettingBSRepositoryImpl(private val prefRepo: PrefRepo,
                              private val apiService: ApiService,
                              private val eventWriterHelper: EventWriterHelper,
                              private val baselineDatabase: NudgeBaselineDatabase,
                              private val getSectionUseCase: GetSectionUseCase
    ):SettingBSRepository {

    override suspend fun performLogout(): ApiResponseModel<String> {
        return apiService.performLogout()
    }

    override fun clearSharedPref() {
        prefRepo.saveAccessToken(BLANK_STRING)
        val coreSharedPrefs = CoreSharedPrefs.getInstance(BaselineCore.getAppContext())
        coreSharedPrefs.setBackupFileName(
            getDefaultBackUpFileName(
                prefRepo.getMobileNumber() ?: BLANK_STRING
            )
        )
        coreSharedPrefs.setImageBackupFileName(
            getDefaultImageBackUpFileName(
                prefRepo.getMobileNumber() ?: ""
            )
        )
        coreSharedPrefs.setFileExported(false)
        prefRepo.setPreviousUserMobile(prefRepo.getMobileNumber() ?: BLANK_STRING)

    }

    override fun saveLanguageScreenOpenFrom() {
        prefRepo.savePref(LANGUAGE_OPEN_FROM_SETTING,true)
    }

    override suspend fun regenerateAllBaselineEvent() {
        val events = ArrayList<Events>()
    }

    suspend fun regenerateMATStatusEvent(events: ArrayList<Events>) {
        val userID = prefRepo.getUniqueUserId()
        baselineDatabase.missionEntityDao().getMissions(userID).forEach { missionEntity ->

            events.add(
                eventWriterHelper.createMissionStatusUpdateEvent(
                    missionId = missionEntity.missionId,
                    SectionStatus.valueOf(getSectionStatusNameFromOrdinal(missionEntity.missionStatus))
                )
            )
        }
        baselineDatabase.missionActivityEntityDao().getAllActivities(userID).forEach {
            events.add(
                eventWriterHelper.createActivityStatusUpdateEvent(
                    missionId = it.missionId,
                    activityId = it.activityId,
                    status = SectionStatus.valueOf(getSectionStatusNameFromOrdinal(it.activityStatus))

                )
            )

        }

        baselineDatabase.activityTaskEntityDao().getAllActivityTask(userID).forEach {
            events.add(
                eventWriterHelper.createTaskStatusUpdateEvent(
                    subjectId = it.subjectId,

                    sectionStatus = SectionStatus.valueOf(it.status ?: "")

                )
            )

            baselineDatabase.didiSectionProgressEntityDao()
                .getAllSectionProgress(prefRepo.getUniqueUserId()).forEach {
                eventWriterHelper.createUpdateSectionStatusEvent(
                    it.surveyId,
                    it.sectionId,
                    it.didiId,
                    SectionStatus.valueOf(getSectionStatusNameFromOrdinal(it.sectionStatus))
                )
            }

            //  eventWriterHelper.createUpdateSectionStatusEvent(surveyId = )

        }
    }

    private suspend fun generateResponseEvent(events: ArrayList<Events>) {
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
                eventWriterHelper.createSaveAnswerEvent(
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
                )


            }

        baselineDatabase.sectionAnswerEntityDao().getAllAnswer(prefRepo.getUniqueUserId()).forEach {
            val tag = baselineDatabase.questionEntityDao()
                .getQuestionTag(it.surveyId, it.sectionId, it.questionId)
            val optionList = baselineDatabase.optionItemDao()
                .getSurveySectionQuestionOptions(it.sectionId, it.surveyId, it.questionId, 2)


            eventWriterHelper.createSaveAnswerEvent(
                it.surveyId,
                it.sectionId,
                it.didiId,
                it.questionId,
                it.questionType,
                tag,
                true,
                optionList.convertToSaveAnswerEventOptionItemDto(QuestionType.valueOf(it.questionType))
            )
        }


//       val formResponseList= baselineDatabase.formQuestionResponseDao().getAllFormResponses(prefRepo.getUniqueUserId())
//        val questionIdMap= formResponseList.map {
//            it.questionId
//        }.distinct()
//
//
//
//
//
//           formResponseList.groupBy { it.questionId }.forEach{formQuestionResponseEntity->
//
//               formQuestionResponseEntity
//               val tag = baselineDatabase.questionEntityDao().getQuestionTag(formQuestionResponseEntity[formQuestionResponseEntity.key].surveyId, formQuestionResponseEntity.sectionId, formQuestionResponseEntity.questionId)
//
//               eventWriterHelper.createSaveAnswerEventForFormTypeQuestion(formQuestionResponseEntity.surveyId,formQuestionResponseEntity.sectionId,formQuestionResponseEntity.didiId,formQuestionResponseEntity.questionId,QuestionType.Form.name,
//                   tag, )
//           }


        baselineDatabase.didiInfoEntityDao().getDidiInfo()
    }


}