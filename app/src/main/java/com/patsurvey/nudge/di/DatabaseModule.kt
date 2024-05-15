package com.patsurvey.nudge.di

import android.content.Context
import androidx.room.Room
import com.nudge.core.NUDGE_DATABASE
import com.nudge.core.NUDGE_GRANT_DATABASE
import com.nudge.core.SYNC_MANAGER_DATABASE
import com.nudge.syncmanager.database.SyncManagerDatabase
import com.patsurvey.nudge.activities.ui.login.dataloadingscreen.domain.DataLoadingScreenRepositoryImpl
import com.patsurvey.nudge.activities.ui.login.dataloadingscreen.domain.FetchDataUseCase
import com.patsurvey.nudge.activities.ui.login.dataloadingscreen.domain.use_case.FetchMissionDataFromNetworkUseCase
import com.patsurvey.nudge.activities.ui.login.dataloadingscreen.repository.IDataLoadingScreenRepository
import com.patsurvey.nudge.database.NudgeDatabase
import com.patsurvey.nudge.database.NudgeGrantDatabase
import com.patsurvey.nudge.network.interfaces.ApiService
import com.sarathi.missionactivitytask.data.dao.ActivityTaskDao
import com.sarathi.missionactivitytask.data.dao.MissionActivityDao
import com.sarathi.missionactivitytask.data.dao.MissionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, NudgeDatabase::class.java, NUDGE_DATABASE)
            // Add Migrations for each migration object created.
            .addMigrations(NudgeDatabase.MIGRATION_1_2).addCallback(NudgeDatabase.NudgeDatabaseCallback())
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideSyncDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, SyncManagerDatabase::class.java, SYNC_MANAGER_DATABASE)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideVillageDao(db: NudgeDatabase) = db.villageListDao()

    @Provides
    @Singleton
    fun provideUserDao(db: NudgeDatabase) = db.userDao()

    @Provides
    @Singleton
    fun provideLanguageDao(db: NudgeDatabase) = db.languageListDao()

    @Provides
    @Singleton
    fun provideStepsListDao(db: NudgeDatabase) = db.stepsListDao()

    @Provides
    @Singleton
    fun provideTolaDao(db: NudgeDatabase) = db.tolaDao()

    @Provides
    @Singleton
    fun provideCasteListDao(db: NudgeDatabase) = db.casteListDao()

    @Provides
    @Singleton
    fun provideDidiDao(db: NudgeDatabase) = db.didiDao()

    @Provides
    @Singleton
    fun provideLastSelectedTolaDao(db: NudgeDatabase) = db.lastSelectedTola()

    @Provides
    @Singleton
    fun provideQuestionDao(db: NudgeDatabase) = db.questionListDao()

    @Provides
    @Singleton
    fun provideAnswerDao(db: NudgeDatabase) = db.answerDao()

    @Provides
    @Singleton
    fun provideNumericAnswerDao(db: NudgeDatabase) = db.numericAnswerDao()

    @Provides
    @Singleton
    fun provideTrainingVideoDao(db: NudgeDatabase) = db.trainingVideoDao()

    @Provides
    @Singleton
    fun provideBpcSummaryDao(db: NudgeDatabase) = db.bpcSummaryDao()

    @Provides
    @Singleton
    fun provideBpcScorePercentageDao(db: NudgeDatabase) = db.bpcScorePercentageDao()

    @Provides
    @Singleton
    fun providePoorDidiListDao(db: NudgeDatabase) = db.poorDidiListDao()

    @Provides
    @Singleton
    fun providesEventsDao(syncDb: SyncManagerDatabase) = syncDb.eventsDao()

    @Provides
    @Singleton
    fun providesEventDependencyDao(syncDb: SyncManagerDatabase) = syncDb.eventsDependencyDao()

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
    fun provideFetchDataUseCaseUseCase(
        repository: IDataLoadingScreenRepository,
    ): FetchDataUseCase {
        return FetchDataUseCase(
            fetchMissionDataFromNetworkUseCase = FetchMissionDataFromNetworkUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideDataLoadingScreenRepository(
        missionDao: MissionDao,
        activityDao: MissionActivityDao,
        activityTaskDao: ActivityTaskDao,
        apiService: ApiService
    ): IDataLoadingScreenRepository {
        return DataLoadingScreenRepositoryImpl(apiService, missionDao, activityDao, activityTaskDao)
    }

}