package com.sarathi.dataloadingmangement.di

import android.app.Application
import android.content.Context
import androidx.room.Room
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
import com.sarathi.dataloadingmangement.data.dao.SubjectAttributeDao
import com.sarathi.dataloadingmangement.data.dao.SubjectEntityDao
import com.sarathi.dataloadingmangement.data.dao.TaskAttributeDao
import com.sarathi.dataloadingmangement.data.dao.TaskDao
import com.sarathi.dataloadingmangement.data.dao.UiConfigDao
import com.sarathi.dataloadingmangement.data.dao.smallGroup.SmallGroupDidiMappingDao
import com.sarathi.dataloadingmangement.data.database.NudgeGrantDatabase
import com.sarathi.dataloadingmangement.domain.DataLoadingScreenRepositoryImpl
import com.sarathi.dataloadingmangement.domain.FetchDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchContentDataFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchMissionDataFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchUserDetailsUseCase
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.FetchDidiDetailsFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.FetchSmallGroupFromNetworkUseCase
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import com.sarathi.dataloadingmangement.repository.IDataLoadingScreenRepository
import com.sarathi.dataloadingmangement.repository.smallGroup.FetchDidiDetailsFromNetworkRepository
import com.sarathi.dataloadingmangement.repository.smallGroup.FetchDidiDetailsFromNetworkRepositoryImpl
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
    fun providesSubjectEntityDao(db: NudgeGrantDatabase) = db.subjectEntityDao()

    @Provides
    @Singleton
    fun providesSmallGroupDidiMappingDao(db: NudgeGrantDatabase) = db.smallGroupDidiMappingDao()


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
        taskAttributeDao: TaskAttributeDao,
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
            taskAttributeDao,
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
        application: Application
    ): FetchDataUseCase {
        return FetchDataUseCase(
            fetchMissionDataFromNetworkUseCase = FetchMissionDataFromNetworkUseCase(repository),
            fetchContentDataFromNetworkUseCase = FetchContentDataFromNetworkUseCase(
                repository,
                application
            ),
            fetchUserDetailsUseCase = FetchUserDetailsUseCase(repository)
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

}