package com.nudge.auditTrail.di

import android.content.Context
import com.nudge.auditTrail.domain.repository.AuditTrailRepository
import com.nudge.auditTrail.domain.usecase.AuditTrailUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class UsecaseModule {
    @Singleton
    @Provides
    fun provideAuditTrailUsecase(
        auditTrailRepository: AuditTrailRepository,
        @ApplicationContext context: Context
    ): AuditTrailUseCase {
        return AuditTrailUseCase(auditTrailRepository, context)
    }

}