package com.sarathi.dataloadingmangement.repository

import com.nudge.core.database.dao.CasteListDao
import com.nudge.core.database.dao.api.ApiCallJournalDao
import com.nudge.core.database.dao.language.LanguageListDao
import com.nudge.core.database.dao.translation.TranslationConfigDao
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.database.NudgeGrantDatabase
import javax.inject.Inject

class DeleteAllDataRepositoryImpl @Inject constructor(
    private val nudgeGrantDatabase: NudgeGrantDatabase,
    private val coreSharedPrefs: CoreSharedPrefs,
    private val casteListDao: CasteListDao,
    private val languageListDao: LanguageListDao,
    private val translationConfigDao: TranslationConfigDao,
    private val apiCallJournalDao: ApiCallJournalDao

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
            missionLivelihoodConfigEntityDao().deleteLivelihoodConfigForUser(userId)
            casteListDao.deleteCasteTable()
            languageListDao.deleteAllLanguage()
            translationConfigDao.deleteTranslationConfigModelForUser(userId)
            apiCallJournalDao.deleteApiCallJournalTable(userId)
        }
    }
}