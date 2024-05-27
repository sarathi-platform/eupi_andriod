package com.sarathi.dataloadingmangement.di

import android.content.Context
import androidx.room.Room
import com.nudge.core.NUDGE_GRANT_DATABASE
import com.sarathi.dataloadingmangement.data.dao.ActivityTaskDao
import com.sarathi.dataloadingmangement.data.dao.MissionActivityDao
import com.sarathi.dataloadingmangement.data.dao.MissionDao
import com.sarathi.dataloadingmangement.data.dao.SubjectEntityDao
import com.sarathi.dataloadingmangement.data.dao.smallGroup.SmallGroupDidiMappingDao
import com.sarathi.dataloadingmangement.data.database.NudgeGrantDatabase
import com.sarathi.dataloadingmangement.domain.DataLoadingScreenRepositoryImpl
import com.sarathi.dataloadingmangement.domain.FetchDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchMissionDataFromNetworkUseCase
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
    fun provideDataLoadingScreenRepository(
//        corePrefRepo: CorePrefRepo,
        missionDao: MissionDao,
        activityDao: MissionActivityDao,
        activityTaskDao: ActivityTaskDao,
        apiService: DataLoadingApiService
    ): IDataLoadingScreenRepository {
        return DataLoadingScreenRepositoryImpl(/*corePrefRepo, */apiService,
            missionDao,
            activityDao,
            activityTaskDao
        )
    }

    @Provides
    @Singleton
    fun provideFetchDataUseCaseUseCase(
        repository: DataLoadingScreenRepositoryImpl,
    ): FetchDataUseCase {
        return FetchDataUseCase(
            fetchMissionDataFromNetworkUseCase = FetchMissionDataFromNetworkUseCase(repository),
        )
    }

    @Provides
    @Singleton
    fun fetchDidiDetailsFromNetworkRepository(
//        corePrefRepo: CorePrefRepo,
        dataLoadingApiService: DataLoadingApiService,
        subjectEntityDao: SubjectEntityDao
    ): FetchDidiDetailsFromNetworkRepository {
        return FetchDidiDetailsFromNetworkRepositoryImpl(/*corePrefRepo, */dataLoadingApiService,
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
//        corePrefRepo: CorePrefRepo,
        dataLoadingApiService: DataLoadingApiService,
        smallGroupDidiMappingDao: SmallGroupDidiMappingDao
    ): FetchSmallGroupDetailsFromNetworkRepository {
        return FetchSmallGroupDetailsFromNetworkRepositoryImpl(/*corePrefRepo, */
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