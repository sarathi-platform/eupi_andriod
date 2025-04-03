package com.nudge.core.di

import android.content.Context
import androidx.room.Room
import com.nudge.core.CORE_DATABASE
import com.nudge.core.database.CoreDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class StorageModule {

    @Provides
    @Singleton
    fun provideCoreDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, CoreDatabase::class.java, CORE_DATABASE)
            .addMigrations(
                CoreDatabase.CORE_DATABASE_MIGRATION_1_2,
                CoreDatabase.CORE_DATABASE_MIGRATION_2_3
            )
            .fallbackToDestructiveMigration()
            .build()


    @Provides
    @Singleton
    fun provideAppConfigDao(db: CoreDatabase) = db.appConfigDao()

    @Provides
    @Singleton
    fun provideTranslationConfigDao(db: CoreDatabase) = db.translationConfigDao()

    @Provides
    @Singleton
    fun provideLanguageListDao(db: CoreDatabase) = db.languageListDao()

    @Provides
    @Singleton
    fun prodiveCasteListDao(db: CoreDatabase) = db.casteListDao()

    @Provides
    @Singleton
    fun provideRemoteQueryAuditTrailEntityDao(db: CoreDatabase) =
        db.remoteQueryAuditTrailEntityDao()
}
