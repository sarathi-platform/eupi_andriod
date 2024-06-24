package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.database.NudgeGrantDatabase
import javax.inject.Inject

class DeleteAllDataRepositoryImpl @Inject constructor(
    private val nudgeGrantDatabase: NudgeGrantDatabase,
    private val coreSharedPrefs: CoreSharedPrefs
) : IDeleteAllDataRepository {
    override suspend fun deleteAllDataFromDb() {
        nudgeGrantDatabase.activityConfigDao()
            .deleteActivityConfigForUser(coreSharedPrefs.getUniqueUserIdentifier())
        nudgeGrantDatabase.activityDao()
            .deleteActivityForUser(coreSharedPrefs.getUniqueUserIdentifier())
        nudgeGrantDatabase.activityLanguageDao()
            .deleteActivityLanguageAttributeForUser(coreSharedPrefs.getUniqueUserIdentifier())
        nudgeGrantDatabase.attributeValueReferenceDao()
            .deleteAttributeValueReferenceForUser(coreSharedPrefs.getUniqueUserIdentifier())
        nudgeGrantDatabase.contentConfigDao()
            .deleteContentConfigForUser(coreSharedPrefs.getUniqueUserIdentifier())
        nudgeGrantDatabase.contentDao().deleteContent(coreSharedPrefs.getUniqueUserIdentifier())
        nudgeGrantDatabase.documentDao()
            .deleteDocumentForUser(coreSharedPrefs.getUniqueUserIdentifier())
        nudgeGrantDatabase.formEDao().deleteFormForUser(coreSharedPrefs.getUniqueUserIdentifier())
        nudgeGrantDatabase.formUiConfigDao()
            .deleteActivityFormUiConfigForUser(coreSharedPrefs.getUniqueUserIdentifier())
        nudgeGrantDatabase.grantConfigDao()
            .deleteGrantConfigForUser(coreSharedPrefs.getUniqueUserIdentifier())
        nudgeGrantDatabase.missionDao()
            .deleteMissionsForUser(coreSharedPrefs.getUniqueUserIdentifier())
        nudgeGrantDatabase.missionLanguageAttributeDao()
            .deleteMissionLanguageAttributeForUser(coreSharedPrefs.getUniqueUserIdentifier())
        nudgeGrantDatabase.optionItemDao()
            .deleteOptionsForUser(coreSharedPrefs.getUniqueUserIdentifier())
        nudgeGrantDatabase.programmeDao().deleteProgramme(coreSharedPrefs.getUniqueUserIdentifier())
        nudgeGrantDatabase.questionEntityDao()
            .deleteAllQuestionsForUser(coreSharedPrefs.getUniqueUserIdentifier())
        nudgeGrantDatabase.surveyEntityDao()
            .deleteAllSurveyForUser(coreSharedPrefs.getUniqueUserIdentifier())
        nudgeGrantDatabase.subjectAttributeDao()
            .deleteSubjectAttributes(coreSharedPrefs.getUniqueUserIdentifier())
        nudgeGrantDatabase.surveyAnswersDao()
            .deleteSurveyAnswerForUser(coreSharedPrefs.getUniqueUserIdentifier())
        nudgeGrantDatabase.surveyEntityDao()
            .deleteAllSurveyForUser(coreSharedPrefs.getUniqueUserIdentifier())
        nudgeGrantDatabase.surveyLanguageAttributeDao()
            .deleteSurveyLanguageAttributeForUser(coreSharedPrefs.getUniqueUserIdentifier())
        nudgeGrantDatabase.taskDao()
            .deleteActivityTaskForUser(coreSharedPrefs.getUniqueUserIdentifier())
        nudgeGrantDatabase.uiConfigDao()
            .deleteActivityUiConfigForUser(coreSharedPrefs.getUniqueUserIdentifier())
    }
}