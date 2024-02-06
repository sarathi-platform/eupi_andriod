package com.nrlm.baselinesurvey.di

import android.content.Context
import androidx.room.Room
import com.nrlm.baselinesurvey.NUDGE_BASELINE_DATABASE
import com.nrlm.baselinesurvey.database.NudgeBaselineDatabase
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
            /*.addMigrations(NudgeBaselineDatabase.MIGRATION_1_2).addCallback(NudgeBaselineDatabase.NudgeBaselineDatabaseCallback())*/
            .fallbackToDestructiveMigration()
            .build()


//    @Provides
//    @Singleton
//    fun provideSyncDatabase(@ApplicationContext context: Context) =
//        Room.databaseBuilder(context, com.nudge.core.database.SyncManagerDatabase::class.java,
//            com.nudge.core.SYNC_MANAGER_DATABASE
//        )
//            .fallbackToDestructiveMigration()
//            .build()

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
    fun provideOptionEntityDao(db: NudgeBaselineDatabase) = db.optionItemDao()

    @Provides
    @Singleton
    fun provideMissionEntityDao(db: NudgeBaselineDatabase) = db.missionEntityDao()

    @Provides
    @Singleton
    fun provideDidiSectionProgressEntityDao(db: NudgeBaselineDatabase) =
        db.didiSectionProgressEntityDao()

    @Provides
    @Singleton
    fun provideSectionAnswerEntityDao(db: NudgeBaselineDatabase) = db.sectionAnswerEntityDao()

    /*@Provides
    @Singleton
    fun providesEventsDao(syncDb: com.nudge.core.database.SyncManagerDatabase) = syncDb.eventsDao()*/
}