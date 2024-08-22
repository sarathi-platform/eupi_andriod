package com.patsurvey.nudge.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nudge.core.NUDGE_DATABASE
import com.nudge.core.SYNC_MANAGER_DATABASE
import com.nudge.syncmanager.database.SyncManagerDatabase
import com.patsurvey.nudge.database.NudgeDatabase
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
            .addMigrations(NudgeDatabase.MIGRATION_1_2)
            .addCallback(NudgeDatabase.NudgeDatabaseCallback())
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideSyncDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, SyncManagerDatabase::class.java, SYNC_MANAGER_DATABASE)
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
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
    fun providesApiStatusDao(syncDb: SyncManagerDatabase) = syncDb.apiStatusDao()

    @Provides
    @Singleton
    fun providesEventStatusDao(syncDb: SyncManagerDatabase) = syncDb.eventStatusDao()

    @Provides
    @Singleton
    fun provideImageEventStatusDao(syncDb: SyncManagerDatabase) = syncDb.imageStatusDao()

}