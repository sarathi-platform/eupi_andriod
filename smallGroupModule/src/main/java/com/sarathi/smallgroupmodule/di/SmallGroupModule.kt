package com.sarathi.smallgroupmodule.di

import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.syncmanager.EventWriterHelper
import com.sarathi.dataloadingmangement.data.dao.AttributeValueReferenceDao
import com.sarathi.dataloadingmangement.data.dao.SubjectAttributeDao
import com.sarathi.dataloadingmangement.data.dao.SubjectEntityDao
import com.sarathi.dataloadingmangement.data.dao.smallGroup.SmallGroupDidiMappingDao
import com.sarathi.dataloadingmangement.domain.use_case.ContentDownloaderUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchDidiDetailsFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.FetchDidiDetailsFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.FetchSmallGroupAttendanceHistoryFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.FetchSmallGroupFromNetworkUseCase
import com.sarathi.dataloadingmangement.repository.liveihood.FetchDidiDetailsFromDbRepository
import com.sarathi.dataloadingmangement.repository.smallGroup.FetchDidiDetailsFromNetworkRepository
import com.sarathi.dataloadingmangement.repository.smallGroup.FetchSmallGroupAttendanceHistoryFromNetworkRepository
import com.sarathi.dataloadingmangement.repository.smallGroup.FetchSmallGroupDetailsFromNetworkRepository
import com.sarathi.smallgroupmodule.data.domain.EventWriterHelperImpl
import com.sarathi.smallgroupmodule.ui.didiTab.domain.repository.FetchSmallGroupListFromDbRepository
import com.sarathi.smallgroupmodule.ui.didiTab.domain.repository.FetchSmallGroupListFromDbRepositoryImpl
import com.sarathi.smallgroupmodule.ui.didiTab.domain.use_case.DidiTabUseCase
import com.sarathi.smallgroupmodule.ui.didiTab.domain.use_case.FetchSmallGroupListsFromDbUseCase
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.repository.FetchAttendanceHistoryForDateFromDbRepository
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.repository.FetchAttendanceHistoryForDateFromDbRepositoryImpl
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.repository.FetchDidiListForSmallGroupFromDbRepository
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.repository.FetchDidiListForSmallGroupFromDbRepositoryImpl
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.repository.FetchMarkedDatesRepository
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.repository.FetchMarkedDatesRepositoryImpl
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.repository.SaveAttendanceToDbRepository
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.repository.SaveAttendanceToDbRepositoryImpl
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.repository.UpdateAttendanceToDbRepository
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.repository.UpdateAttendanceToDbRepositoryImpl
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.useCase.DeleteAttendanceToDbUseCase
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.useCase.FetchAttendanceHistoryForDateFromDbUseCase
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.useCase.FetchDidiListForSmallGroupFromDbUseCase
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.useCase.FetchMarkedDatesUseCase
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.useCase.SaveAttendanceToDbUseCase
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.useCase.SmallGroupAttendanceEditUserCase
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.useCase.SmallGroupAttendanceUserCase
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.useCase.UpdateAttendanceToDbUseCase
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.domain.repository.FetchSmallGroupAttendanceHistoryFromDbRepository
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.domain.repository.FetchSmallGroupAttendanceHistoryFromDbRepositoryImpl
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.domain.repository.FetchSmallGroupDetailsFromDbRepository
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.domain.repository.FetchSmallGroupDetailsFromDbRepositoryImpl
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.domain.useCase.FetchSmallGroupAttendanceHistoryFromDbUseCase
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
    fun provideDidiTabUseCase(
        coreSharedPrefs: CoreSharedPrefs,
        fetchDidiDetailsFromDbRepository: FetchDidiDetailsFromDbRepository,
        fetchSmallGroupListFromDbRepository: FetchSmallGroupListFromDbRepository,
        fetchDidiDetailsFromNetworkRepository: FetchDidiDetailsFromNetworkRepository,
        fetchSmallGroupDetailsFromNetworkRepository: FetchSmallGroupDetailsFromNetworkRepository,
        fetchSmallGroupAttendanceHistoryFromNetworkRepository: FetchSmallGroupAttendanceHistoryFromNetworkRepository,
        contentDownloaderUseCase: ContentDownloaderUseCase,
    ): DidiTabUseCase {
        return DidiTabUseCase(
            coreSharedPrefs = coreSharedPrefs,
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
            ),
            fetchSmallGroupAttendanceHistoryFromNetworkUseCase = FetchSmallGroupAttendanceHistoryFromNetworkUseCase(
                fetchSmallGroupAttendanceHistoryFromNetworkRepository
            ),
            contentDownloaderUseCase = contentDownloaderUseCase
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
    fun provideSmallGroupAttendanceHistoryUseCase(
        fetchSmallGroupDetailsFromDbRepository: FetchSmallGroupDetailsFromDbRepository,
        fetchSmallGroupAttendanceHistoryFromDbRepository: FetchSmallGroupAttendanceHistoryFromDbRepository,
        updateAttendanceToDbRepository: UpdateAttendanceToDbRepository,
        fetchMarkedDatesRepository: FetchMarkedDatesRepository
    ): SmallGroupAttendanceHistoryUseCase {
        return SmallGroupAttendanceHistoryUseCase(
            fetchSmallGroupDetailsFromDbUseCase = FetchSmallGroupDetailsFromDbUseCase(
                fetchSmallGroupDetailsFromDbRepository
            ),
            fetchSmallGroupAttendanceHistoryFromDbUseCase = FetchSmallGroupAttendanceHistoryFromDbUseCase(
                fetchSmallGroupAttendanceHistoryFromDbRepository
            ),
            deleteAttendanceToDbUseCase = DeleteAttendanceToDbUseCase(updateAttendanceToDbRepository),
            fetchMarkedDatesUseCase = FetchMarkedDatesUseCase(fetchMarkedDatesRepository)
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
        saveAttendanceToDbRepository: SaveAttendanceToDbRepository,
        fetchMarkedDatesRepository: FetchMarkedDatesRepository
    ): SmallGroupAttendanceUserCase {
        return SmallGroupAttendanceUserCase(
            fetchSmallGroupDetailsFromDbUseCase = FetchSmallGroupDetailsFromDbUseCase(
                fetchSmallGroupDetailsFromDbRepository
            ),
            fetchDidiListForSmallGroupFromDbUseCase = FetchDidiListForSmallGroupFromDbUseCase(
                fetchDidiListForSmallGroupFromDbRepository
            ),
            saveAttendanceToDbUseCase = SaveAttendanceToDbUseCase(saveAttendanceToDbRepository),
            fetchMarkedDatesUseCase = FetchMarkedDatesUseCase(fetchMarkedDatesRepository)
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

    @Provides
    @Singleton
    fun provideSaveAttendanceToDbUseCase(saveAttendanceToDbRepository: SaveAttendanceToDbRepository): SaveAttendanceToDbUseCase {
        return SaveAttendanceToDbUseCase(saveAttendanceToDbRepository)
    }

    @Provides
    @Singleton
    fun provideSaveAttendanceToDbRepository(
        coreSharedPrefs: CoreSharedPrefs,
        subjectEntityDao: SubjectEntityDao,
        subjectAttributeDao: SubjectAttributeDao,
        attributeValueReferenceDao: AttributeValueReferenceDao
    ): SaveAttendanceToDbRepository {
        return SaveAttendanceToDbRepositoryImpl(
            coreSharedPrefs = coreSharedPrefs,
            subjectEntityDao = subjectEntityDao,
            subjectAttributeDao = subjectAttributeDao,
            attributeValueReferenceDao = attributeValueReferenceDao
        )
    }

    @Provides
    @Singleton
    fun provideFetchMarkedDatesUseCase(fetchMarkedDatesRepository: FetchMarkedDatesRepository): FetchMarkedDatesUseCase {
        return FetchMarkedDatesUseCase(fetchMarkedDatesRepository)
    }

    @Provides
    @Singleton
    fun provideFetchMarkedDatesRepository(
        coreSharedPrefs: CoreSharedPrefs,
        subjectEntityDao: SubjectEntityDao,
        subjectAttributeDao: SubjectAttributeDao,
        attributeValueReferenceDao: AttributeValueReferenceDao
    ): FetchMarkedDatesRepository {
        return FetchMarkedDatesRepositoryImpl(
            coreSharedPrefs = coreSharedPrefs,
            subjectEntityDao = subjectEntityDao,
            subjectAttributeDao = subjectAttributeDao,
            attributeValueReferenceDao = attributeValueReferenceDao
        )
    }

    @Provides
    @Singleton
    fun provideFetchSmallGroupAttendanceHistoryFromDbRepository(
        coreSharedPrefs: CoreSharedPrefs,
        smallGroupDidiMappingDao: SmallGroupDidiMappingDao,
        subjectEntityDao: SubjectEntityDao,
        subjectAttributeDao: SubjectAttributeDao,
        attributeValueReferenceDao: AttributeValueReferenceDao
    ): FetchSmallGroupAttendanceHistoryFromDbRepository {
        return FetchSmallGroupAttendanceHistoryFromDbRepositoryImpl(
            coreSharedPrefs = coreSharedPrefs,
            smallGroupDidiMappingDao = smallGroupDidiMappingDao,
            subjectEntityDao = subjectEntityDao,
            subjectAttributeDao = subjectAttributeDao,
            attributeValueReferenceDao = attributeValueReferenceDao
        )
    }

    @Provides
    @Singleton
    fun provideFetchSmallGroupAttendanceHistoryFromDbUseCase(
        fetchSmallGroupAttendanceHistoryFromDbRepository: FetchSmallGroupAttendanceHistoryFromDbRepository
    ): FetchSmallGroupAttendanceHistoryFromDbUseCase {
        return FetchSmallGroupAttendanceHistoryFromDbUseCase(
            fetchSmallGroupAttendanceHistoryFromDbRepository
        )
    }

    @Provides
    @Singleton
    fun provideSmallGroupAttendanceEditUserCase(
        fetchSmallGroupDetailsFromDbRepository: FetchSmallGroupDetailsFromDbRepository,
        fetchDidiListForSmallGroupFromDbRepository: FetchDidiListForSmallGroupFromDbRepository,
        updateAttendanceToDbRepository: UpdateAttendanceToDbRepository,
        fetchMarkedDatesRepository: FetchMarkedDatesRepository,
        fetchAttendanceHistoryForDateFromDbRepository: FetchAttendanceHistoryForDateFromDbRepository
    ): SmallGroupAttendanceEditUserCase {
        return SmallGroupAttendanceEditUserCase(
            fetchSmallGroupDetailsFromDbUseCase = FetchSmallGroupDetailsFromDbUseCase(
                fetchSmallGroupDetailsFromDbRepository
            ),
            fetchDidiListForSmallGroupFromDbUseCase = FetchDidiListForSmallGroupFromDbUseCase(
                fetchDidiListForSmallGroupFromDbRepository
            ),
            updateAttendanceToDbUseCase = UpdateAttendanceToDbUseCase(updateAttendanceToDbRepository),
            fetchMarkedDatesUseCase = FetchMarkedDatesUseCase(fetchMarkedDatesRepository),
            fetchAttendanceHistoryForDateFromDbUseCase = FetchAttendanceHistoryForDateFromDbUseCase(
                fetchAttendanceHistoryForDateFromDbRepository
            )
        )
    }

    @Provides
    @Singleton
    fun provideFetchAttendanceHistoryForDateFromDbUseCase(
        fetchAttendanceHistoryForDateFromDbRepository: FetchAttendanceHistoryForDateFromDbRepository
    ): FetchAttendanceHistoryForDateFromDbUseCase {
        return FetchAttendanceHistoryForDateFromDbUseCase(
            fetchAttendanceHistoryForDateFromDbRepository = fetchAttendanceHistoryForDateFromDbRepository
        )
    }

    @Provides
    @Singleton
    fun provideFetchAttendanceHistoryForDateFromDbRepository(
        coreSharedPrefs: CoreSharedPrefs,
        subjectEntityDao: SubjectEntityDao,
        smallGroupDidiMappingDao: SmallGroupDidiMappingDao,
        subjectAttributeDao: SubjectAttributeDao,
        attributeValueReferenceDao: AttributeValueReferenceDao
    ): FetchAttendanceHistoryForDateFromDbRepository {
        return FetchAttendanceHistoryForDateFromDbRepositoryImpl(
            coreSharedPrefs = coreSharedPrefs,
            smallGroupDidiMappingDao = smallGroupDidiMappingDao,
            subjectEntityDao = subjectEntityDao,
            subjectAttributeDao = subjectAttributeDao,
            attributeValueReferenceDao = attributeValueReferenceDao
        )
    }

    @Provides
    @Singleton
    fun provideUpdateAttendanceToDbRepository(
        coreSharedPrefs: CoreSharedPrefs,
        subjectEntityDao: SubjectEntityDao,
        subjectAttributeDao: SubjectAttributeDao,
        attributeValueReferenceDao: AttributeValueReferenceDao
    ): UpdateAttendanceToDbRepository {
        return UpdateAttendanceToDbRepositoryImpl(
            coreSharedPrefs = coreSharedPrefs,
            subjectEntityDao = subjectEntityDao,
            subjectAttributeDao = subjectAttributeDao,
            attributeValueReferenceDao = attributeValueReferenceDao
        )
    }

}