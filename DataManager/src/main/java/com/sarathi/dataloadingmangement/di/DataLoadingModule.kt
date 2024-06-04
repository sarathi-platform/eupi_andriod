package com.sarathi.dataloadingmangement.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.NUDGE_GRANT_DATABASE
import com.sarathi.dataloadingmangement.data.dao.ActivityConfigDao
import com.sarathi.dataloadingmangement.data.dao.ActivityDao
import com.sarathi.dataloadingmangement.data.dao.ActivityLanguageAttributeDao
import com.sarathi.dataloadingmangement.data.dao.ActivityLanguageDao
import com.sarathi.dataloadingmangement.data.dao.AttributeValueReferenceDao
import com.sarathi.dataloadingmangement.data.dao.ContentConfigDao
import com.sarathi.dataloadingmangement.data.dao.ContentDao
import com.sarathi.dataloadingmangement.data.dao.MissionDao
import com.sarathi.dataloadingmangement.data.dao.MissionLanguageAttributeDao
import com.sarathi.dataloadingmangement.data.dao.OptionItemDao
import com.sarathi.dataloadingmangement.data.dao.ProgrammeDao
import com.sarathi.dataloadingmangement.data.dao.QuestionEntityDao
import com.sarathi.dataloadingmangement.data.dao.SectionEntityDao
import com.sarathi.dataloadingmangement.data.dao.SubjectAttributeDao
import com.sarathi.dataloadingmangement.data.dao.SurveyAnswersDao
import com.sarathi.dataloadingmangement.data.dao.SurveyEntityDao
import com.sarathi.dataloadingmangement.data.dao.TaskDao
import com.sarathi.dataloadingmangement.data.dao.UiConfigDao
import com.sarathi.dataloadingmangement.data.database.NudgeGrantDatabase
import com.sarathi.dataloadingmangement.domain.FetchDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchContentDataFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchMissionDataFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchSurveyDataFromDB
import com.sarathi.dataloadingmangement.domain.use_case.FetchSurveyDataFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SurveyAnswerEventWriterUseCase
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import com.sarathi.dataloadingmangement.repository.DataLoadingScreenRepositoryImpl
import com.sarathi.dataloadingmangement.repository.EventWriterRepositoryImpl
import com.sarathi.dataloadingmangement.repository.IDataLoadingScreenRepository
import com.sarathi.dataloadingmangement.repository.IEventWriterRepository
import com.sarathi.dataloadingmangement.repository.IMATStatusEventRepository
import com.sarathi.dataloadingmangement.repository.ISurveyAnswerEventRepository
import com.sarathi.dataloadingmangement.repository.ISurveyDownloadRepository
import com.sarathi.dataloadingmangement.repository.ISurveyRepository
import com.sarathi.dataloadingmangement.repository.ISurveySaveRepository
import com.sarathi.dataloadingmangement.repository.ITaskStatusRepository
import com.sarathi.dataloadingmangement.repository.MATStatusEventRepositoryImpl
import com.sarathi.dataloadingmangement.repository.SurveyAnswerEventRepositoryImpl
import com.sarathi.dataloadingmangement.repository.SurveyDownloadRepository
import com.sarathi.dataloadingmangement.repository.SurveyRepositoryImpl
import com.sarathi.dataloadingmangement.repository.SurveySaveRepositoryImpl
import com.sarathi.dataloadingmangement.repository.TaskStatusRepositoryImpl
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
    fun provideUiConfigDao(db: NudgeGrantDatabase) = db.uiConfigDao()

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
    fun provideSurveyDownloadRepository(
        dataLoadingApiService: DataLoadingApiService,
        surveyDao: SurveyEntityDao,
        sectionEntityDao: SectionEntityDao,
        coreSharedPrefs: CoreSharedPrefs,
        optionItemDao: OptionItemDao,
        questionEntityDao: QuestionEntityDao
    ): ISurveyDownloadRepository {
        return SurveyDownloadRepository(
            dataLoadingApiService = dataLoadingApiService,
            surveyDao = surveyDao,
            sectionEntityDao = sectionEntityDao,
            coreSharedPrefs = coreSharedPrefs,
            optionItemDao = optionItemDao,
            questionEntityDao = questionEntityDao

        )
    }


    @Provides
    @Singleton
    fun provideDataLoadingScreenRepository(
        missionDao: MissionDao,
        activityDao: ActivityDao,
        taskDao: TaskDao,
        activityConfigDao: ActivityConfigDao,
        activityLanguageAttributeDao: ActivityLanguageAttributeDao,
        activityLanguageDao: ActivityLanguageDao,
        attributeValueReferenceDao: AttributeValueReferenceDao,
        contentConfigDao: ContentConfigDao,
        missionLanguageAttributeDao: MissionLanguageAttributeDao,
        subjectAttributeDao: SubjectAttributeDao,
        programmeDao: ProgrammeDao,
        uiConfigDao: UiConfigDao,
        apiService: DataLoadingApiService,
        sharedPrefs: CoreSharedPrefs,
        contentDao: ContentDao,
    ): IDataLoadingScreenRepository {
        return DataLoadingScreenRepositoryImpl(
            apiService, missionDao, activityDao, taskDao,
            activityConfigDao,
            activityLanguageAttributeDao,
            activityLanguageDao,
            attributeValueReferenceDao,
            contentConfigDao,
            missionLanguageAttributeDao,
            subjectAttributeDao,
            programmeDao,
            uiConfigDao,
            contentDao,
            sharedPrefs,
        )
    }

    @Provides
    @Singleton
    fun provideContentDao(db: NudgeGrantDatabase) = db.contentDao()

    @Provides
    @Singleton
    fun provideFetchDataUseCaseUseCase(
        repository: DataLoadingScreenRepositoryImpl,
        surveyRepo: SurveyDownloadRepository,
        surveyRepositoryImpl: SurveyRepositoryImpl,
        application: Application
    ): FetchDataUseCase {
        return FetchDataUseCase(
            fetchMissionDataFromNetworkUseCase = FetchMissionDataFromNetworkUseCase(repository),
            fetchSurveyDataFromNetworkUseCase = FetchSurveyDataFromNetworkUseCase(surveyRepo),
            fetchSurveyDataFromDB = FetchSurveyDataFromDB(surveyRepositoryImpl),
            fetchContentDataFromNetworkUseCase = FetchContentDataFromNetworkUseCase(
                repository,
                application
            )
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
        surveyDao: SurveyEntityDao
    ): ISurveyRepository {
        return SurveyRepositoryImpl(
            questionDao = questionEntityDao,
            surveyAnswersDao = surveyAnswersDao,
            optionItemDao = optionItemDao,
            coreSharedPrefs = coreSharedPrefs,
            surveyEntityDao = surveyDao

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
        coreSharedPrefs: CoreSharedPrefs

    ): ITaskStatusRepository {
        return TaskStatusRepositoryImpl(
            taskDao = taskDao,
            coreSharedPrefs = coreSharedPrefs
        )
    }

    @Provides
    @Singleton
    fun provideMatStatusEventRepository(
        coreSharedPrefs: CoreSharedPrefs

    ): IMATStatusEventRepository {
        return MATStatusEventRepositoryImpl(
            coreSharedPrefs = coreSharedPrefs
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
}