package com.sarathi.dataloadingmangement.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.contentmodule.download_manager.DownloaderManager
import com.sarathi.dataloadingmangement.NUDGE_GRANT_DATABASE
import com.sarathi.dataloadingmangement.data.dao.ActivityConfigDao
import com.sarathi.dataloadingmangement.data.dao.ActivityDao
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
import com.sarathi.dataloadingmangement.data.dao.SurveyEntityDao
import com.sarathi.dataloadingmangement.data.dao.TaskDao
import com.sarathi.dataloadingmangement.data.dao.UiConfigDao
import com.sarathi.dataloadingmangement.data.database.NudgeGrantDatabase
import com.sarathi.dataloadingmangement.domain.DataLoadingUseCase
import com.sarathi.dataloadingmangement.domain.use_case.ContentDownloaderUseCase
import com.sarathi.dataloadingmangement.domain.use_case.ContentUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchAllDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchContentDataFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchMissionDataFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchSurveyDataFromNetworkUseCase
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import com.sarathi.dataloadingmangement.repository.ContentDownloaderRepositoryImpl
import com.sarathi.dataloadingmangement.repository.ContentRepositoryImpl
import com.sarathi.dataloadingmangement.repository.IContentDownloader
import com.sarathi.dataloadingmangement.repository.IContentRepository
import com.sarathi.dataloadingmangement.repository.IMissionRepository
import com.sarathi.dataloadingmangement.repository.ISurveyDownloadRepository
import com.sarathi.dataloadingmangement.repository.MissionRepositoryImpl
import com.sarathi.dataloadingmangement.repository.SurveyDownloadRepository
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
    fun provideContentDao(db: NudgeGrantDatabase) = db.contentDao()

    @Provides
    @Singleton
    fun provideFetchDataUseCaseUseCase(
        surveyRepo: SurveyDownloadRepository,
        application: Application,
        missionRepositoryImpl: MissionRepositoryImpl,
        contentRepositoryImpl: ContentRepositoryImpl
    ): DataLoadingUseCase {
        return DataLoadingUseCase(
            fetchMissionDataFromNetworkUseCase = FetchMissionDataFromNetworkUseCase(
                missionRepositoryImpl
            ),
            fetchSurveyDataFromNetworkUseCase = FetchSurveyDataFromNetworkUseCase(surveyRepo),
            fetchContentDataFromNetworkUseCase = FetchContentDataFromNetworkUseCase(
                contentRepositoryImpl,
                application
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
            sharedPrefs = sharedPrefs
        )
    }

    @Provides
    @Singleton
    fun provideContentRepository(
        contentDao: ContentDao,
        apiService: DataLoadingApiService,
    ): IContentRepository {
        return ContentRepositoryImpl(
            apiInterface = apiService, contentDao = contentDao
        )
    }

    @Provides
    @Singleton
    fun provideContentDownloaderRepositoryImpl(
        contentDao: ContentDao,
    ): IContentDownloader {
        return ContentDownloaderRepositoryImpl(
            contentDao
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
    fun provideFetchAllDataUseCase(
        surveyRepo: SurveyDownloadRepository,
        application: Application,
        missionRepositoryImpl: MissionRepositoryImpl,
        contentRepositoryImpl: ContentRepositoryImpl,
        repository: IContentDownloader,
        downloaderManager: DownloaderManager
    ): FetchAllDataUseCase {
        return FetchAllDataUseCase(
            fetchMissionDataFromNetworkUseCase = FetchMissionDataFromNetworkUseCase(
                missionRepositoryImpl
            ),
            fetchSurveyDataFromNetworkUseCase = FetchSurveyDataFromNetworkUseCase(surveyRepo),
            fetchContentDataFromNetworkUseCase = FetchContentDataFromNetworkUseCase(
                contentRepositoryImpl,
                application
            ),
            contentDownloaderUseCase = ContentDownloaderUseCase(repository, downloaderManager)
        )
    }
}
