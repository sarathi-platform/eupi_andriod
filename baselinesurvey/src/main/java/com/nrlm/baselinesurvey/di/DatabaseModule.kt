package com.nrlm.baselinesurvey.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nrlm.baselinesurvey.NUDGE_BASELINE_DATABASE
import com.nrlm.baselinesurvey.database.NudgeBaselineDatabase
import com.nudge.syncmanager.SYNC_MANAGER_DATABASE
import com.nudge.syncmanager.database.SyncManagerDatabase
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
        Room.databaseBuilder(context, NudgeBaselineDatabase::class.java, NUDGE_BASELINE_DATABASE)
            // Add Migrations for each migration object created.
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .addMigrations(NudgeBaselineDatabase.MIGRATION_1_2)
            .addCallback(NudgeBaselineDatabase.NudgeDatabaseCallback())
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
    fun provideVillageDao(db: NudgeBaselineDatabase) = db.villageListDao()

    @Provides
    @Singleton
    fun provideLanguageDao(db: NudgeBaselineDatabase) = db.languageListDao()

    @Provides
    @Singleton
    fun provideDidiDao(db: NudgeBaselineDatabase) = db.didiDao()

    @Provides
    @Singleton
    fun provideSurveyEntityDao(db: NudgeBaselineDatabase) = db.surveyEntityDao()

    @Provides
    @Singleton
    fun provideSectionEntityDao(db: NudgeBaselineDatabase) = db.sectionEntityDao()

    @Provides
    @Singleton
    fun provideQuestionEntityDao(db: NudgeBaselineDatabase) = db.questionEntityDao()

    @Provides
    @Singleton
    fun provideOptionItemDao(db: NudgeBaselineDatabase) = db.optionItemDao()

    @Provides
    @Singleton
    fun provideMissionEntityDao(db: NudgeBaselineDatabase) = db.missionEntityDao()

    @Provides
    @Singleton
    fun provideDidiInfoEntityDao(db: NudgeBaselineDatabase) = db.didiInfoEntityDao()

    @Provides
    @Singleton
    fun provideMissionActivityEntityDao(db: NudgeBaselineDatabase) = db.missionActivityEntityDao()

    @Provides
    @Singleton
    fun provideActivityTaskEntityDao(db: NudgeBaselineDatabase) = db.activityTaskEntityDao()

    @Provides
    @Singleton
    fun provideContentEntityDao(db: NudgeBaselineDatabase) = db.contentEntityDao()

    @Provides
    @Singleton
    fun provideDidiSectionProgressEntityDao(db: NudgeBaselineDatabase) =
        db.didiSectionProgressEntityDao()

    @Provides
    @Singleton
    fun provideSectionAnswerEntityDao(db: NudgeBaselineDatabase) = db.sectionAnswerEntityDao()

    @Provides
    @Singleton
    fun provideFormQuestionResponseDao(db: NudgeBaselineDatabase) = db.formQuestionResponseDao()

    @Provides
    @Singleton
    fun provideInputTypeQuestionAnswerDao(db: NudgeBaselineDatabase) =
        db.inputTypeQuestionAnswerDao()

    @Provides
    @Singleton
    fun providesEventsDao(syncDb: SyncManagerDatabase) = syncDb.eventsDao()

    @Provides
    @Singleton
    fun providesEventDependencyDao(syncDb: SyncManagerDatabase) = syncDb.eventsDependencyDao()


    @Provides
    @Singleton
    fun providesApiStatusDao(syncDb: SyncManagerDatabase) = syncDb.apiStatusDao()


}