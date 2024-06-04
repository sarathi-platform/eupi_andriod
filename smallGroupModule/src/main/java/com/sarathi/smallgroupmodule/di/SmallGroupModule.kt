package com.sarathi.smallgroupmodule.di

import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.syncmanager.EventWriterHelper
import com.sarathi.dataloadingmangement.data.dao.SubjectEntityDao
import com.sarathi.dataloadingmangement.data.dao.smallGroup.SmallGroupDidiMappingDao
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.FetchDidiDetailsFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.FetchSmallGroupFromNetworkUseCase
import com.sarathi.dataloadingmangement.repository.smallGroup.FetchDidiDetailsFromNetworkRepository
import com.sarathi.dataloadingmangement.repository.smallGroup.FetchSmallGroupDetailsFromNetworkRepository
import com.sarathi.smallgroupmodule.data.domain.EventWriterHelperImpl
import com.sarathi.smallgroupmodule.ui.didiTab.domain.repository.FetchDidiDetailsFromDbRepository
import com.sarathi.smallgroupmodule.ui.didiTab.domain.repository.FetchDidiDetailsFromDbRepositoryImpl
import com.sarathi.smallgroupmodule.ui.didiTab.domain.repository.FetchSmallGroupListFromDbRepository
import com.sarathi.smallgroupmodule.ui.didiTab.domain.repository.FetchSmallGroupListFromDbRepositoryImpl
import com.sarathi.smallgroupmodule.ui.didiTab.domain.use_case.DidiTabUseCase
import com.sarathi.smallgroupmodule.ui.didiTab.domain.use_case.FetchDidiDetailsFromDbUseCase
import com.sarathi.smallgroupmodule.ui.didiTab.domain.use_case.FetchSmallGroupListsFromDbUseCase
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.repository.FetchDidiListForSmallGroupFromDbRepository
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.repository.FetchDidiListForSmallGroupFromDbRepositoryImpl
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.useCase.FetchDidiListForSmallGroupFromDbUseCase
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.useCase.SmallGroupAttendanceUserCase
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.domain.repository.FetchSmallGroupDetailsFromDbRepository
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.domain.repository.FetchSmallGroupDetailsFromDbRepositoryImpl
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.domain.useCase.FetchSmallGroupDetailsFromDbUseCase
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.domain.useCase.SmallGroupAttendanceHistoryUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class SmallGroupModule {

    @Provides
    @Singleton
    fun provideFetchDidiDetailsFromDbRepository(
        corePrefRepo: CoreSharedPrefs,
        subjectEntityDao: SubjectEntityDao,
        smallGroupDidiMappingDao: SmallGroupDidiMappingDao
    ): FetchDidiDetailsFromDbRepository {
        return FetchDidiDetailsFromDbRepositoryImpl(
            corePrefRepo, subjectEntityDao,
            smallGroupDidiMappingDao
        )
    }

    @Provides
    @Singleton
    fun provideFetchDidiDetailsFromDbUseCase(
        fetchDidiDetailsFromDbRepository: FetchDidiDetailsFromDbRepository
    ): FetchDidiDetailsFromDbUseCase {
        return FetchDidiDetailsFromDbUseCase(fetchDidiDetailsFromDbRepository)
    }

    @Provides
    @Singleton
    fun provideDidiTabUseCase(
        fetchDidiDetailsFromDbRepository: FetchDidiDetailsFromDbRepository,
        fetchSmallGroupListFromDbRepository: FetchSmallGroupListFromDbRepository,
        fetchDidiDetailsFromNetworkRepository: FetchDidiDetailsFromNetworkRepository,
        fetchSmallGroupDetailsFromNetworkRepository: FetchSmallGroupDetailsFromNetworkRepository
    ): DidiTabUseCase {
        return DidiTabUseCase(
            fetchDidiDetailsFromDbUseCase = FetchDidiDetailsFromDbUseCase(
                fetchDidiDetailsFromDbRepository
            ),
            fetchDidiDetailsFromNetworkUseCase = FetchDidiDetailsFromNetworkUseCase(
                fetchDidiDetailsFromNetworkRepository
            ),
            fetchSmallGroupListsFromDbUseCase = FetchSmallGroupListsFromDbUseCase(
                fetchSmallGroupListFromDbRepository
            ),
            fetchSmallGroupFromNetworkUseCase = FetchSmallGroupFromNetworkUseCase(
                fetchSmallGroupDetailsFromNetworkRepository
            )
        )
    }

    @Provides
    @Singleton
    fun provideFetchSmallGroupListFromDbUseCase(
        fetchSmallGroupListFromDbRepository: FetchSmallGroupListFromDbRepository
    ): FetchSmallGroupListsFromDbUseCase {
        return FetchSmallGroupListsFromDbUseCase(fetchSmallGroupListFromDbRepository)
    }

    @Provides
    @Singleton
    fun provideFetchSmallGroupListFromDbRepository(
        coreSharedPrefs: CoreSharedPrefs,
        smallGroupEntityDao: SmallGroupDidiMappingDao
    ): FetchSmallGroupListFromDbRepository {
        return FetchSmallGroupListFromDbRepositoryImpl(coreSharedPrefs, smallGroupEntityDao)
    }

    @Provides
    @Singleton
    fun provideSmallGroupAttendanceHistoryUseCase(fetchSmallGroupDetailsFromDbRepository: FetchSmallGroupDetailsFromDbRepository): SmallGroupAttendanceHistoryUseCase {
        return SmallGroupAttendanceHistoryUseCase(
            fetchSmallGroupDetailsFromDbUseCase = FetchSmallGroupDetailsFromDbUseCase(
                fetchSmallGroupDetailsFromDbRepository
            )
        )
    }

    @Provides
    @Singleton
    fun provideFetchSmallGroupDetailsFromDbUseCase(
        fetchSmallGroupDetailsFromDbRepository: FetchSmallGroupDetailsFromDbRepository
    ): FetchSmallGroupDetailsFromDbUseCase {
        return FetchSmallGroupDetailsFromDbUseCase(fetchSmallGroupDetailsFromDbRepository)
    }

    @Provides
    @Singleton
    fun provideFetchSmallGroupDetailsFromDbRepository(
        coreSharedPrefs: CoreSharedPrefs,
        smallGroupDidiMappingDao: SmallGroupDidiMappingDao
    ): FetchSmallGroupDetailsFromDbRepository {
        return FetchSmallGroupDetailsFromDbRepositoryImpl(
            coreSharedPrefs, smallGroupDidiMappingDao
        )
    }

    @Provides
    @Singleton
    fun provideSmallGroupAttendanceUserCase(
        fetchSmallGroupDetailsFromDbRepository: FetchSmallGroupDetailsFromDbRepository,
        fetchDidiListForSmallGroupFromDbRepository: FetchDidiListForSmallGroupFromDbRepository,
    ): SmallGroupAttendanceUserCase {
        return SmallGroupAttendanceUserCase(
            fetchSmallGroupDetailsFromDbUseCase = FetchSmallGroupDetailsFromDbUseCase(
                fetchSmallGroupDetailsFromDbRepository
            ),
            fetchDidiListForSmallGroupFromDbUseCase = FetchDidiListForSmallGroupFromDbUseCase(
                fetchDidiListForSmallGroupFromDbRepository
            )
        )
    }

    @Provides
    @Singleton
    fun provideFetchDidiListForSmallGroupFromDbUseCase(
        fetchDidiListForSmallGroupFromDbRepository: FetchDidiListForSmallGroupFromDbRepository,
    ): FetchDidiListForSmallGroupFromDbUseCase {
        return FetchDidiListForSmallGroupFromDbUseCase(fetchDidiListForSmallGroupFromDbRepository)
    }

    @Provides
    @Singleton
    fun provideFetchDidiListForSmallGroupFromDbRepository(
        coreSharedPrefs: CoreSharedPrefs,
        smallGroupDidiMappingDao: SmallGroupDidiMappingDao,
        subjectEntityDao: SubjectEntityDao
    ): FetchDidiListForSmallGroupFromDbRepository {
        return FetchDidiListForSmallGroupFromDbRepositoryImpl(
            coreSharedPrefs, smallGroupDidiMappingDao, subjectEntityDao
        )
    }

    @Provides
    @Singleton
    fun provideEventWriterHelper(
        coreSharedPrefs: CoreSharedPrefs,
        eventsDao: EventsDao,
        eventDependencyDao: EventDependencyDao,
        subjectEntityDao: SubjectEntityDao,
        smallGroupDidiMappingDao: SmallGroupDidiMappingDao
    ): EventWriterHelper {
        return EventWriterHelperImpl(
            coreSharedPrefs = coreSharedPrefs,
            eventsDao = eventsDao,
            eventDependencyDao = eventDependencyDao,
            subjectEntityDao = subjectEntityDao,
            smallGroupDidiMappingDao = smallGroupDidiMappingDao
        )
    }

}