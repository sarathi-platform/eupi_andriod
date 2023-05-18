package com.patsurvey.nudge.di

import android.content.Context
import androidx.room.Room
import com.patsurvey.nudge.database.NudgeDatabase
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.utils.NUDGE_DATABASE
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

}