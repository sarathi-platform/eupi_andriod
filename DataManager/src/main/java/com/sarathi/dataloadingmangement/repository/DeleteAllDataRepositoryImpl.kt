package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.database.NudgeGrantDatabase
import javax.inject.Inject

class DeleteAllDataRepositoryImpl @Inject constructor(
    private val nudgeGrantDatabase: NudgeGrantDatabase,
    private val coreSharedPrefs: CoreSharedPrefs
) : IDeleteAllDataRepository {
    override suspend fun deleteAllDataFromDb() {
        val userId = coreSharedPrefs.getUniqueUserIdentifier()
        nudgeGrantDatabase.apply {
            activityConfigDao()
                .deleteActivityConfigForUser(userId)

            activityDao()
                .deleteActivityForUser(userId)

            activityLanguageDao()
                .deleteActivityLanguageAttributeForUser(userId)

            attributeValueReferenceDao()
                .deleteAttributeValueReferenceForUser(userId)

            contentConfigDao()
                .deleteContentConfigForUser(userId)

            contentDao()
                .deleteContent(userId)

            documentDao()
                .deleteDocumentForUser(userId)

            formEDao()
                .deleteFormForUser(userId)

            formUiConfigDao()
                .deleteActivityFormUiConfigForUser(userId)

            grantConfigDao()
                .deleteGrantConfigForUser(userId)

            missionDao()
                .deleteMissionsForUser(userId)

            missionLanguageAttributeDao()
                .deleteMissionLanguageAttributeForUser(userId)

            optionItemDao()
                .deleteOptionsForUser(userId)

            programmeDao()
                .deleteProgramme(userId)

            questionEntityDao()
                .deleteAllQuestionsForUser(userId)

            surveyEntityDao()
                .deleteAllSurveyForUser(userId)

            subjectAttributeDao()
                .deleteSubjectAttributes(userId)

            surveyAnswersDao()
                .deleteSurveyAnswerForUser(userId)

            surveyEntityDao()
                .deleteAllSurveyForUser(userId)

            surveyLanguageAttributeDao()
                .deleteSurveyLanguageAttributeForUser(userId)

            taskDao()
                .deleteActivityTaskForUser(userId)

            uiConfigDao()
                .deleteActivityUiConfigForUser(userId)

            subjectEntityDao()
                .deleteSubjectsForUsers(userId)

            smallGroupDidiMappingDao()
                .deleteSmallGroupDidiMappingForUser(userId)
            tagReferenceEntityDao().deleteTagReferenceEntityForUser(userId)

        }
    }
}