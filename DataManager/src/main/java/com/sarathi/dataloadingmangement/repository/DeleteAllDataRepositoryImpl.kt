package com.sarathi.dataloadingmangement.repository

import com.nudge.core.database.dao.CasteListDao
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.database.NudgeGrantDatabase
import javax.inject.Inject

class DeleteAllDataRepositoryImpl @Inject constructor(
    private val nudgeGrantDatabase: NudgeGrantDatabase,
    private val coreSharedPrefs: CoreSharedPrefs,
    private val casteListDao: CasteListDao
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

            //Livelihood Table delete..
            moneyJournalDao().deleteMoneyJournal(userId)
            subjectLivelihoodMappingDao().deleteLivelihoodSubjectsForUsers(userId)
            assetDao().deleteAssetsForUser(userId)
            assetJournalDao().deleteAssetJournal(userId)
            livelihoodDao().deleteLivelihoodForUser(userId)
            livelihoodEventDao().deleteLivelihoodEventForUser(userId)
            livelihoodLanguageDao().deleteLivelihoodLanguageForUser(userId)
            productDao().deleteProductForUser(userId)
            subjectLivelihoodEventMappingDao().deleteSubjectLivelihoodEventMappingForUser(userId)
            subjectLivelihoodMappingDao().deleteSubjectLivelihoodMappingForUser(userId)
            sectionEntityDao().deleteSurveySectionsForUser(userId)
            sectionStatusEntityDao().deleteSectionStatusForUser(userId)
            sourceTargetQuestionMappingEntityDao().deleteSourceTargetQuestionMappingForUser(userId)
            surveyConfigEntityDao().deleteSurveyConfigForUser(userId)
            conditionsEntityDao().clearAllConditionsForUser(userId)
            missionConfigEntityDao().deleteMissionConfigForUser(userId)
            livelihoodConfigEntityDao().deleteLivelihoodConfigForUser(userId)
            casteListDao.deleteCasteTable()
        }
    }
}