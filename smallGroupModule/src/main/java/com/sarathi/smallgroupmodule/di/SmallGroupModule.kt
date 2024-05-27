package com.sarathi.smallgroupmodule.di

import com.sarathi.dataloadingmangement.data.dao.SubjectEntityDao
import com.sarathi.dataloadingmangement.data.dao.smallGroup.SmallGroupDidiMappingDao
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.FetchDidiDetailsFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.FetchSmallGroupFromNetworkUseCase
import com.sarathi.dataloadingmangement.repository.smallGroup.FetchDidiDetailsFromNetworkRepository
import com.sarathi.dataloadingmangement.repository.smallGroup.FetchSmallGroupDetailsFromNetworkRepository
import com.sarathi.smallgroupmodule.ui.didiTab.domain.repository.FetchDidiDetailsFromDbRepository
import com.sarathi.smallgroupmodule.ui.didiTab.domain.repository.FetchDidiDetailsFromDbRepositoryImpl
import com.sarathi.smallgroupmodule.ui.didiTab.domain.repository.FetchSmallGroupDetailsFromDbRepository
import com.sarathi.smallgroupmodule.ui.didiTab.domain.repository.FetchSmallGroupDetailsFromDbRepositoryImpl
import com.sarathi.smallgroupmodule.ui.didiTab.domain.use_case.DidiTabUseCase
import com.sarathi.smallgroupmodule.ui.didiTab.domain.use_case.FetchDidiDetailsFromDbUseCase
import com.sarathi.smallgroupmodule.ui.didiTab.domain.use_case.FetchSmallGroupListsFromDbUseCase
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
//        corePrefRepo: CorePrefRepo,
        subjectEntityDao: SubjectEntityDao,
        smallGroupDidiMappingDao: SmallGroupDidiMappingDao
    ): FetchDidiDetailsFromDbRepository {
        return FetchDidiDetailsFromDbRepositoryImpl(/*corePrefRepo, */subjectEntityDao,
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
        fetchSmallGroupDetailsFromDbRepository: FetchSmallGroupDetailsFromDbRepository,
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
                fetchSmallGroupDetailsFromDbRepository
            ),
            fetchSmallGroupFromNetworkUseCase = FetchSmallGroupFromNetworkUseCase(
                fetchSmallGroupDetailsFromNetworkRepository
            )
        )
    }

    @Provides
    @Singleton
    fun provideFetchSmallGroupListFromDbUseCase(
        fetchSmallGroupDetailsFromDbRepository: FetchSmallGroupDetailsFromDbRepository
    ): FetchSmallGroupListsFromDbUseCase {
        return FetchSmallGroupListsFromDbUseCase(fetchSmallGroupDetailsFromDbRepository)
    }

    @Provides
    @Singleton
    fun provideFetchSmallGroupDetailsFromDbRepository(
        smallGroupEntityDao: SmallGroupDidiMappingDao
    ): FetchSmallGroupDetailsFromDbRepository {
        return FetchSmallGroupDetailsFromDbRepositoryImpl(smallGroupEntityDao)
    }

}