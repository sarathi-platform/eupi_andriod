package com.nudge.auditTrail.di

import com.nudge.auditTrail.apiService.AuditTrailApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AuditTrailModule {
    @Singleton
    @Provides
    fun provideAuditTrailApi(retrofit: Retrofit): AuditTrailApiService {
        return retrofit.create(AuditTrailApiService::class.java)
    }
//    @Provides
//    @Singleton
//    fun auditRepository(coreSharedPrefs: CoreSharedPrefs): AuditTrailRepositoryImpl {
//        return AuditTrailRepositoryImpl(coreSharedPrefs=coreSharedPrefs)
//    }
}