package com.nudge.auditTrail.di

import com.nudge.auditTrail.apiService.AuditTrailApiService
import com.nudge.auditTrail.database.dao.AuditTrailDao
import com.nudge.auditTrail.domain.repository.AuditTrailRepository
import com.nudge.auditTrail.domain.repository.AuditTrailRepositoryImpl
import com.nudge.core.preference.CoreSharedPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {
    @Singleton
    @Provides
    fun provideAuditTrailRepository(
        auditTrailApiService: AuditTrailApiService,
        coreSharedPrefs: CoreSharedPrefs,
        auditDao: AuditTrailDao

    ): AuditTrailRepository {
        return AuditTrailRepositoryImpl(auditTrailApiService, coreSharedPrefs, auditDao)
    }

}