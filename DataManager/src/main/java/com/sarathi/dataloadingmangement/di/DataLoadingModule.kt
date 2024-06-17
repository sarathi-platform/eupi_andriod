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
import com.sarathi.dataloadingmangement.data.dao.FormUiConfigDao
import com.sarathi.dataloadingmangement.data.dao.GrantConfigDao
import com.sarathi.dataloadingmangement.data.dao.LanguageDao
import com.sarathi.dataloadingmangement.data.dao.MissionDao
import com.sarathi.dataloadingmangement.data.dao.MissionLanguageAttributeDao
import com.sarathi.dataloadingmangement.data.dao.OptionItemDao
import com.sarathi.dataloadingmangement.data.dao.ProgrammeDao
import com.sarathi.dataloadingmangement.data.dao.QuestionEntityDao
import com.sarathi.dataloadingmangement.data.dao.SectionEntityDao
import com.sarathi.dataloadingmangement.data.dao.SubjectAttributeDao
import com.sarathi.dataloadingmangement.data.dao.SurveyAnswersDao
import com.sarathi.dataloadingmangement.data.dao.SurveyEntityDao
import com.sarathi.dataloadingmangement.data.dao.SurveyLanguageAttributeDao
import com.sarathi.dataloadingmangement.data.dao.TaskDao
import com.sarathi.dataloadingmangement.data.dao.UiConfigDao
import com.sarathi.dataloadingmangement.data.database.NudgeGrantDatabase
import com.sarathi.dataloadingmangement.domain.DataLoadingUseCase
import com.sarathi.dataloadingmangement.domain.use_case.ContentDownloaderUseCase
import com.sarathi.dataloadingmangement.domain.use_case.ContentUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchAllDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchContentDataFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchLanguageUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchMissionDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchSurveyAnswerFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchSurveyDataFromDB
import com.sarathi.dataloadingmangement.domain.use_case.FetchSurveyDataFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchUserDetailUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FormUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SurveyAnswerEventWriterUseCase
import com.sarathi.dataloadingmangement.download_manager.DownloaderManager
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import com.sarathi.dataloadingmangement.repository.ContentDownloaderRepositoryImpl
import com.sarathi.dataloadingmangement.repository.ContentRepositoryImpl
import com.sarathi.dataloadingmangement.repository.EventWriterRepositoryImpl
import com.sarathi.dataloadingmangement.repository.FormRepositoryImpl
import com.sarathi.dataloadingmangement.repository.IContentDownloader
import com.sarathi.dataloadingmangement.repository.IContentRepository
import com.sarathi.dataloadingmangement.repository.IEventWriterRepository
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
import com.sarathi.dataloadingmangement.repository.SurveyAnswerEventRepositoryImpl
import com.sarathi.dataloadingmangement.repository.SurveyDownloadRepository
import com.sarathi.dataloadingmangement.repository.SurveyRepositoryImpl
import com.sarathi.dataloadingmangement.repository.SurveySaveNetworkRepositoryImpl
import com.sarathi.dataloadingmangement.repository.SurveySaveRepositoryImpl
import com.sarathi.dataloadingmangement.repository.TaskStatusRepositoryImpl
import com.sarathi.dataloadingmangement.repository.UserDetailRepository
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
            .fallbackToDestructiveMigration().build()

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
    fun provideSurveyLanguageAttributeDao(db: NudgeGrantDatabase) = db.surveyLanguageAttributeDao()


    @Provides
    @Singleton
    fun provideSurveyDownloadRepository(
        dataLoadingApiService: DataLoadingApiService,
        surveyDao: SurveyEntityDao,
        sectionEntityDao: SectionEntityDao,
        coreSharedPrefs: CoreSharedPrefs,
        optionItemDao: OptionItemDao,
        questionEntityDao: QuestionEntityDao,
        surveyLanguageAttributeDao: SurveyLanguageAttributeDao
    ): ISurveyDownloadRepository {
        return SurveyDownloadRepository(
            dataLoadingApiService = dataLoadingApiService,
            surveyDao = surveyDao,
            sectionEntityDao = sectionEntityDao,
            coreSharedPrefs = coreSharedPrefs,
            optionItemDao = optionItemDao,
            questionEntityDao = questionEntityDao,
            surveyLanguageAttributeDao = surveyLanguageAttributeDao

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
        contentConfigDao: ContentConfigDao
    ): IContentRepository {
        return ContentRepositoryImpl(
            apiInterface = apiService,
            contentDao = contentDao,
            coreSharedPrefs = coreSharedPrefs,
            contentConfigDao = contentConfigDao
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
    fun provideContentDownloaderRepositoryImpl(
        contentDao: ContentDao,
        coreSharedPrefs: CoreSharedPrefs,
        contentConfigDao: ContentConfigDao,
        attributeValueReferenceDao: AttributeValueReferenceDao
    ): IContentDownloader {
        return ContentDownloaderRepositoryImpl(
            contentDao,
            coreSharedPrefs = coreSharedPrefs,
            contentConfigDao = contentConfigDao,
            attributeValueReferenceDao = attributeValueReferenceDao
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
    ): FormUseCase {
        return FormUseCase(repository = repository, downloaderManager = downloaderManager)
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
        coreSharedPrefs: CoreSharedPrefs
    ): FetchAllDataUseCase {
        return FetchAllDataUseCase(
            fetchMissionDataUseCase = FetchMissionDataUseCase(
                missionRepositoryImpl
            ),
            fetchContentDataFromNetworkUseCase = FetchContentDataFromNetworkUseCase(
                contentRepositoryImpl,
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
            coreSharedPrefs = coreSharedPrefs
        )
    }


    @Provides
    @Singleton
    fun provideSaveSurveyRepository(
        surveyAnswersDao: SurveyAnswersDao,
        coreSharedPrefs: CoreSharedPrefs

    ): ISurveySaveRepository {
        return SurveySaveRepositoryImpl(
            surveyAnswersDao = surveyAnswersDao,
            coreSharedPrefs = coreSharedPrefs
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
        coreSharedPrefs: CoreSharedPrefs

    ): ISurveyAnswerEventRepository {
        return SurveyAnswerEventRepositoryImpl(
            coreSharedPrefs = coreSharedPrefs
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
        grantConfigDao: GrantConfigDao
    ): ISurveySaveNetworkRepository {
        return SurveySaveNetworkRepositoryImpl(
            coreSharedPrefs = coreSharedPrefs,
            activityConfigDao = activityConfigDao,
            questionEntityDao = questionEntityDao,
            optionItemDao = optionItemDao,
            dataLoadingApiService = dataLoadingApiService,
            surveyAnswersDao = surveyAnswersDao,
            grantConfigDao = grantConfigDao

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
}
