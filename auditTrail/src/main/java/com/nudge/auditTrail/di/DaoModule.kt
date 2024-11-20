package com.nudge.auditTrail.di

import android.content.Context
import androidx.room.Room
import com.nudge.auditTrail.database.AUDIT_TRAIL_DATABASE_NAME
import com.nudge.auditTrail.database.AuditTrailDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DaoModule {
    @Provides
    @Singleton
    fun provideAuditTrailDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AuditTrailDatabase::class.java, AUDIT_TRAIL_DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()


    @Provides
    @Singleton
    fun provideAppConfigDao(db: AuditTrailDatabase) = db.auditTrailDao()

}