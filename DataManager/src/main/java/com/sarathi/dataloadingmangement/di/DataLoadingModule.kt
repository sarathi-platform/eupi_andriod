package com.sarathi.dataloadingmangement.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nudge.core.database.dao.ApiStatusDao
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.usecase.FetchAppConfigFromNetworkUseCase
import com.sarathi.dataloadingmangement.NUDGE_GRANT_DATABASE
import com.sarathi.dataloadingmangement.data.dao.ActivityConfigDao
import com.sarathi.dataloadingmangement.data.dao.ActivityDao
import com.sarathi.dataloadingmangement.data.dao.ActivityLanguageDao
import com.sarathi.dataloadingmangement.data.dao.AttributeValueReferenceDao
import com.sarathi.dataloadingmangement.data.dao.ConditionsEntityDao
import com.sarathi.dataloadingmangement.data.dao.ContentConfigDao
import com.sarathi.dataloadingmangement.data.dao.ContentDao
import com.sarathi.dataloadingmangement.data.dao.DocumentDao
import com.sarathi.dataloadingmangement.data.dao.FormDao
import com.sarathi.dataloadingmangement.data.dao.FormUiConfigDao
import com.sarathi.dataloadingmangement.data.dao.GrantConfigDao
import com.sarathi.dataloadingmangement.data.dao.LanguageDao
import com.sarathi.dataloadingmangement.data.dao.MissionDao
import com.sarathi.dataloadingmangement.data.dao.MissionLanguageAttributeDao
import com.sarathi.dataloadingmangement.data.dao.OptionItemDao
import com.sarathi.dataloadingmangement.data.dao.ProgrammeDao
import com.sarathi.dataloadingmangement.data.dao.QuestionEntityDao
import com.sarathi.dataloadingmangement.data.dao.SectionEntityDao
import com.sarathi.dataloadingmangement.data.dao.SectionStatusEntityDao
import com.sarathi.dataloadingmangement.data.dao.SourceTargetQuestionMappingEntityDao
import com.sarathi.dataloadingmangement.data.dao.SubjectAttributeDao
import com.sarathi.dataloadingmangement.data.dao.SubjectEntityDao
import com.sarathi.dataloadingmangement.data.dao.SurveyAnswersDao
import com.sarathi.dataloadingmangement.data.dao.SurveyConfigEntityDao
import com.sarathi.dataloadingmangement.data.dao.SurveyEntityDao
import com.sarathi.dataloadingmangement.data.dao.SurveyLanguageAttributeDao
import com.sarathi.dataloadingmangement.data.dao.TagReferenceEntityDao
import com.sarathi.dataloadingmangement.data.dao.TaskDao
import com.sarathi.dataloadingmangement.data.dao.UiConfigDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.AssetDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.AssetJournalDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.LivelihoodDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.LivelihoodEventDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.LivelihoodLanguageDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.MoneyJournalDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.ProductDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.SubjectLivelihoodEventMappingDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.SubjectLivelihoodMappingDao
import com.sarathi.dataloadingmangement.data.dao.smallGroup.SmallGroupDidiMappingDao
import com.sarathi.dataloadingmangement.data.database.NudgeGrantDatabase
import com.sarathi.dataloadingmangement.domain.DataLoadingUseCase
import com.sarathi.dataloadingmangement.domain.use_case.ContentDownloaderUseCase
import com.sarathi.dataloadingmangement.domain.use_case.ContentUseCase
import com.sarathi.dataloadingmangement.domain.use_case.DeleteAllGrantDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.DocumentEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.DocumentUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchAllDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchContentDataFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchLanguageUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchMissionDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchMoneyJournalUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchSurveyAnswerFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchSurveyDataFromDB
import com.sarathi.dataloadingmangement.domain.use_case.FetchSurveyDataFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchUserDetailUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FormEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FormUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUiConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetConditionQuestionMappingsUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetSectionListUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetSurveyConfigFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetSurveyValidationsFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.RegenerateGrantEventUsecase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveTransactionMoneyJournalUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SectionStatusEventWriterUserCase
import com.sarathi.dataloadingmangement.domain.use_case.SectionStatusUpdateUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SurveyAnswerEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SurveyValidationUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchAssetUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchLivelihoodEventUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchProductUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchSavedEventUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchSubjectIncomeExpenseSummaryUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchSubjectLivelihoodEventHistoryUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.LivelihoodEventValidationUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.RegenerateLivelihoodEventUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.SaveLivelihoodEventUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.WriteLivelihoodEventUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchDidiDetailsFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchDidiDetailsWithLivelihoodMappingUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchLivelihoodOptionNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchSubjectLivelihoodEventMappingUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.GetLivelihoodListFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.GetSubjectLivelihoodMappingFromUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.LivelihoodUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.SaveLivelihoodMappingUseCase
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.AttendanceEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.FetchDidiDetailsFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.FetchSmallGroupAttendanceHistoryFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.FetchSmallGroupFromNetworkUseCase
import com.sarathi.dataloadingmangement.download_manager.DownloaderManager
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import com.sarathi.dataloadingmangement.repository.ContentDownloaderRepositoryImpl
import com.sarathi.dataloadingmangement.repository.ContentRepositoryImpl
import com.sarathi.dataloadingmangement.repository.DeleteAllDataRepositoryImpl
import com.sarathi.dataloadingmangement.repository.DocumentEventRepositoryImpl
import com.sarathi.dataloadingmangement.repository.DocumentRepositoryImpl
import com.sarathi.dataloadingmangement.repository.EventWriterRepositoryImpl
import com.sarathi.dataloadingmangement.repository.FormEventRepositoryImpl
import com.sarathi.dataloadingmangement.repository.FormRepositoryImpl
import com.sarathi.dataloadingmangement.repository.GetConditionQuestionMappingsRepository
import com.sarathi.dataloadingmangement.repository.GetConditionQuestionMappingsRepositoryImpl
import com.sarathi.dataloadingmangement.repository.GetSurveyConfigFromDbRepository
import com.sarathi.dataloadingmangement.repository.GetSurveyConfigFromDbRepositoryImpl
import com.sarathi.dataloadingmangement.repository.GetSurveyValidationsFromDbRepository
import com.sarathi.dataloadingmangement.repository.GetSurveyValidationsFromDbRepositoryImpl
import com.sarathi.dataloadingmangement.repository.IActivitySelectSurveyRepository
import com.sarathi.dataloadingmangement.repository.IContentDownloader
import com.sarathi.dataloadingmangement.repository.IContentRepository
import com.sarathi.dataloadingmangement.repository.IDocumentEventRepository
import com.sarathi.dataloadingmangement.repository.IEventWriterRepository
import com.sarathi.dataloadingmangement.repository.IFormEventRepository
import com.sarathi.dataloadingmangement.repository.ILanguageRepository
import com.sarathi.dataloadingmangement.repository.IMATStatusEventRepository
import com.sarathi.dataloadingmangement.repository.IMissionRepository
import com.sarathi.dataloadingmangement.repository.ISurveyAnswerEventRepository
import com.sarathi.dataloadingmangement.repository.ISurveyDownloadRepository
import com.sarathi.dataloadingmangement.repository.ISurveyRepository
import com.sarathi.dataloadingmangement.repository.ISurveySaveNetworkRepository
import com.sarathi.dataloadingmangement.repository.ISurveySaveRepository
import com.sarathi.dataloadingmangement.repository.ITaskStatusRepository
import com.sarathi.dataloadingmangement.repository.IUserDetailRepository
import com.sarathi.dataloadingmangement.repository.LanguageRepositoryImpl
import com.sarathi.dataloadingmangement.repository.MATStatusEventRepositoryImpl
import com.sarathi.dataloadingmangement.repository.MissionRepositoryImpl
import com.sarathi.dataloadingmangement.repository.MoneyJournalNetworkRepository
import com.sarathi.dataloadingmangement.repository.MoneyJournalRepositoryImpl
import com.sarathi.dataloadingmangement.repository.RegenerateGrantEventRepositoryImpl
import com.sarathi.dataloadingmangement.repository.SectionListRepository
import com.sarathi.dataloadingmangement.repository.SectionListRepositoryImpl
import com.sarathi.dataloadingmangement.repository.SectionStatusEventWriterRepository
import com.sarathi.dataloadingmangement.repository.SectionStatusEventWriterRepositoryImpl
import com.sarathi.dataloadingmangement.repository.SectionStatusUpdateRepository
import com.sarathi.dataloadingmangement.repository.SectionStatusUpdateRepositoryImpl
import com.sarathi.dataloadingmangement.repository.SelectActivitySurveyRepositoryImpl
import com.sarathi.dataloadingmangement.repository.SurveyAnswerEventRepositoryImpl
import com.sarathi.dataloadingmangement.repository.SurveyDownloadRepository
import com.sarathi.dataloadingmangement.repository.SurveyRepositoryImpl
import com.sarathi.dataloadingmangement.repository.SurveySaveNetworkRepositoryImpl
import com.sarathi.dataloadingmangement.repository.SurveySaveRepositoryImpl
import com.sarathi.dataloadingmangement.repository.TaskStatusRepositoryImpl
import com.sarathi.dataloadingmangement.repository.UserDetailRepository
import com.sarathi.dataloadingmangement.repository.liveihood.AssetJournalRepositoryImpl
import com.sarathi.dataloadingmangement.repository.liveihood.AssetRepositoryImpl
import com.sarathi.dataloadingmangement.repository.liveihood.CoreLivelihoodRepositoryImpl
import com.sarathi.dataloadingmangement.repository.liveihood.FetchDidiDetailsFromDbRepository
import com.sarathi.dataloadingmangement.repository.liveihood.FetchDidiDetailsFromDbRepositoryImpl
import com.sarathi.dataloadingmangement.repository.liveihood.FetchDidiDetailsWithLivelihoodMappingRepository
import com.sarathi.dataloadingmangement.repository.liveihood.FetchDidiDetailsWithLivelihoodMappingRepositoryImpl
import com.sarathi.dataloadingmangement.repository.liveihood.FetchLivelihoodOptionRepositoryImpl
import com.sarathi.dataloadingmangement.repository.liveihood.FetchSubjectIncomeExpenseSummaryRepository
import com.sarathi.dataloadingmangement.repository.liveihood.FetchSubjectIncomeExpenseSummaryRepositoryImpl
import com.sarathi.dataloadingmangement.repository.liveihood.FetchSubjectLivelihoodEventHistoryRepository
import com.sarathi.dataloadingmangement.repository.liveihood.FetchSubjectLivelihoodEventHistoryRepositoryImpl
import com.sarathi.dataloadingmangement.repository.liveihood.GetLivelihoodListFromDbRepository
import com.sarathi.dataloadingmangement.repository.liveihood.GetLivelihoodListFromDbRepositoryImpl
import com.sarathi.dataloadingmangement.repository.liveihood.GetLivelihoodMappingForSubjectFromDbRepository
import com.sarathi.dataloadingmangement.repository.liveihood.GetLivelihoodMappingForSubjectFromDbRepositoryImpl
import com.sarathi.dataloadingmangement.repository.liveihood.IAssetRepository
import com.sarathi.dataloadingmangement.repository.liveihood.ICoreLivelihoodRepository
import com.sarathi.dataloadingmangement.repository.liveihood.ILivelihoodEventRepository
import com.sarathi.dataloadingmangement.repository.liveihood.IProductRepository
import com.sarathi.dataloadingmangement.repository.liveihood.LivelihoodEventRepositoryImpl
import com.sarathi.dataloadingmangement.repository.liveihood.ProductRepositoryImpl
import com.sarathi.dataloadingmangement.repository.liveihood.SaveLivelihoodMappingForSubjectRepository
import com.sarathi.dataloadingmangement.repository.liveihood.SaveLivelihoodMappingForSubjectRepositoryImpl
import com.sarathi.dataloadingmangement.repository.liveihood.SubjectLivelihoodEventMappingRepositoryImpl
import com.sarathi.dataloadingmangement.repository.smallGroup.AttendanceEventWriterRepository
import com.sarathi.dataloadingmangement.repository.smallGroup.AttendanceEventWriterRepositoryImpl
import com.sarathi.dataloadingmangement.repository.smallGroup.FetchDidiDetailsFromNetworkRepository
import com.sarathi.dataloadingmangement.repository.smallGroup.FetchDidiDetailsFromNetworkRepositoryImpl
import com.sarathi.dataloadingmangement.repository.smallGroup.FetchSmallGroupAttendanceHistoryFromNetworkRepository
import com.sarathi.dataloadingmangement.repository.smallGroup.FetchSmallGroupAttendanceHistoryFromNetworkRepositoryImpl
import com.sarathi.dataloadingmangement.repository.smallGroup.FetchSmallGroupDetailsFromNetworkRepository
import com.sarathi.dataloadingmangement.repository.smallGroup.FetchSmallGroupDetailsFromNetworkRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataLoadingModule {

    @Singleton
    @Provides
    fun provideDataLoadingApiService(retrofit: Retrofit): DataLoadingApiService {
        return retrofit.create(DataLoadingApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGrantDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, NudgeGrantDatabase::class.java, NUDGE_GRANT_DATABASE)
            .addMigrations(
                NudgeGrantDatabase.NUDGE_GRANT_DATABASE_MIGRATION_1_2,
                NudgeGrantDatabase.NUDGE_GRANT_DATABASE_MIGRATION_2_3,
                NudgeGrantDatabase.NUDGE_GRANT_DATABASE_MIGRATION_3_4
            )
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .addCallback(NudgeGrantDatabase.NudgeGrantDatabaseCallback())
            .build()

    @Provides
    @Singleton
    fun provideMissionDao(db: NudgeGrantDatabase) = db.missionDao()

    @Provides
    @Singleton
    fun provideActivityDao(db: NudgeGrantDatabase) = db.activityDao()

    @Provides
    @Singleton
    fun provideTaskDao(db: NudgeGrantDatabase) = db.taskDao()

    @Provides
    @Singleton
    fun provideActivityConfigDao(db: NudgeGrantDatabase) = db.activityConfigDao()

    @Provides
    @Singleton
    fun provideLivelihoodDao(db: NudgeGrantDatabase) = db.livelihoodDao()

    @Provides
    @Singleton
    fun provideAssetDao(db: NudgeGrantDatabase) = db.assetDao()

    @Provides
    @Singleton
    fun provideProductDao(db: NudgeGrantDatabase) = db.productDao()

    @Provides
    @Singleton
    fun provideLivelihoodEventDao(db: NudgeGrantDatabase) = db.livelihoodEventDao()

    @Provides
    @Singleton
    fun provideLivelihoodLanguageDao(db: NudgeGrantDatabase) = db.livelihoodLanguageDao()

    @Provides
    @Singleton
    fun provideFormEDao(db: NudgeGrantDatabase) = db.formEDao()

    @Provides
    @Singleton
    fun provideDocumentDao(db: NudgeGrantDatabase) = db.documentDao()

    @Provides
    @Singleton
    fun provideActivityLanguageAttributeDao(db: NudgeGrantDatabase) =
        db.activityLanguageAttributeDao()

    @Provides
    @Singleton
    fun provideActivityLanguageDao(db: NudgeGrantDatabase) = db.activityLanguageDao()

    @Provides
    @Singleton
    fun provideAttributeValueReferenceDao(db: NudgeGrantDatabase) = db.attributeValueReferenceDao()

    @Provides
    @Singleton
    fun provideContentConfigDao(db: NudgeGrantDatabase) = db.contentConfigDao()

    @Provides
    @Singleton
    fun provideMissionLanguageAttributeDao(db: NudgeGrantDatabase) =
        db.missionLanguageAttributeDao()

    @Provides
    @Singleton
    fun provideSubjectAttributeDao(db: NudgeGrantDatabase) = db.subjectAttributeDao()

    @Provides
    @Singleton
    fun provideTaskAttributeDao(db: NudgeGrantDatabase) = db.taskAttributeDao()

    @Provides
    @Singleton
    fun provideLanguageDao(db: NudgeGrantDatabase) = db.languageDao()

    @Provides
    @Singleton
    fun provideUiConfigDao(db: NudgeGrantDatabase) = db.uiConfigDao()


    @Provides
    @Singleton
    fun providesSubjectEntityDao(db: NudgeGrantDatabase) = db.subjectEntityDao()

    @Provides
    @Singleton
    fun providesSmallGroupDidiMappingDao(db: NudgeGrantDatabase) = db.smallGroupDidiMappingDao()


    @Provides
    @Singleton
    fun provideFormUiConfigDao(db: NudgeGrantDatabase) = db.formUiConfigDao()

    @Provides
    @Singleton
    fun provideSurveyEntityDao(db: NudgeGrantDatabase) = db.surveyEntityDao()

    @Provides
    @Singleton
    fun provideSectionEntityDao(db: NudgeGrantDatabase) = db.sectionEntityDao()

    @Provides
    @Singleton
    fun provideQuestionEntityDao(db: NudgeGrantDatabase) = db.questionEntityDao()

    @Provides
    @Singleton
    fun provideOptionItemDao(db: NudgeGrantDatabase) = db.optionItemDao()

    @Provides
    @Singleton
    fun provideProgrammeDao(db: NudgeGrantDatabase) = db.programmeDao()

    @Provides
    @Singleton
    fun provideSurveyAnswersDao(db: NudgeGrantDatabase) = db.surveyAnswersDao()


    @Provides
    @Singleton
    fun provideGrantConfigDao(db: NudgeGrantDatabase) = db.grantConfigDao()

    @Provides
    @Singleton
    fun provideTagReferenceDao(db: NudgeGrantDatabase) = db.tagReferenceEntityDao()

    @Provides
    @Singleton
    fun provideSurveyLanguageAttributeDao(db: NudgeGrantDatabase) = db.surveyLanguageAttributeDao()

    @Provides
    @Singleton
    fun provideMoneyJournalDao(db: NudgeGrantDatabase) = db.moneyJournalDao()

    @Provides
    @Singleton
    fun provideAssetJournalDao(db: NudgeGrantDatabase) = db.assetJournalDao()

    @Provides
    @Singleton
    fun subjectLivelihoodMappingDao(db: NudgeGrantDatabase) = db.subjectLivelihoodMappingDao()

    @Provides
    @Singleton
    fun subjectLivelihoodEventMappingDao(db: NudgeGrantDatabase) =
        db.subjectLivelihoodEventMappingDao()

    @Provides
    @Singleton
    fun provideSectionStatusEntityDao(db: NudgeGrantDatabase) = db.sectionStatusEntityDao()

    @Provides
    @Singleton
    fun provideSourceTargetQuestionMappingEntityDao(db: NudgeGrantDatabase) =
        db.sourceTargetQuestionMappingEntityDao()

    @Provides
    @Singleton
    fun provideConditionsEntityDao(db: NudgeGrantDatabase) = db.conditionsEntityDao()

    @Provides
    @Singleton
    fun provideSurveyConfigEntityDao(db: NudgeGrantDatabase) = db.surveyConfigEntityDao()


    @Provides
    @Singleton
    fun provideSurveyDownloadRepository(
        dataLoadingApiService: DataLoadingApiService,
        surveyDao: SurveyEntityDao,
        sectionEntityDao: SectionEntityDao,
        coreSharedPrefs: CoreSharedPrefs,
        optionItemDao: OptionItemDao,
        questionEntityDao: QuestionEntityDao,
        surveyLanguageAttributeDao: SurveyLanguageAttributeDao,
        tagReferenceEntityDao: TagReferenceEntityDao,
        sourceTargetQuestionMappingEntityDao: SourceTargetQuestionMappingEntityDao,
        conditionsEntityDao: ConditionsEntityDao
    ): ISurveyDownloadRepository {
        return SurveyDownloadRepository(
            dataLoadingApiService = dataLoadingApiService,
            surveyDao = surveyDao,
            sectionEntityDao = sectionEntityDao,
            coreSharedPrefs = coreSharedPrefs,
            optionItemDao = optionItemDao,
            questionEntityDao = questionEntityDao,
            surveyLanguageAttributeDao = surveyLanguageAttributeDao,
            tagReferenceEntityDao = tagReferenceEntityDao,
            sourceTargetQuestionMappingEntityDao = sourceTargetQuestionMappingEntityDao,
            conditionsEntityDao = conditionsEntityDao
        )
    }

    @Provides
    @Singleton
    fun provideContentDao(db: NudgeGrantDatabase) = db.contentDao()

    @Provides
    @Singleton
    fun provideFetchDataUseCaseUseCase(
        surveyRepo: SurveyDownloadRepository,
        missionRepositoryImpl: MissionRepositoryImpl,
        contentRepositoryImpl: ContentRepositoryImpl,
        activityConfigDao: ActivityConfigDao,
        fetchSurveyDataFromDB: FetchSurveyDataFromDB,
        coreSharedPrefs: CoreSharedPrefs
    ): DataLoadingUseCase {
        return DataLoadingUseCase(
            fetchMissionDataFromNetworkUseCase = FetchMissionDataUseCase(
                missionRepositoryImpl
            ),
            fetchSurveyDataFromNetworkUseCase = FetchSurveyDataFromNetworkUseCase(
                repository = surveyRepo,
                activityConfigDao = activityConfigDao,
                sharedPrefs = coreSharedPrefs
            ),
            fetchSurveyDataFromDB = fetchSurveyDataFromDB,
            fetchContentDataFromNetworkUseCase = FetchContentDataFromNetworkUseCase(
                contentRepositoryImpl,
                coreSharedPrefs = coreSharedPrefs
            )
        )

    }

    @Provides
    @Singleton
    fun provideMissionRepository(
        missionDao: MissionDao,
        activityDao: ActivityDao,
        taskDao: TaskDao,
        activityConfigDao: ActivityConfigDao,
        activityLanguageDao: ActivityLanguageDao,
        attributeValueReferenceDao: AttributeValueReferenceDao,
        contentConfigDao: ContentConfigDao,
        missionLanguageAttributeDao: MissionLanguageAttributeDao,
        subjectAttributeDao: SubjectAttributeDao,
        programmeDao: ProgrammeDao,
        uiConfigDao: UiConfigDao,
        apiService: DataLoadingApiService,
        sharedPrefs: CoreSharedPrefs,
        grantConfigDao: GrantConfigDao,
        formUiConfigDao: FormUiConfigDao,
        surveyConfigEntityDao: SurveyConfigEntityDao
    ): IMissionRepository {
        return MissionRepositoryImpl(
            apiInterface = apiService,
            missionActivityDao = activityDao,
            activityConfigDao = activityConfigDao,
            uiConfigDao = uiConfigDao,
            activityLanguageDao = activityLanguageDao,
            taskDao = taskDao,
            programmeDao = programmeDao,
            subjectAttributeDao = subjectAttributeDao,
            attributeValueReferenceDao = attributeValueReferenceDao,
            missionDao = missionDao,
            contentConfigDao = contentConfigDao,
            missionLanguageAttributeDao = missionLanguageAttributeDao,
            sharedPrefs = sharedPrefs,
            grantConfigDao = grantConfigDao,
            formUiConfigDao = formUiConfigDao,
            surveyConfigEntityDao = surveyConfigEntityDao
        )
    }

    @Provides
    @Singleton
    fun provideContentRepository(
        contentDao: ContentDao,
        apiService: DataLoadingApiService,
        coreSharedPrefs: CoreSharedPrefs,
        contentConfigDao: ContentConfigDao,
        uiConfigDao: UiConfigDao,
        surveyAnswersDao: SurveyAnswersDao,
        activityConfigDao: ActivityConfigDao,
        livelihoodDao: LivelihoodDao
    ): IContentRepository {
        return ContentRepositoryImpl(
            apiInterface = apiService,
            contentDao = contentDao,
            coreSharedPrefs = coreSharedPrefs,
            contentConfigDao = contentConfigDao,
            uiConfigDao = uiConfigDao,
            surveyAnswersDao = surveyAnswersDao,
            activityConfigDao = activityConfigDao,
            livelihoodDao = livelihoodDao
        )
    }

    @Provides
    @Singleton
    fun provideLanguageRepository(
        languageDao: LanguageDao,
        apiService: DataLoadingApiService,
    ): ILanguageRepository {
        return LanguageRepositoryImpl(
            apiInterface = apiService, languageDao = languageDao
        )
    }

    @Provides
    @Singleton
    fun provideUserDetailRepository(
        languageDao: LanguageDao,
        sharedPrefs: CoreSharedPrefs,
        apiService: DataLoadingApiService,
    ): IUserDetailRepository {
        return UserDetailRepository(
            sharedPrefs = sharedPrefs,
            apiInterface = apiService,
            languageDao = languageDao
        )
    }

    @Provides
    @Singleton
    fun provideDocumentEventRepository(
        coreSharedPrefs: CoreSharedPrefs
    ): IDocumentEventRepository {
        return DocumentEventRepositoryImpl(coreSharedPrefs)
    }

    @Provides
    @Singleton
    fun provideDocumentEventWriterUsecase(
        eventWriterRepositoryImpl: EventWriterRepositoryImpl,
        documentEventRepository: DocumentEventRepositoryImpl,
    ): DocumentEventWriterUseCase {
        return DocumentEventWriterUseCase(
            repository = documentEventRepository, eventWriterRepositoryImpl
        )
    }

    @Provides
    @Singleton
    fun provideFormEventRepository(
    ): IFormEventRepository {
        return FormEventRepositoryImpl(
        )
    }

    @Provides
    @Singleton
    fun provideFormEventWriterUsecase(
        eventWriterRepositoryImpl: EventWriterRepositoryImpl,
        formEventRepository: FormEventRepositoryImpl,
    ): FormEventWriterUseCase {
        return FormEventWriterUseCase(
            repository = formEventRepository, eventWriterRepositoryImpl
        )
    }

    @Provides
    @Singleton
    fun provideContentDownloaderRepositoryImpl(
        contentDao: ContentDao,
        coreSharedPrefs: CoreSharedPrefs,
        contentConfigDao: ContentConfigDao,
        attributeValueReferenceDao: AttributeValueReferenceDao,
        subjectEntityDao: SubjectEntityDao
    ): IContentDownloader {
        return ContentDownloaderRepositoryImpl(
            contentDao,
            coreSharedPrefs = coreSharedPrefs,
            contentConfigDao = contentConfigDao,
            attributeValueReferenceDao = attributeValueReferenceDao,
            subjectEntityDao = subjectEntityDao
        )
    }

    @Provides
    @Singleton
    fun provideContentUseCase(
        repository: ContentDownloaderRepositoryImpl,
        downloaderManager: DownloaderManager,

        ): ContentUseCase {
        return ContentUseCase(
            contentDownloaderUseCase = ContentDownloaderUseCase(repository, downloaderManager),
        )
    }

    @Provides
    @Singleton
    fun provideFormUseCase(
        repository: FormRepositoryImpl,
        downloaderManager: DownloaderManager,
        coreSharedPrefs: CoreSharedPrefs
    ): FormUseCase {
        return FormUseCase(
            repository = repository,
            downloaderManager = downloaderManager,
            coreSharedPrefs = coreSharedPrefs
        )
    }

    @Provides
    @Singleton
    fun provideDocumentUseCase(
        repository: DocumentRepositoryImpl,
        downloaderManager: DownloaderManager,
    ): DocumentUseCase {
        return DocumentUseCase(repository = repository, downloaderManager = downloaderManager)
    }

    @Provides
    @Singleton
    fun provideFetchAllDataUseCase(
        surveyRepo: SurveyDownloadRepository,
        missionRepositoryImpl: MissionRepositoryImpl,
        contentRepositoryImpl: ContentRepositoryImpl,
        repository: IContentDownloader,
        downloaderManager: DownloaderManager,
        languageRepository: LanguageRepositoryImpl,
        userDetailRepository: UserDetailRepository,
        activityConfigDao: ActivityConfigDao,
        fetchSurveyAnswerFromNetworkUseCase: FetchSurveyAnswerFromNetworkUseCase,
        coreSharedPrefs: CoreSharedPrefs,
        formUseCase: FormUseCase,
        fetchMoneyJournalUseCase: FetchMoneyJournalUseCase,
        livelihoodUseCase: LivelihoodUseCase,
        fetchLivelihoodOptionNetworkUseCase: FetchLivelihoodOptionNetworkUseCase,
        fetchAppConfigFromNetworkUseCase: FetchAppConfigFromNetworkUseCase,
        ): FetchAllDataUseCase {
        return FetchAllDataUseCase(
            fetchMissionDataUseCase = FetchMissionDataUseCase(
                missionRepositoryImpl
            ),
            fetchContentDataFromNetworkUseCase = FetchContentDataFromNetworkUseCase(
                contentRepositoryImpl,
                coreSharedPrefs = coreSharedPrefs
            ),
            fetchSurveyDataFromNetworkUseCase = FetchSurveyDataFromNetworkUseCase(
                repository = surveyRepo,
                activityConfigDao = activityConfigDao,
                sharedPrefs = coreSharedPrefs
            ),
            contentDownloaderUseCase = ContentDownloaderUseCase(repository, downloaderManager),
            fetchLanguageUseCase = FetchLanguageUseCase(languageRepository),
            fetchUserDetailUseCase = FetchUserDetailUseCase(userDetailRepository),
            fetchSurveyAnswerFromNetworkUseCase = fetchSurveyAnswerFromNetworkUseCase,
            coreSharedPrefs = coreSharedPrefs,
            formUseCase = formUseCase,
            moneyJournalUseCase = fetchMoneyJournalUseCase,
            livelihoodUseCase = livelihoodUseCase,
            fetchLivelihoodOptionNetworkUseCase =fetchLivelihoodOptionNetworkUseCase,
            fetchAppConfigFromNetworkUseCase = fetchAppConfigFromNetworkUseCase

        )
    }


    @Provides
    @Singleton
    fun provideSaveSurveyRepository(
        surveyAnswersDao: SurveyAnswersDao,
        coreSharedPrefs: CoreSharedPrefs,
        optionItemDao: OptionItemDao,
        grantConfigDao: GrantConfigDao
    ): ISurveySaveRepository {
        return SurveySaveRepositoryImpl(
            surveyAnswersDao = surveyAnswersDao,
            coreSharedPrefs = coreSharedPrefs,
            optionItemDao = optionItemDao,
            grantConfigDao = grantConfigDao
        )
    }

    @Provides
    @Singleton
    fun provideTaskStatusRepository(
        taskDao: TaskDao,
        coreSharedPrefs: CoreSharedPrefs,
        missionDao: MissionDao,
        activityDao: ActivityDao

    ): ITaskStatusRepository {
        return TaskStatusRepositoryImpl(
            taskDao = taskDao,
            coreSharedPrefs = coreSharedPrefs,
            missionDao = missionDao,
            activityDao = activityDao
        )
    }

    @Provides
    @Singleton
    fun provideMatStatusEventRepository(
        taskDao: TaskDao,
        coreSharedPrefs: CoreSharedPrefs,
        missionDao: MissionDao,
        activityDao: ActivityDao
    ): IMATStatusEventRepository {
        return MATStatusEventRepositoryImpl(
            coreSharedPrefs = coreSharedPrefs, missionDao = missionDao,
            activityDao = activityDao,
            taskDao = taskDao

        )
    }

    @Provides
    @Singleton
    fun provideMatStatusEventUseCase(
        repositoryImpl: MATStatusEventRepositoryImpl,
        eventWriterRepositoryImpl: EventWriterRepositoryImpl
    ): MATStatusEventWriterUseCase {
        return MATStatusEventWriterUseCase(
            repository = repositoryImpl,
            eventWriterRepositoryImpl = eventWriterRepositoryImpl
        )
    }

    @Provides
    @Singleton
    fun provideEventWriterRepository(
        @ApplicationContext context: Context,
        eventsDao: EventsDao,
        eventDependencyDao: EventDependencyDao,
        coreSharedPrefs: CoreSharedPrefs

    ): IEventWriterRepository {
        return EventWriterRepositoryImpl(
            eventsDao = eventsDao,
            eventDependencyDao = eventDependencyDao,
            coreSharedPrefs = coreSharedPrefs,
            context = context
        )
    }

    @Provides
    @Singleton
    fun provideSaveSurveyAnswerEventRepository(
        coreSharedPrefs: CoreSharedPrefs,
        tagReferenceEntityDao: TagReferenceEntityDao

    ): ISurveyAnswerEventRepository {
        return SurveyAnswerEventRepositoryImpl(
            coreSharedPrefs = coreSharedPrefs,
            tagReferenceEntityDao = tagReferenceEntityDao
        )
    }

    @Provides
    @Singleton
    fun provideSaveSurveyAnswerEventUseCase(
        eventWriterRepositoryImpl: EventWriterRepositoryImpl,
        surveyAnswerRepo: SurveyAnswerEventRepositoryImpl
    ): SurveyAnswerEventWriterUseCase {
        return SurveyAnswerEventWriterUseCase(
            repository = surveyAnswerRepo,
            eventWriterRepositoryImpl = eventWriterRepositoryImpl
        )
    }

    @Provides
    @Singleton
    fun provideFetchSurveyAnswerFromNetworkUseCase(
        repository: ISurveySaveNetworkRepository,
        coreSharedPrefs: CoreSharedPrefs
    ): FetchSurveyAnswerFromNetworkUseCase {
        return FetchSurveyAnswerFromNetworkUseCase(repository, coreSharedPrefs)
    }

    @Provides
    @Singleton
    fun provideSurveySaveNetworkRepository(
        activityConfigDao: ActivityConfigDao,
        surveyAnswersDao: SurveyAnswersDao,
        questionEntityDao: QuestionEntityDao,
        optionItemDao: OptionItemDao,
        dataLoadingApiService: DataLoadingApiService,
        coreSharedPrefs: CoreSharedPrefs,
        grantConfigDao: GrantConfigDao,
        taskDao: TaskDao,
    ): ISurveySaveNetworkRepository {
        return SurveySaveNetworkRepositoryImpl(
            coreSharedPrefs = coreSharedPrefs,
            activityConfigDao = activityConfigDao,
            questionEntityDao = questionEntityDao,
            optionItemDao = optionItemDao,
            dataLoadingApiService = dataLoadingApiService,
            surveyAnswersDao = surveyAnswersDao,
            grantConfigDao = grantConfigDao,
            taskDao = taskDao
        )
    }

    @Provides
    @Singleton
    fun provideSaveSurveyUseCase(
        repositoryImpl: SurveySaveRepositoryImpl
    ): SaveSurveyAnswerUseCase {
        return SaveSurveyAnswerUseCase(repositoryImpl)
    }

    @Provides
    @Singleton
    fun provideSurveyRepository(
        questionEntityDao: QuestionEntityDao,
        surveyAnswersDao: SurveyAnswersDao,
        optionItemDao: OptionItemDao,
        coreSharedPrefs: CoreSharedPrefs,
        surveyDao: SurveyEntityDao,
        grantConfigDao: GrantConfigDao
    ): ISurveyRepository {
        return SurveyRepositoryImpl(
            questionDao = questionEntityDao,
            surveyAnswersDao = surveyAnswersDao,
            optionItemDao = optionItemDao,
            coreSharedPrefs = coreSharedPrefs,
            surveyEntityDao = surveyDao,
            grantConfigDao = grantConfigDao

        )
    }

    @Provides
    @Singleton
    fun provideSelectActivitySurveyRepository(
        questionEntityDao: QuestionEntityDao,
        surveyAnswersDao: SurveyAnswersDao,
        optionItemDao: OptionItemDao,
        coreSharedPrefs: CoreSharedPrefs,
        surveyDao: SurveyEntityDao,
        grantConfigDao: GrantConfigDao
    ): IActivitySelectSurveyRepository {
        return SelectActivitySurveyRepositoryImpl(
            questionDao = questionEntityDao,
            surveyAnswersDao = surveyAnswersDao,
            optionItemDao = optionItemDao,
            coreSharedPrefs = coreSharedPrefs,
            surveyEntityDao = surveyDao,
            grantConfigDao = grantConfigDao

        )
    }

    @Singleton
    @Provides
    fun provideRegenerateGrantEventRepository(
        coreSharedPrefs: CoreSharedPrefs,
        activityDao: ActivityDao, missionDao: MissionDao,
        taskDao: TaskDao,
        activityConfigDao: ActivityConfigDao,
        surveyAnswersDao: SurveyAnswersDao,
        formDao: FormDao,
        documentDao: DocumentDao
    ): RegenerateGrantEventRepositoryImpl {
        return RegenerateGrantEventRepositoryImpl(
            coreSharedPrefs = coreSharedPrefs,
            activityDao = activityDao,
            missionDao = missionDao,
            taskDao = taskDao,
            activityConfigDao = activityConfigDao,
            surveyAnswersDao = surveyAnswersDao,
            formDao = formDao,
            documentDao = documentDao
        )
    }

    @Singleton
    @Provides
    fun provideRegenerateGrantEventUseCase(
        regenerateGrantEventRepositoryImpl: RegenerateGrantEventRepositoryImpl,
        matStatusEventWriterUseCase: MATStatusEventWriterUseCase,
        fetchDataUseCase: FetchSurveyDataFromDB,
        surveyAnswerEventWriterUseCase: SurveyAnswerEventWriterUseCase,
        formEventWriterUseCase: FormEventWriterUseCase,
        documentEventWriterUseCase: DocumentEventWriterUseCase,
        attendanceEventWriterUseCase: AttendanceEventWriterUseCase,
        regenerateLivelihoodEventUseCase: RegenerateLivelihoodEventUseCase,
        coreSharedPrefs: CoreSharedPrefs,
        getActivityUiConfigUseCase: GetActivityUiConfigUseCase,

        ): RegenerateGrantEventUsecase {
        return RegenerateGrantEventUsecase(
            regenerateGrantEventRepositoryImpl = regenerateGrantEventRepositoryImpl,
            matStatusEventWriterUseCase = matStatusEventWriterUseCase,
            fetchDataUseCase = fetchDataUseCase,
            surveyAnswerEventWriterUseCase = surveyAnswerEventWriterUseCase,
            formEventWriterUseCase = formEventWriterUseCase,
            documentEventWriterUseCase = documentEventWriterUseCase,
            attendanceEventWriterUseCase = attendanceEventWriterUseCase,
            coreSharedPrefs = coreSharedPrefs,
            regenerateLivelihoodEventUseCase = regenerateLivelihoodEventUseCase,
            getActivityUiConfigUseCase = getActivityUiConfigUseCase,
        )
    }

    @Provides
    @Singleton
    fun fetchDidiDetailsFromNetworkRepository(
        coreSharedPrefs: CoreSharedPrefs,
        dataLoadingApiService: DataLoadingApiService,
        subjectEntityDao: SubjectEntityDao,
        apiStatusDao: ApiStatusDao
    ): FetchDidiDetailsFromNetworkRepository {
        return FetchDidiDetailsFromNetworkRepositoryImpl(
            coreSharedPrefs, dataLoadingApiService,
            subjectEntityDao,
            apiStatusDao = apiStatusDao
        )
    }

    @Provides
    @Singleton
    fun provideFetchDidiDetailsFromNetworkUseCase(
        fetchDidiDetailsFromNetworkRepository: FetchDidiDetailsFromNetworkRepository
    ): FetchDidiDetailsFromNetworkUseCase {
        return FetchDidiDetailsFromNetworkUseCase(fetchDidiDetailsFromNetworkRepository)
    }

    @Provides
    @Singleton
    fun provideFetchSmallGroupDetailsFromNetworkRepository(
        coreSharedPrefs: CoreSharedPrefs,
        dataLoadingApiService: DataLoadingApiService,
        smallGroupDidiMappingDao: SmallGroupDidiMappingDao,
        apiStatusDao: ApiStatusDao
    ): FetchSmallGroupDetailsFromNetworkRepository {
        return FetchSmallGroupDetailsFromNetworkRepositoryImpl(
            coreSharedPrefs,
            dataLoadingApiService,
            smallGroupDidiMappingDao,
            apiStatusDao = apiStatusDao
        )
    }

    @Provides
    @Singleton
    fun provideFetchSmallGroupFromNetworkUseCase(
        fetchSmallGroupDetailsFromNetworkRepository: FetchSmallGroupDetailsFromNetworkRepository
    ): FetchSmallGroupFromNetworkUseCase {
        return FetchSmallGroupFromNetworkUseCase(fetchSmallGroupDetailsFromNetworkRepository)
    }

    @Provides
    @Singleton
    fun provideFetchSmallGroupAttendanceHistoryFromNetworkUseCase(
        fetchSmallGroupAttendanceHistoryFromNetworkRepository: FetchSmallGroupAttendanceHistoryFromNetworkRepository
    ): FetchSmallGroupAttendanceHistoryFromNetworkUseCase {
        return FetchSmallGroupAttendanceHistoryFromNetworkUseCase(
            fetchSmallGroupAttendanceHistoryFromNetworkRepository
        )
    }

    @Provides
    @Singleton
    fun provideFetchSmallGroupAttendanceHistoryFromNetworkRepository(
        coreSharedPrefs: CoreSharedPrefs,
        dataLoadingApiService: DataLoadingApiService,
        subjectEntityDao: SubjectEntityDao,
        subjectAttributeDao: SubjectAttributeDao,
        attributeValueReferenceDao: AttributeValueReferenceDao,
        apiStatusDao: ApiStatusDao
    ): FetchSmallGroupAttendanceHistoryFromNetworkRepository {
        return FetchSmallGroupAttendanceHistoryFromNetworkRepositoryImpl(
            coreSharedPrefs = coreSharedPrefs,
            dataLoadingApiService = dataLoadingApiService,
            subjectEntityDao = subjectEntityDao,
            subjectAttributeDao = subjectAttributeDao,
            attributeValueReferenceDao = attributeValueReferenceDao,
            apiStatusDao = apiStatusDao
        )
    }

    @Provides
    @Singleton
    fun provideDataDeleteRepository(
        nudgeGrantDatabase: NudgeGrantDatabase,
        coreSharedPrefs: CoreSharedPrefs
    ): DeleteAllDataRepositoryImpl {
        return DeleteAllDataRepositoryImpl(nudgeGrantDatabase, coreSharedPrefs)
    }

    @Provides
    @Singleton
    fun provideDataDeleteUsecase(
        deleteAllDataRepositoryImpl: DeleteAllDataRepositoryImpl
    ): DeleteAllGrantDataUseCase {
        return DeleteAllGrantDataUseCase(deleteAllDataRepositoryImpl)
    }

    @Provides
    @Singleton
    fun provideAttendanceEventWriterUseCase(
        attendanceEventWriterRepository: AttendanceEventWriterRepository,
        eventWriterRepositoryImpl: EventWriterRepositoryImpl
    ): AttendanceEventWriterUseCase {
        return AttendanceEventWriterUseCase(
            attendanceEventWriterRepository,
            eventWriterRepositoryImpl
        )
    }

    @Provides
    @Singleton
    fun provideAttendanceEventWriterRepository(
        coreSharedPrefs: CoreSharedPrefs,
        smallGroupDidiMappingDao: SmallGroupDidiMappingDao,
        subjectEntityDao: SubjectEntityDao,
        subjectAttributeDao: SubjectAttributeDao
    ): AttendanceEventWriterRepository {
        return AttendanceEventWriterRepositoryImpl(
            coreSharedPrefs = coreSharedPrefs,
            smallGroupDidiMappingDao = smallGroupDidiMappingDao,
            subjectEntityDao = subjectEntityDao,
            subjectAttributeDao = subjectAttributeDao
        )
    }

    @Provides
    @Singleton
    fun provideMoneyJournalRepository(
        coreSharedPrefs: CoreSharedPrefs,
        moneyJournalDao: MoneyJournalDao
    ) = MoneyJournalRepositoryImpl(
        coreSharedPrefs = coreSharedPrefs,
        moneyJournalDao = moneyJournalDao
    )

    @Provides
    @Singleton
    fun provideMoneyJournalUsecase(moneyJournalRepository: MoneyJournalRepositoryImpl) =
        SaveTransactionMoneyJournalUseCase(moneyJournalRepository)

    @Provides
    @Singleton
    fun provideMoneyJournalNetworkRepository(
        coreSharedPrefs: CoreSharedPrefs,
        moneyJournalDao: MoneyJournalDao,
        apiService: DataLoadingApiService
    ) = MoneyJournalNetworkRepository(
        sharedPrefs = coreSharedPrefs,
        apiInterface = apiService,
        moneyJournalDao = moneyJournalDao
    )

    @Provides
    @Singleton
    fun provideFetchMoneyJournalUseCase(moneyJournalNetworkRepository: MoneyJournalNetworkRepository) =
        FetchMoneyJournalUseCase(moneyJournalNetworkRepository)

    @Provides
    @Singleton
    fun provideFetchDidiDetailsFromDbUseCase(
        fetchDidiDetailsFromDbRepository: FetchDidiDetailsFromDbRepository
    ): FetchDidiDetailsFromDbUseCase {
        return FetchDidiDetailsFromDbUseCase(fetchDidiDetailsFromDbRepository)
    }

    @Provides
    @Singleton
    fun provideFetchSubjectLivelihoodEventMappingUseCase(
        subjectLivelihoodEventMappingRepositoryImpl: SubjectLivelihoodEventMappingRepositoryImpl
    ): FetchSubjectLivelihoodEventMappingUseCase {
        return FetchSubjectLivelihoodEventMappingUseCase(subjectLivelihoodEventMappingRepositoryImpl)
    }


    @Provides
    @Singleton
    fun provideFetchDidiDetailsFromDbRepository(
        corePrefRepo: CoreSharedPrefs,
        subjectEntityDao: SubjectEntityDao,
        smallGroupDidiMappingDao: SmallGroupDidiMappingDao
    ): FetchDidiDetailsFromDbRepository {
        return FetchDidiDetailsFromDbRepositoryImpl(
            corePrefRepo, subjectEntityDao,
            smallGroupDidiMappingDao
        )
    }


    @Provides
    @Singleton
    fun provideGetLivelihoodListFromDbRepository(
        corePrefRepo: CoreSharedPrefs,
        livelihoodDao: LivelihoodDao
    ): GetLivelihoodListFromDbRepository {
        return GetLivelihoodListFromDbRepositoryImpl(
            livelihoodDao = livelihoodDao,
            coreSharedPrefs = corePrefRepo,
        )
    }

    @Provides
    @Singleton
    fun provideGetLivelihoodListFromDbUseCase(
        getLivelihoodListFromDbRepository: GetLivelihoodListFromDbRepository
    ): GetLivelihoodListFromDbUseCase {
        return GetLivelihoodListFromDbUseCase(getLivelihoodListFromDbRepository = getLivelihoodListFromDbRepository)
    }

    @Provides
    @Singleton
    fun provideGetSubjectLivelihoodMappingFromUseCase(
        getLivelihoodMappingForSubjectFromDbRepository: GetLivelihoodMappingForSubjectFromDbRepository
    ): GetSubjectLivelihoodMappingFromUseCase {
        return GetSubjectLivelihoodMappingFromUseCase(getLivelihoodMappingForSubjectFromDbRepository)
    }

    @Provides
    @Singleton
    fun provideGetLivelihoodMappingForSubjectFromDbRepository(
        subjectLivelihoodMappingDao: SubjectLivelihoodMappingDao,
        coreSharedPrefs: CoreSharedPrefs
    ): GetLivelihoodMappingForSubjectFromDbRepository {
        return GetLivelihoodMappingForSubjectFromDbRepositoryImpl(
            subjectLivelihoodMappingDao = subjectLivelihoodMappingDao,
            coreSharedPrefs = coreSharedPrefs
        )
    }

    @Provides
    @Singleton
    fun provideSaveLivelihoodMappingForSubjectUseCase(
        saveLivelihoodMappingForSubjectRepository: SaveLivelihoodMappingForSubjectRepository
    ): SaveLivelihoodMappingUseCase {
        return SaveLivelihoodMappingUseCase(
            saveLivelihoodMappingForSubjectRepository
        )
    }

    @Provides
    @Singleton
    fun provideFetchDidiDetailsWithLivelihoodMappingRepository(
        coreSharedPrefs: CoreSharedPrefs,
        subjectEntityDao: SubjectEntityDao
    ): FetchDidiDetailsWithLivelihoodMappingRepository {
        return FetchDidiDetailsWithLivelihoodMappingRepositoryImpl(
            coreSharedPrefs, subjectEntityDao
        )
    }

    @Provides
    @Singleton
    fun provideFetchDidiDetailsWithLivelihoodMappingUseCase(
        fetchDidiDetailsWithLivelihoodMappingRepository: FetchDidiDetailsWithLivelihoodMappingRepository
    ): FetchDidiDetailsWithLivelihoodMappingUseCase {
        return FetchDidiDetailsWithLivelihoodMappingUseCase(
            fetchDidiDetailsWithLivelihoodMappingRepository
        )
    }

    @Provides
    @Singleton
    fun provideLivelihoodUseCase(
        coreLivelihoodRepositoryImpl: ICoreLivelihoodRepository
    ): LivelihoodUseCase {
        return LivelihoodUseCase(
            coreLivelihoodRepositoryImpl
        )
    }

    @Provides
    @Singleton
    fun provideCoreLivelihoodRepositoryImpl(
        assetDao: AssetDao,
        productDao: ProductDao,
        livelihoodEventDao: LivelihoodEventDao,
        livelihoodDao: LivelihoodDao,
        livelihoodLanguageDao: LivelihoodLanguageDao,
        coreSharedPrefs: CoreSharedPrefs,
        dataLoadingApiService: DataLoadingApiService
    ): ICoreLivelihoodRepository {
        return CoreLivelihoodRepositoryImpl(
            assetDao,
            productDao,
            livelihoodEventDao,
            livelihoodDao,
            livelihoodLanguageDao,
            coreSharedPrefs,
            dataLoadingApiService
        )
    }

    @Provides
    @Singleton
    fun provideSaveLivelihoodMappingForSubjectRepository(
        subjectLivelihoodMappingDao: SubjectLivelihoodMappingDao,
        coreSharedPrefs: CoreSharedPrefs
    ): SaveLivelihoodMappingForSubjectRepository {
        return SaveLivelihoodMappingForSubjectRepositoryImpl(
            subjectLivelihoodMappingDao = subjectLivelihoodMappingDao,
            coreSharedPrefs = coreSharedPrefs
        )
    }

    @Provides
    @Singleton
    fun provideAssetRepository(
        coreSharedPrefs: CoreSharedPrefs,
        assetDao: AssetDao
    ): IAssetRepository {
        return AssetRepositoryImpl(coreSharedPrefs = coreSharedPrefs, assetDao = assetDao)
    }

    @Provides
    @Singleton
    fun provideProductRepository(
        coreSharedPrefs: CoreSharedPrefs,
        productDao: ProductDao
    ): IProductRepository {
        return ProductRepositoryImpl(coreSharedPrefs = coreSharedPrefs, productDao = productDao)
    }

    @Provides
    @Singleton
    fun provideLivelihoodEventRepository(
        coreSharedPrefs: CoreSharedPrefs,
        livelihoodEventDao: LivelihoodEventDao
    ): ILivelihoodEventRepository {
        return LivelihoodEventRepositoryImpl(
            coreSharedPrefs = coreSharedPrefs,
            livelihoodEventDao = livelihoodEventDao
        )
    }


    @Provides
    @Singleton
    fun provideSaveLivelihoodOptionUseCase(
        repository: FetchLivelihoodOptionRepositoryImpl,
        coreSharedPrefs: CoreSharedPrefs
    ): FetchLivelihoodOptionNetworkUseCase {
        return FetchLivelihoodOptionNetworkUseCase(
            repository = repository,
            coreSharedPrefs = coreSharedPrefs
        )
    }

    @Provides
    @Singleton
    fun provideLivelihoodEventUseCase(
        livelihoodEventRepositoryImpl: LivelihoodEventRepositoryImpl
    ): FetchLivelihoodEventUseCase {
        return FetchLivelihoodEventUseCase(
            livelihoodEventRepositoryImpl
        )
    }

    @Provides
    @Singleton
    fun provideAssetUseCase(
        assetRepositoryImpl: AssetRepositoryImpl
    ): FetchAssetUseCase {
        return FetchAssetUseCase(assetRepositoryImpl)
    }

    @Provides
    @Singleton
    fun provideProductUseCase(
        productRepositoryImpl: ProductRepositoryImpl
    ): FetchProductUseCase {
        return FetchProductUseCase(productRepositoryImpl)
    }

    @Provides
    @Singleton
    fun provideAssetJournalRepository(
        coreSharedPrefs: CoreSharedPrefs,
        assetJournalDao: AssetJournalDao
    ): AssetJournalRepositoryImpl {
        return AssetJournalRepositoryImpl(
            coreSharedPrefs = coreSharedPrefs,
            assetJournalDao = assetJournalDao
        )
    }

    @Provides
    @Singleton
    fun provideSaveLivelihoodEventUseCase(
        assetJournalRepo: AssetJournalRepositoryImpl,
        moneyJournalRepository: MoneyJournalRepositoryImpl,
        subjectLivelihoodEventMappingRepositoryImpl: SubjectLivelihoodEventMappingRepositoryImpl
    ): SaveLivelihoodEventUseCase {
        return SaveLivelihoodEventUseCase(
            assetJournalRepository = assetJournalRepo,
            moneyJournalRepo = moneyJournalRepository,
            subjectLivelihoodEventMappingRepository = subjectLivelihoodEventMappingRepositoryImpl
        )
    }

    @Provides
    @Singleton
    fun provideFetchSubjectIncomeExpenseSummaryUseCase(
        fetchSubjectIncomeExpenseSummaryRepository: FetchSubjectIncomeExpenseSummaryRepository,
        assetRepositoryImpl: AssetRepositoryImpl
    ): FetchSubjectIncomeExpenseSummaryUseCase {
        return FetchSubjectIncomeExpenseSummaryUseCase(
            fetchSubjectIncomeExpenseSummaryRepository,
            assetRepositoryImpl
        )
    }

    @Singleton
    @Provides
    fun provideFetchSubjectIncomeExpenseSummaryRepository(
        coreSharedPrefs: CoreSharedPrefs,
        livelihoodEventDao: LivelihoodEventDao,
        moneyJournalDao: MoneyJournalDao,
        assetJournalDao: AssetJournalDao,
        livelihoodDao: LivelihoodDao,
        assetDao: AssetDao
    ): FetchSubjectIncomeExpenseSummaryRepository {
        return FetchSubjectIncomeExpenseSummaryRepositoryImpl(
            coreSharedPrefs = coreSharedPrefs,
            livelihoodEventDao = livelihoodEventDao,
            moneyJournalDao = moneyJournalDao,
            assetJournalDao = assetJournalDao,
            livelihoodDao = livelihoodDao,
            assetDao = assetDao
        )
    }

    @Provides
    @Singleton
    fun provideFetchSavesLivelihoodEventUseCase(
        assetJournalRepo: AssetJournalRepositoryImpl,
        moneyJournalRepository: MoneyJournalRepositoryImpl,
        subjectLivelihoodEventMappingRepositoryImpl: SubjectLivelihoodEventMappingRepositoryImpl
    ): FetchSavedEventUseCase {
        return FetchSavedEventUseCase(
            assetJournalRepository = assetJournalRepo,
            moneyJournalRepo = moneyJournalRepository,
            subjectLivelihoodEventMappingRepository = subjectLivelihoodEventMappingRepositoryImpl
        )
    }
    @Provides
    @Singleton
    fun provideSectionListRepository(
        coreSharedPrefs: CoreSharedPrefs,
        surveyEntityDao: SurveyEntityDao,
        sectionEntityDao: SectionEntityDao,
        sectionSectionEntityDao: SectionStatusEntityDao

    ): SectionListRepository {
        return SectionListRepositoryImpl(
            coreSharedPrefs,
            surveyEntityDao,
            sectionEntityDao,
            sectionSectionEntityDao
        )
    }

    @Provides
    @Singleton
    fun provideGetSectionListUseCase(
        sectionListRepository: SectionListRepository
    ): GetSectionListUseCase {
        return GetSectionListUseCase(sectionListRepository)
    }

    @Provides
    @Singleton
    fun provideSectionStatusUpdateUseCase(
        sectionStatusUpdateRepository: SectionStatusUpdateRepository
    ): SectionStatusUpdateUseCase {
        return SectionStatusUpdateUseCase(sectionStatusUpdateRepository)
    }

    @Provides
    @Singleton
    fun provideSectionStatusUpdateRepository(
        coreSharedPrefs: CoreSharedPrefs,
        sectionSectionEntityDao: SectionStatusEntityDao
    ): SectionStatusUpdateRepository {

        return SectionStatusUpdateRepositoryImpl(coreSharedPrefs, sectionSectionEntityDao)

    }

    @Provides
    @Singleton
    fun provideSectionStatusEventWriterUserCase(
        sectionStatusEventWriterRepository: SectionStatusEventWriterRepository,
        eventWriterRepositoryImpl: EventWriterRepositoryImpl
    ): SectionStatusEventWriterUserCase {
        return SectionStatusEventWriterUserCase(
            sectionStatusEventWriterRepository,
            eventWriterRepositoryImpl
        )
    }

    @Provides
    @Singleton
    fun provideSectionStatusEventWriterRepository(
        coreSharedPrefs: CoreSharedPrefs,
        taskDao: TaskDao,
        sectionStatusEntityDao: SectionStatusEntityDao,
        surveyEntityDao: SurveyEntityDao
    ): SectionStatusEventWriterRepository {
        return SectionStatusEventWriterRepositoryImpl(
            coreSharedPrefs,
            taskDao,
            sectionStatusEntityDao,
            surveyEntityDao
        )
    }


    @Provides
    @Singleton
    fun provideWriteLivelihoodEventUseCase(
        assetJournalRepo: AssetJournalRepositoryImpl,
        moneyJournalRepository: MoneyJournalRepositoryImpl,
        subjectLivelihoodEventMappingRepositoryImpl: SubjectLivelihoodEventMappingRepositoryImpl,
        eventWriterRepositoryImpl: EventWriterRepositoryImpl
    ): WriteLivelihoodEventUseCase {
        return WriteLivelihoodEventUseCase(
            assetJournalRepository = assetJournalRepo,
            moneyJournalRepo = moneyJournalRepository,
            subjectLivelihoodEventMappingRepository = subjectLivelihoodEventMappingRepositoryImpl,
            eventWriterRepository = eventWriterRepositoryImpl
        )
    }

    @Provides
    @Singleton
    fun provideFetchSubjectLivelihoodEventHistoryUseCase(
        fetchSubjectLivelihoodEventHistoryRepository: FetchSubjectLivelihoodEventHistoryRepository
    ): FetchSubjectLivelihoodEventHistoryUseCase {
        return FetchSubjectLivelihoodEventHistoryUseCase(
            fetchSubjectLivelihoodEventHistoryRepository
        )
    }

    @Provides
    @Singleton
    fun provideFetchSubjectLivelihoodEventHistoryRepository(
        coreSharedPrefs: CoreSharedPrefs,
        subjectLivelihoodEventMappingDao: SubjectLivelihoodEventMappingDao
    ): FetchSubjectLivelihoodEventHistoryRepository {
        return FetchSubjectLivelihoodEventHistoryRepositoryImpl(
            coreSharedPrefs, subjectLivelihoodEventMappingDao
        )
    }

    @Provides
    @Singleton
    fun provideGetConditionQuestionMappingsUseCase(
        getConditionQuestionMappingsRepository: GetConditionQuestionMappingsRepository
    ): GetConditionQuestionMappingsUseCase {
        return GetConditionQuestionMappingsUseCase(getConditionQuestionMappingsRepository = getConditionQuestionMappingsRepository)
    }

    @Provides
    @Singleton
    fun provideGetConditionQuestionMappingsRepository(
        corePrefRepo: CoreSharedPrefs,
        sourceTargetQuestionMappingEntityDao: SourceTargetQuestionMappingEntityDao,
        conditionsEntityDao: ConditionsEntityDao
    ): GetConditionQuestionMappingsRepository {

        return GetConditionQuestionMappingsRepositoryImpl(
            corePrefRepo = corePrefRepo,
            sourceTargetQuestionMappingEntityDao = sourceTargetQuestionMappingEntityDao,
            conditionsEntityDao = conditionsEntityDao
        )

    }

    @Provides
    @Singleton
    fun provideRegenerateLivelihoodEventUseCase(
        assetJournalRepo: AssetJournalRepositoryImpl,
        moneyJournalRepository: MoneyJournalRepositoryImpl,
        subjectLivelihoodEventMappingRepositoryImpl: SubjectLivelihoodEventMappingRepositoryImpl,
        eventWriterRepositoryImpl: EventWriterRepositoryImpl
    ): RegenerateLivelihoodEventUseCase {
        return RegenerateLivelihoodEventUseCase(
            assetJournalRepository = assetJournalRepo,
            moneyJournalRepo = moneyJournalRepository,
            subjectLivelihoodEventMappingRepository = subjectLivelihoodEventMappingRepositoryImpl,
            eventWriterRepository = eventWriterRepositoryImpl
        )
    }

    @Provides
    @Singleton
    fun provideLivelihoodEventValidationUseCase(
        assetJournalRepo: AssetJournalRepositoryImpl,
        assetRepositoryImpl: AssetRepositoryImpl

        ): LivelihoodEventValidationUseCase {
        return LivelihoodEventValidationUseCase(
            assetJournalRepository = assetJournalRepo,
            assetRepository = assetRepositoryImpl
        )
    }

    @Provides
    @Singleton
    fun providesGetSurveyConfigFromDbUseCase(
        getSurveyConfigFromDbRepository: GetSurveyConfigFromDbRepository
    ): GetSurveyConfigFromDbUseCase {
        return GetSurveyConfigFromDbUseCase(
            getSurveyConfigFromDbRepository = getSurveyConfigFromDbRepository
        )
    }

    @Provides
    @Singleton
    fun providesGetSurveyConfigFromDbRepository(
        coreSharedPrefs: CoreSharedPrefs,
        surveyConfigEntityDao: SurveyConfigEntityDao
    ): GetSurveyConfigFromDbRepository {
        return GetSurveyConfigFromDbRepositoryImpl(
            coreSharedPrefs = coreSharedPrefs,
            surveyConfigEntityDao = surveyConfigEntityDao
        )

    }

    @Provides
    @Singleton
    fun providesSurveyValidationUseCase(
        coreSharedPrefs: CoreSharedPrefs
    ): SurveyValidationUseCase {
        return SurveyValidationUseCase(coreSharedPrefs)
    }

    @Provides
    @Singleton
    fun providesGetSurveyValidationsFromDbUseCase(
        getSurveyValidationsFromDbRepository: GetSurveyValidationsFromDbRepository
    ): GetSurveyValidationsFromDbUseCase {
        return GetSurveyValidationsFromDbUseCase(
            getSurveyValidationsFromDbRepository = getSurveyValidationsFromDbRepository
        )
    }

    @Provides
    @Singleton
    fun providesGetSurveyValidationsFromDbRepository(
        coreSharedPrefs: CoreSharedPrefs,
        surveyEntityDao: SurveyEntityDao
    ): GetSurveyValidationsFromDbRepository {
        return GetSurveyValidationsFromDbRepositoryImpl(
            coreSharedPrefs = coreSharedPrefs,
            surveyEntityDao = surveyEntityDao
        )
    }
}