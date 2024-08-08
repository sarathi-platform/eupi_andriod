package com.sarathi.dataloadingmangement.di

import android.content.Context
import androidx.room.Room
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.NUDGE_GRANT_DATABASE
import com.sarathi.dataloadingmangement.data.dao.ActivityConfigDao
import com.sarathi.dataloadingmangement.data.dao.ActivityDao
import com.sarathi.dataloadingmangement.data.dao.ActivityLanguageDao
import com.sarathi.dataloadingmangement.data.dao.AttributeValueReferenceDao
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
import com.sarathi.dataloadingmangement.data.dao.SubjectAttributeDao
import com.sarathi.dataloadingmangement.data.dao.SubjectEntityDao
import com.sarathi.dataloadingmangement.data.dao.SurveyAnswersDao
import com.sarathi.dataloadingmangement.data.dao.SurveyEntityDao
import com.sarathi.dataloadingmangement.data.dao.SurveyLanguageAttributeDao
import com.sarathi.dataloadingmangement.data.dao.TagReferenceEntityDao
import com.sarathi.dataloadingmangement.data.dao.TaskDao
import com.sarathi.dataloadingmangement.data.dao.UiConfigDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.MoneyJournalDao
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
import com.sarathi.dataloadingmangement.domain.use_case.GetSectionListUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.RegenerateGrantEventUsecase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SectionStatusEventWriterUserCase
import com.sarathi.dataloadingmangement.domain.use_case.SectionStatusUpdateUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveTransactionMoneyJournalUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SurveyAnswerEventWriterUseCase
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
            .addMigrations(NudgeGrantDatabase.NUDGE_GRANT_DATABASE_MIGRATION_1_2)
            .addCallback(NudgeGrantDatabase.NudgeGrantDatabaseCallback())
            .fallbackToDestructiveMigration()
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
    fun provideSectionStatusEntityDao(db: NudgeGrantDatabase) = db.sectionStatusEntityDao()
    @Provides
    @Singleton
    fun provideMoneyJournalDao(db: NudgeGrantDatabase) = db.moneyJournalDao()

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
        tagReferenceEntityDao: TagReferenceEntityDao
    ): ISurveyDownloadRepository {
        return SurveyDownloadRepository(
            dataLoadingApiService = dataLoadingApiService,
            surveyDao = surveyDao,
            sectionEntityDao = sectionEntityDao,
            coreSharedPrefs = coreSharedPrefs,
            optionItemDao = optionItemDao,
            questionEntityDao = questionEntityDao,
            surveyLanguageAttributeDao = surveyLanguageAttributeDao,
            tagReferenceEntityDao = tagReferenceEntityDao

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
        formUiConfigDao: FormUiConfigDao
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
            formUiConfigDao = formUiConfigDao
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
        activityConfigDao: ActivityConfigDao
    ): IContentRepository {
        return ContentRepositoryImpl(
            apiInterface = apiService,
            contentDao = contentDao,
            coreSharedPrefs = coreSharedPrefs,
            contentConfigDao = contentConfigDao,
            uiConfigDao = uiConfigDao,
            surveyAnswersDao = surveyAnswersDao,
            activityConfigDao = activityConfigDao
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
        surveySaveNetworkRepositoryImpl: SurveySaveNetworkRepositoryImpl,
        activityConfigDao: ActivityConfigDao,
        fetchSurveyAnswerFromNetworkUseCase: FetchSurveyAnswerFromNetworkUseCase,
        coreSharedPrefs: CoreSharedPrefs,
        formUseCase: FormUseCase,
        fetchMoneyJournalUseCase: FetchMoneyJournalUseCase
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
            moneyJournalUseCase = fetchMoneyJournalUseCase
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
        coreSharedPrefs: CoreSharedPrefs

    ): RegenerateGrantEventUsecase {
        return RegenerateGrantEventUsecase(
            regenerateGrantEventRepositoryImpl = regenerateGrantEventRepositoryImpl,
            matStatusEventWriterUseCase = matStatusEventWriterUseCase,
            fetchDataUseCase = fetchDataUseCase,
            surveyAnswerEventWriterUseCase = surveyAnswerEventWriterUseCase,
            formEventWriterUseCase = formEventWriterUseCase,
            documentEventWriterUseCase = documentEventWriterUseCase,
            attendanceEventWriterUseCase = attendanceEventWriterUseCase,
            coreSharedPrefs = coreSharedPrefs
        )
    }

    @Provides
    @Singleton
    fun fetchDidiDetailsFromNetworkRepository(
        coreSharedPrefs: CoreSharedPrefs,
        dataLoadingApiService: DataLoadingApiService,
        subjectEntityDao: SubjectEntityDao
    ): FetchDidiDetailsFromNetworkRepository {
        return FetchDidiDetailsFromNetworkRepositoryImpl(
            coreSharedPrefs, dataLoadingApiService,
            subjectEntityDao
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
        smallGroupDidiMappingDao: SmallGroupDidiMappingDao
    ): FetchSmallGroupDetailsFromNetworkRepository {
        return FetchSmallGroupDetailsFromNetworkRepositoryImpl(
            coreSharedPrefs,
            dataLoadingApiService,
            smallGroupDidiMappingDao
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
        attributeValueReferenceDao: AttributeValueReferenceDao
    ): FetchSmallGroupAttendanceHistoryFromNetworkRepository {
        return FetchSmallGroupAttendanceHistoryFromNetworkRepositoryImpl(
            coreSharedPrefs = coreSharedPrefs,
            dataLoadingApiService = dataLoadingApiService,
            subjectEntityDao = subjectEntityDao,
            subjectAttributeDao = subjectAttributeDao,
            attributeValueReferenceDao = attributeValueReferenceDao
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

}